package com.devterin.service.impl;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.repository.ProductImageRepository;
import com.devterin.service.ProductImageService;
import com.devterin.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductService productService;

    @Value("${upload.image.path}")
    private String PATH;


    @Override
    public ProductImageDTO createProductImage(Long productId, MultipartFile file)
            throws IOException {
        Product product = productService.getProductObjById(productId);
        // no more than 5 image per product
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParameterException("Number of images has reached limit "
                    + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        String image = storeFile(file);

        ProductImage productImg = ProductImage.builder()
                .imageUrl(image)
                .product(product)
                .build();

        ProductImage savedImage = productImageRepository.save(productImg);

        return ProductImageDTO.builder()
                .productId(savedImage.getProduct().getId())
                .imageId(savedImage.getId())
                .imageUrl(savedImage.getImageUrl())
                .build();
    }

    @Override
    public ProductImageDTO updateProductImage(Long productId, Long imageId, MultipartFile file)
            throws IOException {

        productService.getProductObjById(productId);

        ProductImage existingImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

//        delete image old
//        deleteFile(existingImage.getImageUrl());

        String image = storeFile(file);
        existingImage.setImageUrl(image);

        ProductImage updatedImage = productImageRepository.save(existingImage);

        return ProductImageDTO.builder()
                .productId(updatedImage.getProduct().getId())
                .imageId(updatedImage.getId())
                .imageUrl(updatedImage.getImageUrl())
                .build();
    }

    @Override
    public List<ProductImageDTO> getProductImageById(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        if (productImages.isEmpty()) {
            throw new RuntimeException("Product not exist");
        }

        return productImages.stream().map(image -> ProductImageDTO.builder()
                .productId(productId)
                .imageId(image.getId())
                .imageUrl(image.getImageUrl())
                .build()).toList();
    }


    private void deleteFile(String imageUrl) {
        Path imagePath = Paths.get(PATH).resolve(imageUrl);
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to delete old image: " + imageUrl, exception);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
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
        String newFileName = UUID.randomUUID() + "-" + fileName;

//        Path pathFileUpload = folder.resolve(newFileName);
//        Files.copy(file.getInputStream(), pathFileUpload, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.startsWith("image/");
    }

}
