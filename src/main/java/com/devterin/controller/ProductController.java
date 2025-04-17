package com.devterin.controller;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.entity.Variant;
import com.devterin.service.ProductImageService;
import com.devterin.service.ProductService;
import com.devterin.service.VariantService;
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
    private final VariantService variantService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestPart("request") @Valid ProductRequest request,
                                                      @RequestPart("thumbnail") MultipartFile thumbnail) {
        return ApiResponse.<ProductResponse>builder().message("Product created").result(productService.createProduct(request, thumbnail)).build();
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(@RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {

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
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long productId, @RequestPart ProductRequest request, @RequestPart("thumbnail") MultipartFile thumbnail) {
        return ApiResponse.<ProductResponse>builder().message("Product updated").result(productService.updateProduct(productId, request, thumbnail)).build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {

        productService.deleteProduct(productId);

        return ApiResponse.<Void>builder().message("Product deleted").build();
    }

    @PostMapping("/upload/{variantId}")
    public ApiResponse<List<ProductImageDTO>> uploadImage(@PathVariable Long variantId, @RequestParam("file") List<MultipartFile> file) {

        Variant variant = variantService.getVariantObjById(variantId);
        List<ProductImageDTO> savedImage = productImageService.createProductImage(variant.getId(), file);

        return ApiResponse.<List<ProductImageDTO>>builder().message("Image uploaded successfully").result(savedImage).build();
    }

    @PutMapping("/update/{variantId}/{imageId}")
    public ApiResponse<ProductImageDTO> updateImage(@PathVariable Long variantId, @PathVariable Long imageId, @RequestParam("file") MultipartFile file) {

        ProductImageDTO updatedImage = productImageService.updateProductImage(variantId, imageId, file);

        return ApiResponse.<ProductImageDTO>builder().message("Image updated successfully").result(updatedImage).build();
    }


}
