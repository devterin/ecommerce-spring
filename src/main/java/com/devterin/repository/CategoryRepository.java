package com.devterin.repository;

import com.devterin.entity.Category;
import com.devterin.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
