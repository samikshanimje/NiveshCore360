package com.niveshcore360.service;

import com.niveshcore360.dto.GoalDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for savings and investment Goal configuration.
 */
public interface GoalService {
    GoalDTO createGoal(GoalDTO dto);
    GoalDTO updateGoal(Long goalId, GoalDTO dto);
    void deleteGoal(Long goalId);
    List<GoalDTO> getGoalsByPortfolioId(Long portfolioId);
    void updateGoalProgress(Long goalId, BigDecimal newCurrentAmount);
}
