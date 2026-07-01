package com.niveshcore360.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a Mutual Fund asset.
 */
@Entity
@Table(name = "mutual_funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fund_name", unique = true, nullable = false, length = 100)
    private String fundName;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal nav;

    @Column(name = "risk_rating", length = 20)
    private String riskRating; // e.g. "Low", "Medium", "High"

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
