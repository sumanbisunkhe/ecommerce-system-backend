package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    Optional<Analytics> findByAnalyticsDate(LocalDate date);

    @Query("SELECT a FROM Analytics a WHERE a.analyticsDate BETWEEN :startDate AND :endDate ORDER BY a.analyticsDate DESC")
    List<Analytics> findByDateRange(LocalDate startDate, LocalDate endDate);

    boolean existsByAnalyticsDate(LocalDate date);
}