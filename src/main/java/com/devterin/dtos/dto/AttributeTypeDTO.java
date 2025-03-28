package com.devterin.dtos.dto;

import com.devterin.entity.AttributeValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeTypeDTO  {
    private Long attributeTypeId;
    private String name;
    private String description;
}
