package com.devterin.mapper;

import com.devterin.dtos.dto.CartItemDTO;
import com.devterin.dtos.response.CartResponse;
import com.devterin.entity.Cart;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartResponse toDto(Cart cart) {

        List<CartItemDTO> cartItem = cart.getCartItems().stream()
                .map(item -> CartItemDTO.builder()
                        .variantId(item.getVariant().getId())
                        .name(item.getVariant().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build()).toList();

        return CartResponse.builder()
                .cartId(cart.getId())
                .totalPrice(cart.getTotalPrice())
                .items(cartItem)
                .build();
    }
}
