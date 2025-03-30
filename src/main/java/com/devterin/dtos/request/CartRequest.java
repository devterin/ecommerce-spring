package com.devterin.dtos.request;

import com.devterin.dtos.dto.CartItemDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartRequest {
    private Long variantId;
    private Integer quantity;
}
