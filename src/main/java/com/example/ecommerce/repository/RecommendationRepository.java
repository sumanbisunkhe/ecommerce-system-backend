package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Recommendation;
import com.example.ecommerce.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUser(User user, Pageable pageable);

    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId ORDER BY r.score DESC")
    List<Recommendation> findTopRecommendationsByUser(Long userId, Pageable pageable);
}
