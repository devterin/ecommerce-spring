package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {
    private Long wishlistId;
    private Long variantId;
    private String variantName;
    private Integer price;
}
