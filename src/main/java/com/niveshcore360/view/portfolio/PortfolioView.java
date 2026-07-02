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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Portfolio View with premium styled table, warm-tinted search, and filter chips.
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

        JLabel lblTitle = new JLabel("Manage Portfolio Investments");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(200, 36));
        comboPortfolios.addActionListener(e -> reloadHoldings());
        headerPanel.add(comboPortfolios, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Content ────────────────────────────────────────────────
        JPanel mainContent = new JPanel(new BorderLayout(UIConstants.SPACE_SM, UIConstants.SPACE_SM));
        mainContent.setOpaque(false);

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

        JLabel filterLabel = new JLabel("Asset Type:");
        filterLabel.setFont(UIConstants.FONT_CAPTION);
        toolbarPanel.add(filterLabel);

        comboFilter = new JComboBox<>(new String[]{"All Assets", "Stocks", "Mutual Funds"});
        comboFilter.setPreferredSize(new Dimension(130, 36));
        comboFilter.addActionListener(e -> filterAndSearchData());
        toolbarPanel.add(comboFilter);

        RoundedButton btnAdd = new RoundedButton("Add Investment");
        btnAdd.setPreferredSize(new Dimension(140, 36));
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

        mainContent.add(toolbarPanel, BorderLayout.NORTH);

        // ─── Table Card ─────────────────────────────────────────────
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

        // Alternating row colors
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
                // Color-code P/L column
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

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
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
