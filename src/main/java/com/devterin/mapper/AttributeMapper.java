package com.devterin.mapper;

import com.devterin.dtos.dto.AttributeTypeDTO;
import com.devterin.dtos.dto.AttributeValueDTO;
import com.devterin.entity.AttributeType;
import com.devterin.entity.AttributeValue;
import org.springframework.stereotype.Service;

@Service
public class AttributeMapper {
    public AttributeTypeDTO toDto(AttributeType attributeType) {
        return AttributeTypeDTO.builder()
                .attributeTypeId(attributeType.getId())
                .name(attributeType.getName())
                .description(attributeType.getDescription())
                .build();
    }

    public AttributeValueDTO toDto(AttributeValue attributeValue) {
        return AttributeValueDTO.builder()
                .attributeId(attributeValue.getId())
                .name(attributeValue.getValue())
                .attributeType(attributeValue.getAttributeType().getName())
                .build();
    }

    public AttributeValue toEntity(AttributeValueDTO dto) {
        return AttributeValue.builder()
                .id(dto.getAttributeId())
                .value(dto.getName())
                .build();
    }
}
