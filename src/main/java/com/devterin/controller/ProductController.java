package com.devterin.controller;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @Value("${upload.image.path}")
    private String PATH;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Product created")
                .result(productService.createProduct(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAllProducts())
                .build();
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long productId,
                                                      @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Product updated")
                .result(productService.updateProduct(productId, request))
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ApiResponse.<Void>builder()
                .message("Product deleted")
                .build();
    }

    @PostMapping("/upload/{productId}")
    public ApiResponse<ProductImageDTO> uploadImage(@PathVariable Long productId,
                                                    @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productService.getProductById(productId);
        String image = storeFile(file);

        ProductImageDTO savedImage = productService.createProductImage(product.getId(),
                ProductImage.builder()
                        .imageUrl(image)
                        .build());

        return ApiResponse.<ProductImageDTO>builder()
                .message("Image uploaded successfully")
                .result(savedImage)
                .build();
    }


    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file)) {
            throw new IOException("Invalid image file.");
        }

        final long MAX_FILE_SIZE = 5 * 1024 * 1024; //5MB
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File too large! Maximum allowed size is 5MB.");
        }

        Path folder = Paths.get(PATH);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String newFileName = UUID.randomUUID() + "." + fileName;

//        Path pathFileUpload = folder.resolve(newFileName);
//        Files.copy(file.getInputStream(), pathFileUpload, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.startsWith("image/");
    }


}
