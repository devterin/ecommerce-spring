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
