package com.devterin.dtos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeValueDTO {
    private String attributeType;
    private Long attributeId;
    private String name;

}
