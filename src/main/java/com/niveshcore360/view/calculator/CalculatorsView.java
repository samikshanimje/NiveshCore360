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
 * Calculators View supporting SIP, EMI, Compound Interest, and Retirement projection models.
 */
@Component
public class CalculatorsView extends JPanel {

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public CalculatorsView() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Financial Planning Calculators");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("SIP Calculator", createSipPanel());
        tabbedPane.addTab("EMI Calculator", createEmiPanel());
        tabbedPane.addTab("Compound Interest", createCiPanel());
        tabbedPane.addTab("Retirement Planner", createRetirementPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSipPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(500, 360));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(new JLabel("Monthly SIP Amount (₹):"), gbc);
        JTextField txtSipAmt = new JTextField("5000");
        gbc.gridx = 1;
        card.add(txtSipAmt, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Expected Return Rate (% p.a.):"), gbc);
        JTextField txtRate = new JTextField("12");
        gbc.gridx = 1;
        card.add(txtRate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Time Horizon (Years):"), gbc);
        JTextField txtYears = new JTextField("10");
        gbc.gridx = 1;
        card.add(txtYears, gbc);

        // Result Label
        JLabel lblResult = new JLabel("Future Value: ₹0.00", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_HEADER);
        lblResult.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(lblResult, gbc);

        RoundedButton btnCalc = new RoundedButton("Calculate Maturity");
        gbc.gridy = 4;
        card.add(btnCalc, gbc);

        btnCalc.addActionListener(e -> {
            try {
                BigDecimal p = new BigDecimal(txtSipAmt.getText().trim());
                BigDecimal r = new BigDecimal(txtRate.getText().trim());
                int t = Integer.parseInt(txtYears.getText().trim());
                BigDecimal output = FinancialCalculatorUtil.calculateSIP(p, r, t);
                
                BigDecimal totalInvested = p.multiply(BigDecimal.valueOf(t * 12L));
                BigDecimal wealthGained = output.subtract(totalInvested);

                String msg = "<html><center>Invested Capital: " + currencyFormatter.format(totalInvested) +
                        "<br/>Wealth Gained: " + currencyFormatter.format(wealthGained) +
                        "<br/><b>Maturity Value: " + currencyFormatter.format(output) + "</b></center></html>";
                lblResult.setText(msg);
            } catch (Exception ex) {
                lblResult.setText("Invalid Inputs: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createEmiPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(500, 360));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(new JLabel("Loan Principal Amount (₹):"), gbc);
        JTextField txtPrincipal = new JTextField("1000000");
        gbc.gridx = 1;
        card.add(txtPrincipal, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Interest Rate (% p.a.):"), gbc);
        JTextField txtRate = new JTextField("8.5");
        gbc.gridx = 1;
        card.add(txtRate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Loan Tenure (Years):"), gbc);
        JTextField txtYears = new JTextField("15");
        gbc.gridx = 1;
        card.add(txtYears, gbc);

        JLabel lblResult = new JLabel("Monthly EMI: ₹0.00", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_HEADER);
        lblResult.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(lblResult, gbc);

        RoundedButton btnCalc = new RoundedButton("Calculate EMI");
        gbc.gridy = 4;
        card.add(btnCalc, gbc);

        btnCalc.addActionListener(e -> {
            try {
                BigDecimal p = new BigDecimal(txtPrincipal.getText().trim());
                BigDecimal r = new BigDecimal(txtRate.getText().trim());
                int t = Integer.parseInt(txtYears.getText().trim());
                BigDecimal emi = FinancialCalculatorUtil.calculateEMI(p, r, t);

                BigDecimal totalRepay = emi.multiply(BigDecimal.valueOf(t * 12L));
                BigDecimal totalInterest = totalRepay.subtract(p);

                String msg = "<html><center>Monthly EMI: <b>" + currencyFormatter.format(emi) + "</b>" +
                        "<br/>Total Interest: " + currencyFormatter.format(totalInterest) +
                        "<br/>Total Repayment: " + currencyFormatter.format(totalRepay) + "</center></html>";
                lblResult.setText(msg);
            } catch (Exception ex) {
                lblResult.setText("Invalid Inputs: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createCiPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(500, 360));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(new JLabel("Principal Sum (₹):"), gbc);
        JTextField txtPrincipal = new JTextField("50000");
        gbc.gridx = 1;
        card.add(txtPrincipal, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Annual Return Rate (%):"), gbc);
        JTextField txtRate = new JTextField("7.5");
        gbc.gridx = 1;
        card.add(txtRate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Tenure (Years):"), gbc);
        JTextField txtYears = new JTextField("5");
        gbc.gridx = 1;
        card.add(txtYears, gbc);

        JLabel lblResult = new JLabel("Accumulated Amount: ₹0.00", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_HEADER);
        lblResult.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(lblResult, gbc);

        RoundedButton btnCalc = new RoundedButton("Compound Maturity");
        gbc.gridy = 4;
        card.add(btnCalc, gbc);

        btnCalc.addActionListener(e -> {
            try {
                BigDecimal p = new BigDecimal(txtPrincipal.getText().trim());
                BigDecimal r = new BigDecimal(txtRate.getText().trim());
                int t = Integer.parseInt(txtYears.getText().trim());
                BigDecimal output = FinancialCalculatorUtil.calculateCompoundInterest(p, r, t, 1); // Yearly compounding

                BigDecimal interest = output.subtract(p);
                String msg = "<html><center>Maturity Value: <b>" + currencyFormatter.format(output) + "</b>" +
                        "<br/>Interest Earned: " + currencyFormatter.format(interest) + "</center></html>";
                lblResult.setText(msg);
            } catch (Exception ex) {
                lblResult.setText("Invalid Inputs: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private JPanel createRetirementPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(520, 420));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(new JLabel("Current Age / Target Age:"), gbc);
        JPanel agePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        agePanel.setOpaque(false);
        JTextField txtCurrAge = new JTextField("30");
        JTextField txtRetAge = new JTextField("60");
        agePanel.add(txtCurrAge);
        agePanel.add(txtRetAge);
        gbc.gridx = 1;
        card.add(agePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Current Monthly Expenses (₹):"), gbc);
        JTextField txtExp = new JTextField("40000");
        gbc.gridx = 1;
        card.add(txtExp, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Inflation Rate / Post-Ret Return (%):"), gbc);
        JPanel ratePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        ratePanel.setOpaque(false);
        JTextField txtInf = new JTextField("6.0");
        JTextField txtPostRet = new JTextField("8.0");
        ratePanel.add(txtInf);
        ratePanel.add(txtPostRet);
        gbc.gridx = 1;
        card.add(ratePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Life Expectancy (Age):"), gbc);
        JTextField txtLife = new JTextField("85");
        gbc.gridx = 1;
        card.add(txtLife, gbc);

        JLabel lblResult = new JLabel("Retirement target values will print here.", JLabel.CENTER);
        lblResult.setFont(UIConstants.FONT_BODY);
        lblResult.setForeground(UIConstants.ACCENT_COLOR);
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

                String msg = "<html><center>Target Corpus Needed: <b>" + currencyFormatter.format(res.corpusNeeded) + "</b>" +
                        "<br/>Pre-retirement Monthly Savings Needed: <b>" + currencyFormatter.format(res.monthlySavingsNeeded) + "</b></center></html>";
                lblResult.setText(msg);
            } catch (Exception ex) {
                lblResult.setText("Validation failed: " + ex.getMessage());
            }
        });

        wrapper.add(card);
        return wrapper;
    }
}
