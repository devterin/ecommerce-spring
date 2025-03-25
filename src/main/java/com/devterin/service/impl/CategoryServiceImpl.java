package com.devterin.service.impl;

import com.devterin.dtos.dto.CategoryDTO;
import com.devterin.entity.Category;
import com.devterin.repository.CategoryRepository;
import com.devterin.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDTO createCategories(Category category) {
        Category savedCategory = categoryRepository.findByName(category.getName());

        if (savedCategory != null) {
            throw new RuntimeException("Category: " + category.getName() + " existed");
        }

        savedCategory = categoryRepository.save(category);

        return CategoryDTO.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build()).toList();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).
                orElseThrow(() -> new EntityNotFoundException("Categories not exist"));
    }

    public CategoryDTO updateCategory(Long categoryId, Category request) {
        Category category = getCategoryById(categoryId);

        category.setName(request.getName());

        category = categoryRepository.save(category);

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);

        categoryRepository.delete(category);
    }
}
