package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.RecommendationDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Recommendation;
import com.example.ecommerce.mapper.RecommendationMapper;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.RecommendationRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper mapper;

    @Override
    @Cacheable(value = "userRecommendations", key = "#userId")
    public List<RecommendationDto> generateHybridRecommendations(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        List<Long> purchasedProductIds = orderItemRepository.findPurchasedProductIdsByUser(userId);

        Map<Long, Long> categoryCounts = orderItemRepository.findCategoryCountsByUser(userId)
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        List<Long> categoryIdList = new ArrayList<>(categoryCounts.keySet());
        Pageable topFive = PageRequest.of(0, 5);

        // Content-based filtering
        List<Product> contentBased = productRepository.findTopByCategoryIds(categoryIdList, topFive)
                .stream()
                .filter(p -> !purchasedProductIds.contains(p.getId()))
                .toList();

        // Collaborative filtering
        List<Long> collaborativeProductIds = orderItemRepository.findSimilarProducts(userId, purchasedProductIds);

        List<Product> collaborative = productRepository.findAllById(collaborativeProductIds)
                .stream()
                .filter(p -> !purchasedProductIds.contains(p.getId()))
                .collect(Collectors.toList());

        // Hybrid recommendations
        Set<Product> hybridSet = new LinkedHashSet<>();
        hybridSet.addAll(contentBased);
        hybridSet.addAll(collaborative);

        // ✅ Fallback: if no recommendations found
        if (hybridSet.isEmpty()) {
            List<Product> fallback = productRepository.findAll(PageRequest.of(0, 5))
                    .stream()
                    .filter(p -> !purchasedProductIds.contains(p.getId()))
                    .toList();
            hybridSet.addAll(fallback);
        }

        // Save & return
        List<Recommendation> hybridEntities = hybridSet.stream()
                .map(product -> Recommendation.builder()
                        .product(product)
                        .user(user)
                        .score(Math.random())
                        .type(product.getId() % 2 == 0
                                ? com.example.ecommerce.enums.RecommendationType.CONTENT_BASED
                                : com.example.ecommerce.enums.RecommendationType.COLLABORATIVE)
                        .build())
                .collect(Collectors.toList());

        recommendationRepository.saveAll(hybridEntities);

        System.out.println("Purchased Products: " + purchasedProductIds);
        System.out.println("Category Counts: " + categoryCounts);
        System.out.println("Content-based size: " + contentBased.size());
        System.out.println("Collaborative size: " + collaborative.size());



        return hybridEntities.stream().map(mapper::toDto).collect(Collectors.toList());
    }


    @Override
    public RecommendationDto getRecommendationById(Long id) {
        return recommendationRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));
    }

    @Override
    public List<RecommendationDto> getAllRecommendations(int page, int size) {
        return recommendationRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "userRecommendations", key = "#userId")
    public void invalidateCache(Long userId) {
        // clears cache after significant user activity
    }
}
