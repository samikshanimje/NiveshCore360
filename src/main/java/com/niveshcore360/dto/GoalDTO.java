package com.niveshcore360.dto;

import com.niveshcore360.entity.GoalStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for savings goals and status tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDTO {
    private Long id;
    private Long portfolioId;
    private String portfolioName;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private GoalStatus status;

    // Evaluated fields
    private double progressPercentage;
    private long daysRemaining;
}
