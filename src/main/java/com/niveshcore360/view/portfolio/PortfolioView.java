package com.niveshcore360.view.portfolio;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.controller.InvestmentController;
import com.niveshcore360.controller.PortfolioController;
import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.MutualFund;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.Stock;
import com.niveshcore360.security.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Portfolio View allowing full CRUD management of investment holdings (stocks/mutual funds).
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

        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setOpaque(false);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Manage Portfolio Investments");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(200, 36));
        comboPortfolios.addActionListener(e -> reloadHoldings());
        headerPanel.add(comboPortfolios, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Grid contents
        JPanel mainContent = new JPanel(new BorderLayout(12, 12));
        mainContent.setOpaque(false);

        // Toolbar Panel (Search + Filter + CRUD operations)
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbarPanel.setOpaque(false);

        toolbarPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 32));
        txtSearch.addCaretListener(e -> filterAndSearchData());
        toolbarPanel.add(txtSearch);

        toolbarPanel.add(new JLabel("Asset Type:"));
        comboFilter = new JComboBox<>(new String[]{"All Assets", "Stocks", "Mutual Funds"});
        comboFilter.setPreferredSize(new Dimension(130, 32));
        comboFilter.addActionListener(e -> filterAndSearchData());
        toolbarPanel.add(comboFilter);

        RoundedButton btnAdd = new RoundedButton("Add Investment");
        btnAdd.setPreferredSize(new Dimension(130, 32));
        btnAdd.addActionListener(e -> openInvestmentDialog(null));
        toolbarPanel.add(btnAdd);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setPreferredSize(new Dimension(80, 32));
        btnEdit.addActionListener(e -> editSelectedHolding());
        toolbarPanel.add(btnEdit);

        JButton btnDelete = new JButton("Liquidate");
        btnDelete.setPreferredSize(new Dimension(90, 32));
        btnDelete.addActionListener(e -> deleteSelectedHolding());
        toolbarPanel.add(btnDelete);

        mainContent.add(toolbarPanel, BorderLayout.NORTH);

        // JTable Card
        CardPanel tableCard = new CardPanel(new BorderLayout());
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Asset Type", "Symbol", "Company/Fund Name", "Qty", "Buy Price", "Current Price", "Current Value", "Profit/Loss"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblInvestments = new JTable(tableModel);
        tblInvestments.setRowHeight(28);
        tblInvestments.getTableHeader().setFont(UIConstants.FONT_BOLD);
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
            // Search validation
            boolean matchSearch = inv.getSymbol().toLowerCase().contains(searchStr)
                    || inv.getName().toLowerCase().contains(searchStr);

            // Filter validation
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
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Fields
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Asset Type:"), gbc);

        JComboBox<AssetType> comboType = new JComboBox<>(AssetType.values());
        gbc.gridx = 1;
        dialog.add(comboType, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Select Asset:"), gbc);

        JComboBox<Object> comboAssetList = new JComboBox<>();
        gbc.gridx = 1;
        dialog.add(comboAssetList, gbc);

        // Populate assets dynamically based on Type selection
        comboType.addActionListener(e -> {
            comboAssetList.removeAllItems();
            if (comboType.getSelectedItem() == AssetType.STOCK) {
                List<Stock> stocks = investmentController.getAvailableStocks();
                for (Stock s : stocks) {
                    comboAssetList.addItem(new AssetListItem(s.getId(), s.getTicker() + " - " + s.getCompanyName()));
                }
            } else {
                List<MutualFund> funds = investmentController.getAvailableMutualFunds();
                for (MutualFund mf : funds) {
                    comboAssetList.addItem(new AssetListItem(mf.getId(), mf.getFundName()));
                }
            }
        });

        // Initialize selection
        comboType.setSelectedItem(AssetType.STOCK);
        comboType.getActionListeners()[0].actionPerformed(null);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Quantity:"), gbc);

        JTextField txtQty = new JTextField("1.0");
        gbc.gridx = 1;
        dialog.add(txtQty, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Purchase Price:"), gbc);

        JTextField txtPrice = new JTextField("0.0");
        gbc.gridx = 1;
        dialog.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Purchase Date (YYYY-MM-DD):"), gbc);

        JTextField txtDate = new JTextField(LocalDate.now().format(dateFormatter));
        gbc.gridx = 1;
        dialog.add(txtDate, gbc);

        // If editing, lock Type/Asset selects and pre-fill prices
        if (editDto != null) {
            comboType.setSelectedItem(editDto.getAssetType());
            comboType.setEnabled(false);
            comboAssetList.setEnabled(false);
            txtQty.setText(editDto.getQuantity().toString());
            txtPrice.setText(editDto.getPurchasePrice().toString());
            txtDate.setText(editDto.getPurchaseDate().format(dateFormatter));
        }

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 12, 6, 12);
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
