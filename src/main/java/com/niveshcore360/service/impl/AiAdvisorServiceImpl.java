package com.niveshcore360.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.niveshcore360.entity.ChatHistory;
import com.niveshcore360.entity.RiskAssessment;
import com.niveshcore360.entity.User;
import com.niveshcore360.repository.ChatHistoryRepository;
import com.niveshcore360.repository.RiskAssessmentRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.AiAdvisorService;
import com.niveshcore360.exception.ResourceNotFoundException;
import com.niveshcore360.util.PDFGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for AI-powered client financial advice.
 */
@Service
@Slf4j
public class AiAdvisorServiceImpl implements AiAdvisorService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String askAdvisor(Long userId, String prompt) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Save User prompt
        saveChatRecord(user, "USER", prompt);

        // Fetch User financial profile context
        Optional<RiskAssessment> assessmentOpt = riskAssessmentRepository.findByUserId(userId);
        String context = compileContext(assessmentOpt);

        String aiResponse;
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("your-key")) {
            aiResponse = callOpenAiApi(prompt, context, userId);
        } else {
            aiResponse = generateSimulatedResponse(prompt, assessmentOpt);
        }

        // Save AI reply
        saveChatRecord(user, "ASSISTANT", aiResponse);

        return aiResponse;
    }

    @Override
    public List<ChatHistory> getChatHistory(Long userId) {
        return chatHistoryRepository.findByUserIdOrderByTimestampAsc(userId);
    }

    @Override
    public void clearChatHistory(Long userId) {
        List<ChatHistory> history = chatHistoryRepository.findByUserIdOrderByTimestampAsc(userId);
        chatHistoryRepository.deleteAll(history);
    }

    @Override
    public byte[] generateFinancialReport(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<ChatHistory> history = getChatHistory(userId);
        Optional<RiskAssessment> assessmentOpt = riskAssessmentRepository.findByUserId(userId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDFGeneratorUtil.generateFinancialReportPDF(user, assessmentOpt, history, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate AI financial report PDF", e);
            throw new RuntimeException("Failed to compile AI PDF statement", e);
        }
    }

    private void saveChatRecord(User user, String sender, String message) {
        ChatHistory record = ChatHistory.builder()
            .user(user)
            .sender(sender)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
        chatHistoryRepository.save(record);
    }

    private String compileContext(Optional<RiskAssessment> assessmentOpt) {
        if (assessmentOpt.isEmpty()) {
            return "No risk profile completed. User has standard parameters.";
        }
        RiskAssessment ra = assessmentOpt.get();
        return String.format(
            "User Profile: Risk Score is %d/100 (%s appetite), Investment horizon is %d years.",
            ra.getRiskScore(), ra.getRiskAppetite(), ra.getInvestmentHorizon()
        );
    }

    private String callOpenAiApi(String prompt, String context, Long userId) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Fetch chat history for context (last 5 messages)
            List<ChatHistory> recentHistory = chatHistoryRepository.findByUserIdOrderByTimestampAsc(userId);
            int startIdx = Math.max(0, recentHistory.size() - 6);

            ObjectNode requestJson = objectMapper.createObjectNode();
            requestJson.put("model", "gpt-4o-mini");

            ArrayNode messagesArray = objectMapper.createArrayNode();
            
            // System context
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a professional wealth advisor. " + context + " Provide concise, mathematical asset allocation percentages (Stocks, Bonds, Gold) and actionable SIP growth paths. Use standard Markdown styling.");
            messagesArray.add(systemMessage);

            // Append context history
            for (int i = startIdx; i < recentHistory.size() - 1; i++) {
                ChatHistory hist = recentHistory.get(i);
                ObjectNode msg = objectMapper.createObjectNode();
                msg.put("role", hist.getSender().equals("USER") ? "user" : "assistant");
                msg.put("content", hist.getMessage());
                messagesArray.add(msg);
            }

            // Current prompt
            ObjectNode userMessage = objectMapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messagesArray.add(userMessage);

            requestJson.set("messages", messagesArray);
            String requestBody = objectMapper.writeValueAsString(requestJson);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").path(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            log.error("Failed contacting OpenAI API service", e);
        }
        return "System error contacting AI service. Fallback simulator active.";
    }

    private String generateSimulatedResponse(String prompt, Optional<RiskAssessment> assessmentOpt) {
        String promptLower = prompt.toLowerCase();
        String appetite = assessmentOpt.map(RiskAssessment::getRiskAppetite).orElse("Moderate");
        int score = assessmentOpt.map(RiskAssessment::getRiskScore).orElse(50);
        int horizon = assessmentOpt.map(RiskAssessment::getInvestmentHorizon).orElse(10);

        if (promptLower.contains("strategy") || promptLower.contains("recommend") || promptLower.contains("portfolio")) {
            if ("aggressive".equalsIgnoreCase(appetite) || score > 70) {
                return "### 🚀 AI Advisory: Aggressive Wealth Accumulation Strategy\n\n" +
                    "Based on your risk tolerance of **" + score + "/100 (Aggressive)**, here is your customized model:\n\n" +
                    "#### 📊 Asset Allocation Mix:\n" +
                    "- **Domestic Equities (Large/Midcap Stocks)**: 60%\n" +
                    "- **Global/International ETFs**: 15%\n" +
                    "- **Sectoral Mutual Funds**: 10%\n" +
                    "- **Bonds / Fixed Income Securities**: 10%\n" +
                    "- **Digital Assets / Crypto / Gold**: 5%\n\n" +
                    "#### 💡 Recommendations:\n" +
                    "1. Set up monthly SIPs in diversified index funds (e.g., Nifty 50 or S&P 500 equivalent ETFs).\n" +
                    "2. Liquidate short-term low-yield deposits to capitalize on high-growth equities.\n" +
                    "3. Monitor covariance weekly. Rebalance assets if equity allocation shifts past 75% due to price gains.";
            } else if ("conservative".equalsIgnoreCase(appetite) || score < 35) {
                return "### 🛡️ AI Advisory: Capital Preservation Strategy\n\n" +
                    "Based on your risk profile score of **" + score + "/100 (Conservative)**, here is your tailored allocation:\n\n" +
                    "#### 📊 Asset Allocation Mix:\n" +
                    "- **Government Bonds & Debt Mutual Funds**: 60%\n" +
                    "- **Fixed Deposits / Liquid Funds**: 20%\n" +
                    "- **Blue-Chip Large Cap Equities**: 15%\n" +
                    "- **Sovereign Gold Bonds (SGBs)**: 5%\n\n" +
                    "#### 💡 Recommendations:\n" +
                    "1. Avoid mid-and-small-cap high-beta equities to limit drawdowns.\n" +
                    "2. Allocate emergency targets to high-yield sweep-in fixed deposits.\n" +
                    "3. Goal Achievement Probability is high if you remain invested for the full **" + horizon + " years** horizon.";
            } else {
                return "### ⚖️ AI Advisory: Balanced Wealth Accumulation Strategy\n\n" +
                    "Based on your profile score of **" + score + "/100 (Balanced)**, here is your target allocation:\n\n" +
                    "#### 📊 Asset Allocation Mix:\n" +
                    "- **Large-Cap Blue-chip Equities**: 40%\n" +
                    "- **Corporate Debt & Bond Funds**: 35%\n" +
                    "- **Multi-Cap Mutual Funds**: 15%\n" +
                    "- **Gold (ETFs or Sovereign Bonds)**: 10%\n\n" +
                    "#### 💡 Recommendations:\n" +
                    "1. Spread allocations 60:40 between equities and fixed bonds.\n" +
                    "2. Rebalance quarterly to locking in equity returns during market spikes.\n" +
                    "3. Maintain 6 months of expenses in highly liquid cash buffers.";
            }
        }

        if (promptLower.contains("tax")) {
            return "### 💸 AI Advisory: Tax Optimization Plan\n\n" +
                "To optimize your portfolio yield against income taxes:\n\n" +
                "1. **Equity Savings Schemes (ELSS)**: Lock in investments for a 3-year term to claim exemptions.\n" +
                "2. **Debt Mutual Funds**: Consider holding long-term debt assets to qualify for capital gains adjustments.\n" +
                "3. **Tax Loss Harvesting**: Periodically offset short-term capital gains with paper losses before the end of the financial year.";
        }

        return "Hello! I am your AI Wealth Advisor. How can I assist you with your asset allocation, risk diversification, or portfolio optimization targets today?";
    }
}
