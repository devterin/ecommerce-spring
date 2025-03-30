package com.devterin.dtos.response;

import com.devterin.dtos.dto.CartItemDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private Integer totalPrice;
    private List<CartItemDTO> items;
}
