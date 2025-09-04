package com.example.ecommerce.service;

import com.example.ecommerce.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {

    List<RecommendationDto> generateHybridRecommendations(Long userId);

    RecommendationDto getRecommendationById(Long id);

    List<RecommendationDto> getAllRecommendations(int page, int size);

    void invalidateCache(Long userId);
}
