package com.example.ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AnalyticsDto {
    private Long id;
    private LocalDate analyticsDate;

    // User analytics
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer inactiveUsers;
    private Integer maleUsers;
    private Integer femaleUsers;
    private Integer otherGenderUsers;
    private Integer usersFromTenToTwenty;
    private Integer usersFromTwentyToThirty;
    private Integer usersAboveThirty;

    // Product analytics
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer inactiveProducts;
    private List<ProductDto> newProducts;
    private List<ProductDto> popularProducts;

    // Category analytics
    private Integer totalCategories;
    private List<CategoryDto> newCategories;

    // Cart analytics
    private Integer totalCarts;

    // Order analytics
    private Integer totalOrders;
    private Integer pendingOrders;
    private Integer confirmedOrders;
    private Integer shippedOrders;
    private Integer deliveredOrders;
    private Integer cancelledOrders;

    // Payment analytics
    private Integer totalPayments;
    private BigDecimal totalRevenue;
    private BigDecimal totalRevenueLastMonth;
    private Integer paymentViaKhalti;
    private Integer paymentViaCashOnDelivery;
    private Integer paymentViaCreditCard;
    private Integer paymentViaDebitCard;
    private Integer paymentViaEsewa;
    private Integer paymentViaBankTransfer;
    private Integer pendingPayment;
    private Integer completedPayment;
    private Integer failedPayment;
    private Integer refundedPayment;

    private LocalDate createdAt;
}