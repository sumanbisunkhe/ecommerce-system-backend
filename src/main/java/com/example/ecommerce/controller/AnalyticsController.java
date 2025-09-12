package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/system")
    public ResponseEntity<ApiResponse<AnalyticsDto>> getSystemAnalytics() {
        AnalyticsDto analytics = analyticsService.getSystemAnalytics();
        return ResponseEntity.ok(ApiResponse.success("System analytics retrieved successfully", analytics));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<AnalyticsDto>> getAnalyticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AnalyticsDto analytics = analyticsService.getAnalyticsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<AnalyticsDto>>> getAnalyticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AnalyticsDto> analytics = analyticsService.getAnalyticsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }

    @PostMapping("/generate-daily")
    public ResponseEntity<ApiResponse<AnalyticsDto>> generateDailyAnalytics() {
        AnalyticsDto analytics = analyticsService.generateDailyAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Daily analytics generated successfully", analytics));
    }
}