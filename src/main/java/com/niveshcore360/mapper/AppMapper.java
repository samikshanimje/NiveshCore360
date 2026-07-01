package com.niveshcore360.mapper;

import com.niveshcore360.dto.GoalDTO;
import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.entity.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility mapper class for converting between database Entities and DTO models.
 */
public class AppMapper {

    /**
     * Map User entity to UserDTO.
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        String roleStr = user.getRoles() == null || user.getRoles().isEmpty() ? "ROLE_USER" : user.getRoles().iterator().next().getName();
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(roleStr)
                .build();
    }

    /**
     * Map UserDTO to User entity.
     */
    public static User toUser(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(dto.getPassword())
                .build();
    }

    /**
     * Map Investment entity to InvestmentDTO, calculating value, profit/loss, and percentages.
     */
    public static InvestmentDTO toInvestmentDTO(Investment inv) {
        if (inv == null) return null;
        BigDecimal currentPrice = BigDecimal.ZERO;
        String symbol = "";
        String name = "";
        Long assetId = null;

        if (inv.getAsset() != null) {
            currentPrice = inv.getAsset().getCurrentPrice();
            symbol = inv.getAsset().getSymbol();
            name = inv.getAsset().getName();
            assetId = inv.getAsset().getId();
        }

        BigDecimal qty = inv.getQuantity();
        BigDecimal buyPrice = inv.getPurchasePrice();
        BigDecimal costBasis = qty.multiply(buyPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal currentValue = qty.multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profitLoss = currentValue.subtract(costBasis).setScale(2, RoundingMode.HALF_UP);

        BigDecimal profitLossPercentage = BigDecimal.ZERO;
        if (costBasis.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercentage = profitLoss.multiply(BigDecimal.valueOf(100))
                    .divide(costBasis, 2, RoundingMode.HALF_UP);
        }

        return InvestmentDTO.builder()
                .id(inv.getId())
                .portfolioId(inv.getPortfolio() != null ? inv.getPortfolio().getId() : null)
                .assetType(inv.getAssetType())
                .assetId(assetId)
                .symbol(symbol)
                .name(name)
                .quantity(qty)
                .purchasePrice(buyPrice)
                .purchaseDate(inv.getPurchaseDate())
                .currentPrice(currentPrice)
                .costBasis(costBasis)
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .profitLossPercentage(profitLossPercentage)
                .build();
    }

    /**
     * Map Goal entity to GoalDTO, computing progress percentage and time limits.
     */
    public static GoalDTO toGoalDTO(Goal goal) {
        if (goal == null) return null;
        
        double progress = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = goal.getCurrentAmount().multiply(BigDecimal.valueOf(100.0))
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP).doubleValue();
            if (progress > 100.0) progress = 100.0;
        }

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate());
        if (daysRemaining < 0) daysRemaining = 0;

        return GoalDTO.builder()
                .id(goal.getId())
                .portfolioId(goal.getPortfolio() != null ? goal.getPortfolio().getId() : null)
                .portfolioName(goal.getPortfolio() != null ? goal.getPortfolio().getName() : "")
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus())
                .progressPercentage(progress)
                .daysRemaining(daysRemaining)
                .build();
    }
}
