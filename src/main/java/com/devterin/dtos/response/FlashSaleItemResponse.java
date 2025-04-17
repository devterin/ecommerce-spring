package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashSaleItemResponse {
    private Long id;
    private Long variantId;
    private String variantName;
    private String productName;
    private String imageUrl;
    private Integer salePrice;
    private Integer quantity;
    private Integer soldQuantity;
}
