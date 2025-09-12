package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category already exists with name: " + dto.getName());
        }
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return  categoryRepository.findAll().stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    public Page<CategoryDto> searchCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllCategories(pageable);
        }
        return categoryRepository.findByNameOrDescription(keyword, pageable)
                .map(categoryMapper::toDto);
    }


    @Override
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        return categoryMapper.toDto(categoryRepository.save(existing));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
