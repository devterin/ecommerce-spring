package com.devterin.mapper;

import com.devterin.dtos.dto.AttributeDTO;
import com.devterin.dtos.dto.AttributeTypeDTO;
import com.devterin.dtos.response.VariantResponse;
import com.devterin.entity.Attribute;
import com.devterin.entity.AttributeType;
import com.devterin.entity.Variant;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {
    public AttributeTypeDTO toDto(AttributeType attributeType) {
        return AttributeTypeDTO.builder()
                .attributeTypeId(attributeType.getId())
                .name(attributeType.getName())
                .description(attributeType.getDescription())
                .build();
    }

    public AttributeDTO toDto(Attribute attribute) {
        return AttributeDTO.builder()
                .attributeId(attribute.getId())
                .name(attribute.getValue())
                .attributeType(attribute.getAttributeType().getName())
                .build();
    }

    public Attribute toEntity(AttributeDTO dto) {
        return Attribute.builder()
                .id(dto.getAttributeId())
                .value(dto.getName())
                .build();
    }

    public VariantResponse toDto(Variant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .price(variant.getPrice())
                .quantity(variant.getStockQuantity())
                .attribute(variant.getAttributes().stream().map(Attribute::getValue).toList())
                .image(variant.getImages() != null ? variant.getImages().size() : 0)
                .build();
    }
}
