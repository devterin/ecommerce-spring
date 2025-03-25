package com.devterin.service;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long productId, ProductRequest request);
    void deleteProduct(Long productId);
    Product getProductById(Long productId);
    ProductImageDTO createProductImage(Long productId, ProductImage productImage);
}
