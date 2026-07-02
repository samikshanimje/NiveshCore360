package com.niveshcore360.view.calculator;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.util.FinancialCalculatorUtil;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Calculators View with warm-themed cards and styled result displays.
 */
@Component
public class CalculatorsView extends JPanel {

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public CalculatorsView() {
        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Financial Planning Calculators");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_BODY);
        tabbedPane.addTab("SIP Calculator", createSipPanel());
        tabbedPane.addTab("EMI Calculator", createEmiPanel());
        tabbedPane.addTab("Compound Interest", createCiPanel());
        tabbedPane.addTab("Retirement Planner", createRetirementPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSipPanel() {
        return buildCalcPanel(new String[]{"Monthly SIP Amount (₹):", "Expected Return Rate (% p.a.):", "Time Horizon (Years):"},
                new String[]{"5000", "12", "10"}, "Calculate Maturity", (fields, result) -> {
            BigDecimal p = new BigDecimal(fields[0].getText().trim());
            BigDecimal r = new BigDecimal(fields[1].getText().trim());
            int t = Integer.parseInt(fields[2].getText().trim());
            BigDecimal output = FinancialCalculatorUtil.calculateSIP(p, r, t);
            BigDecimal totalInvested = p.multiply(BigDecimal.valueOf(t * 12L));
            BigDecimal wealthGained = output.subtract(totalInvested);
            result.setText("<html><center>Invested Capital: " + currencyFormatter.format(totalInvested) +
                    "<br/>Wealth Gained: <span style='color:#2D8B55;'>" + currencyFormatter.format(wealthGained) + "</span>" +
                    "<br/><b>Maturity Value: " + currencyFormatter.format(output) + "</b></center></html>");
        });
    }

    private JPanel createEmiPanel() {
        return buildCalcPanel(new String[]{"Loan Principal Amount (₹):", "Interest Rate (% p.a.):", "Loan Tenure (Years):"},
                new String[]{"1000000", "8.5", "15"}, "Calculate EMI", (fields, result) -> {
            BigDecimal p = new BigDecimal(fields[0].getText().trim());
            BigDecimal r = new BigDecimal(fields[1].getText().trim());
            int t = Integer.parseInt(fields[2].getText().trim());
            BigDecimal emi = FinancialCalculatorUtil.calculateEMI(p, r, t);
            BigDecimal totalRepay = emi.multiply(BigDecimal.valueOf(t * 12L));
            BigDecimal totalInterest = totalRepay.subtract(p);
            result.setText("<html><center>Monthly EMI: <b>" + currencyFormatter.format(emi) + "</b>" +
                    "<br/>Total Interest: " + currencyFormatter.format(totalInterest) +
                    "<br/>Total Repayment: " + currencyFormatter.format(totalRepay) + "</center></html>");
        });
    }

    private JPanel createCiPanel() {
        return buildCalcPanel(new String[]{"Principal Sum (₹):", "Annual Return Rate (%):", "Tenure (Years):"},
                new String[]{"50000", "7.5", "5"}, "Compound Maturity", (fields, result) -> {
            BigDecimal p = new BigDecimal(fields[0].getText().trim());
            BigDecimal r = new BigDecimal(fields[1].getText().trim());
            int t = Integer.parseInt(fields[2].getText().trim());
            BigDecimal output = FinancialCalculatorUtil.calculateCompoundInterest(p, r, t, 1);
            BigDecimal interest = output.subtract(p);
            result.setText("<html><center>Maturity Value: <b>" + currencyFormatter.format(output) + "</b>" +
                    "<br/>Interest Earned: <span style='color:#2D8B55;'>" + currencyFormatter.format(interest) + "</span></center></html>");
        });
    }

    private JPanel createRetirementPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        card.setPreferredSize(new Dimension(540, 440));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(styledLabel("Current Age / Target Age:"), gbc);
        JPanel agePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        agePanel.setOpaque(false);
        JTextField txtCurrAge = styledField("30");
        JTextField txtRetAge = styledField("60");
        agePanel.add(txtCurrAge);
        agePanel.add(txtRetAge);
        gbc.gridx = 1;
        card.add(agePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(styledLabel("Current Monthly Expenses (₹):"), gbc);
        JTextField txtExp = styledField("40000");
        gbc.gridx = 1;
        card.add(txtExp, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(styledLabel("Inflation Rate / Post-Ret Return (%):"), gbc);
        JPanel ratePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        ratePanel.setOpaque(false);
        JTextField txtInf = styledField("6.0");
        JTextField txtPostRet = styledField("8.0");
        ratePanel.add(txtInf);
        ratePanel.add(txtPostRet);
        gbc.gridx = 1;
        card.add(ratePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(styledLabel("Life Expectancy (Age):"), gbc);
        JTextField txtLife = styledField("85");
        gbc.gridx = 1;
        card.add(txtLife, gbc);

        JLabel lblResult = new JLabel("Retirement target values will print here.", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_BODY);
        lblResult.setForeground(UIConstants.GOLD_ACCENT);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(lblResult, gbc);

        RoundedButton btnCalc = new RoundedButton("Run Retirement Estimate");
        gbc.gridy = 5;
        card.add(btnCalc, gbc);

        btnCalc.addActionListener(e -> {
            try {
                int currAge = Integer.parseInt(txtCurrAge.getText().trim());
                int retAge = Integer.parseInt(txtRetAge.getText().trim());
                BigDecimal monthlyExp = new BigDecimal(txtExp.getText().trim());
                BigDecimal inf = new BigDecimal(txtInf.getText().trim());
                BigDecimal postRet = new BigDecimal(txtPostRet.getText().trim());
                int life = Integer.parseInt(txtLife.getText().trim());

                FinancialCalculatorUtil.RetirementResult res = FinancialCalculatorUtil.calculateRetirement(
                        currAge, retAge, monthlyExp, inf, postRet, life
                );

                lblResult.setText("<html><center>Target Corpus: <b>" + currencyFormatter.format(res.corpusNeeded) + "</b>" +
                        "<br/>Monthly Savings Needed: <b>" + currencyFormatter.format(res.monthlySavingsNeeded) + "</b></center></html>");
            } catch (Exception ex) {
                lblResult.setText("Validation failed: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildCalcPanel(String[] labels, String[] defaults, String btnText, CalcAction action) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        card.setPreferredSize(new Dimension(520, 380));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            card.add(styledLabel(labels[i]), gbc);
            fields[i] = styledField(defaults[i]);
            gbc.gridx = 1;
            card.add(fields[i], gbc);
        }

        JLabel lblResult = new JLabel("", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_SUBHEADING);
        lblResult.setForeground(UIConstants.GOLD_ACCENT);
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        card.add(lblResult, gbc);

        RoundedButton btnCalc = new RoundedButton(btnText);
        gbc.gridy = labels.length + 1;
        card.add(btnCalc, gbc);

        btnCalc.addActionListener(e -> {
            try {
                action.execute(fields, lblResult);
            } catch (Exception ex) {
                lblResult.setText("Invalid Inputs: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY);
        return lbl;
    }

    private JTextField styledField(String defaultVal) {
        JTextField field = new JTextField(defaultVal);
        field.setFont(UIConstants.FONT_BODY);
        field.setPreferredSize(new Dimension(180, 36));
        return field;
    }

    @FunctionalInterface
    private interface CalcAction {
        void execute(JTextField[] fields, JLabel result) throws Exception;
    }
}
