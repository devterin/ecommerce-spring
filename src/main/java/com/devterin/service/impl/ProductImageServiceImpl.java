package com.devterin.service.impl;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.mapper.ProductMapper;
import com.devterin.repository.ProductImageRepository;
import com.devterin.service.CloudinaryService;
import com.devterin.service.ProductImageService;
import com.devterin.service.ProductService;
import com.devterin.ultil.AppConstants;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;


    @Override
    public List<ProductImageDTO> createProductImage(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductObjById(productId);
        // no more than 5 image per product
        int imageCount = productImageRepository.findByProductId(productId).size();
        int newImageCount = files.size();
        int totalImages = imageCount + newImageCount;


        if (totalImages > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParameterException("Number of images has reached limit "
                    + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        List<ProductImage> productImages = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String images = cloudinaryService.uploadImage(multipartFile, AppConstants.PRODUCT_IMAGE);
            ProductImage productImg = ProductImage.builder()
                    .imageUrl(images)
                    .product(product)
                    .build();
            productImages.add(productImg);
        }

        List<ProductImage> savedImages = productImageRepository.saveAll(productImages);

        return savedImages.stream().map(productMapper::toDto).toList();
    }

    @Override
    public ProductImageDTO updateProductImage(Long productId, Long imageId, MultipartFile file) {

        productService.getProductObjById(productId);

        ProductImage existingImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        String newImageUrl = cloudinaryService.uploadImage(file, AppConstants.PRODUCT_IMAGE);
        existingImage.setImageUrl(newImageUrl);

        ProductImage updatedImage = productImageRepository.save(existingImage);

        return productMapper.toDto(updatedImage);
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


}
