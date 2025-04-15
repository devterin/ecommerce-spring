package com.devterin.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistRequest {
    private Long variantId;
}
