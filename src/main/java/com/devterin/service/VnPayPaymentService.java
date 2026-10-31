package com.devterin.service;

import com.devterin.configuration.VNPayConfig;
import com.devterin.entity.Order;
import com.devterin.entity.Payment;
import com.devterin.enums.OrderStatus;
import com.devterin.enums.PaymentMethod;
import com.devterin.enums.PaymentStatus;
import com.devterin.repository.OrderRepository;
import com.devterin.repository.PaymentRepository;
import com.devterin.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class VnPayPaymentService {
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String SUCCESS_CODE = "00";

    private final VNPayConfig properties;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public String createPaymentUrlForUser(Long orderId, Long userId, String clientIp) {
        orderService.getOrderUserByOrderId(orderId, userId);
        return createPaymentUrl(orderId, clientIp);
    }

    @Transactional(readOnly = true)
    public LocalDateTime getPaymentExpiry(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return order.getPayment().getVnpayExpireAt();
    }

    @Transactional
    public String createPaymentUrl(Long orderId, String clientIp) {
        requireConfigured();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        Payment payment = order.getPayment();
        if (payment == null || payment.getPaymentMethod() != PaymentMethod.VN_PAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order does not use VNPay");
        }
        if (payment.getPaymentStatus() != PaymentStatus.PENDING || order.getOrderStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VNPay payment is no longer pending");
        }

        LocalDateTime now = LocalDateTime.now(VIETNAM_ZONE);
        if (payment.getVnpayPaymentUrl() != null) {
            if (payment.getVnpayExpireAt() == null || !now.isBefore(payment.getVnpayExpireAt())) {
                throw new ResponseStatusException(HttpStatus.GONE, "VNPay payment link has expired");
            }
            return payment.getVnpayPaymentUrl();
        }

        LocalDateTime expiresAt = now.plusMinutes(properties.getExpireMinutes());
        String txnRef = order.getId() + "-" + UUID.randomUUID().toString().replace("-", "");
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", properties.getTmnCode());
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VNPay amount must be greater than zero");
        }
        params.put("vnp_Amount", Long.toString(Math.multiplyExact(payment.getAmount().longValue(), 100L)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", properties.getOrderType());
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", properties.getReturnUrl());
        params.put("vnp_IpAddr", normalizeIp(clientIp));
        params.put("vnp_CreateDate", VNPAY_DATE.format(now));
        params.put("vnp_ExpireDate", VNPAY_DATE.format(expiresAt));

        String hashData = VNPayUtil.encodeParams(params);
        String signature = VNPayUtil.hmacSha512(properties.getHashSecret(), hashData);
        String paymentUrl = properties.getPayUrl() + "?" + hashData
                + "&vnp_SecureHash=" + signature;

        payment.setVnpayTxnRef(txnRef);
        payment.setVnpayExpireAt(expiresAt);
        payment.setVnpayPaymentUrl(paymentUrl);
        paymentRepository.save(payment);
        return paymentUrl;
    }

    @Transactional
    public Map<String, String> processIpn(Map<String, String> params) {
        requireConfigured();
        if (!isValidSignature(params)) {
            return ipnResponse("97", "Invalid signature");
        }
        if (!properties.getTmnCode().equals(params.get("vnp_TmnCode"))) {
            return ipnResponse("97", "Invalid merchant");
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null || txnRef.isBlank()) {
            return ipnResponse("01", "Order not found");
        }
        Optional<Payment> paymentResult = paymentRepository.findByVnpayTxnRefForUpdate(txnRef);
        if (paymentResult.isEmpty()) {
            return ipnResponse("01", "Order not found");
        }
        Payment payment = paymentResult.get();
        Order order = payment.getOrder();
        if (order == null || !amountMatches(payment, params.get("vnp_Amount"))) {
            return ipnResponse("04", "Invalid amount");
        }
        if (!txnRef.equals(payment.getVnpayTxnRef())) {
            return ipnResponse("04", "Invalid transaction reference");
        }

        String transactionNo = params.get("vnp_TransactionNo");
        boolean paid = SUCCESS_CODE.equals(params.get("vnp_ResponseCode"))
                && SUCCESS_CODE.equals(params.get("vnp_TransactionStatus"));
        if (paid && isBlank(transactionNo)) {
            return ipnResponse("04", "Missing transaction number");
        }
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return transactionNo != null && transactionNo.equals(payment.getVnpayTransactionNo())
                    ? ipnResponse("00", "Confirm Success")
                    : ipnResponse("04", "Transaction already processed");
        }
        if (payment.getPaymentStatus() == PaymentStatus.FAILED && !paid) {
            return ipnResponse("00", "Confirm Success");
        }

        if (paid) {
            if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                boolean stockRestored = orderService.restoreExpiredVnPayOrder(order.getId());
                if (!stockRestored) {
                    log.error("VNPay payment {} succeeded after its order was cancelled and stock could not be restored; manual fulfillment or refund is required",
                            txnRef);
                }
            } else if (order.getOrderStatus() != OrderStatus.PENDING) {
                return ipnResponse("02", "Order already confirmed");
            }
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setVnpayTransactionNo(transactionNo);
            paymentRepository.save(payment);
            return ipnResponse("00", "Confirm Success");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setVnpayTransactionNo(transactionNo);
            paymentRepository.save(payment);
            orderService.failVnPayOrder(order.getId());
        }
        return ipnResponse("00", "Confirm Success");
    }

    @Transactional
    public URI createReturnRedirect(Map<String, String> params) {
        requireConfigured();
        String status = "invalid";
        Long orderId = null;
        if (isValidSignature(params)
                && properties.getTmnCode().equals(params.get("vnp_TmnCode"))) {
            Payment payment = paymentRepository.findByVnpayTxnRefForUpdate(params.get("vnp_TxnRef"))
                    .orElse(null);
            if (payment != null && amountMatches(payment, params.get("vnp_Amount"))) {
                orderId = payment.getOrder().getId();
                Map<String, String> callbackParams = new java.util.HashMap<>(params);
                callbackParams.putIfAbsent(
                        "vnp_TransactionStatus",
                        callbackParams.getOrDefault("vnp_ResponseCode", ""));
                processIpn(callbackParams);
                status = payment.getPaymentStatus() == PaymentStatus.COMPLETED
                        ? "success"
                        : payment.getPaymentStatus() == PaymentStatus.FAILED
                        ? "failed"
                        : "checking";
            }
        }

        UriComponentsBuilder redirect = UriComponentsBuilder.fromUriString(properties.getFrontendResultUrl())
                .queryParam("status", status);
        if (orderId != null) {
            redirect.queryParam("orderId", orderId);
        }
        return redirect.build().encode().toUri();
    }

    private void requireConfigured() {
        if (!properties.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "VNPay is disabled");
        }
        if (isBlank(properties.getTmnCode()) || isBlank(properties.getHashSecret())
                || isBlank(properties.getPayUrl()) || isBlank(properties.getReturnUrl())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "VNPay credentials and callback URLs are not configured");
        }
        if (properties.getExpireMinutes() < 1) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "VNPay expiration must be at least one minute");
        }
    }

    private boolean isValidSignature(Map<String, String> params) {
        String suppliedSignature = params.get("vnp_SecureHash");
        if (isBlank(suppliedSignature)) {
            return false;
        }
        Map<String, String> signedParams = new TreeMap<>(params);
        signedParams.remove("vnp_SecureHash");
        signedParams.remove("vnp_SecureHashType");
        return VNPayUtil.hasValidSignature(
                properties.getHashSecret(), signedParams, suppliedSignature);
    }

    private boolean amountMatches(Payment payment, String vnpAmount) {
        if (isBlank(vnpAmount) || payment.getAmount() == null) {
            return false;
        }
        try {
            return Long.parseLong(vnpAmount) == Math.multiplyExact(payment.getAmount().longValue(), 100L);
        } catch (NumberFormatException | ArithmeticException invalidAmount) {
            return false;
        }
    }

    private String normalizeIp(String ip) {
        if (isBlank(ip) || ip.length() > 45 || ip.contains(",")) {
            return "127.0.0.1";
        }
        return ip;
    }

    private Map<String, String> ipnResponse(String code, String message) {
        return Map.of("RspCode", code, "Message", message);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
