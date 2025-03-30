package com.devterin.service.impl;

import com.devterin.dtos.request.VariantRequest;
import com.devterin.dtos.response.VariantResponse;
import com.devterin.entity.Attribute;
import com.devterin.entity.Product;
import com.devterin.entity.Variant;
import com.devterin.mapper.AttributeMapper;
import com.devterin.repository.AttributeRepository;
import com.devterin.repository.ProductRepository;
import com.devterin.repository.VariantRepository;
import com.devterin.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public List<VariantResponse> getAllVariant() {
        List<Variant> list = variantRepository.findAll();

        return list.stream().map(attributeMapper::toDto).toList();
    }

    @Override
    public VariantResponse getVariantById(Long variantId) {
        Variant variant = variantRepository.findById(variantId).orElseThrow(
                () -> new RuntimeException("Variant not found"));

        return attributeMapper.toDto(variant);
    }

    @Override
    public VariantResponse createVariant(Long productId, VariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Long> attributeIds = request.getAttributeIds();
        if (attributeIds.isEmpty()) {
            throw new IllegalArgumentException("Attributes cannot be null or empty");
        }
        // valid attribute and check only value attribute type
        List<Attribute> attributes = validateAndGetAttributes(request.getAttributeIds());

        String variantName = attributes.stream()
                .map(Attribute::getValue)
                .collect(Collectors.joining(" - ", product.getName() + " - ", ""));

        boolean variantExists = variantRepository.existsByNameAndProductId(variantName, productId);
        if (variantExists) {
            throw new IllegalArgumentException("Variant with name '" + variantName + "' already exists");
        }
        Variant savedVariant = Variant.builder()
                .name(variantName)
                .price(request.getPrice())
                .stockQuantity(request.getQuantity())
                .product(product)
                .attributes(attributes)
                .build();

        return attributeMapper.toDto(variantRepository.save(savedVariant));
    }

    @Override
    public VariantResponse updateVariant(Long variantId, VariantRequest request) {
        Variant variant = variantRepository.findById(variantId).orElseThrow(
                () -> new RuntimeException("Variant not found"));
        variant.setPrice(request.getPrice());
        variant.setStockQuantity(request.getQuantity());
        // valid attribute and check only value attribute type
        List<Attribute> attributes = validateAndGetAttributes(request.getAttributeIds());
        variant.setAttributes(attributes);
        // update new name
        String newName = attributes.stream()
                .map(Attribute::getValue)
                .collect(Collectors
                        .joining(" - ", variant.getProduct().getName() + " - ", ""));
        variant.setName(newName);
        //check name is duplicate
        boolean variantExists = variantRepository.existsByNameAndProductId(newName, variant.getProduct().getId());
        if (variantExists) {
            throw new IllegalArgumentException("Variant with name '" + newName + "' already exists");
        }
        Variant updatedVariant = variantRepository.save(variant);

        return attributeMapper.toDto(updatedVariant);

    }

    private List<Attribute> validateAndGetAttributes(List<Long> attributeIds) {
        List<Attribute> attributes = attributeRepository.findAllById(attributeIds);
        if (attributes.size() != attributeIds.size()) {
            throw new IllegalArgumentException("One or more attribute ID invalid.");
        }
        // group by attribute type
        Map<Long, List<Attribute>> attributesByType = attributes.stream()
                .collect(Collectors.groupingBy(attr -> attr.getAttributeType().getId()));
        // check each type has only one value
        for (List<Attribute> attrList : attributesByType.values()) {
            if (attrList.size() > 1) {
                throw new IllegalArgumentException("Only one value is allowed per attribute type.");
            }
        }
        return attributes;
    }

    @Override
    public void deleteVariant(Long variantId) {
        Variant variant = variantRepository.findById(variantId).orElseThrow(
                () -> new RuntimeException("Variant not found"));

        variantRepository.delete(variant);
    }


}
