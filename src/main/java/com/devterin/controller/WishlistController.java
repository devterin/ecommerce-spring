package com.devterin.controller;

import com.devterin.dtos.request.WishlistRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.WishlistResponse;
import com.devterin.service.WishlistService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<WishlistResponse>> getMyWishlist() {

        return ApiResponse.<List<WishlistResponse>>builder()
                .result(wishlistService.myWishlist()).build();
    }

    @PostMapping
    public ApiResponse<WishlistResponse> addProductToWishlist(@RequestBody WishlistRequest request) {

        return ApiResponse.<WishlistResponse>builder()
                .message("Added to wishlist")
                .result(wishlistService.addProductToWishlist(request)).build();
    }

    @DeleteMapping("/{variantId}")
    public ApiResponse<Void> removeFromWishlist(@PathVariable Long variantId) {
        wishlistService.removeFromWishlist(variantId);

        return ApiResponse.<Void>builder()
                .message("Removed to wishlist").build();
    }

}
