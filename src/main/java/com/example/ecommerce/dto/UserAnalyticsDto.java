package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnalyticsDto {

    private Boolean isActive;
    private LocalDateTime joinedDate;


    // Order summary
    private Integer totalOrders;
    private Integer pendingOrders;
    private Integer deliveredOrders;
    private Integer cancelledOrders;

    // Financial summary
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    private BigDecimal lastOrderAmount;
    private LocalDateTime lastOrderDate;

    // Current cart status
    private Integer cartItemsCount;
    private BigDecimal cartTotalValue;

    // Payment summary
    private Integer totalPayments;
    private Integer successfulPayments;
    private Integer failedPayments;

    // Favorite categories (top 3)
    private List<String> favoriteCategories;

    // Activity metrics
    private String loyaltyTier;
    private Integer ordersLast30Days;
}