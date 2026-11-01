package com.devterin.mapper;

import com.devterin.dtos.dto.OrderItemDTO;
import com.devterin.dtos.dto.PaymentDTO;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.entity.Order;
import com.devterin.entity.OrderItem;
import com.devterin.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final long confirmationHoldMinutes;

    public OrderMapper(@Value("${order.confirmation-hold-minutes:30}") long confirmationHoldMinutes) {
        this.confirmationHoldMinutes = confirmationHoldMinutes;
    }

    public OrderResponse toDto(Order order) {

        List<OrderItemDTO> items = order.getOrderItems().stream().map(item ->
                        OrderItemDTO.builder()
                                .variantId(item.getVariant().getId())
                                .productId(item.getVariant().getProduct().getId())
                                .name(item.getVariant().getName())
                                .unitPrice(item.getUnitPrice())
                                .totalPrice(item.getTotalPrice())
                                .quantity(item.getQuantity())
                                .build())
                .collect(Collectors.toList());

        PaymentDTO payment = PaymentDTO.builder()
                .paymentId(order.getPayment().getId())
                .amount(order.getPayment().getAmount())
                .paymentDate(LocalDate.now())
                .paymentMethod(String.valueOf(order.getPayment().getPaymentMethod()))
                .paymentStatus(String.valueOf(order.getPayment().getPaymentStatus()))
                .build();


        LocalDateTime statusConfirmableAt = order.getCreatedAt() == null
                ? null
                : order.getCreatedAt().plusMinutes(confirmationHoldMinutes);
        boolean canAdminConfirm = order.getOrderStatus() == com.devterin.enums.OrderStatus.PENDING
                && statusConfirmableAt != null
                && !LocalDateTime.now().isBefore(statusConfirmableAt);

        return OrderResponse.builder()
                .orderId(order.getId())
                .userEmail(order.getUser().getEmail())
                .customerName(order.getUser().getFullName())
                .phoneNumber(order.getUser().getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderStatus(order.getOrderStatus().name())
                .orderDate(order.getOrderDate())
                .createdAt(order.getCreatedAt())
                .statusConfirmableAt(statusConfirmableAt)
                .canAdminConfirm(canAdminConfirm)
                .shippingFee(order.getShippingFee() != null ? order.getShippingFee() : 0)
                .totalAmount(order.getTotalAmount())
                .coupon(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getTotalAmount() - order.getDiscountAmount()
                        + (order.getShippingFee() != null ? order.getShippingFee() : 0))
                .items(items)
                .payment(payment)
                .build();
    }
}
