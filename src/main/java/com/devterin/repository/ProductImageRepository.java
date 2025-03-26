package com.devterin.repository;

import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
    int countByProductId(Long productId);
}
