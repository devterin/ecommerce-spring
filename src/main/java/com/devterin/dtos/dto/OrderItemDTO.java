package com.devterin.dtos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {
    private Long variantId; // ID sản phẩm
    private String name; // Tên sản phẩm
    private Integer quantity;  // Số lượng đặt mua
    private Integer unitPrice; // Giá mỗi sản phẩm
    private Integer totalPrice; // Giá tổng sản phẩm
}
