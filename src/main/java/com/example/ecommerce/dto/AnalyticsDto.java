package com.example.ecommerce.dto;

import com.example.ecommerce.enums.AnalyticsType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsDto {
    private Long id;
    private AnalyticsType type;
    private String metricName;
    private Double value;
    private String description;
}
