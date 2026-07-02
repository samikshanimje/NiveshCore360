package com.niveshcore360.view.dialog;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.components.ThemeManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Premium 15-question Risk Profiling Wizard Dialog.
 * Outputs Conservative, Moderate, Aggressive profiles with score out of 100.
 */
public class RiskProfilingDialog extends JDialog {

    private final CardLayout wizardLayout;
    private final JPanel wizardContainer;
    private final JLabel lblProgress;
    private int currentStep = 0;
    
    // Storing answers
    private int totalScore = 0;
    private String riskCategory = "Moderate";

    private final String[] questions = {
        "What is your age group?",
        "What is your gross annual salary?",
        "What portion of your monthly income is spent on expenses?",
        "What value of existing investments do you have?",
        "Do you have a dedicated emergency fund?",
        "How many financial goals are you currently tracking?",
        "How many financial dependents do you support?",
        "How would you rate your general risk tolerance?",
        "What is your primary investment time horizon?",
        "What is your preferred asset class for high returns?",
        "What is your income tax slab tier?",
        "What is the timeline of your primary financial goal?",
        "What is your level of previous investment experience?",
        "If your portfolio dropped 20% tomorrow, what would you do?",
        "What is your target target allocation priority?"
    };

    private final String[][] options = {
        {"Under 30 years (Active)", "30 to 50 years (Accumulating)", "Over 50 years (Preserving)"},
        {"Under ₹5 Lakhs", "₹5 Lakhs to ₹15 Lakhs", "Above ₹15 Lakhs"},
        {"Less than 30%", "30% to 60%", "More than 60%"},
        {"Minimal (< ₹2 Lakhs)", "Moderate (₹2 - ₹10 Lakhs)", "Significant (> ₹10 Lakhs)"},
        {"Yes, > 6 months expenses", "Yes, 1-3 months expenses", "No emergency reserve"},
        {"1 to 2 goals", "3 to 5 goals", "No clear target goals"},
        {"None", "1 to 2 dependents", "3 or more dependents"},
        {"High (growth-focused)", "Average (balance-seeking)", "Low (capital preservation)"},
        {"Long term (> 7 years)", "Medium term (3-7 years)", "Short term (< 3 years)"},
        {"Equities & Mutual Funds", "Gold & Sovereign Bonds", "Fixed Deposits & Cash equivalents"},
        {"10% or lower", "20% slab", "30% premium slab"},
        {"More than 10 years", "5 to 10 years", "Less than 5 years"},
        {"Experienced trader/investor", "Some basic mutual fund experience", "Complete beginner"},
        {"Buy more on discount", "Hold and wait it out", "Sell everything immediately"},
        {"Maximize wealth growth", "Balanced portfolio", "Capital safety first"}
    };

