package com.niveshcore360.view.chatbot;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
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
 * Panel hosting the AI chatbot advisor conversation screen.
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
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("AI Wealth Advisor");
        titleLabel.setFont(new Font("sansserif", Font.BOLD, 22));
        titleLabel.setForeground(UIConstants.DARK_TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnExport = new JButton("Export Advisory PDF");
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.setForeground(UIConstants.ACCENT_COLOR);
        actionPanel.add(btnExport);

        btnClear = new JButton("Clear Chat");
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.setForeground(UIConstants.LOSS_RED);
        actionPanel.add(btnClear);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Chat Display Area (Glassmorphic Card)
        CardPanel displayCard = new CardPanel(new BorderLayout());
        displayCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setBackground(UIConstants.DARK_CARD);
        chatArea.setForeground(UIConstants.DARK_TEXT_PRIMARY);
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        displayCard.add(scrollPane, BorderLayout.CENTER);

        add(displayCard, BorderLayout.CENTER);

        // Input Control Bar
        JPanel inputPanel = new JPanel(new BorderLayout(15, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        promptField = new JTextField();
        promptField.setPreferredSize(new Dimension(0, 42));
        promptField.setBackground(UIConstants.DARK_CARD);
        promptField.setForeground(UIConstants.DARK_TEXT_PRIMARY);
        promptField.setCaretColor(Color.WHITE);
        promptField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.DARK_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputPanel.add(promptField, BorderLayout.CENTER);

        btnSend = new RoundedButton("Send Message");
        btnSend.setPreferredSize(new Dimension(140, 42));
        inputPanel.add(btnSend, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

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
        if (userId == null) {
            chatArea.setText("<html><body style='font-family:sans-serif; color:#94a3b8;'>Please sign in to initiate conversation logs.</body></html>");
            return;
        }

        List<ChatHistory> history = aiAdvisorService.getChatHistory(userId);
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif; padding: 10px;'>");
        
        if (history.isEmpty()) {
            html.append("<div style='color:#94a3b8; text-align:center; margin-top:50px;'>")
                .append("<h3>Welcome to your AI Financial Advisor</h3>")
                .append("<p>Ask me details about asset allocations, tax optimizations, or SIP projection targets.</p>")
                .append("</div>");
        } else {
            for (ChatHistory chat : history) {
                boolean isUser = "USER".equals(chat.getSender());
                String color = isUser ? "#8b5cf6" : "#10b981"; // Purple or Emerald
                String name = isUser ? "You" : "AI Advisor";
                String msgText = chat.getMessage().replaceAll("\n", "<br/>");

                html.append("<p><b style='color:").append(color).append(";'>").append(name).append(":</b><br/>")
                    .append("<span style='color:#f8fafc;'>").append(msgText).append("</span></p><br/>");
            }
        }
        
        html.append("</body></html>");
        chatArea.setText(html.toString());
    }

    private void appendMessage(String sender, String text, boolean isUser) {
        String currentText = chatArea.getText();
        // Simple HTML injection to append message
        int bodyCloseIdx = currentText.lastIndexOf("</body>");
        if (bodyCloseIdx == -1) {
            loadHistory();
            return;
        }

        String color = isUser ? "#8b5cf6" : "#10b981";
        String msgText = text.replaceAll("\n", "<br/>");
        String addition = "<p><b style='color:" + color + ";'>" + sender + ":</b><br/>" +
            "<span style='color:#f8fafc;'>" + msgText + "</span></p><br/>";

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
