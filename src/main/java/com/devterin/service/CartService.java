package com.devterin.service;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> getAllCarts();
    CartResponse addProductToCart(Long userId, CartRequest request);
    CartResponse updateCartItem(Long userId, CartRequest request);
    CartResponse removeProductFromCart(Long userId, Long variantId);
    CartResponse getCartByUserId(Long userId);

}
