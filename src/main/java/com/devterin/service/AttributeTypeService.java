package com.devterin.service;

import com.devterin.dtos.dto.AttributeTypeDTO;

import java.util.List;

public interface AttributeTypeService {
    AttributeTypeDTO addAttributeType(String value, String description);
    List<AttributeTypeDTO> getAllAttributeTypes();
    AttributeTypeDTO getAttributeTypeById(Long attributeTypeId);
    AttributeTypeDTO updateAttributeType(Long attributeTypeId, String value, String description);
    void deleteAttributeType(Long attributeTypeId);
}
