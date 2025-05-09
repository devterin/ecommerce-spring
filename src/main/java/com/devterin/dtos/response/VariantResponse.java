package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VariantResponse {
    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
    private List<String> attribute;
    private Integer image;

}
