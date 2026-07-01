package com.niveshcore360.service.impl;

import com.niveshcore360.dto.GoalDTO;
import com.niveshcore360.entity.*;
import com.niveshcore360.exception.ResourceNotFoundException;
import com.niveshcore360.mapper.AppMapper;
import com.niveshcore360.repository.GoalRepository;
import com.niveshcore360.repository.NotificationRepository;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.service.AuditLogService;
import com.niveshcore360.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing user investment goals and triggering milestone alerts.
 */
@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final PortfolioRepository portfolioRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public GoalServiceImpl(GoalRepository goalRepository,
                           PortfolioRepository portfolioRepository,
                           NotificationRepository notificationRepository,
                           AuditLogService auditLogService) {
        this.goalRepository = goalRepository;
        this.portfolioRepository = portfolioRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public GoalDTO createGoal(GoalDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(dto.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + dto.getPortfolioId()));

        GoalStatus status = GoalStatus.ACTIVE;
        if (dto.getCurrentAmount().compareTo(dto.getTargetAmount()) >= 0) {
            status = GoalStatus.COMPLETED;
        }

        Goal goal = Goal.builder()
                .portfolio(portfolio)
                .name(dto.getName())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(dto.getCurrentAmount())
                .targetDate(dto.getTargetDate())
                .status(status)
                .build();

        Goal saved = goalRepository.save(goal);

        if (status == GoalStatus.COMPLETED) {
            triggerCompletionNotification(portfolio.getUser(), saved);
        }

        auditLogService.log("CREATE_GOAL", "Created savings goal: " + dto.getName());

        return AppMapper.toGoalDTO(saved);
    }

    @Override
    public GoalDTO updateGoal(Long goalId, GoalDTO dto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setTargetDate(dto.getTargetDate());

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            if (goal.getStatus() != GoalStatus.COMPLETED) {
                goal.setStatus(GoalStatus.COMPLETED);
                triggerCompletionNotification(goal.getPortfolio().getUser(), goal);
            }
        } else {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        Goal saved = goalRepository.save(goal);
        auditLogService.log("UPDATE_GOAL", "Updated savings goal: " + goal.getName());
        return AppMapper.toGoalDTO(saved);
    }

    @Override
    public void deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
        goalRepository.delete(goal);
        auditLogService.log("DELETE_GOAL", "Deleted savings goal: " + goal.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByPortfolioId(Long portfolioId) {
        return goalRepository.findByPortfolioId(portfolioId)
                .stream()
                .map(AppMapper::toGoalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateGoalProgress(Long goalId, BigDecimal newCurrentAmount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        goal.setCurrentAmount(newCurrentAmount);
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            if (goal.getStatus() != GoalStatus.COMPLETED) {
                goal.setStatus(GoalStatus.COMPLETED);
                triggerCompletionNotification(goal.getPortfolio().getUser(), goal);
            }
        } else {
            goal.setStatus(GoalStatus.ACTIVE);
        }

        goalRepository.save(goal);
    }

    private void triggerCompletionNotification(User user, Goal goal) {
        Notification notification = Notification.builder()
                .user(user)
                .message("Congratulations! You have achieved your financial milestone: '" + goal.getName() + "'!")
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public String getGoalForecastMessage(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            return "Milestone achieved! You have successfully funded this goal.";
        }

        java.time.LocalDate now = java.time.LocalDate.now();
        if (goal.getTargetDate().isBefore(now)) {
            return "Goal target date has passed. Focus on re-aligning your target timelines.";
        }

        long months = java.time.temporal.ChronoUnit.MONTHS.between(now, goal.getTargetDate());
        if (months <= 0) {
            months = 1;
        }

        BigDecimal gap = goal.getTargetAmount().subtract(goal.getCurrentAmount());
        if (gap.compareTo(BigDecimal.ZERO) <= 0) {
            return "Your current funds cover this goal. Mark it completed!";
        }

        // Project current funds growth at a conservative 8% annual return (0.66% monthly)
        double rate = 0.08 / 12.0;
        double growthFactor = Math.pow(1.0 + rate, months);
        BigDecimal projectedCurrentValue = goal.getCurrentAmount().multiply(BigDecimal.valueOf(growthFactor));
        
        BigDecimal remainingGap = goal.getTargetAmount().subtract(projectedCurrentValue);
        if (remainingGap.compareTo(BigDecimal.ZERO) <= 0) {
            return String.format("On Track! Compounded growth at 8%% projection will grow your holdings to exceed the target amount in %d months.", months);
        }

        // Calculate the monthly contribution needed to bridge the remaining gap
        double monthlyRate = 0.08 / 12.0;
        double futureValueFactor = ((Math.pow(1.0 + monthlyRate, months) - 1.0) / monthlyRate) * (1.0 + monthlyRate);
        double monthlyContribution = remainingGap.doubleValue() / futureValueFactor;

        return String.format(
            "Underfunded. To hit your target in %d months, you need to set up a monthly SIP of ₹%,.2f (assuming 8%% returns). Current Gap: ₹%,.2f",
            months, monthlyContribution, remainingGap
        );
    }
}