    private final int[][] scoresMap = {
        {8, 5, 2},
        {3, 6, 8},
        {8, 5, 2},
        {2, 5, 8},
        {8, 5, 1},
        {5, 7, 3},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 2},
        {8, 5, 1},
        {8, 5, 2}
    };

    private ButtonGroup[] btnGroups;

    public RiskProfilingDialog(Frame parent) {
        super(parent, "AI Risk Profile Assessment Wizard", true);
        setSize(520, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        boolean dark = ThemeManager.isDarkMode();
        getContentPane().setBackground(dark ? UIConstants.DARK_BG : UIConstants.LIGHT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 24, 8, 24));

        JLabel title = new JLabel("Risk Profiling Wizard");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.GOLD_ACCENT);
        header.add(title, BorderLayout.WEST);

        lblProgress = new JLabel("Question 1 of 15");
        lblProgress.setFont(UIConstants.FONT_CAPTION);
        lblProgress.setForeground(dark ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        header.add(lblProgress, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Wizard Panels container
        wizardLayout = new CardLayout();
        wizardContainer = new JPanel(wizardLayout);
        wizardContainer.setOpaque(false);

        btnGroups = new ButtonGroup[questions.length];
        for (int i = 0; i < questions.length; i++) {
            wizardContainer.add(createQuestionPanel(i), "STEP_" + i);
        }

        // Add final result card page
        wizardContainer.add(createResultPanel(), "RESULT");

        add(wizardContainer, BorderLayout.CENTER);

        // Bottom Nav controls
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 24, 16, 24));

        RoundedButton btnPrev = RoundedButton.secondary("Previous");
        btnPrev.setPreferredSize(new Dimension(100, 36));
        btnPrev.setEnabled(false);
        footer.add(btnPrev);

        RoundedButton btnNext = new RoundedButton("Next Question");
        btnNext.setPreferredSize(new Dimension(140, 36));
        footer.add(btnNext);

        add(footer, BorderLayout.SOUTH);

        // Navigation Actions
        btnNext.addActionListener(e -> {
            if (currentStep < questions.length) {
                // Validate if selection made
                ButtonGroup grp = btnGroups[currentStep];
                if (grp.getSelection() == null) {
                    JOptionPane.showMessageDialog(this, "Please select an answer option to proceed.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (currentStep < questions.length - 1) {
                currentStep++;
                wizardLayout.show(wizardContainer, "STEP_" + currentStep);
                lblProgress.setText("Question " + (currentStep + 1) + " of 15");
                btnPrev.setEnabled(true);
            } else if (currentStep == questions.length - 1) {
                // Process final scores and show results
                calculateRiskResult();
                currentStep++;
                wizardLayout.show(wizardContainer, "RESULT");
                lblProgress.setText("Assessment Completed");
                btnPrev.setVisible(false);
                btnNext.setText("Finish Assessment");
            } else {
                dispose(); // Finish Wizard
            }
        });

        btnPrev.addActionListener(e -> {
            if (currentStep > 0 && currentStep < questions.length) {
                currentStep--;
                wizardLayout.show(wizardContainer, "STEP_" + currentStep);
                lblProgress.setText("Question " + (currentStep + 1) + " of 15");
                btnPrev.setEnabled(currentStep > 0);
            }
        });
    }

    private JPanel createQuestionPanel(int index) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout(), UIConstants.SPACE_MD);
        card.setPreferredSize(new Dimension(460, 280));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Question label
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblQ = new JLabel("<html><body style='width: 380px;'><b>Q" + (index + 1) + ".</b> " + questions[index] + "</body></html>");
        lblQ.setFont(UIConstants.FONT_SUBHEADING);
        lblQ.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        card.add(lblQ, gbc);

        // Answer selections
        ButtonGroup group = new ButtonGroup();
        btnGroups[index] = group;

        JPanel optionsList = new JPanel(new GridLayout(3, 1, 0, 10));
        optionsList.setOpaque(false);

        for (int optIdx = 0; optIdx < options[index].length; optIdx++) {
            JRadioButton radio = new JRadioButton(options[index][optIdx]);
            radio.setFont(UIConstants.FONT_BODY);
            radio.setOpaque(false);
            radio.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
            group.add(radio);
            optionsList.add(radio);
            
            // Set action command to map option index for score lookup
            radio.setActionCommand(String.valueOf(optIdx));
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(16, 12, 8, 12);
        card.add(optionsList, gbc);

        wrapper.add(card);
        return wrapper;
    }

    private void calculateRiskResult() {
        totalScore = 0;
        for (int i = 0; i < questions.length; i++) {
            ButtonGroup grp = btnGroups[i];
            if (grp.getSelection() != null) {
                int selectedOpt = Integer.parseInt(grp.getSelection().getActionCommand());
                totalScore += scoresMap[i][selectedOpt];
            }
        }

        // Map total score (max possible score is around 120, normalize to 100)
        double rawPct = (totalScore / 115.0) * 100;
        totalScore = Math.max(10, Math.min(100, (int) rawPct));

        if (totalScore < 45) {
            riskCategory = "Conservative";
        } else if (totalScore < 75) {
            riskCategory = "Moderate";
        } else {
            riskCategory = "Aggressive";
        }
    }

    private JPanel createResultPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        card.setPreferredSize(new Dimension(460, 280));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblHeader = new JLabel("Your AI Investment Risk Profile Result", JLabel.CENTER);
        lblHeader.setFont(UIConstants.FONT_SUBHEADING);
        lblHeader.setForeground(UIConstants.GOLD_ACCENT);
        card.add(lblHeader, gbc);

        gbc.gridy = 1;
        JLabel lblScore = new JLabel("Risk Appetite Score: " + totalScore + " / 100", JLabel.CENTER);
        lblScore.setFont(UIConstants.FONT_TITLE);
        lblScore.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        
        // Timer update task to show dynamic results upon load
        Timer timer = new Timer(150, e -> {
            lblScore.setText("Risk Appetite Score: " + totalScore + " / 100");
        });
        timer.setRepeats(false);
        timer.start();
        card.add(lblScore, gbc);

        gbc.gridy = 2;
        JLabel lblCategory = new JLabel("Category: " + riskCategory, JLabel.CENTER);
        lblCategory.setFont(UIConstants.FONT_HEADER);
        lblCategory.setForeground(UIConstants.PROFIT_GREEN);
        
        Timer timerCat = new Timer(150, e -> {
            lblCategory.setText("Category: " + riskCategory);
            if ("Aggressive".equals(riskCategory)) {
                lblCategory.setForeground(UIConstants.LOSS_RED);
            } else if ("Conservative".equals(riskCategory)) {
                lblCategory.setForeground(UIConstants.WARN_AMBER);
            } else {
                lblCategory.setForeground(UIConstants.PROFIT_GREEN);
            }
        });
        timerCat.setRepeats(false);
        timerCat.start();
        card.add(lblCategory, gbc);

        gbc.gridy = 3;
        JLabel lblDesc = new JLabel("<html><body style='text-align: center; color: #888;'>This allocation model balances growth & capital safety matching your metrics. You can re-take this wizard anytime.</body></html>", JLabel.CENTER);
        lblDesc.setFont(UIConstants.FONT_CAPTION);
        card.add(lblDesc, gbc);

        wrapper.add(card);
        return wrapper;
    }

    public int getFinalScore() {
        return totalScore;
    }

    public String getRiskCategory() {
        return riskCategory;
    }
}
