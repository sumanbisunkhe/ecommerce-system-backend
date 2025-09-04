package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Analytics;
import com.example.ecommerce.enums.AnalyticsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    List<Analytics> findByType(AnalyticsType type);
}
