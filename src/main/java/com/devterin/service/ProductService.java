package com.devterin.service;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long productId, ProductRequest request);
    void deleteProduct(Long productId);
    ProductResponse getProductById(Long productId);
    Product getProductObjById(Long productId);

}
