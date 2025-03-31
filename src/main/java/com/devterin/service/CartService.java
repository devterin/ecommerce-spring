package com.devterin.service;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.CartResponse;

public interface CartService {
    CartResponse addProductToCart(Long userId, CartRequest request);
    CartResponse updateCartItem(Long userId, CartRequest request);
    CartResponse removeProductFromCart(Long userId, Long variantId);
}
