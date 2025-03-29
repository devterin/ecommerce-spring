package com.devterin.service.impl;

import com.devterin.dtos.dto.AttributeTypeDTO;
import com.devterin.entity.AttributeType;
import com.devterin.mapper.AttributeMapper;
import com.devterin.repository.AttributeTypeRepository;
import com.devterin.repository.AttributeRepository;
import com.devterin.service.AttributeTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttributeTypeServiceImpl implements AttributeTypeService {

    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public AttributeTypeDTO addAttributeType(String value, String description) {
        if (attributeTypeRepository.existsByName(value)) {
            throw new RuntimeException("Attribute Type: " + value + " existed");
        }
        if (value.length() > 50) {
            throw new RuntimeException("Name length must be less than 50 characters");
        }
        if (description.length() > 200) {
            throw new RuntimeException("Description length should be less than 200 characters");
        }
        AttributeType attributeType = AttributeType.builder()
                .name(value)
                .description(description)
                .build();
        attributeType = attributeTypeRepository.save(attributeType);

        return attributeMapper.toDto(attributeType);
    }

    @Override
    public List<AttributeTypeDTO> getAllAttributeTypes() {
        var list = attributeTypeRepository.findAll();
        return list.stream()
                .map(attributeMapper::toDto)
                .toList();
    }

    @Override
    public AttributeTypeDTO getAttributeTypeById(Long attributeTypeId) {
        var attributeType = findAttributeTypeById(attributeTypeId);

        return attributeMapper.toDto(attributeType);
    }

    @Override
    public AttributeTypeDTO updateAttributeType(Long attributeTypeId, String value, String description) {
        if (attributeTypeRepository.existsByName(value)) {
            throw new RuntimeException("Attribute Type: " + value + " existed");
        }

        var attributeType = findAttributeTypeById(attributeTypeId);

        if (Objects.nonNull(value) && !value.isEmpty()) {
            attributeType.setName(value);
        }
        if (Objects.nonNull(description) && !description.isEmpty()) {
            attributeType.setDescription(description);
        }
        attributeTypeRepository.save(attributeType);

        return attributeMapper.toDto(attributeType);
    }

    @Override
    public void deleteAttributeType(Long attributeTypeId) {
        var attributeType = findAttributeTypeById(attributeTypeId);

        attributeTypeRepository.delete(attributeType);
    }

    private AttributeType findAttributeTypeById(Long id) {
        return attributeTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("AttributeType not found with id: " + id));
    }
}
