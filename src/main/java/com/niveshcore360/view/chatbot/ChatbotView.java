package com.niveshcore360.view.chatbot;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.entity.ChatHistory;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.AiAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * AI Chatbot advisor panel with premium warm-themed message bubbles,
 * quick action chips, and styled input bar.
 */
@Component
public class ChatbotView extends JPanel {

    private final AiAdvisorService aiAdvisorService;
    private final UserSession userSession;
    private final JTextPane chatArea;
    private final JTextField promptField;
    private final RoundedButton btnSend;
    private final JButton btnExport;
    private final JButton btnClear;

    @Autowired
    public ChatbotView(AiAdvisorService aiAdvisorService, UserSession userSession) {
        this.aiAdvisorService = aiAdvisorService;
        this.userSession = userSession;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));

        // ─── Header Section ─────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, UIConstants.SPACE_MD, 0));

        JLabel titleLabel = new JLabel("AI Wealth Advisor");
        titleLabel.setFont(UIConstants.FONT_HEADING);
        titleLabel.setForeground(UIConstants.GOLD_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnExport = new JButton("Export Report");
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.setFont(UIConstants.FONT_CAPTION);
        btnExport.setForeground(UIConstants.GOLD_ACCENT);
        btnExport.setContentAreaFilled(false);
        btnExport.setBorderPainted(false);
        actionPanel.add(btnExport);

        btnClear = new JButton("Clear Chat");
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.setFont(UIConstants.FONT_CAPTION);
        btnClear.setForeground(UIConstants.LOSS_RED);
        btnClear.setContentAreaFilled(false);
        btnClear.setBorderPainted(false);
        actionPanel.add(btnClear);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Chat Display Area ──────────────────────────────────────
        CardPanel displayCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_SM);
        displayCard.setHoverLiftEnabled(false);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(null);
        displayCard.add(scrollPane, BorderLayout.CENTER);

        add(displayCard, BorderLayout.CENTER);

        // ─── Bottom: Quick Actions + Input Bar ──────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout(0, UIConstants.SPACE_SM));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(UIConstants.SPACE_MD, 0, 0, 0));

        // Initialize promptField early so chip lambdas can reference it
        promptField = new JTextField();

        // Quick action chips
        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chipsPanel.setOpaque(false);
        String[] suggestions = {"Analyze portfolio", "Tax tips", "Rebalance"};
        for (String sug : suggestions) {
            RoundedButton chip = RoundedButton.secondary(sug);
            chip.setPreferredSize(new Dimension(140, 30));
            chip.setFont(UIConstants.FONT_CAPTION);
            chip.addActionListener(e -> {
                promptField.setText(sug);
                sendMessage();
            });
            chipsPanel.add(chip);
        }
        bottomPanel.add(chipsPanel, BorderLayout.NORTH);

        // Input bar
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        promptField.setPreferredSize(new Dimension(0, 46));
        promptField.setFont(UIConstants.FONT_BODY);
        promptField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        inputPanel.add(promptField, BorderLayout.CENTER);

        btnSend = new RoundedButton("Send");
        btnSend.setPreferredSize(new Dimension(100, 46));
        inputPanel.add(btnSend, BorderLayout.EAST);

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnSend.addActionListener(e -> sendMessage());
        promptField.addActionListener(e -> sendMessage());
        btnClear.addActionListener(e -> clearHistory());
        btnExport.addActionListener(e -> exportAdvisoryReport());

        // Initial Load
        loadHistory();
    }
    private void sendMessage() {
        String text = promptField.getText().trim();
        if (text.isEmpty()) return;

        Long userId = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getId() : null;
        if (userId == null) return;

        promptField.setText("");
        appendMessage("You", text, true);

        // Execute API call asynchronously to prevent UI freeze
        btnSend.setEnabled(false);
        promptField.setEnabled(false);

        new Thread(() -> {
            try {
                String response = aiAdvisorService.askAdvisor(userId, text);
                SwingUtilities.invokeLater(() -> {
                    appendMessage("AI Advisor", response, false);
                    btnSend.setEnabled(true);
                    promptField.setEnabled(true);
                    promptField.requestFocus();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    appendMessage("System", "Failed to retrieve advice: " + ex.getMessage(), false);
                    btnSend.setEnabled(true);
                    promptField.setEnabled(true);
                });
            }
        }).start();
    }

    private void clearHistory() {
        Long userId = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getId() : null;
        if (userId == null) return;

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear your advisory history?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            aiAdvisorService.clearChatHistory(userId);
            loadHistory();
        }
    }

    public void loadHistory() {
        Long userId = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getId() : null;
        boolean dark = ThemeManager.isDarkMode();
        String bgColor = dark ? "#2C2C2E" : "#FFFFFF";
        String textColor = dark ? "#FAF8F5" : "#292524";
        String bodyStyle = "font-family: 'system-ui', 'Dialog', sans-serif; padding: 10px; background-color: " + bgColor + "; color: " + textColor + "; line-height: 1.6;";

        if (userId == null) {
            chatArea.setText("<html><body style='" + bodyStyle + "'><div style='color:#78716C; text-align:center; margin-top:50px;'>Please sign in to start a conversation.</div></body></html>");
            return;
        }

        List<ChatHistory> history = aiAdvisorService.getChatHistory(userId);
        StringBuilder html = new StringBuilder("<html><body style='" + bodyStyle + "'>");

        if (history.isEmpty()) {
            html.append("<div style='text-align:center; margin-top:50px; color:#78716C;'>")
                .append("<h3 style='color:#C9A84C;'>Welcome to your AI Financial Advisor</h3>")
                .append("<p>Ask me about asset allocations, tax optimizations, or SIP projection targets.</p>")
                .append("</div>");
        } else {
            for (ChatHistory chat : history) {
                boolean isUser = "USER".equals(chat.getSender());
                String color = isUser ? "#C9A84C" : "#2D8B55";
                String name = isUser ? "You" : "AI Advisor";
                String msgText = chat.getMessage().replaceAll("\n", "<br/>");

                html.append("<p style='margin: 8px 0; padding: 12px 16px; border-radius: 12px;'><b style='color:").append(color).append(";'>").append(name).append(":</b><br/>")
                    .append("<span>").append(msgText).append("</span></p>");
            }
        }

        html.append("</body></html>");
        chatArea.setText(html.toString());
    }

    private void appendMessage(String sender, String text, boolean isUser) {
        String currentText = chatArea.getText();
        int bodyCloseIdx = currentText.lastIndexOf("</body>");
        if (bodyCloseIdx == -1) {
            loadHistory();
            return;
        }

        String color = isUser ? "#C9A84C" : "#2D8B55";
        String msgText = text.replaceAll("\n", "<br/>");
        String addition = "<p style='margin: 8px 0; padding: 12px 16px; border-radius: 12px;'><b style='color:" + color + ";'>" + sender + ":</b><br/>" +
            "<span>" + msgText + "</span></p>";

        String updated = currentText.substring(0, bodyCloseIdx) + addition + "</body></html>";
        chatArea.setText(updated);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void exportAdvisoryReport() {
        Long userId = userSession.getCurrentUser() != null ? userSession.getCurrentUser().getId() : null;
        if (userId == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("NiveshCore360_Advisory_Report.pdf"));
        int choice = fileChooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File dest = fileChooser.getSelectedFile();
            new Thread(() -> {
                try {
                    byte[] pdfBytes = aiAdvisorService.generateFinancialReport(userId);
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(pdfBytes);
                    }
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "AI report successfully exported to:\n" + dest.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed compiling PDF: " + ex.getMessage(), "Export Failure", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        }
    }
}
