package com.example.ecommerce.repository;

import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all purchased product IDs by user
    @Query("SELECT oi.product.id FROM OrderItem oi WHERE oi.order.user.id = :userId")
    List<Long> findPurchasedProductIdsByUser(@Param("userId") Long userId);

    @Query("""
       SELECT p.category.id, SUM(oi.quantity) 
       FROM OrderItem oi 
       JOIN oi.product p 
       JOIN oi.order o 
       WHERE o.user.id = :userId
       GROUP BY p.category.id
       """)
    List<Object[]> findCategoryCountsByUser(@Param("userId") Long userId);


    // Find similar products (collaborative filtering placeholder)
    @Query("""
       SELECT DISTINCT oi2.product.id 
       FROM OrderItem oi1 
       JOIN oi1.order o1 
       JOIN OrderItem oi2 ON oi1.order.id = oi2.order.id
       WHERE o1.user.id <> :userId
         AND oi1.product.id IN :purchasedProductIds
         AND oi2.product.id NOT IN :purchasedProductIds
       """)
    List<Long> findSimilarProducts(@Param("userId") Long userId,
                                   @Param("purchasedProductIds") List<Long> purchasedProductIds);


    // NEW: Find top 5 most bought products by quantity
    @Query("SELECT p FROM Product p " +
            "WHERE p.id IN (" +
            "    SELECT oi.product.id FROM OrderItem oi " +
            "    GROUP BY oi.product.id " +
            "    ORDER BY SUM(oi.quantity) DESC " +
            "    LIMIT 5" +
            ")")
    List<Product> findTop5ProductsByQuantity();

    // NEW: Find top 8 most bought products by quantity
    @Query("SELECT p FROM Product p " +
            "WHERE p.id IN (" +
            "    SELECT oi.product.id FROM OrderItem oi " +
            "    GROUP BY oi.product.id " +
            "    ORDER BY SUM(oi.quantity) DESC " +
            "    LIMIT 5" +
            ")")
    List<Product> findTop8ProductsByQuantity();

    // NEW: Find order items by order ID
    List<OrderItem> findByOrderId(Long orderId);

    // NEW: Find order items by product ID
    List<OrderItem> findByProductId(Long productId);
}
