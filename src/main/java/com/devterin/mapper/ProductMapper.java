package com.devterin.mapper;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory().getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .price(product.getVariants().stream()
                        .map(variant -> variant.getPrice())
                        .filter(price -> price != null)
                        .min(Integer::compareTo)
                        .orElse(0))
                .sold(product.getVariants().stream()
                        .map(variant -> variant.getSoldQuantity())
                        .filter(quantity -> quantity != null)
                        .mapToInt(Integer::intValue)
                        .sum())
                .variantId(product.getVariants().stream()
                        .findFirst()
                        .map(variant -> variant.getId())
                        .orElse(null))
                .attributes(product.getVariants().stream()
                        .flatMap(variant -> variant.getAttributes().stream())
                        .map(attribute -> attribute.getValue())
                        .distinct()
                        .toList())
                .build();
    }

    public ProductImageDTO toDto(ProductImage productImage) {
        return ProductImageDTO.builder()
                .variantId(productImage.getVariant().getId())
                .imageId(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}
