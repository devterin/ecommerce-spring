package com.devterin.controller;

import com.devterin.dtos.dto.CategoryDTO;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.entity.Category;
import com.devterin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories() {

        return ApiResponse.<List<CategoryDTO>>builder()
                .result(categoryService.getAllCategories()).build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<Category> getCategoryById(@PathVariable Long categoryId) {

        return ApiResponse.<Category>builder()
                .result(categoryService.getCategoryById(categoryId)).build();

    }

    @PostMapping
    public ApiResponse<CategoryDTO> createCategory(@RequestBody Category request) {

        return ApiResponse.<CategoryDTO>builder()
                .message("Category created")
                .result(categoryService.createCategories(request)).build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long categoryId,
                                                   @RequestBody Category request) {

        return ApiResponse.<CategoryDTO>builder()
                .message("Category updated")
                .result(categoryService.updateCategory(categoryId, request)).build();
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Category> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ApiResponse.<Category>builder()
                .message("Category deleted").build();
    }
}
