package com.devterin.service.impl;

import com.devterin.dtos.request.ProductRequest;
import com.devterin.dtos.response.ProductResponse;
import com.devterin.entity.Category;
import com.devterin.entity.Feedback;
import com.devterin.entity.Product;
import com.devterin.exception.AppException;
import com.devterin.mapper.ProductMapper;
import com.devterin.repository.CategoryRepository;
import com.devterin.repository.ProductImageRepository;
import com.devterin.repository.FeedbackRepository;
import com.devterin.repository.ProductRepository;
import com.devterin.service.CloudinaryService;
import com.devterin.service.ProductService;
import com.devterin.util.AppConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.text.Normalizer;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final FeedbackRepository feedbackRepository;


    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = productRepository.findAll(pageable).getContent();

        return products.stream().map(this::enrich).toList();

    }


    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::enrich).toList();
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

        return enrich(productRepository.save(product));
    }

    @Override
    public Product getProductObjById(Long productId) {

        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not exist"));
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = getProductObjById(productId);


        return enrich(ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .category(product.getCategory().getName())
                .price(product.getVariants().stream()
                        .map(variant -> variant.getPrice())
                        .filter(price -> price != null)
                        .min(Integer::compareTo)
                        .orElse(0))
                .sold(product.getVariants().stream()
                        .map(variant -> variant.getSoldQuantity())
                        .filter(quantity -> quantity != null)
                        .mapToInt(Integer::intValue)
                        .sum())
                .variantId(product.getVariants().stream()
                        .findFirst()
                        .map(variant -> variant.getId())
                        .orElse(null))
                .attributes(product.getVariants().stream()
                        .flatMap(variant -> variant.getAttributes().stream())
                        .map(attribute -> attribute.getValue())
                        .distinct()
                        .toList())
                .build());
    }


    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request, MultipartFile thumbnail) {
        Product product = getProductObjById(productId);

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Categories not exist"));
        if (productRepository.existsByNameAndIdNot(request.getName(), productId)) {
            throw new RuntimeException("Product already exists");
        }
        String thumbnailUrl = cloudinaryService.uploadImage(thumbnail, AppConstants.THUMBNAIL);
        product.setName(request.getName());
        product.setThumbnail(thumbnailUrl);
        product.setDescription(request.getDescription());
        product.setCategory(category);

        return enrich(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductObjById(productId);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductPage(int pageNumber, int pageSize, String query) {
        if (pageNumber < 0) throw new IllegalArgumentException("pageNumber must be zero or greater");
        int safeSize = Math.min(Math.max(pageSize, 1), 100);
        Pageable pageable = PageRequest.of(pageNumber, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> products = query == null || query.isBlank()
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query.trim(), query.trim(), pageable);
        return products.map(product -> enrich(productMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        String expected = slugify(slug);
        Product product = productRepository.findAll().stream()
                .filter(candidate -> slugify(candidate.getName()).equals(expected))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return enrich(productMapper.toDto(product));
    }

    private ProductResponse enrich(Product product) {
        return enrich(productMapper.toDto(product));
    }

    private ProductResponse enrich(ProductResponse response) {
        List<Feedback> reviews = feedbackRepository.findAllProductFeedback(response.getId());
        response.setAverageRating(reviews.isEmpty() ? 0d : reviews.stream().mapToInt(Feedback::getRating).average().orElse(0d));
        response.setReviewCount((long) reviews.size());
        response.setSlug(slugify(response.getName()));
        return response;
    }

    private String slugify(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

}
