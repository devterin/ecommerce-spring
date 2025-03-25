package com.devterin.repository;

import com.devterin.entity.Product;
import com.devterin.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
