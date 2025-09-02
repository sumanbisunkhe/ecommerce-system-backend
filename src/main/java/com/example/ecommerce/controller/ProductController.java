package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ResponseEntity<ApiResponse<ProductDto>> createProductWithImage(
            @RequestPart("product") ProductDto dto,
            @RequestPart("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(
                ApiResponse.success("Product created successfully",
                        productService.createProduct(dto, image))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully", productService.getProductById(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam("search") Optional<String> search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) Boolean active
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ProductDto> products = productService.searchProducts(
                search.orElse(null),
                minPrice,
                maxPrice,
                minStock,
                maxStock,
                active,
                categoryId,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", products)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDto dto,
            @RequestPart("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully",
                        productService.updateProduct(id, dto, image))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
