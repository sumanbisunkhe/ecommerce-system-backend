package com.example.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsDashboardDto {

    // Orders & Sales
    private long totalOrders;

    // Revenue
    private double totalRevenue;

    // Users
    private long totalUsers;

    // Products
    private long totalProducts;

    // Avg order value (optional KPI)
    private double averageOrderValue;
}
