package com.devterin.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private String note;
    private String address;
    private Long deliveryAddressId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    private String couponCode;

//    // Flash sale fields
    private Long flashSaleItemId;

    @Min(value = 1, message = "Flash sale quantity must be at least 1")
    private Integer flashSaleQuantity;
}
