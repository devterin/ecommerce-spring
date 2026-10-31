package com.devterin.service;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.dtos.response.VnPayCheckoutResponse;
import com.devterin.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VnPayCheckoutService {
    private final OrderService orderService;
    private final VnPayPaymentService vnPayPaymentService;

    @Transactional
    public VnPayCheckoutResponse createCheckout(Long userId, OrderRequest request, String clientIp) {
        if (request.getPaymentMethod() == null
                || !PaymentMethod.VN_PAY.name().equals(request.getPaymentMethod())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VNPay checkout requires paymentMethod=VN_PAY");
        }
        OrderResponse order = orderService.createOrder(userId, request);
        String paymentUrl = vnPayPaymentService.createPaymentUrl(order.getOrderId(), clientIp);
        return VnPayCheckoutResponse.builder()
                .orderId(order.getOrderId())
                .paymentUrl(paymentUrl)
                .expiresAt(vnPayPaymentService.getPaymentExpiry(order.getOrderId()))
                .build();
    }

}
