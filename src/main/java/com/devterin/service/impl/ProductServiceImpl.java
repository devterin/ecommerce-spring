package com.devterin.service.impl;

import com.devterin.dtos.dto.ProductImageDTO;
import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Category;
import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import com.devterin.mapper.ProductMapper;
import com.devterin.repository.CategoryRepository;
import com.devterin.repository.ProductImageRepository;
import com.devterin.repository.ProductRepository;
import com.devterin.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(productMapper::toDto).toList();
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Categories not exist"));

        Product product = Product.builder()
                .name(request.getName())
                .thumbnail(request.getThumbnail())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .build();

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public Product getProductById(Long productId) {

        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not exist"));
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = getProductById(productId);

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Categories not exist"));

        product.setName(request.getName());
        product.setThumbnail(request.getThumbnail());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public ProductImageDTO createProductImage(Long productId, ProductImage productImage) {
        Product product = getProductById(productId);

        // no more than 5 image per product
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParameterException("Number of images has reached limit "
                    + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        ProductImage image = ProductImage.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .product(product)
                .build();
        ProductImage savedImage = productImageRepository.save(image);

        return ProductImageDTO.builder()
                .productId(savedImage.getProduct().getId())
                .imageId(savedImage.getId())
                .imageUrl(savedImage.getImageUrl())
                .build();
    }


}
