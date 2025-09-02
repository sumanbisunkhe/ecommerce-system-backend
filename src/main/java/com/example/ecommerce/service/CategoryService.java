package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    CategoryDto getCategoryById(Long id);
    Page<CategoryDto> getAllCategories(Pageable pageable);
    Page<CategoryDto> searchCategories(String keyword, Pageable pageable);
    CategoryDto updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);
}
