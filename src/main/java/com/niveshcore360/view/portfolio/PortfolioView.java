package com.niveshcore360.view.portfolio;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.controller.InvestmentController;
import com.niveshcore360.controller.PortfolioController;
import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.Asset;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.security.UserSession;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Redesigned Portfolio View containing Holdings list, AI Health & Rebalancer metrics,
 * and Tax & Dividend calculators.
 */
@Component
public class PortfolioView extends JPanel {

    private final InvestmentController investmentController;
    private final PortfolioController portfolioController;
    private final UserSession userSession;

    private JTable tblInvestments;
    private DefaultTableModel tableModel;
    private JComboBox<PortfolioItem> comboPortfolios;
    private JTextField txtSearch;
    private JComboBox<String> comboFilter;
    private List<InvestmentDTO> currentList;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public PortfolioView(InvestmentController investmentController,
                         PortfolioController portfolioController,
                         UserSession userSession) {
        this.investmentController = investmentController;
        this.portfolioController = portfolioController;
        this.userSession = userSession;

        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        // ─── Header ─────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Investments Portfolio");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(200, 36));
        comboPortfolios.addActionListener(e -> reloadHoldings());
        headerPanel.add(comboPortfolios, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Tabs Layout ────────────────────────────────────────────
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_BODY);

        tabbedPane.addTab("Holdings Directory", createHoldingsPanel());
        tabbedPane.addTab("AI Health & Rebalancer", createHealthPanel());
        tabbedPane.addTab("Tax & Dividends", createTaxPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHoldingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACE_SM, UIConstants.SPACE_SM));
        panel.setOpaque(false);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbarPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.FONT_CAPTION);
        toolbarPanel.add(searchLabel);

        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(180, 36));
        txtSearch.setFont(UIConstants.FONT_BODY);
        txtSearch.addCaretListener(e -> filterAndSearchData());
        toolbarPanel.add(txtSearch);

        JLabel filterLabel = new JLabel("Asset:");
        filterLabel.setFont(UIConstants.FONT_CAPTION);
        toolbarPanel.add(filterLabel);

        comboFilter = new JComboBox<>(new String[]{"All Assets", "Stocks", "Mutual Funds"});
        comboFilter.setPreferredSize(new Dimension(130, 36));
        comboFilter.addActionListener(e -> filterAndSearchData());
        toolbarPanel.add(comboFilter);

        RoundedButton btnAdd = new RoundedButton("Add Asset");
        btnAdd.setPreferredSize(new Dimension(110, 36));
        btnAdd.addActionListener(e -> openInvestmentDialog(null));
        toolbarPanel.add(btnAdd);

        RoundedButton btnEdit = RoundedButton.secondary("Edit");
        btnEdit.setPreferredSize(new Dimension(80, 36));
        btnEdit.addActionListener(e -> editSelectedHolding());
        toolbarPanel.add(btnEdit);

        RoundedButton btnDelete = RoundedButton.danger("Liquidate");
        btnDelete.setPreferredSize(new Dimension(100, 36));
        btnDelete.addActionListener(e -> deleteSelectedHolding());
        toolbarPanel.add(btnDelete);

        panel.add(toolbarPanel, BorderLayout.NORTH);

        // Table Card
        CardPanel tableCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_SM);
        tableCard.setHoverLiftEnabled(false);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Asset Type", "Symbol", "Company/Fund Name", "Qty", "Buy Price", "Current Price", "Current Value", "Profit/Loss"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblInvestments = new JTable(tableModel);
        tblInvestments.setRowHeight(36);
        tblInvestments.setShowGrid(false);
        tblInvestments.setIntercellSpacing(new Dimension(0, 0));
        tblInvestments.setFont(UIConstants.FONT_BODY);
        tblInvestments.getTableHeader().setFont(UIConstants.FONT_BUTTON);
        tblInvestments.getTableHeader().setPreferredSize(new Dimension(0, 40));

        tblInvestments.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (isSelected) {
                    setBackground(UIConstants.GOLD_SUBTLE);
                } else if (row % 2 == 0) {
                    setBackground(ThemeManager.isDarkMode() ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD);
                } else {
                    setBackground(ThemeManager.isDarkMode() ? UIConstants.DARK_BG : UIConstants.LIGHT_BG);
                }
                
                if (column == 8 && value != null) {
                    String val = value.toString();
                    if (val.startsWith("-")) {
                        setForeground(UIConstants.LOSS_RED);
                    } else {
                        setForeground(UIConstants.PROFIT_GREEN);
                    }
                } else {
                    setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblInvestments);
        scrollPane.setBorder(null);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHealthPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // LEFT: Health Gauge & warnings
        CardPanel leftCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_LG);
        
        JPanel gaugeHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gaugeHeader.setOpaque(false);
        JLabel lblScoreTitle = new JLabel("Portfolio Health Score");
        lblScoreTitle.setFont(UIConstants.FONT_SUBHEADING);
        gaugeHeader.add(lblScoreTitle);
        leftCard.add(gaugeHeader, BorderLayout.NORTH);

        JPanel gaugeContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2, cy = getHeight() / 2 + 10;
                int r = 70;

                // Arc background
                g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, 180, -180);

                // Progress Arc
                g2.setColor(UIConstants.GOLD_ACCENT);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, 180, -165); // Mocks 92/100 score

                // Score text
                g2.setFont(new Font("Dialog", Font.BOLD, 28));
                g2.setColor(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
                String scoreText = "92";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(scoreText, cx - fm.stringWidth(scoreText) / 2, cy - 5);

                g2.setFont(UIConstants.FONT_CAPTION);
                g2.setColor(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
                g2.drawString("out of 100", cx - g2.getFontMetrics().stringWidth("out of 100") / 2, cy + 15);

                g2.dispose();
            }
        };
        gaugeContent.setOpaque(false);
        leftCard.add(gaugeContent, BorderLayout.CENTER);

        // Checklist at bottom of left card
        JPanel checkList = new JPanel(new GridLayout(5, 1, 0, 6));
        checkList.setOpaque(false);
        checkList.add(createCheckItem("✔ High asset diversification maintained", UIConstants.PROFIT_GREEN));
        checkList.add(createCheckItem("✔ Emergency reserves successfully mapped", UIConstants.PROFIT_GREEN));
        checkList.add(createCheckItem("✔ SIP goals tracking with 85%+ projection probability", UIConstants.PROFIT_GREEN));
        checkList.add(createCheckItem("⚠ Gold asset class allocation target missing", UIConstants.WARN_AMBER));
        checkList.add(createCheckItem("⚠ Low allocation to short-term liquid funds", UIConstants.WARN_AMBER));
        
        leftCard.add(checkList, BorderLayout.SOUTH);

        // RIGHT: Allocation charts & rebalancer recommendations
        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 16));
        rightCol.setOpaque(false);

        // Rebalancer card
        CardPanel rebalanceCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_MD);
        JLabel rebalanceTitle = new JLabel("AI Rebalance Recommendations");
        rebalanceTitle.setFont(UIConstants.FONT_SUBHEADING);
        rebalanceTitle.setForeground(UIConstants.GOLD_ACCENT);
        rebalanceCard.add(rebalanceTitle, BorderLayout.NORTH);

        JTextArea rebalanceText = new JTextArea(
            "● Overexposure detected in technology sector (INFY/TCS is 42% of holdings).\n" +
            "● ACTION: Sell ₹25,000 equivalent units of technology assets to lock in gains.\n" +
            "● ACTION: Buy ₹15,000 Sovereign Gold Bonds (SGB) or Gold ETF to hedge inflation.\n" +
            "● ACTION: Direct remaining ₹10,000 into short-term debt index fund reserves."
        );
        rebalanceText.setFont(UIConstants.FONT_BODY);
        rebalanceText.setEditable(false);
        rebalanceText.setOpaque(false);
        rebalanceText.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        rebalanceText.setLineWrap(true);
        rebalanceText.setWrapStyleWord(true);
        rebalanceCard.add(rebalanceText, BorderLayout.CENTER);
        rightCol.add(rebalanceCard);

        // Sector allocation chart card
        CardPanel chartCard = new CardPanel(new BorderLayout(), UIConstants.SPACE_SM);
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("IT & Services", 42);
        dataset.setValue("Banking & Finance", 28);
        dataset.setValue("Healthcare", 15);
        dataset.setValue("Energy & Auto", 10);
        dataset.setValue("Commodities (Gold)", 5);

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);

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

        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        cp.setBackground(new Color(0, 0, 0, 0));
        chartCard.add(cp, BorderLayout.CENTER);
        rightCol.add(chartCard);

        mainPanel.add(leftCard);
        mainPanel.add(rightCol);

        return mainPanel;
    }

    private JLabel createCheckItem(String text, Color color) {
        JLabel item = new JLabel(text);
        item.setFont(UIConstants.FONT_CAPTION);
        item.setForeground(color);
        return item;
    }

    private JPanel createTaxPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // LEFT: Dividend Tracker
        CardPanel divCard = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel divTitle = new JLabel("Dividend Yield Tracker", JLabel.CENTER);
        divTitle.setFont(UIConstants.FONT_SUBHEADING);
        divTitle.setForeground(UIConstants.GOLD_ACCENT);
        divCard.add(divTitle, gbc);

        gbc.gridy = 1;
        JLabel lblTotalDiv = new JLabel("Total Dividends Earned: ₹14,250", JLabel.CENTER);
        lblTotalDiv.setFont(UIConstants.FONT_HEADER);
        divCard.add(lblTotalDiv, gbc);

        gbc.gridy = 2;
        JLabel lblYield = new JLabel("Portfolio Dividend Yield: 1.85%", JLabel.CENTER);
        lblYield.setFont(UIConstants.FONT_BODY);
        lblYield.setForeground(UIConstants.PROFIT_GREEN);
        divCard.add(lblYield, gbc);

        gbc.gridy = 3;
        JLabel lblNextDate = new JLabel("Upcoming Payout: INFY (₹18.00/share) on 2026-07-25", JLabel.CENTER);
        lblNextDate.setFont(UIConstants.FONT_CAPTION);
        lblNextDate.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        divCard.add(lblNextDate, gbc);

        mainPanel.add(divCard);

        // RIGHT: Capital Gains Calculator Form
        CardPanel taxCard = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        gbc.gridy = 0;
        JLabel taxTitle = new JLabel("Capital Gains Tax Estimator", JLabel.CENTER);
        taxTitle.setFont(UIConstants.FONT_SUBHEADING);
        taxTitle.setForeground(UIConstants.GOLD_ACCENT);
        taxCard.add(taxTitle, gbc);

        gbc.gridy = 1;
        taxCard.add(new JLabel("Selling Value (₹):"), gbc);
        JTextField txtSellVal = new JTextField("150000");
        txtSellVal.setFont(UIConstants.FONT_BODY);
        gbc.gridy = 2;
        taxCard.add(txtSellVal, gbc);

        gbc.gridy = 3;
        taxCard.add(new JLabel("Original Cost Basis (₹):"), gbc);
        JTextField txtCostVal = new JTextField("120000");
        txtCostVal.setFont(UIConstants.FONT_BODY);
        gbc.gridy = 4;
        taxCard.add(txtCostVal, gbc);

        gbc.gridy = 5;
        JComboBox<String> comboPeriod = new JComboBox<>(new String[]{"Long Term (> 1 Year)", "Short Term (< 1 Year)"});
        comboPeriod.setFont(UIConstants.FONT_BODY);
        taxCard.add(comboPeriod, gbc);

        gbc.gridy = 6;
        JLabel lblTaxRes = new JLabel("Tax Payable: ₹0.00", JLabel.CENTER);
        lblTaxRes.setFont(UIConstants.FONT_HEADER);
        lblTaxRes.setForeground(UIConstants.LOSS_RED);
        taxCard.add(lblTaxRes, gbc);

        RoundedButton btnTaxCalc = new RoundedButton("Calculate Gain Taxes");
        btnTaxCalc.addActionListener(e -> {
            try {
                BigDecimal sell = new BigDecimal(txtSellVal.getText().trim());
                BigDecimal cost = new BigDecimal(txtCostVal.getText().trim());
                BigDecimal gain = sell.subtract(cost);
                if (gain.compareTo(BigDecimal.ZERO) <= 0) {
                    lblTaxRes.setText("No Gains: ₹0.00");
                    lblTaxRes.setForeground(UIConstants.PROFIT_GREEN);
                } else {
                    boolean isLtcg = comboPeriod.getSelectedIndex() == 0;
                    BigDecimal rate = isLtcg ? new BigDecimal("0.10") : new BigDecimal("0.15"); // 10% LTCG, 15% STCG
                    BigDecimal tax = gain.multiply(rate);
                    lblTaxRes.setText("Tax Payable (" + (isLtcg ? "10% LTCG" : "15% STCG") + "): ₹" + tax.setScale(2, BigDecimal.ROUND_HALF_UP));
                    lblTaxRes.setForeground(UIConstants.LOSS_RED);
                }
            } catch (Exception ex) {
                lblTaxRes.setText("Calculation Error");
            }
        });
        gbc.gridy = 7;
        taxCard.add(btnTaxCalc, gbc);

        mainPanel.add(taxCard);

        return mainPanel;
    }

    public void setupPortfolioViewData() {
        if (!userSession.isLoggedIn()) return;
        comboPortfolios.removeAllItems();
        List<Portfolio> list = portfolioController.getPortfolios();
        for (Portfolio p : list) {
            comboPortfolios.addItem(new PortfolioItem(p.getId(), p.getName()));
        }
        reloadHoldings();
    }

    private void reloadHoldings() {
        PortfolioItem selected = (PortfolioItem) comboPortfolios.getSelectedItem();
        if (selected == null) {
            tableModel.setRowCount(0);
            return;
        }

        try {
            currentList = investmentController.getInvestments(selected.id);
            filterAndSearchData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load holdings: " + ex.getMessage());
        }
    }

    private void filterAndSearchData() {
        if (currentList == null) return;

        tableModel.setRowCount(0);
        String searchStr = txtSearch.getText().toLowerCase().trim();
        String filterStr = (String) comboFilter.getSelectedItem();

        for (InvestmentDTO inv : currentList) {
            boolean matchSearch = inv.getSymbol().toLowerCase().contains(searchStr)
                    || inv.getName().toLowerCase().contains(searchStr);

            boolean matchFilter = true;
            if ("Stocks".equals(filterStr)) {
                matchFilter = inv.getAssetType() == AssetType.STOCK;
            } else if ("Mutual Funds".equals(filterStr)) {
                matchFilter = inv.getAssetType() == AssetType.MUTUAL_FUND;
            }

            if (matchSearch && matchFilter) {
                tableModel.addRow(new Object[]{
                        inv.getId(),
                        inv.getAssetType(),
                        inv.getSymbol(),
                        inv.getName(),
                        inv.getQuantity(),
                        currencyFormatter.format(inv.getPurchasePrice()),
                        currencyFormatter.format(inv.getCurrentPrice()),
                        currencyFormatter.format(inv.getCurrentValue()),
                        inv.getProfitLoss() + " (" + inv.getProfitLossPercentage() + "%)"
                });
            }
        }
    }

    private void editSelectedHolding() {
        int row = tblInvestments.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an investment row from the table first.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        InvestmentDTO selectedDto = currentList.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
        if (selectedDto != null) {
            openInvestmentDialog(selectedDto);
        }
    }

    private void deleteSelectedHolding() {
        int row = tblInvestments.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an investment row to liquidate.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to liquidate this holding?", "Confirm Liquidation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                investmentController.deleteInvestment(id);
                reloadHoldings();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete investment: " + ex.getMessage());
            }
        }
    }

    private void openInvestmentDialog(InvestmentDTO editDto) {
        PortfolioItem selected = (PortfolioItem) comboPortfolios.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please create/select a portfolio first.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editDto == null ? "Add New Holding" : "Modify Holding", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Asset Type:");
        lbl1.setFont(UIConstants.FONT_BODY);
        dialog.add(lbl1, gbc);

        JComboBox<AssetType> comboType = new JComboBox<>(AssetType.values());
        gbc.gridx = 1;
        dialog.add(comboType, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lbl2 = new JLabel("Select Asset:");
        lbl2.setFont(UIConstants.FONT_BODY);
        dialog.add(lbl2, gbc);

        JComboBox<Object> comboAssetList = new JComboBox<>();
        gbc.gridx = 1;
        dialog.add(comboAssetList, gbc);

        comboType.addActionListener(e -> {
            comboAssetList.removeAllItems();
            if (comboType.getSelectedItem() == AssetType.STOCK) {
                List<Asset> stocks = investmentController.getAvailableStocks();
                for (Asset s : stocks) {
                    comboAssetList.addItem(new AssetListItem(s.getId(), s.getSymbol() + " - " + s.getName()));
                }
            } else if (comboType.getSelectedItem() == AssetType.MUTUAL_FUND) {
                List<Asset> funds = investmentController.getAvailableMutualFunds();
                for (Asset mf : funds) {
                    comboAssetList.addItem(new AssetListItem(mf.getId(), mf.getName()));
                }
            }
        });

        comboType.setSelectedItem(AssetType.STOCK);
        comboType.getActionListeners()[0].actionPerformed(null);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Quantity:");
        lbl3.setFont(UIConstants.FONT_BODY);
        dialog.add(lbl3, gbc);
        JTextField txtQty = new JTextField("1.0");
        txtQty.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtQty, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lbl4 = new JLabel("Purchase Price:");
        lbl4.setFont(UIConstants.FONT_BODY);
        dialog.add(lbl4, gbc);
        JTextField txtPrice = new JTextField("0.0");
        txtPrice.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lbl5 = new JLabel("Purchase Date (YYYY-MM-DD):");
        lbl5.setFont(UIConstants.FONT_BODY);
        dialog.add(lbl5, gbc);
        JTextField txtDate = new JTextField(LocalDate.now().format(dateFormatter));
        txtDate.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtDate, gbc);

        if (editDto != null) {
            comboType.setSelectedItem(editDto.getAssetType());
            comboType.setEnabled(false);
            comboAssetList.setEnabled(false);
            txtQty.setText(editDto.getQuantity().toString());
            txtPrice.setText(editDto.getPurchasePrice().toString());
            txtDate.setText(editDto.getPurchaseDate().format(dateFormatter));
        }

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 16, 8, 16);
        RoundedButton btnSave = new RoundedButton("Save Record");
        dialog.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            try {
                BigDecimal qty = new BigDecimal(txtQty.getText().trim());
                BigDecimal price = new BigDecimal(txtPrice.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), dateFormatter);
                AssetListItem asset = (AssetListItem) comboAssetList.getSelectedItem();

                if (editDto == null) {
                    if (asset == null) throw new IllegalArgumentException("Asset selection is required.");
                    investmentController.addInvestment(selected.id, (AssetType) comboType.getSelectedItem(), asset.id, qty, price, date);
                } else {
                    investmentController.editInvestment(editDto.getId(), qty, price, date);
                }

                dialog.dispose();
                reloadHoldings();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Form Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private static class PortfolioItem {
        final Long id;
        final String name;

        PortfolioItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class AssetListItem {
        final Long id;
        final String label;

        AssetListItem(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
