package com.niveshcore360.view.admin;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.entity.AuditLog;
import com.niveshcore360.entity.User;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin View with premium metric cards and warm-themed data tables.
 */
@Component
public class AdminView extends JPanel {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final AuditLogService auditLogService;

    private JLabel lblTotalUsers;
    private JLabel lblTotalPortfolios;
    private JLabel lblTotalLogs;

    private JTable tblUsers;
    private DefaultTableModel modelUsers;
    private JTable tblLogs;
    private DefaultTableModel modelLogs;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public AdminView(UserRepository userRepository,
                     PortfolioRepository portfolioRepository,
                     AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.auditLogService = auditLogService;

        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Administrator Panel");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        RoundedButton btnRefresh = RoundedButton.secondary("Refresh Diagnostics");
        btnRefresh.setPreferredSize(new Dimension(180, 38));
        btnRefresh.addActionListener(e -> reloadAdminDiagnostics());
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Grid for diagnostic panels
        JPanel centerGrid = new JPanel(new GridBagLayout());
        centerGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // ─── Metric Cards ───────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.15;
        centerGrid.add(createMetricCard("System Profiles", lblTotalUsers = createMetricValue()), gbc);

        gbc.gridx = 1;
        centerGrid.add(createMetricCard("Active Portfolios", lblTotalPortfolios = createMetricValue()), gbc);

        gbc.gridx = 2;
        centerGrid.add(createMetricCard("Audit Logs", lblTotalLogs = createMetricValue()), gbc);

        // ─── Tabbed Data Tables ─────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weighty = 0.85;
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_BODY);

        // Users tab
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setOpaque(false);
        modelUsers = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Full Name", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = createStyledTable(modelUsers);
        usersPanel.add(new JScrollPane(tblUsers), BorderLayout.CENTER);
        tabbedPane.addTab("User Directory", usersPanel);

        // Logs tab
        JPanel logsPanel = new JPanel(new BorderLayout());
        logsPanel.setOpaque(false);
        modelLogs = new DefaultTableModel(new Object[]{"Log ID", "Auditor", "Action Executed", "Event Details", "Timestamp"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLogs = createStyledTable(modelLogs);
        logsPanel.add(new JScrollPane(tblLogs), BorderLayout.CENTER);
        tabbedPane.addTab("Audit Logs Ledger", logsPanel);

        centerGrid.add(tabbedPane, gbc);
        add(centerGrid, BorderLayout.CENTER);
    }

    private CardPanel createMetricCard(String title, JLabel valueLabel) {
        CardPanel card = new CardPanel(new BorderLayout(4, 8), UIConstants.SPACE_LG);

        JLabel titleLbl = new JLabel(title, JLabel.CENTER);
        titleLbl.setFont(UIConstants.FONT_CAPTION);
        titleLbl.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        card.add(titleLbl, BorderLayout.NORTH);

        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createMetricValue() {
        JLabel lbl = new JLabel("0", JLabel.CENTER);
        lbl.setFont(UIConstants.FONT_MONO);
        lbl.setForeground(UIConstants.GOLD_ACCENT);
        return lbl;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_BUTTON);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (isSelected) {
                    setBackground(UIConstants.GOLD_SUBTLE);
                } else if (row % 2 == 0) {
                    setBackground(ThemeManager.isDarkMode() ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD);
                } else {
                    setBackground(ThemeManager.isDarkMode() ? UIConstants.DARK_BG : UIConstants.LIGHT_BG);
                }
                setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
                return this;
            }
        });

        return table;
    }

    public void reloadAdminDiagnostics() {
        try {
            long userCount = userRepository.count();
            long portCount = portfolioRepository.count();
            List<AuditLog> logs = auditLogService.getLogs();

            lblTotalUsers.setText(String.valueOf(userCount));
            lblTotalPortfolios.setText(String.valueOf(portCount));
            lblTotalLogs.setText(String.valueOf(logs.size()));

            modelUsers.setRowCount(0);
            List<User> users = userRepository.findAll();
            for (User u : users) {
                modelUsers.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getFullName() != null ? u.getFullName() : "",
                        u.getRoles() == null || u.getRoles().isEmpty() ? "ROLE_USER" : u.getRoles().iterator().next().getName()
                });
            }

            modelLogs.setRowCount(0);
            for (AuditLog log : logs) {
                modelLogs.addRow(new Object[]{
                        log.getId(),
                        log.getUser() != null ? log.getUser().getUsername() : "System",
                        log.getAction(),
                        log.getDetails(),
                        log.getTimestamp().format(dateTimeFormatter)
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Diagnostics failure: " + ex.getMessage());
        }
    }
}
