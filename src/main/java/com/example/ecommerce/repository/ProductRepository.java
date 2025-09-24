package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameOrDescription(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:minStock IS NULL OR p.stockQuantity >= :minStock) " +
            "AND (:maxStock IS NULL OR p.stockQuantity <= :maxStock) " +
            "AND (:active IS NULL OR p.active = :active) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("active") Boolean active,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    List<Product> findTopByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    // NEW: Find top 5 newest products
    List<Product> findTop5ByOrderByCreatedAtDesc();

    List<Product> findTop8ByOrderByCreatedAtDesc();

    // Add this method to the repository
    @Query("SELECT p FROM Product p WHERE p.category.id = (SELECT p2.category.id FROM Product p2 WHERE p2.id = :productId) AND p.id != :productId")
    Page<Product> findRelatedProductsByProductId(@Param("productId") Long productId, Pageable pageable);

    // NEW: Find active products
    List<Product> findByActiveTrue();

    // NEW: Find inactive products
    List<Product> findByActiveFalse();
}
