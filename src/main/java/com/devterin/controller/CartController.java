package com.devterin.controller;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.CartResponse;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.entity.Cart;
import com.devterin.service.impl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    @GetMapping
    public ApiResponse<List<CartResponse>> getALlCarts() {

        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getAllCarts())
                .build();
    }

    @GetMapping("/{userId}/{cartId}")
    public ApiResponse<CartResponse> getCartById(@PathVariable Long userId,
                                                 @PathVariable Long cartId) {

        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartById(userId, cartId))
                .build();
    }


    @PostMapping("/{userId}")
    public ApiResponse<CartResponse> addProductToCart(@PathVariable Long userId,
                                                      @RequestBody CartRequest request) {

        return ApiResponse.<CartResponse>builder()
                .message("Added product to cart")
                .result(cartService.addProductToCart(userId, request))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<CartResponse> updateCart(@PathVariable Long userId,
                                                @RequestBody CartRequest request) {

        return ApiResponse.<CartResponse>builder()
                .message("Updated cart")
                .result(cartService.updateCartItem(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}/{variantId}")
    public ApiResponse<CartResponse> deleteCart(@PathVariable Long userId,
                                                @PathVariable Long variantId) {

        return ApiResponse.<CartResponse>builder()
                .message("Deleted product to cart")
                .result(cartService.removeProductFromCart(userId, variantId))
                .build();
    }

}
