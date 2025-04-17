package com.devterin.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private String note;
    private String address;
    private String paymentMethod;
    private String couponCode;

//    // Flash sale fields
    private Long flashSaleItemId;
    private Integer flashSaleQuantity;
}
