package com.devterin.service;

import com.devterin.dtos.dto.AttributeDTO;

import java.util.List;

public interface AttributeService {
    AttributeDTO addAttributeValue(String value, Long attributeTypeId);
    List<AttributeDTO> findAttributeByTypeId(Long attributeTypeId);
    AttributeDTO updateAttributeValue(String value, Long attributeId);
    void deleteAttributeValue(Long attributeId);
}
