package com.devterin.service.impl;

import com.devterin.dtos.dto.AttributeValueDTO;
import com.devterin.entity.AttributeType;
import com.devterin.entity.AttributeValue;
import com.devterin.mapper.AttributeMapper;
import com.devterin.repository.AttributeTypeRepository;
import com.devterin.repository.AttributeValueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeValueServiceImpl {
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeMapper attributeMapper;

    public AttributeValueDTO addAttributeValue(String value, Long attributeTypeId) {
        if (attributeValueRepository.existsByValue(value)) {
            throw new RuntimeException("Attribute Value: " + value + " existed");
        }
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId).orElseThrow(
                () -> new EntityNotFoundException("AttributeType not found with id: " + attributeTypeId));

        AttributeValue attributeValue = AttributeValue.builder()
                .value(value)
                .attributeType(attributeType)
                .build();

        attributeValue = attributeValueRepository.save(attributeValue);

        return attributeMapper.toDto(attributeValue);
    }


    public List<AttributeValueDTO> findAttributeByTypeId(Long attributeTypeId) {
        var attributeValues = attributeValueRepository.findByAttributeTypeId(attributeTypeId);
        return attributeValues.stream().map(attributeMapper::toDto).toList();
    }

    public AttributeValueDTO updateAttributeValue(String value, Long attributeId) {
        if (attributeValueRepository.existsByValue(value)) {
            throw new RuntimeException("Attribute Value: " + value + " existed");
        }
        var attribute = findAttributeValue(attributeId);


        attribute.setValue(value);
        attribute = attributeValueRepository.save(attribute);

        return attributeMapper.toDto(attribute);
    }

    public void deleteAttributeValue(Long attributeId) {
        var attribute = findAttributeValue(attributeId);

        attributeValueRepository.delete(attribute);
    }

    private AttributeValue findAttributeValue(Long attributeId) {
        return attributeValueRepository.findById(attributeId).orElseThrow(
                () -> new EntityNotFoundException("AttributeType not found with id: " + attributeId));
    }

}
