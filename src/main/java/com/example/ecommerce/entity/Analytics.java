package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analytics_date", unique = true, nullable = false)
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

    // Category analytics
    private Integer totalCategories;

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

    @Column(precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(precision = 15, scale = 2)
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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}