package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.RecommendationDto;
import com.example.ecommerce.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> getUserRecommendations(@PathVariable Long userId) {
        List<RecommendationDto> recommendations = recommendationService.generateHybridRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success("User recommendations fetched", recommendations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecommendationDto>> getRecommendationById(@PathVariable Long id) {
        RecommendationDto recommendation = recommendationService.getRecommendationById(id);
        return ResponseEntity.ok(ApiResponse.success("Recommendation fetched", recommendation));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> getAllRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<RecommendationDto> recommendations = recommendationService.getAllRecommendations(page, size);
        return ResponseEntity.ok(ApiResponse.success("All recommendations fetched", recommendations));
    }
}
