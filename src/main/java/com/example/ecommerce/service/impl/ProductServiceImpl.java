package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.FileUploadService;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileUploadService;

    @Override
    public ProductDto createProduct(ProductDto dto, MultipartFile image) throws IOException {
        Product product = productMapper.toEntity(dto);
        product.setUpdatedAt(LocalDateTime.now());
        
        if (image != null && !image.isEmpty()) {
            Map<String, String> uploadResult = fileUploadService.uploadImage(image, "products");
            product.setImageUrl(uploadResult.get("secure_url"));
        }
        
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Override
    public Page<ProductDto> searchProducts(String keyword,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           Integer minStock,
                                           Integer maxStock,
                                           Boolean active,
                                           Long categoryId,
                                           Pageable pageable) {

        if ((keyword == null || keyword.isEmpty())
                && minPrice == null
                && maxPrice == null
                && minStock == null
                && maxStock == null
                && active == null
                && categoryId == null) {
            return getAllProducts(pageable); // fallback to all products
        }

        return productRepository.searchProducts(
                keyword,
                minPrice,
                maxPrice,
                minStock,
                maxStock,
                active,
                categoryId,
                pageable
        ).map(productMapper::toDto);
    }


    @Override
    public ProductDto updateProduct(Long id, ProductDto dto, MultipartFile image) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStockQuantity(dto.getStockQuantity());
        existing.setActive(dto.getActive());
        
        if (image != null && !image.isEmpty()) {
            Map<String, String> uploadResult = fileUploadService.uploadImage(image, "products");
            existing.setImageUrl(uploadResult.get("secure_url"));
        } else {
            existing.setImageUrl(dto.getImageUrl());
        }
        
        if (dto.getCategoryId() != null) {
            existing.setCategory(productMapper.mapCategory(dto.getCategoryId()));
        }
        existing.setUpdatedAt(LocalDateTime.now());

        return productMapper.toDto(productRepository.save(existing));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
