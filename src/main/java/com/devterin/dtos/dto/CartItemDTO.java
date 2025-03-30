package com.devterin.dtos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {
    private Long variantId;
    private String name;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
}
