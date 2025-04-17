package com.devterin.service.impl;

import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Category;
import com.devterin.entity.Product;
import com.devterin.exception.AppException;
import com.devterin.mapper.ProductMapper;
import com.devterin.repository.CategoryRepository;
import com.devterin.repository.ProductImageRepository;
import com.devterin.repository.ProductRepository;
import com.devterin.service.CloudinaryService;
import com.devterin.service.ProductService;
import com.devterin.ultil.AppConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;


    public List<ProductResponse> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = productRepository.findAll(pageable).getContent();

        return products.stream().map(productMapper::toDto).toList();

    }


    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(productMapper::toDto).toList();
    }


    @Override
    public ProductResponse createProduct(ProductRequest request, MultipartFile thumbnail) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Categories not exist"));

        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product already exists");
        }
        String thumbnailUrl = cloudinaryService.uploadImage(thumbnail, AppConstants.THUMBNAIL);
        Product product = Product.builder()
                .name(request.getName())
                .thumbnail(thumbnailUrl)
                .description(request.getDescription())
                .category(category)
                .build();

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public Product getProductObjById(Long productId) {

        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not exist"));
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = getProductObjById(productId);


        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .category(product.getCategory().getName())
                .build();
    }


    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request, MultipartFile thumbnail) {
        Product product = getProductObjById(productId);

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Categories not exist"));
        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product already exists");
        }
        String thumbnailUrl = cloudinaryService.uploadImage(thumbnail, AppConstants.THUMBNAIL);
        product.setName(request.getName());
        product.setThumbnail(thumbnailUrl);
        product.setDescription(request.getDescription());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductObjById(productId);
        productRepository.delete(product);
    }

}
