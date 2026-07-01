package com.niveshcore360.view.admin;

import com.niveshcore360.components.CardPanel;
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
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin View displaying user profiles lists and system diagnostic audit logs.
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

        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Administrator Panel");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Diagnostics");
        btnRefresh.setPreferredSize(new Dimension(160, 36));
        btnRefresh.addActionListener(e -> reloadAdminDiagnostics());
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Grid for diagnostic panels
        JPanel centerGrid = new JPanel(new GridBagLayout());
        centerGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // Metric Counts
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.15;
        CardPanel cardUsers = new CardPanel(new BorderLayout());
        lblTotalUsers = new JLabel("0", JLabel.CENTER);
        lblTotalUsers.setFont(new Font("sansserif", Font.BOLD, 24));
        JLabel titleUsers = new JLabel("System Profiles", JLabel.CENTER);
        titleUsers.setFont(UIConstants.FONT_SUBTITLE);
        titleUsers.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardUsers.add(titleUsers, BorderLayout.NORTH);
        cardUsers.add(lblTotalUsers, BorderLayout.CENTER);
        centerGrid.add(cardUsers, gbc);

        gbc.gridx = 1;
        CardPanel cardPortfolios = new CardPanel(new BorderLayout());
        lblTotalPortfolios = new JLabel("0", JLabel.CENTER);
        lblTotalPortfolios.setFont(new Font("sansserif", Font.BOLD, 24));
        JLabel titlePorts = new JLabel("Active Portfolios", JLabel.CENTER);
        titlePorts.setFont(UIConstants.FONT_SUBTITLE);
        titlePorts.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardPortfolios.add(titlePorts, BorderLayout.NORTH);
        cardPortfolios.add(lblTotalPortfolios, BorderLayout.CENTER);
        centerGrid.add(cardPortfolios, gbc);

        gbc.gridx = 2;
        CardPanel cardLogs = new CardPanel(new BorderLayout());
        lblTotalLogs = new JLabel("0", JLabel.CENTER);
        lblTotalLogs.setFont(new Font("sansserif", Font.BOLD, 24));
        JLabel titleLogs = new JLabel("Audit Logs Tracked", JLabel.CENTER);
        titleLogs.setFont(UIConstants.FONT_SUBTITLE);
        titleLogs.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        cardLogs.add(titleLogs, BorderLayout.NORTH);
        cardLogs.add(lblTotalLogs, BorderLayout.CENTER);
        centerGrid.add(cardLogs, gbc);

        // Tabbed Panel for User List and Audit Logs
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weighty = 0.85;
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Users Tab
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setOpaque(false);
        modelUsers = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Full Name", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = new JTable(modelUsers);
        tblUsers.setRowHeight(26);
        usersPanel.add(new JScrollPane(tblUsers), BorderLayout.CENTER);
        tabbedPane.addTab("User Directory", usersPanel);

        // 2. Logs Tab
        JPanel logsPanel = new JPanel(new BorderLayout());
        logsPanel.setOpaque(false);
        modelLogs = new DefaultTableModel(new Object[]{"Log ID", "Auditor", "Action Executed", "Event Details", "Timestamp"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLogs = new JTable(modelLogs);
        tblLogs.setRowHeight(26);
        logsPanel.add(new JScrollPane(tblLogs), BorderLayout.CENTER);
        tabbedPane.addTab("Audit Logs Ledger", logsPanel);

        centerGrid.add(tabbedPane, gbc);
        add(centerGrid, BorderLayout.CENTER);
    }

    public void reloadAdminDiagnostics() {
        try {
            // Stats
            long userCount = userRepository.count();
            long portCount = portfolioRepository.count();
            List<AuditLog> logs = auditLogService.getLogs();

            lblTotalUsers.setText(String.valueOf(userCount));
            lblTotalPortfolios.setText(String.valueOf(portCount));
            lblTotalLogs.setText(String.valueOf(logs.size()));

            // User directory grid
            modelUsers.setRowCount(0);
            List<User> users = userRepository.findAll();
            for (User u : users) {
                modelUsers.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getFullName() != null ? u.getFullName() : "",
                        u.getRole().toString()
                });
            }

            // Audit logs grid
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
