package com.example.ecommerce.service;

import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.UserAnalyticsDto;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {

    AnalyticsDto getSystemAnalytics();

    AnalyticsDto getProductAnalytics();


    AnalyticsDto getAnalyticsByDate(LocalDate date);

    List<AnalyticsDto> getAnalyticsByDateRange(LocalDate startDate, LocalDate endDate);

    AnalyticsDto generateDailyAnalytics();

    void saveDailyAnalytics();

    UserAnalyticsDto getUserAnalytics(Long userId);
}