package com.devterin.service;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.request.ProductPageRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.dtos.response.PageResponse;
import com.devterin.dtos.response.ProductFilterOptionsResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, MultipartFile file);
    List<ProductResponse> getAllProducts();
    PageResponse<ProductResponse> getAllProducts(int pageNumber, int pageSize);
    ProductResponse updateProduct(Long productId, ProductRequest request,MultipartFile file);
    void deleteProduct(Long productId);
    ProductResponse getProductById(Long productId);
    Product getProductObjById(Long productId);
    Page<ProductResponse> getProductPage(ProductPageRequest request);
    ProductFilterOptionsResponse getProductFilterOptions();
    ProductResponse getProductBySlug(String slug);

}
