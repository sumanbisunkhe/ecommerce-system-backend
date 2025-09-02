package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

public interface ProductService {
    ProductDto createProduct(ProductDto dto, MultipartFile image) throws IOException;

    ProductDto updateProduct(Long id, ProductDto dto, MultipartFile image) throws IOException;

    ProductDto getProductById(Long id);

    Page<ProductDto> getAllProducts(Pageable pageable);

    Page<ProductDto> searchProducts(String keyword,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice,
                                    Integer minStock,
                                    Integer maxStock,
                                    Boolean active,
                                    Long categoryId,
                                    Pageable pageable);

    void deleteProduct(Long id);
}
