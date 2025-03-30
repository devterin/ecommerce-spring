package com.devterin.controller;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.CartResponse;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.service.impl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    @PostMapping("/{userId}")
    public ApiResponse<CartResponse> addProductToCart(@PathVariable Long userId,
                                                      @RequestBody CartRequest request) {

        return ApiResponse.<CartResponse>builder()
                .message("Added product to cart")
                .result(cartService.addProductToCart(userId, request))
                .build();
    }
}
