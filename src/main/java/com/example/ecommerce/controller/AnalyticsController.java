package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AnalyticsDashboardDto;
import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.enums.AnalyticsType;
import com.example.ecommerce.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnalyticsDto>> createAnalytics(@RequestBody AnalyticsDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Analytics created", analyticsService.createAnalytics(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnalyticsDto>>> getAllAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("All analytics fetched", analyticsService.getAllAnalytics()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<AnalyticsDto>>> getAnalyticsByType(@PathVariable AnalyticsType type) {
        return ResponseEntity.ok(ApiResponse.success("Analytics by type fetched", analyticsService.getAnalyticsByType(type)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshAnalytics() {
        analyticsService.refreshAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Analytics refreshed", "All metrics recalculated successfully"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDto>> getDashboardAnalytics() {
        return ResponseEntity.ok(
                ApiResponse.success("Dashboard analytics fetched", analyticsService.getDashboardAnalytics())
        );
    }

}
