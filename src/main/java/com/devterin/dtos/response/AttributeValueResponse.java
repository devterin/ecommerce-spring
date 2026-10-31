package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeValueResponse {
    private String type;
    private String value;
}
