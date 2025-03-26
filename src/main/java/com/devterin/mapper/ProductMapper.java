package com.devterin.mapper;

import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {
    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory().getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .image(product.getImages().size())
                .build();
    }
}
