package com.niveshcore360.view;

import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.entity.Role;
import com.niveshcore360.entity.User;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.view.admin.AdminView;
import com.niveshcore360.view.calculator.CalculatorsView;
import com.niveshcore360.view.dashboard.DashboardView;
import com.niveshcore360.view.goal.GoalView;
import com.niveshcore360.view.login.LoginView;
import com.niveshcore360.view.portfolio.PortfolioView;
import com.niveshcore360.view.report.ReportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Primary Application Window coordinating between authentication and workspace dashboards.
 */
@Component
public class MainFrame extends JFrame implements UserSession.SessionListener {

    private final UserSession userSession;
    private final LoginView loginView;
    private final DashboardView dashboardView;
    private final PortfolioView portfolioView;
    private final GoalView goalView;
    private final CalculatorsView calculatorsView;
    private final ReportView reportView;
    private final AdminView adminView;

    private CardLayout topCardLayout;
    private JPanel topContainer;

    private CardLayout workspaceCardLayout;
    private JPanel workspaceContainer;

    private JLabel lblUserBadge;
    private JButton btnAdminNav;

    @Autowired
    public MainFrame(UserSession userSession,
                     LoginView loginView,
                     DashboardView dashboardView,
                     PortfolioView portfolioView,
                     GoalView goalView,
                     CalculatorsView calculatorsView,
                     ReportView reportView,
                     AdminView adminView) {
        this.userSession = userSession;
        this.loginView = loginView;
        this.dashboardView = dashboardView;
        this.portfolioView = portfolioView;
        this.goalView = goalView;
        this.calculatorsView = calculatorsView;
        this.reportView = reportView;
        this.adminView = adminView;
    }

    @PostConstruct
    public void init() {
        setTitle("NiveshCore360 - Investment Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);

        // Register session state observer
        userSession.addListener(this);

        // Top level switcher (Authentication vs Workspace)
        topCardLayout = new CardLayout();
        topContainer = new JPanel(topCardLayout);
        topContainer.setOpaque(false);

        // Workspace Main Container
        JPanel workspaceShell = createWorkspaceShell();

        topContainer.add(loginView, "LOGIN");
        topContainer.add(workspaceShell, "WORKSPACE");

        add(topContainer);

        // Default screen
        topCardLayout.show(topContainer, "LOGIN");
    }

    private JPanel createWorkspaceShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setOpaque(false);

        // 1. Sidebar Navigation
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(220, 800));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.DARK_BORDER));
        
        // Define navigation buttons
        JButton btnDash = createNavButton("Dashboard");
        JButton btnPort = createNavButton("Portfolio");
        JButton btnGoal = createNavButton("Milestones");
        JButton btnCalc = createNavButton("Calculators");
        JButton btnRep = createNavButton("Statements");
        btnAdminNav = createNavButton("Admin Panel");
        btnAdminNav.setVisible(false); // Hidden by default, toggled upon user role on login

        JButton btnTheme = createNavButton("Switch Theme");
        JButton btnLogout = createNavButton("Sign Out");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Branding Title
        JLabel brand = new JLabel("NIVESH CORE", JLabel.CENTER);
        brand.setFont(new Font("sansserif", Font.BOLD, 18));
        brand.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        sidebar.add(brand, gbc);

        gbc.gridy = 1; sidebar.add(btnDash, gbc);
        gbc.gridy = 2; sidebar.add(btnPort, gbc);
        gbc.gridy = 3; sidebar.add(btnGoal, gbc);
        gbc.gridy = 4; sidebar.add(btnCalc, gbc);
        gbc.gridy = 5; sidebar.add(btnRep, gbc);
        gbc.gridy = 6; sidebar.add(btnAdminNav, gbc);

        // Sidebar Bottom utility items
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        sidebar.add(Box.createGlue(), gbc); // vertical push spacer

        gbc.weighty = 0.0;
        gbc.gridy = 8; sidebar.add(btnTheme, gbc);
        gbc.gridy = 9; sidebar.add(btnLogout, gbc);

        shell.add(sidebar, BorderLayout.WEST);

        // 2. Top Header status bar
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 55));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.DARK_BORDER));
        
        lblUserBadge = new JLabel("Guest Account", JLabel.RIGHT);
        lblUserBadge.setFont(UIConstants.FONT_HEADER);
        lblUserBadge.setBorder(new EmptyBorder(0, 0, 0, 20));
        header.add(lblUserBadge, BorderLayout.EAST);
        shell.add(header, BorderLayout.NORTH);

        // 3. Workspace card panel
        workspaceCardLayout = new CardLayout();
        workspaceContainer = new JPanel(workspaceCardLayout);
        workspaceContainer.setOpaque(false);

        workspaceContainer.add(dashboardView, "DASHBOARD");
        workspaceContainer.add(portfolioView, "PORTFOLIO");
        workspaceContainer.add(goalView, "GOALS");
        workspaceContainer.add(calculatorsView, "CALCS");
        workspaceContainer.add(reportView, "REPORTS");
        workspaceContainer.add(adminView, "ADMIN");

        shell.add(workspaceContainer, BorderLayout.CENTER);

        // Wire Sidebar Events
        btnDash.addActionListener(e -> showWorkspaceCard("DASHBOARD"));
        btnPort.addActionListener(e -> showWorkspaceCard("PORTFOLIO"));
        btnGoal.addActionListener(e -> showWorkspaceCard("GOALS"));
        btnCalc.addActionListener(e -> showWorkspaceCard("CALCS"));
        btnRep.addActionListener(e -> showWorkspaceCard("REPORTS"));
        btnAdminNav.addActionListener(e -> showWorkspaceCard("ADMIN"));

        btnTheme.addActionListener(e -> {
            ThemeManager.toggleTheme();
            SwingUtilities.updateComponentTreeUI(this);
        });

        btnLogout.addActionListener(e -> userSession.logout());

        return shell;
    }

    private JButton createNavButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(UIConstants.FONT_HEADER);
        btn.setPreferredSize(new Dimension(180, 36));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showWorkspaceCard(String name) {
        workspaceCardLayout.show(workspaceContainer, name);
        // Refresh appropriate view content upon activation
        if ("DASHBOARD".equals(name)) {
            dashboardView.refreshDashboard();
        } else if ("PORTFOLIO".equals(name)) {
            portfolioView.setupPortfolioViewData();
        } else if ("GOALS".equals(name)) {
            goalView.setupGoalViewData();
        } else if ("REPORTS".equals(name)) {
            reportView.setupReportViewData();
        } else if ("ADMIN".equals(name)) {
            adminView.reloadAdminDiagnostics();
        }
    }

    @Override
    public void onSessionChanged(User user) {
        if (user != null) {
            // Authenticated: Initialize badges and admin views
            lblUserBadge.setText(user.getFullName() != null ? user.getFullName() + " (" + user.getUsername() + ")" : user.getUsername());
            btnAdminNav.setVisible(user.getRole() == Role.ADMIN);

            topCardLayout.show(topContainer, "WORKSPACE");
            showWorkspaceCard("DASHBOARD");

            // Setup sub-views initial content
            dashboardView.setupDashboardData();
        } else {
            // Logged out
            lblUserBadge.setText("Guest Account");
            btnAdminNav.setVisible(false);
            topCardLayout.show(topContainer, "LOGIN");
            loginView.showCard("LOGIN");
        }
    }
}
