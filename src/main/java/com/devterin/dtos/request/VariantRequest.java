package com.devterin.dtos.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class VariantRequest {
    private String name;
    private Integer price;
    private Integer quantity;
    private List<Long> attributeIds;
}
