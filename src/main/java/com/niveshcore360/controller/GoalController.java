package com.niveshcore360.controller;

import com.niveshcore360.dto.GoalDTO;
import com.niveshcore360.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class coordinating financial goals actions.
 */
@Controller
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    public GoalDTO createGoal(Long portfolioId, String name, BigDecimal target, BigDecimal current, LocalDate targetDate) {
        GoalDTO dto = GoalDTO.builder()
                .portfolioId(portfolioId)
                .name(name)
                .targetAmount(target)
                .currentAmount(current)
                .targetDate(targetDate)
                .build();
        return goalService.createGoal(dto);
    }

    public GoalDTO updateGoal(Long goalId, String name, BigDecimal target, BigDecimal current, LocalDate targetDate) {
        GoalDTO dto = GoalDTO.builder()
                .name(name)
                .targetAmount(target)
                .currentAmount(current)
                .targetDate(targetDate)
                .build();
        return goalService.updateGoal(goalId, dto);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDTO> getGoals(Long portfolioId) {
        return goalService.getGoalsByPortfolioId(portfolioId);
    }
}
