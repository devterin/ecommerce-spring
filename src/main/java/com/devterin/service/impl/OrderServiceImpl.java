package com.devterin.service.impl;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.dtos.response.PageResponse;
import com.devterin.entity.*;
import com.devterin.enums.OrderStatus;
import com.devterin.enums.PaymentMethod;
import com.devterin.enums.PaymentStatus;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.mapper.OrderMapper;
import com.devterin.repository.*;
import com.devterin.service.CouponService;
import com.devterin.service.OrderService;
import com.devterin.util.AppConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Value("${order.confirmation-hold-minutes:30}")
    private long confirmationHoldMinutes;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final VariantRepository variantRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final CouponService couponService;
    private final OrderMapper orderMapper;
    private final PaymentRepository paymentRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;


    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Override
    public PageResponse<OrderResponse> getAllOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> page = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream().map(orderMapper::toDto).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }


    @Override
    public List<OrderResponse> getOrderByUserId(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderUserByOrderId(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view this order");
        }

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot create order.");
        }
        List<Variant> checkAndUpdateStock = checkAndUpdateStock(cart.getCartItems());

        Order order = buildOrder(user, cart.getTotalPrice(), request);
        Order savedOrder = orderRepository.save(order);
        variantRepository.saveAll(checkAndUpdateStock);


        List<OrderItem> orderItems = buildCartOrderItems(order, cart.getCartItems());
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        cartRepository.delete(cart);

        return orderMapper.toDto(order);
    }


    @Override
    @Transactional
    public OrderResponse createFlashSaleOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        FlashSaleItem flashSaleItem = flashSaleItemRepository.findById(request.getFlashSaleItemId())
                .orElseThrow(() -> new IllegalArgumentException("FlashSale Item not found"));

        // Validate quantity
        int availableQuantity = flashSaleItem.getQuantity() - flashSaleItem.getSoldQuantity();
        if (availableQuantity < request.getFlashSaleQuantity()) {
            throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_QUANTITY.getMessage());
        }

        Variant variant = variantRepository.findByIdForUpdate(flashSaleItem.getVariant().getId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        if (variant.getStockQuantity() < request.getFlashSaleQuantity()) {
            throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_QUANTITY.getMessage());
        }
        int totalAmount = flashSaleItem.getSalePrice() * request.getFlashSaleQuantity();
        Order order = buildOrder(user, totalAmount, request);
        List<OrderItem> orderItems = List.of(buildFlashSaleOrderItem(order, flashSaleItem, request));

        Order savedOrder = orderRepository.save(order);
        savedOrder.setOrderItems(orderItems);

        orderItemRepository.saveAll(orderItems);
        // Update quantities
        flashSaleItem.setSoldQuantity(flashSaleItem.getSoldQuantity() + request.getFlashSaleQuantity());
        variant.setStockQuantity(variant.getStockQuantity() - request.getFlashSaleQuantity());
        variant.setSoldQuantity((variant.getSoldQuantity() + request.getFlashSaleQuantity()));

        flashSaleItemRepository.save(flashSaleItem);
        variantRepository.save(variant);

        return orderMapper.toDto(savedOrder);
    }

    private OrderItem buildFlashSaleOrderItem(Order order, FlashSaleItem flashSaleItem, OrderRequest request) {
        return OrderItem.builder()
                .order(order)
                .variant(flashSaleItem.getVariant())
                .flashSaleItem(flashSaleItem)
                .quantity(request.getFlashSaleQuantity())
                .unitPrice(flashSaleItem.getSalePrice())
                .totalPrice(flashSaleItem.getSalePrice() * request.getFlashSaleQuantity())
                .build();
    }

    private List<OrderItem> buildCartOrderItems(Order order, List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItems.add(OrderItem.builder()
                    .order(order)
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build());
        }
        return orderItems;
    }


    private Order buildOrder(User user, Integer totalAmount, OrderRequest request) {
        if (request.getPaymentMethod() == null
                || (!PaymentMethod.COD.name().equals(request.getPaymentMethod())
                && !PaymentMethod.VN_PAY.name().equals(request.getPaymentMethod()))) {
            throw new IllegalArgumentException("Supported payment methods are COD and VN_PAY");
        }
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .note(request.getNote())
                .address(resolveDeliveryAddress(user, request))
                .build();
        order.setCreatedAt(LocalDateTime.now());

        // Apply coupon
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            log.info("Applying coupon code: {}", request.getCouponCode());
            couponService.applyCoupon(request.getCouponCode(), user.getId(), order);
        } else {
            log.info("No coupon code provided, proceeding without discount");
            order.setDiscountAmount(0);
        }

        // Calculate shipping fee
        int shippingFee = order.getTotalAmount() > AppConstants.FREE_SHIPPING_THRESHOLD ? 0 : AppConstants.SHIPPING_FEE;
        order.setShippingFee(shippingFee);

        // Create payment
        long totalAmountPayment = Math.max(0L,
                (long) order.getTotalAmount() - order.getDiscountAmount() + shippingFee);
        Payment payment = Payment.builder()
                .amount(totalAmountPayment)
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(PaymentStatus.PENDING)
                .order(order)
                .build();
        order.setPayment(payment);

        return order;
    }

    private String resolveDeliveryAddress(User user, OrderRequest request) {
        if (request.getDeliveryAddressId() == null) return request.getAddress();
        DeliveryAddress address = deliveryAddressRepository.findByIdAndUser_Id(request.getDeliveryAddressId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Delivery address not found for this user"));
        return String.join(", ", address.getFirstName() + " " + address.getLastName(), address.getPhoneNumber(), address.getAddress(), address.getCity(), address.getPostalCode());
    }


    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.PROCESSING) {
            if (order.getCreatedAt() == null) {
                throw new AppException(ErrorCode.ORDER_TIMESTAMP_MISSING);
            }
            if (LocalDateTime.now().isBefore(order.getCreatedAt().plusMinutes(confirmationHoldMinutes))) {
                throw new AppException(ErrorCode.ORDER_CONFIRMATION_HOLD_ACTIVE);
            }
        } else if (currentStatus == OrderStatus.PROCESSING && newStatus == OrderStatus.SHIPPED) {
            // Valid sequential fulfillment transition.
        } else {
            throw new AppException(ErrorCode.INVALID_ORDER_TRANSITION);
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderDetails(Long orderId, Long userId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId))
            throw new RuntimeException("You are not authorized to view this order");

        if (order.getOrderStatus() != OrderStatus.PENDING)
            throw new IllegalStateException("Order details can only be updated in PENDING status.");

        order.setAddress(resolveDeliveryAddress(order.getUser(), request));
        order.setNote(request.getNote());
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to cancel this order.");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order cannot be cancelled in " + order.getOrderStatus() + " status.");
        }
        if (order.getPayment() == null) {
            throw new IllegalStateException("Order has no payment record");
        }
        Payment payment = paymentRepository.findByIdForUpdate(order.getPayment().getId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (payment.getPaymentMethod() == PaymentMethod.VN_PAY
                && payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("A completed VNPay payment cannot be cancelled online");
        }
        if (payment.getPaymentMethod() == PaymentMethod.VN_PAY
                && payment.getPaymentStatus() == PaymentStatus.PENDING
                && payment.getVnpayExpireAt() != null
                && LocalDateTime.now().isBefore(payment.getVnpayExpireAt())) {
            throw new IllegalStateException("VNPay payment is still active; try cancelling again after it expires.");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        payment.setPaymentStatus(PaymentStatus.FAILED);

        rollbackStock(order.getOrderItems());

        paymentRepository.save(payment);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public void failVnPayOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getPayment().getPaymentMethod() != PaymentMethod.VN_PAY) {
            throw new IllegalStateException("Order does not use VNPay");
        }
        if (order.getPayment().getPaymentStatus() == PaymentStatus.COMPLETED
                || order.getOrderStatus() == OrderStatus.CANCELLED) {
            return;
        }
        order.getPayment().setPaymentStatus(PaymentStatus.FAILED);
        order.setOrderStatus(OrderStatus.CANCELLED);
        rollbackStock(order.getOrderItems());
        paymentRepository.save(order.getPayment());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void expirePendingVnPayOrders(LocalDateTime now) {
        List<Payment> expiredPayments = paymentRepository
                .findAllByPaymentMethodAndPaymentStatusAndVnpayExpireAtBefore(
                        PaymentMethod.VN_PAY, PaymentStatus.PENDING, now);
        for (Payment candidate : expiredPayments) {
            Payment payment = paymentRepository.findByIdForUpdate(candidate.getId()).orElse(null);
            if (payment == null
                    || payment.getPaymentStatus() != PaymentStatus.PENDING
                    || payment.getVnpayExpireAt() == null
                    || payment.getVnpayExpireAt().isAfter(now)) {
                continue;
            }
            Order order = payment.getOrder();
            if (order == null || order.getOrderStatus() != OrderStatus.PENDING) {
                continue;
            }
            payment.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.CANCELLED);
            rollbackStock(order.getOrderItems());
            paymentRepository.save(payment);
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public boolean restoreExpiredVnPayOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.CANCELLED
                || order.getPayment().getPaymentMethod() != PaymentMethod.VN_PAY
                || (order.getPayment().getPaymentStatus() != PaymentStatus.FAILED
                && order.getPayment().getPaymentStatus() != PaymentStatus.PENDING)) {
            return false;
        }

        Map<Long, Integer> quantitiesByVariant = new HashMap<>();
        for (OrderItem item : order.getOrderItems()) {
            quantitiesByVariant.merge(item.getVariant().getId(), item.getQuantity(), Integer::sum);
        }
        Map<Long, Variant> variants = new HashMap<>();
        quantitiesByVariant.keySet().stream().sorted().forEach(variantId ->
                variants.put(variantId, variantRepository.findByIdForUpdate(variantId)
                        .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND))));
        for (var entry : quantitiesByVariant.entrySet()) {
            if (variants.get(entry.getKey()).getStockQuantity() < entry.getValue()) {
                return false;
            }
        }
        for (var entry : quantitiesByVariant.entrySet()) {
            Variant variant = variants.get(entry.getKey());
            variant.setStockQuantity(variant.getStockQuantity() - entry.getValue());
            variant.setSoldQuantity(variant.getSoldQuantity() + entry.getValue());
        }
        variantRepository.saveAll(variants.values());
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        return true;
    }

    @Override
    @Transactional
    public OrderResponse confirmOrderDeliveryByCOD(Long orderId, Long userId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // check roles
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to confirm this order");
        }
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Order is not in SHIPPED status. Cannot confirm delivery.");
        }
        // COD is completed on receipt; VNPay must retain the gateway-confirmed state.
        Payment payment = order.getPayment();
        order.setOrderStatus(OrderStatus.DELIVERED);
        if (payment.getPaymentMethod() == PaymentMethod.COD) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private void rollbackStock(List<OrderItem> orderItems) {
        Map<Long, Integer> quantitiesByVariant = new HashMap<>();
        Map<Long, Integer> quantitiesByFlashSaleItem = new HashMap<>();
        for (OrderItem item : orderItems) {
            quantitiesByVariant.merge(item.getVariant().getId(), item.getQuantity(), Integer::sum);
            if (item.getFlashSaleItem() != null) {
                quantitiesByFlashSaleItem.merge(item.getFlashSaleItem().getId(), item.getQuantity(), Integer::sum);
            }
        }
        List<Variant> variantsToUpdate = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : quantitiesByVariant.entrySet()) {
            Variant variant = variantRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
            int quantity = entry.getValue();
            variant.setStockQuantity(variant.getStockQuantity() + quantity);
            variant.setSoldQuantity(Math.max(0, variant.getSoldQuantity() - quantity));
            variantsToUpdate.add(variant);
        }
        for (Map.Entry<Long, Integer> entry : quantitiesByFlashSaleItem.entrySet()) {
            FlashSaleItem flashSaleItem = flashSaleItemRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Flash Sale item not found"));
            flashSaleItem.setSoldQuantity(Math.max(0, flashSaleItem.getSoldQuantity() - entry.getValue()));
            flashSaleItemRepository.save(flashSaleItem);
        }
        variantRepository.saveAll(variantsToUpdate);
    }

    private List<Variant> checkAndUpdateStock(List<CartItem> cartItems) {
        List<Variant> variantsToUpdate = new ArrayList<>();
        Map<Long, Variant> variantsById = new HashMap<>();
        cartItems.stream().map(item -> item.getVariant().getId()).distinct().sorted().forEach(id ->
                variantsById.put(id, variantRepository.findByIdForUpdate(id)
                        .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND))));
        for (CartItem item : cartItems) {
            Variant variant = variantsById.get(item.getVariant().getId());
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for variant: " + variant.getProduct().getName());
            }
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() + item.getQuantity());
            variantsToUpdate.add(variant);
        }
        return variantsToUpdate;
    }
}
