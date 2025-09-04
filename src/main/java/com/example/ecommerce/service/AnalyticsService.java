package com.example.ecommerce.service;

import com.example.ecommerce.dto.AnalyticsDashboardDto;
import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.enums.AnalyticsType;

import java.util.List;

public interface AnalyticsService {
    AnalyticsDto createAnalytics(AnalyticsDto dto);
    List<AnalyticsDto> getAllAnalytics();
    List<AnalyticsDto> getAnalyticsByType(AnalyticsType type);
    void refreshAnalytics(); // auto-calculate metrics from orders, users, products
    AnalyticsDashboardDto getDashboardAnalytics();

}
