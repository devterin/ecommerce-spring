package com.devterin.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private String category;
    private Integer price;
    private Integer sold;
    private Long variantId;
    private List<String> attributes;
    private Double averageRating;
    private Long reviewCount;
    private String slug;
}
