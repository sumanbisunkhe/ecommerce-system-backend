package com.example.ecommerce.entity;

import com.example.ecommerce.enums.RecommendationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Recommended product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Type of recommendation (CONTENT, COLLABORATIVE, HYBRID)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType type;

    // Target user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double score; // recommendation score
}
