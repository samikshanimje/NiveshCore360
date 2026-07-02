package com.niveshcore360.view.dashboard;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.controller.PortfolioController;
import com.niveshcore360.dto.PortfolioSummaryDTO;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.NotificationService;
import com.niveshcore360.entity.Notification;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard View with time-of-day greeting, premium stat cards,
 * themed pie chart, and notification alerts.
 */
@Component
public class DashboardView extends JPanel {

    private final PortfolioController portfolioController;
    private final NotificationService notificationService;
    private final UserSession userSession;

    private JLabel lblGreeting;
    private JLabel lblTotalValue;
    private JLabel lblTotalInvested;
    private JLabel lblProfitLoss;
    private JPanel chartWrapperPanel;
    private JPanel alertsListPanel;
    private JComboBox<PortfolioComboItem> comboPortfolios;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Autowired
    public DashboardView(PortfolioController portfolioController,
                         NotificationService notificationService,
                         UserSession userSession) {
        this.portfolioController = portfolioController;
        this.notificationService = notificationService;
        this.userSession = userSession;

        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        // ─── Header with greeting + portfolio selector ──────────────
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JPanel greetingPanel = new JPanel();
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));
        greetingPanel.setOpaque(false);

        lblGreeting = new JLabel(getGreetingText());
        lblGreeting.setFont(UIConstants.FONT_DISPLAY);
        lblGreeting.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        greetingPanel.add(lblGreeting);

        JLabel lblSub = new JLabel("Here's your portfolio overview");
        lblSub.setFont(UIConstants.FONT_CAPTION);
        lblSub.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        lblSub.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        greetingPanel.add(lblSub);

        headerPanel.add(greetingPanel, BorderLayout.WEST);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(200, 36));
        comboPortfolios.addActionListener(e -> refreshDashboard());
        headerPanel.add(comboPortfolios, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Main Grid ──────────────────────────────────────────────
        JPanel mainGrid = new JPanel(new GridBagLayout());
        mainGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACE_SM, UIConstants.SPACE_SM, UIConstants.SPACE_SM, UIConstants.SPACE_SM);
        gbc.fill = GridBagConstraints.BOTH;

        // ─── Stat Cards Row ─────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.18;
        CardPanel cardValue = createStatCard("Portfolio Value");
        lblTotalValue = (JLabel) ((JPanel) cardValue.getComponent(1)).getComponent(0);
        mainGrid.add(cardValue, gbc);

        gbc.gridx = 1;
        CardPanel cardInvested = createStatCard("Invested Capital");
        lblTotalInvested = (JLabel) ((JPanel) cardInvested.getComponent(1)).getComponent(0);
        mainGrid.add(cardInvested, gbc);

        gbc.gridx = 2;
        CardPanel cardProfit = createStatCard("Total Returns");
        lblProfitLoss = (JLabel) ((JPanel) cardProfit.getComponent(1)).getComponent(0);
        mainGrid.add(cardProfit, gbc);

        // ─── Chart section ──────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0.82;
        CardPanel chartCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_LG);
        JLabel chartTitle = new JLabel("Asset Allocation Breakdown");
        chartTitle.setFont(UIConstants.FONT_SUBHEADING);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartWrapperPanel = new JPanel(new BorderLayout());
        chartWrapperPanel.setOpaque(false);
        chartCard.add(chartWrapperPanel, BorderLayout.CENTER);
        mainGrid.add(chartCard, gbc);

        // ─── Right Column: Alerts & AI Market Intel ──────────────────
        gbc.gridx = 2; gbc.gridwidth = 1; gbc.weighty = 0.82;
        JPanel rightColPanel = new JPanel(new GridBagLayout());
        rightColPanel.setOpaque(false);
        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.fill = GridBagConstraints.BOTH;
        rGbc.weightx = 1.0;

        // Alerts (45% height)
        rGbc.gridy = 0; rGbc.weighty = 0.45;
        rGbc.insets = new Insets(0, 0, 8, 0);
        CardPanel alertsCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_LG);
        JLabel alertTitle = new JLabel("Milestones & Alerts");
        alertTitle.setFont(UIConstants.FONT_SUBHEADING);
        alertsCard.add(alertTitle, BorderLayout.NORTH);

        alertsListPanel = new JPanel();
        alertsListPanel.setLayout(new BoxLayout(alertsListPanel, BoxLayout.Y_AXIS));
        alertsListPanel.setOpaque(false);
        JScrollPane alertScroll = new JScrollPane(alertsListPanel);
        alertScroll.setBorder(null);
        alertScroll.setOpaque(false);
        alertScroll.getViewport().setOpaque(false);
        alertsCard.add(alertScroll, BorderLayout.CENTER);
        rightColPanel.add(alertsCard, rGbc);

        // Market Intel Summarizer (55% height)
        rGbc.gridy = 1; rGbc.weighty = 0.55;
        rGbc.insets = new Insets(8, 0, 0, 0);
        CardPanel intelCard = createMarketIntelCard();
        rightColPanel.add(intelCard, rGbc);

        mainGrid.add(rightColPanel, gbc);

        add(mainGrid, BorderLayout.CENTER);
    }

    private CardPanel createStatCard(String titleText) {
        CardPanel card = new CardPanel(new BorderLayout(8, 8), UIConstants.SPACE_LG);

        JLabel title = new JLabel(titleText);
        title.setFont(UIConstants.FONT_CAPTION);
        title.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        card.add(title, BorderLayout.NORTH);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);
        JLabel valueLbl = new JLabel("₹0.00");
        valueLbl.setFont(UIConstants.FONT_MONO);
        valuePanel.add(valueLbl);
        card.add(valuePanel, BorderLayout.CENTER);

        return card;
    }

    private String getGreetingText() {
        int hour = LocalTime.now().getHour();
        String greeting;
        if (hour < 12) greeting = "Good Morning";
        else if (hour < 17) greeting = "Good Afternoon";
        else greeting = "Good Evening";

        if (userSession.isLoggedIn() && userSession.getCurrentUser().getFullName() != null) {
            String firstName = userSession.getCurrentUser().getFullName().split("\\s+")[0];
            return greeting + ", " + firstName + " 👋";
        }
        return greeting + " 👋";
    }

    /**
     * Rebuild and reload combobox selections when user logs in.
     */
    public void setupDashboardData() {
        if (!userSession.isLoggedIn()) return;
        lblGreeting.setText(getGreetingText());
        comboPortfolios.removeAllItems();
        List<Portfolio> portfolios = portfolioController.getPortfolios();
        for (Portfolio p : portfolios) {
            comboPortfolios.addItem(new PortfolioComboItem(p.getId(), p.getName()));
        }
        refreshDashboard();
    }

    /**
     * Refreshes dashboard values, JFreeChart plot, and alerts logs.
     */
    public void refreshDashboard() {
        if (!userSession.isLoggedIn() || comboPortfolios.getItemCount() == 0) {
            lblTotalValue.setText("₹0.00");
            lblTotalInvested.setText("₹0.00");
            lblProfitLoss.setText("₹0.00 (0.00%)");
            lblProfitLoss.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
            chartWrapperPanel.removeAll();
            chartWrapperPanel.revalidate();
            chartWrapperPanel.repaint();
            alertsListPanel.removeAll();
            alertsListPanel.revalidate();
            alertsListPanel.repaint();
            return;
        }

        PortfolioComboItem selected = (PortfolioComboItem) comboPortfolios.getSelectedItem();
        if (selected == null) return;

        try {
            PortfolioSummaryDTO summary = portfolioController.getPortfolioSummary(selected.id);

            lblTotalValue.setText(currencyFormatter.format(summary.getTotalCurrentValue()));
            lblTotalInvested.setText(currencyFormatter.format(summary.getTotalInvestment()));

            String profitStr = currencyFormatter.format(summary.getTotalProfitLoss()) + " (" + summary.getTotalProfitLossPercentage() + "%)";
            lblProfitLoss.setText(profitStr);
            if (summary.getTotalProfitLoss().compareTo(BigDecimal.ZERO) >= 0) {
                lblProfitLoss.setForeground(UIConstants.PROFIT_GREEN);
            } else {
                lblProfitLoss.setForeground(UIConstants.LOSS_RED);
            }

            // Redraw Allocation Pie Chart
            chartWrapperPanel.removeAll();
            if (summary.getCategoryAllocation().isEmpty()) {
                JLabel emptyLbl = new JLabel("No investment holdings to display allocation.", JLabel.CENTER);
                emptyLbl.setFont(UIConstants.FONT_BODY);
                emptyLbl.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
                chartWrapperPanel.add(emptyLbl, BorderLayout.CENTER);
            } else {
                JFreeChart chart = createPieChart(summary.getCategoryAllocation());
                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setOpaque(false);
                chartPanel.setBackground(new Color(0, 0, 0, 0));
                chartWrapperPanel.add(chartPanel, BorderLayout.CENTER);
            }
            chartWrapperPanel.revalidate();
            chartWrapperPanel.repaint();

            // Load Alerts / Notifications
            alertsListPanel.removeAll();
            List<Notification> alerts = notificationService.getNotificationsForUser(userSession.getCurrentUser().getId());
            if (alerts.isEmpty()) {
                JLabel noAlerts = new JLabel("All caught up! No new alerts.");
                noAlerts.setFont(UIConstants.FONT_BODY);
                noAlerts.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
                noAlerts.setBorder(new EmptyBorder(20, 10, 10, 10));
                alertsListPanel.add(noAlerts);
            } else {
                for (Notification alert : alerts) {
                    JPanel alertRow = new JPanel(new BorderLayout(8, 4));
                    alertRow.setOpaque(false);
                    alertRow.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, UIConstants.GOLD_ACCENT),
                        BorderFactory.createEmptyBorder(8, 10, 8, 6)
                    ));

                    JLabel alertText = new JLabel("<html><body style='width: 180px;'>" + alert.getMessage() + "</body></html>");
                    alertText.setFont(UIConstants.FONT_CAPTION);
                    alertRow.add(alertText, BorderLayout.CENTER);

                    alertsListPanel.add(alertRow);
                    alertsListPanel.add(Box.createRigidArea(new Dimension(0, 6)));
                }
            }
            alertsListPanel.revalidate();
            alertsListPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching portfolio summary: " + ex.getMessage());
        }
    }

    private JFreeChart createPieChart(Map<String, BigDecimal> allocation) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        allocation.forEach((symbol, value) -> dataset.setValue(symbol, value.doubleValue()));

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);

        // Apply warm chart palette
        Color[] palette = UIConstants.CHART_PALETTE;
        int idx = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable<?>) key, palette[idx % palette.length]);
            idx++;
        }

        boolean dark = ThemeManager.isDarkMode();
        Color textCol = dark ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY;
        Color bgCol = dark ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD;

        chart.setBackgroundPaint(bgCol);
        plot.setLabelPaint(textCol);
        plot.setLabelBackgroundPaint(bgCol);
        plot.setLabelOutlinePaint(bgCol);
        plot.setLabelFont(UIConstants.FONT_CAPTION);
        chart.getLegend().setBackgroundPaint(bgCol);
        chart.getLegend().setItemPaint(textCol);
        chart.getLegend().setItemFont(UIConstants.FONT_CAPTION);

        return chart;
    }

    private CardPanel createMarketIntelCard() {
        CardPanel card = new CardPanel(new BorderLayout(), UIConstants.SPACE_MD);
        JLabel title = new JLabel("Market Intelligence Summary");
        title.setFont(UIConstants.FONT_SUBHEADING);
        title.setForeground(UIConstants.GOLD_ACCENT);
        card.add(title, BorderLayout.NORTH);

        // Content
        JTextArea area = new JTextArea(
            "● Nifty 50: 22,450.20 (+0.45%)  |  BSE Sensex: 74,120.50 (+0.52%)\n" +
            "● RBI NEWS: Repurchase rate remains flat at 6.50% to align core inflation goals.\n" +
            "● US FED NEWS: Policy rates projected steady; future hikes conditional on CPI data.\n" +
            "● GLOBAL MARKETS: Positive momentum in US futures; Asian indices closing higher.\n" +
            "● TOP MOVERS: RELIANCE (+2.15%), TCS (+1.85%), INFOSYS (+1.40%)."
        );
        area.setFont(UIConstants.FONT_CAPTION);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 0, 0, 0));
        card.add(area, BorderLayout.CENTER);

        return card;
    }

    private static class PortfolioComboItem {
        final Long id;
        final String name;

        PortfolioComboItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
