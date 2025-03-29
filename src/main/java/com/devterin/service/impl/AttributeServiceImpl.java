package com.devterin.service.impl;

import com.devterin.dtos.dto.AttributeDTO;
import com.devterin.entity.AttributeType;
import com.devterin.entity.Attribute;
import com.devterin.mapper.AttributeMapper;
import com.devterin.repository.AttributeTypeRepository;
import com.devterin.repository.AttributeRepository;
import com.devterin.service.AttributeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public AttributeDTO addAttributeValue(String value, Long attributeTypeId) {
        if (attributeRepository.existsByValue(value)) {
            throw new RuntimeException("Attribute Value: " + value + " existed");
        }
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId).orElseThrow(
                () -> new EntityNotFoundException("AttributeType not found with id: " + attributeTypeId));

        Attribute attribute = Attribute.builder()
                .value(value)
                .attributeType(attributeType)
                .build();

        attribute = attributeRepository.save(attribute);

        return attributeMapper.toDto(attribute);
    }

    @Override
    public List<AttributeDTO> findAttributeByTypeId(Long attributeTypeId) {
        var attributeValues = attributeRepository.findByAttributeTypeId(attributeTypeId);
        return attributeValues.stream().map(attributeMapper::toDto).toList();
    }

    @Override
    public AttributeDTO updateAttributeValue(String value, Long attributeId) {
        if (attributeRepository.existsByValue(value)) {
            throw new RuntimeException("Attribute Value: " + value + " existed");
        }
        var attribute = findAttributeValue(attributeId);


        attribute.setValue(value);
        attribute = attributeRepository.save(attribute);

        return attributeMapper.toDto(attribute);
    }

    @Override
    public void deleteAttributeValue(Long attributeId) {
        var attribute = findAttributeValue(attributeId);

        attributeRepository.delete(attribute);
    }

    private Attribute findAttributeValue(Long attributeId) {
        return attributeRepository.findById(attributeId).orElseThrow(
                () -> new EntityNotFoundException("AttributeType not found with id: " + attributeId));
    }

}
