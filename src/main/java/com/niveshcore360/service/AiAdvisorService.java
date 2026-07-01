package com.niveshcore360.service;

import com.niveshcore360.entity.ChatHistory;
import java.util.List;

/**
 * Service interface for OpenAI-powered advisory and report generation.
 */
public interface AiAdvisorService {
    String askAdvisor(Long userId, String prompt);
    List<ChatHistory> getChatHistory(Long userId);
    void clearChatHistory(Long userId);
    byte[] generateFinancialReport(Long userId);
}
