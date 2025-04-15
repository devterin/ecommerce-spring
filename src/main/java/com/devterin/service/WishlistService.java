package com.devterin.service;

import com.devterin.dtos.request.WishlistRequest;
import com.devterin.dtos.response.WishlistResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse addProductToWishlist(WishlistRequest request);

    void removeFromWishlist(Long variantId);

    List<WishlistResponse> myWishlist();
}
