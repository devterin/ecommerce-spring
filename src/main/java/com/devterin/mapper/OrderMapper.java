package com.devterin.mapper;

import com.devterin.dtos.dto.OrderItemDTO;
import com.devterin.dtos.dto.PaymentDTO;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.entity.Order;
import com.devterin.entity.OrderItem;
import com.devterin.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class OrderMapper {

    public OrderResponse toDto(Order order) {

        List<OrderItemDTO> items = order.getOrderItems().stream().map(item ->
                        OrderItemDTO.builder()
                                .variantId(item.getVariant().getId())
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


        return OrderResponse.builder()
                .orderId(order.getId())
                .userEmail(order.getUser().getEmail())
                .orderStatus(order.getOrderStatus().name())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .payment(payment)
                .build();
    }
}
