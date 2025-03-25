package com.devterin.service;

import com.devterin.dtos.dto.CategoryDTO;
import com.devterin.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategories(Category request);
    List<CategoryDTO> getAllCategories();
    Category getCategoryById(Long categoryId);
    CategoryDTO updateCategory(Long categoryId, Category category);
    void deleteCategory(Long categoryId);
}
