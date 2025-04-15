package com.devterin.mapper;

import com.devterin.dtos.response.WishlistResponse;
import com.devterin.entity.Wishlist;
import org.springframework.stereotype.Component;

@Component
public class WishlistMapper {

    public WishlistResponse toDto(Wishlist wishlist) {

        return WishlistResponse.builder()
                .wishlistId(wishlist.getId())
                .variantId(wishlist.getVariant().getId())
                .variantName(wishlist.getVariant().getName())
                .price(wishlist.getVariant().getPrice())
                .build();
    }
}
