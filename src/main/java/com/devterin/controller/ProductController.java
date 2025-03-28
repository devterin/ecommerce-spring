package com.devterin.controller;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.service.ProductImageService;
import com.devterin.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;
    private final ProductImageService productImageService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder().message("Product created").result(productService.createProduct(request)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {

        List<ProductResponse> products = productService.getAllProducts(pageNumber, pageSize);

        if (products.isEmpty()) {
            throw new RuntimeException("Product not found.");
        }

        return ApiResponse.<List<ProductResponse>>builder().result(productService.getAllProducts(pageNumber, pageSize)).build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductsById(@PathVariable Long productId) {
        return ApiResponse.<ProductResponse>builder().result(productService.getProductById(productId)).build();
    }

    @GetMapping("/productImage/{productId}")
    public ApiResponse<List<ProductImageDTO>> getProductImageByProductId(@PathVariable Long productId) {

        return ApiResponse.<List<ProductImageDTO>>builder().result(productImageService.getProductImageById(productId)).build();
    }


    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder().message("Product updated").result(productService.updateProduct(productId, request)).build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {

        productService.deleteProduct(productId);

        return ApiResponse.<Void>builder().message("Product deleted").build();
    }

    @PostMapping("/upload/{productId}")
    public ApiResponse<ProductImageDTO> uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws IOException {

        Product product = productService.getProductObjById(productId);
        ProductImageDTO savedImage = productImageService.createProductImage(product.getId(), file);

        return ApiResponse.<ProductImageDTO>builder().message("Image uploaded successfully").result(savedImage).build();
    }

    @PutMapping("/update/{productId}/{imageId}")
    public ApiResponse<ProductImageDTO> updateImage(@PathVariable Long productId, @PathVariable Long imageId, @RequestParam("file") MultipartFile file) throws IOException {

        ProductImageDTO updatedImage = productImageService.updateProductImage(productId, imageId, file);

        return ApiResponse.<ProductImageDTO>builder().message("Image updated successfully").result(updatedImage).build();
    }


}
