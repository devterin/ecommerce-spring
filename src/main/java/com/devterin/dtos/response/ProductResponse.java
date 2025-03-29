package com.devterin.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer image;
}
