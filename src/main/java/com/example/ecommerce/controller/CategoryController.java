package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CategoryDto;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success("Category created successfully", categoryService.createCategory(dto))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Category fetched successfully", categoryService.getCategoryById(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategories(
            @RequestParam("search") Optional<String> search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<CategoryDto> categories;
        String query = search.orElse(null);

        categories = categoryService.searchCategories(query, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Categories retrieved successfully", categories)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully", categoryService.updateCategory(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
