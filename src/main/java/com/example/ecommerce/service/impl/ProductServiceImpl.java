package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.FileUploadService;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileUploadService;

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto dto, MultipartFile image) throws IOException {
        // Map DTO to entity
        Product product = productMapper.toEntity(dto);

        // Ensure ID is null so DB auto-generates it
        product.setId(null);
        product.setUpdatedAt(LocalDateTime.now());

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
            String uniquePublicId = "products/" + UUID.randomUUID();
            Map<String, String> uploadResult = fileUploadService.uploadImage(image, uniquePublicId);
            product.setImageUrl(uploadResult.get("secure_url"));
        }

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
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


    @Transactional
    @Override
    public ProductDto updateProduct(Long id, ProductDto dto, MultipartFile image) throws IOException {
        // Fetch existing product
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update fields only if provided
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getStockQuantity() != null) existing.setStockQuantity(dto.getStockQuantity());
        if (dto.getActive() != null) existing.setActive(dto.getActive());

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            String uniquePublicId = "products/" + UUID.randomUUID();
            Map<String, String> uploadResult = fileUploadService.uploadImage(image, uniquePublicId);
            existing.setImageUrl(uploadResult.get("secure_url"));
        }
        // Only update imageUrl from DTO if explicitly provided
        else if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            existing.setImageUrl(dto.getImageUrl());
        }

        // Update category if provided
        if (dto.getCategoryId() != null) {
            existing.setCategory(productMapper.mapCategory(dto.getCategoryId()));
        }

        // Update timestamp
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

    @Override
    public List<ProductDto> getRelatedProducts(Long productId, int limit) {
        // Get the product to ensure it exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // If product has no category, return empty list
        if (product.getCategory() == null) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, limit);
        Page<Product> relatedProducts = productRepository.findRelatedProductsByProductId(productId, pageable);

        return relatedProducts.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}
