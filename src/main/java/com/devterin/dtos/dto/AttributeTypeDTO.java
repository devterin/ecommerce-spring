package com.devterin.dtos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeTypeDTO  {
    private Long attributeTypeId;
    private String name;
    private String description;
}
