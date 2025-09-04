package com.example.ecommerce.dto;

import com.example.ecommerce.enums.RecommendationType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    private Long id;
    private Long productId;
    private String productName;
    private RecommendationType type;
    private Long userId;
    private Double score;
}
