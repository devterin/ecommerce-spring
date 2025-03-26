package com.devterin.service;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {

    List<ProductImageDTO> getProductImageById(Long productId);
    ProductImageDTO createProductImage(Long productId, MultipartFile file) throws IOException;
    ProductImageDTO updateProductImage(Long productId, Long imageId, MultipartFile file) throws IOException;
    String storeFile(MultipartFile file) throws IOException;


}
