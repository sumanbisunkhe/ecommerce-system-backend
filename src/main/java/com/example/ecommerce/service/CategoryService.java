package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories();

    Page<CategoryDto> getAllCategories(Pageable pageable);

    Page<CategoryDto> searchCategories(String keyword, Pageable pageable);

    CategoryDto updateCategory(Long id, CategoryDto dto);

    void deleteCategory(Long id);
}
