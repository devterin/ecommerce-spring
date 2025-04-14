package com.devterin.dtos.response;

import com.devterin.dtos.dto.OrderItemDTO;
import com.devterin.dtos.dto.PaymentDTO;
import com.devterin.utils.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String userEmail;
    private String orderStatus;
    private LocalDate orderDate;
    private Integer shippingFee;
    private Integer totalAmount; // original price
    private String coupon;
    private Integer discountAmount;
    private Integer finalAmount;
    private List<OrderItemDTO> items; // list sản phẩm trong đơn hàng
    private PaymentDTO payment;

}
