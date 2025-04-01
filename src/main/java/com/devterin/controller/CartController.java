package com.devterin.controller;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.CartResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.service.impl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<CartResponse>> getALlCarts() {

        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getAllCarts())
                .build();
    }

    @GetMapping("/myCart")
    public ApiResponse<CartResponse> getMyCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartByUserId(userId))
                .build();
    }

    @PostMapping
    public ApiResponse<CartResponse> addProductToCart(@RequestBody CartRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        log.info("Info user: {}", userDetails);

        return ApiResponse.<CartResponse>builder()
                .message("Added product to cart")
                .result(cartService.addProductToCart(userId, request))
                .build();
    }

    @PutMapping
    public ApiResponse<CartResponse> updateCart(@RequestBody CartRequest request,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        log.info("Info user: {}", userDetails);

        return ApiResponse.<CartResponse>builder()
                .message("Updated cart")
                .result(cartService.updateCartItem(userId, request))
                .build();
    }


    @DeleteMapping("/{variantId}")
    public ApiResponse<CartResponse> deleteCart(@PathVariable Long variantId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        log.info("Info user: {}", userDetails);

        return ApiResponse.<CartResponse>builder()
                .message("Deleted product to cart")
                .result(cartService.removeProductFromCart(userId, variantId))
                .build();
    }

}
