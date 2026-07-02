package com.niveshcore360.view.report;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.controller.InvestmentController;
import com.niveshcore360.controller.PortfolioController;
import com.niveshcore360.dto.InvestmentDTO;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.entity.Transaction;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.util.CSVExporterUtil;
import com.niveshcore360.util.PDFGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Report View with warm-themed card form and styled export controls.
 */
@Component
public class ReportView extends JPanel {

    private final PortfolioController portfolioController;
    private final InvestmentController investmentController;
    private final UserSession userSession;

    private JComboBox<PortfolioComboItem> comboPortfolios;
    private JComboBox<String> comboReportType;
    private JComboBox<String> comboFormatType;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public ReportView(PortfolioController portfolioController,
                      InvestmentController investmentController,
                      UserSession userSession) {
        this.portfolioController = portfolioController;
        this.investmentController = investmentController;
        this.userSession = userSession;

        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Statement Generator");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        add(lblTitle, BorderLayout.NORTH);

        // Center card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        CardPanel formCard = new CardPanel(new GridBagLayout(), UIConstants.SPACE_LG);
        formCard.setPreferredSize(new Dimension(520, 380));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Select Portfolio:");
        l1.setFont(UIConstants.FONT_BODY);
        formCard.add(l1, gbc);
        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(280, 38));
        gbc.gridx = 1;
        formCard.add(comboPortfolios, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Statement Type:");
        l2.setFont(UIConstants.FONT_BODY);
        formCard.add(l2, gbc);
        comboReportType = new JComboBox<>(new String[]{
                "Portfolio Valuation Summary",
                "Transaction Ledger History"
        });
        comboReportType.setPreferredSize(new Dimension(280, 38));
        gbc.gridx = 1;
        formCard.add(comboReportType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("Export Format:");
        l3.setFont(UIConstants.FONT_BODY);
        formCard.add(l3, gbc);
        comboFormatType = new JComboBox<>(new String[]{"PDF Document", "CSV Spreadsheet"});
        comboFormatType.setPreferredSize(new Dimension(280, 38));
        gbc.gridx = 1;
        formCard.add(comboFormatType, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(24, 16, 10, 16);
        RoundedButton btnExport = new RoundedButton("Generate & Save Statement");
        btnExport.setPreferredSize(new Dimension(280, 44));
        formCard.add(btnExport, gbc);

        btnExport.addActionListener(e -> generateReport());

        centerWrapper.add(formCard);
        add(centerWrapper, BorderLayout.CENTER);
    }

    public void setupReportViewData() {
        if (!userSession.isLoggedIn()) return;
        comboPortfolios.removeAllItems();
        List<Portfolio> list = portfolioController.getPortfolios();
        for (Portfolio p : list) {
            comboPortfolios.addItem(new PortfolioComboItem(p.getId(), p.getName()));
        }
    }

    private void generateReport() {
        PortfolioComboItem selected = (PortfolioComboItem) comboPortfolios.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an active portfolio first.");
            return;
        }

        String reportType = (String) comboReportType.getSelectedItem();
        String formatType = (String) comboFormatType.getSelectedItem();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify File Destination");
        String extension = "PDF Document".equals(formatType) ? ".pdf" : ".csv";
        fileChooser.setSelectedFile(new File(selected.name.replace(" ", "_") + "_Statement" + extension));

        int choice = fileChooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = fileChooser.getSelectedFile();
        String path = targetFile.getAbsolutePath();
        if (!path.endsWith(extension)) {
            path += extension;
        }

        try {
            if ("Portfolio Valuation Summary".equals(reportType)) {
                List<InvestmentDTO> investments = investmentController.getInvestments(selected.id);
                String[] headers = {"Asset Type", "Symbol", "Description", "Units Held", "Purchase Price", "Current Price", "Cost Basis", "Current Valuation", "Gains/Losses"};
                List<String[]> dataRows = new ArrayList<>();
                for (InvestmentDTO inv : investments) {
                    dataRows.add(new String[]{
                            inv.getAssetType().toString(),
                            inv.getSymbol(),
                            inv.getName(),
                            inv.getQuantity().toString(),
                            currencyFormatter.format(inv.getPurchasePrice()),
                            currencyFormatter.format(inv.getCurrentPrice()),
                            currencyFormatter.format(inv.getCostBasis()),
                            currencyFormatter.format(inv.getCurrentValue()),
                            currencyFormatter.format(inv.getProfitLoss()) + " (" + inv.getProfitLossPercentage() + "%)"
                    });
                }

                if (extension.equals(".pdf")) {
                    PDFGeneratorUtil.generateReportPDF(
                            path,
                            "NiveshCore360 Portfolio Statement",
                            "Portfolio: " + selected.name + " | Date: " + LocalDateTime.now().format(dateTimeFormatter),
                            headers,
                            dataRows
                    );
                } else {
                    CSVExporterUtil.exportToCSV(path, headers, dataRows);
                }

            } else {
                List<Transaction> transactions = portfolioController.getTransactions(selected.id);
                String[] headers = {"ID", "Type", "Execution Date", "Total Value", "Details"};
                List<String[]> dataRows = new ArrayList<>();
                for (Transaction t : transactions) {
                    dataRows.add(new String[]{
                            t.getId().toString(),
                            t.getTransactionType().toString(),
                            t.getTransactionDate().format(dateTimeFormatter),
                            currencyFormatter.format(t.getAmount()),
                            t.getDescription() != null ? t.getDescription() : ""
                    });
                }

                if (extension.equals(".pdf")) {
                    PDFGeneratorUtil.generateReportPDF(
                            path,
                            "NiveshCore360 Transaction ledger",
                            "Portfolio: " + selected.name + " | Date: " + LocalDateTime.now().format(dateTimeFormatter),
                            headers,
                            dataRows
                    );
                } else {
                    CSVExporterUtil.exportToCSV(path, headers, dataRows);
                }
            }

            JOptionPane.showMessageDialog(this, "Statement generated successfully at:\n" + path, "Export Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to compile statement: " + ex.getMessage(), "Export Failure", JOptionPane.ERROR_MESSAGE);
        }
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
