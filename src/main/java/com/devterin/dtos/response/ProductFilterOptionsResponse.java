package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductFilterOptionsResponse {
    List<String> sizes;
    List<String> colors;
}
