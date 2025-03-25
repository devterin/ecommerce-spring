package com.devterin.dtos.dto;

import com.devterin.entity.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
public class ProductImageDTO {
    private Long productId;
    private Long imageId;
    private String imageUrl;
}
