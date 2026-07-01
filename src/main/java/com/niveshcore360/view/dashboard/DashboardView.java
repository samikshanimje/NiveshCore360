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
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard View consolidating portfolio summaries, allocations, charts, and notification logs.
 */
@Component
public class DashboardView extends JPanel {

    private final PortfolioController portfolioController;
    private final NotificationService notificationService;
    private final UserSession userSession;

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

        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setOpaque(false);

        // Header with portfolio selection
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Performance Dashboard");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(200, 36));
        comboPortfolios.addActionListener(e -> refreshDashboard());
        headerPanel.add(comboPortfolios, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main dashboard Grid
        JPanel mainGrid = new JPanel(new GridBagLayout());
        mainGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // Metric cards
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.2;
        CardPanel cardValue = new CardPanel(new BorderLayout(6, 6));
        lblTotalValue = new JLabel("₹0.00", JLabel.CENTER);
        lblTotalValue.setFont(new Font("sansserif", Font.BOLD, 26));
        JLabel valTitle = new JLabel("Portfolio Value", JLabel.CENTER);
        valTitle.setFont(UIConstants.FONT_SUBTITLE);
        valTitle.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardValue.add(valTitle, BorderLayout.NORTH);
        cardValue.add(lblTotalValue, BorderLayout.CENTER);
        mainGrid.add(cardValue, gbc);

        gbc.gridx = 1;
        CardPanel cardInvested = new CardPanel(new BorderLayout(6, 6));
        lblTotalInvested = new JLabel("₹0.00", JLabel.CENTER);
        lblTotalInvested.setFont(new Font("sansserif", Font.BOLD, 26));
        JLabel invTitle = new JLabel("Invested Capital", JLabel.CENTER);
        invTitle.setFont(UIConstants.FONT_SUBTITLE);
        invTitle.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardInvested.add(invTitle, BorderLayout.NORTH);
        cardInvested.add(lblTotalInvested, BorderLayout.CENTER);
        mainGrid.add(cardInvested, gbc);

        gbc.gridx = 2;
        CardPanel cardProfit = new CardPanel(new BorderLayout(6, 6));
        lblProfitLoss = new JLabel("₹0.00 (0.00%)", JLabel.CENTER);
        lblProfitLoss.setFont(new Font("sansserif", Font.BOLD, 22));
        JLabel profitTitle = new JLabel("Profit / Loss", JLabel.CENTER);
        profitTitle.setFont(UIConstants.FONT_SUBTITLE);
        profitTitle.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardProfit.add(profitTitle, BorderLayout.NORTH);
        cardProfit.add(lblProfitLoss, BorderLayout.CENTER);
        mainGrid.add(cardProfit, gbc);

        // Chart section (left side bottom)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0.8;
        CardPanel chartCard = new CardPanel(new BorderLayout());
        JLabel chartTitle = new JLabel("Asset Allocation Breakdown", JLabel.LEFT);
        chartTitle.setFont(UIConstants.FONT_HEADER);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartWrapperPanel = new JPanel(new BorderLayout());
        chartWrapperPanel.setOpaque(false);
        chartCard.add(chartWrapperPanel, BorderLayout.CENTER);
        mainGrid.add(chartCard, gbc);

        // Alerts logs section (right side bottom)
        gbc.gridx = 2; gbc.gridwidth = 1;
        CardPanel alertsCard = new CardPanel(new BorderLayout());
        JLabel alertTitle = new JLabel("Milestones & Alerts", JLabel.LEFT);
        alertTitle.setFont(UIConstants.FONT_HEADER);
        alertsCard.add(alertTitle, BorderLayout.NORTH);

        alertsListPanel = new JPanel();
        alertsListPanel.setLayout(new BoxLayout(alertsListPanel, BoxLayout.Y_AXIS));
        alertsListPanel.setOpaque(false);
        JScrollPane alertScroll = new JScrollPane(alertsListPanel);
        alertScroll.setBorder(null);
        alertScroll.setOpaque(false);
        alertScroll.getViewport().setOpaque(false);
        alertsCard.add(alertScroll, BorderLayout.CENTER);
        mainGrid.add(alertsCard, gbc);

        add(mainGrid, BorderLayout.CENTER);
    }

    /**
     * Rebuild and reload combobox selections when user logs in.
     */
    public void setupDashboardData() {
        if (!userSession.isLoggedIn()) return;
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
                emptyLbl.setForeground(UIConstants.LIGHT_TEXT_MUTED);
                chartWrapperPanel.add(emptyLbl, BorderLayout.CENTER);
            } else {
                JFreeChart chart = createPieChart(summary.getCategoryAllocation());
                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setOpaque(false);
                chartPanel.setBackground(new Color(0,0,0,0));
                chartWrapperPanel.add(chartPanel, BorderLayout.CENTER);
            }
            chartWrapperPanel.revalidate();
            chartWrapperPanel.repaint();

            // Load Alerts / Notifications
            alertsListPanel.removeAll();
            List<Notification> alerts = notificationService.getNotificationsForUser(userSession.getCurrentUser().getId());
            if (alerts.isEmpty()) {
                JLabel noAlerts = new JLabel("No milestone alerts recorded.");
                noAlerts.setFont(UIConstants.FONT_BODY);
                noAlerts.setForeground(UIConstants.LIGHT_TEXT_MUTED);
                alertsListPanel.add(noAlerts);
            } else {
                for (Notification alert : alerts) {
                    JPanel alertRow = new JPanel(new BorderLayout(5, 5));
                    alertRow.setOpaque(false);
                    alertRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, 
                        ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER));
                    
                    JLabel alertText = new JLabel("<html><body style='width: 200px;'>" + alert.getMessage() + "</body></html>");
                    alertText.setFont(UIConstants.FONT_BODY);
                    alertText.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
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

        JFreeChart chart = ChartFactory.createPieChart(
                "",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);

        boolean dark = ThemeManager.isDarkMode();
        Color textCol = dark ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY;
        Color bgCol = dark ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD;

        chart.setBackgroundPaint(bgCol);
        plot.setLabelPaint(textCol);
        plot.setLabelBackgroundPaint(bgCol);
        plot.setLabelOutlinePaint(bgCol);
        chart.getLegend().setBackgroundPaint(bgCol);
        chart.getLegend().setItemPaint(textCol);

        return chart;
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
