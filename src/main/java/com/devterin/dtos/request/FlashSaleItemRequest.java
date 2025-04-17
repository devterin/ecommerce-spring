package com.devterin.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashSaleItemRequest {
    @NotNull(message = "Variant ID cannot be null")
    private Long variantId;


    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
