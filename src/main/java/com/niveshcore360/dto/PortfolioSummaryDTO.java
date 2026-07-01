package com.niveshcore360.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object summarizing portfolio values and allocations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryDTO {
    private Long portfolioId;
    private String portfolioName;
    private BigDecimal totalInvestment;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitLossPercentage;

    // Asset allocation maps
    private BigDecimal stockWeight;
    private BigDecimal mutualFundWeight;
    private Map<String, BigDecimal> categoryAllocation; // symbol -> allocation value
}
