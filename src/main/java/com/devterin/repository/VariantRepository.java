package com.devterin.repository;

import com.devterin.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    boolean existsByNameAndProductId(String variantName, Long productId);
}
