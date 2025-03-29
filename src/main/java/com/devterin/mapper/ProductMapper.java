package com.devterin.mapper;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {
    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory().getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .image(product.getImages() != null ? product.getImages().size() : 0)
                .build();
    }

    public ProductImageDTO toDto(ProductImage productImage) {
        return ProductImageDTO.builder()
                .productId(productImage.getProduct().getId())
                .imageId(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}
