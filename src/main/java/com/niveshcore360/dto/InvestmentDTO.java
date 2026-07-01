package com.niveshcore360.dto;

import com.niveshcore360.entity.AssetType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object containing full investment details and calculated valuation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentDTO {
    private Long id;
    private Long portfolioId;
    private AssetType assetType;
    
    // Asset reference details
    private Long assetId;      // Stock or Mutual Fund ID
    private String symbol;      // Ticker or Fund name
    private String name;        // Company name or Fund name
    
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;

    // Evaluated fields
    private BigDecimal currentPrice;
    private BigDecimal costBasis;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPercentage;
}
