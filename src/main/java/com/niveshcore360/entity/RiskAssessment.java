package com.niveshcore360.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity storing user risk metrics and calculations.
 */
@Entity
@Table(name = "risk_assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "risk_score", nullable = false)
    private int riskScore; // 1-100 scale

    @Column(name = "risk_appetite", nullable = false, length = 30)
    private String riskAppetite; // e.g., "Conservative", "Moderate", "Aggressive"

    @Column(name = "investment_horizon", nullable = false)
    private int investmentHorizon; // in years

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
