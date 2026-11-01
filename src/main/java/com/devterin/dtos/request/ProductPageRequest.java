package com.devterin.dtos.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductPageRequest {
    private int pageNumber = 0;
    private int pageSize = 12;
    private String q;
    private String category;
    private Integer minPrice;
    private Integer maxPrice;
    private List<String> size;
    private List<String> color;
    private String sort = "id_desc";
}
