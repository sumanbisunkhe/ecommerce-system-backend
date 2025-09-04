package com.example.ecommerce.entity;

import com.example.ecommerce.enums.AnalyticsType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AnalyticsType type;

    @Column(nullable = false)
    private String metricName; // e.g., "Total Orders", "Active Users"

    @Column(nullable = false)
    private Double value; // numeric value of the metric

    @Column(length = 255)
    private String description; // optional explanation
}
