package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.AnalyticsDashboardDto;
import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.entity.Analytics;
import com.example.ecommerce.enums.AnalyticsType;
import com.example.ecommerce.mapper.AnalyticsMapper;
import com.example.ecommerce.repository.AnalyticsRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    public AnalyticsDto createAnalytics(AnalyticsDto dto) {
        Analytics analytics = analyticsMapper.toEntity(dto);
        return analyticsMapper.toDto(analyticsRepository.save(analytics));
    }

    @Override
    public List<AnalyticsDto> getAllAnalytics() {
        return analyticsRepository.findAll().stream()
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsDto> getAnalyticsByType(AnalyticsType type) {
        return analyticsRepository.findByType(type).stream()
                .map(analyticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void refreshAnalytics() {
        analyticsRepository.deleteAll();

        // Sales (Total Orders)
        long totalOrders = orderRepository.count();
        analyticsRepository.save(Analytics.builder()
                .type(AnalyticsType.SALES)
                .metricName("Total Orders")
                .value((double) totalOrders)
                .description("Total number of orders placed")
                .build());

        // Revenue
        Double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        analyticsRepository.save(Analytics.builder()
                .type(AnalyticsType.REVENUE)
                .metricName("Total Revenue")
                .value(totalRevenue)
                .description("Total revenue generated")
                .build());

        // Active Users
        long totalUsers = userRepository.count();
        analyticsRepository.save(Analytics.builder()
                .type(AnalyticsType.USER)
                .metricName("Total Users")
                .value((double) totalUsers)
                .description("Registered users in the system")
                .build());

        // Products
        long totalProducts = productRepository.count();
        analyticsRepository.save(Analytics.builder()
                .type(AnalyticsType.PRODUCT)
                .metricName("Total Products")
                .value((double) totalProducts)
                .description("Available products in catalog")
                .build());
    }

    @Override
    public AnalyticsDashboardDto getDashboardAnalytics() {
        long totalOrders = orderRepository.count();
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();

        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        return AnalyticsDashboardDto.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .averageOrderValue(avgOrderValue)
                .build();
    }

}
