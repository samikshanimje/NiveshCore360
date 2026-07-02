package com.niveshcore360.view;

import com.niveshcore360.components.LogoPainter;
import com.niveshcore360.components.NavButton;
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
import com.niveshcore360.view.chatbot.ChatbotView;
import com.niveshcore360.components.CommandPaletteDialog;
import java.awt.event.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary Application Window with premium forest-green sidebar and warm header.
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
    private final ChatbotView chatbotView;

    private CardLayout topCardLayout;
    private JPanel topContainer;

    private CardLayout workspaceCardLayout;
    private JPanel workspaceContainer;

    private JLabel lblUserBadge;
    private JLabel lblPageTitle;
    private NavButton btnAdminNav;

    private final List<NavButton> navButtons = new ArrayList<>();

    @Autowired
    public MainFrame(UserSession userSession,
                     LoginView loginView,
                     DashboardView dashboardView,
                     PortfolioView portfolioView,
                     GoalView goalView,
                     CalculatorsView calculatorsView,
                     ReportView reportView,
                     AdminView adminView,
                     ChatbotView chatbotView) {
        this.userSession = userSession;
        this.loginView = loginView;
        this.dashboardView = dashboardView;
        this.portfolioView = portfolioView;
        this.goalView = goalView;
        this.calculatorsView = calculatorsView;
        this.reportView = reportView;
        this.adminView = adminView;
        this.chatbotView = chatbotView;
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

        // Global Command Palette Hotkey (Cmd/Ctrl + K)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_K &&
                ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                
                SwingUtilities.invokeLater(() -> {
                    CommandPaletteDialog palette = new CommandPaletteDialog(this, this);
                    palette.setVisible(true);
                });
                return true;
            }
            return false;
        });

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

        // ─── SIDEBAR (Forest Green) ────────────────────────────────
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.FOREST_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 800));
        sidebar.setBorder(new EmptyBorder(16, 8, 16, 8));
        sidebar.setOpaque(false);

        // Brand header
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        LogoPainter logo = new LogoPainter(36);
        logo.setPreferredSize(new Dimension(36, 36));
        brandPanel.add(logo);
        JLabel brandLabel = new JLabel("NiveshCore360");
        brandLabel.setFont(UIConstants.FONT_SUBHEADING);
        brandLabel.setForeground(UIConstants.GOLD_ACCENT);
        brandPanel.add(brandLabel);
        sidebar.add(brandPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));

        // Navigation items
        NavButton btnDash = createNavItem("Dashboard", "dashboard");
        NavButton btnPort = createNavItem("Portfolio", "portfolio");
        NavButton btnGoal = createNavItem("Milestones", "goals");
        NavButton btnCalc = createNavItem("Calculators", "calculator");
        NavButton btnRep  = createNavItem("Statements", "statements");
        NavButton btnChat = createNavItem("AI Advisor", "ai");
        btnAdminNav = createNavItem("Admin Panel", "admin");
        btnAdminNav.setVisible(false);

        sidebar.add(btnDash);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnPort);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnGoal);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnCalc);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnRep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnChat);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnAdminNav);

        // Push remaining to bottom
        sidebar.add(Box.createVerticalGlue());

        // Divider
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(UIConstants.FOREST_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        // Bottom utility nav
        NavButton btnTheme  = createNavItem("Switch Theme", "theme");
        NavButton btnLogout = createNavItem("Sign Out", "logout");
        sidebar.add(btnTheme);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(btnLogout);

        shell.add(sidebar, BorderLayout.WEST);

        // ─── TOP HEADER BAR ────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                boolean dark = ThemeManager.isDarkMode();
                g.setColor(dark ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(dark ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        header.setPreferredSize(new Dimension(1000, UIConstants.HEADER_HEIGHT));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));
        header.setOpaque(false);

        lblPageTitle = new JLabel("Dashboard");
        lblPageTitle.setFont(UIConstants.FONT_HEADING);
        header.add(lblPageTitle, BorderLayout.WEST);

        // User badge with avatar circle
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        lblUserBadge = new JLabel("Guest Account");
        lblUserBadge.setFont(UIConstants.FONT_BODY);
        userPanel.add(lblUserBadge);

        // Painted avatar circle
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.GOLD_ACCENT);
                g2.fillOval(0, 0, 34, 34);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Dialog", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String initials = getInitials();
                int tx = (34 - fm.stringWidth(initials)) / 2;
                int ty = (34 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }

            private String getInitials() {
                String name = lblUserBadge.getText();
                if (name.contains("(")) name = name.substring(0, name.indexOf("(")).trim();
                String[] parts = name.split("\\s+");
                if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
                return name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(34, 34));
        avatarCircle.setOpaque(false);
        userPanel.add(avatarCircle);

        header.add(userPanel, BorderLayout.EAST);
        shell.add(header, BorderLayout.NORTH);

        // ─── WORKSPACE CARD PANEL ──────────────────────────────────
        workspaceCardLayout = new CardLayout();
        workspaceContainer = new JPanel(workspaceCardLayout);
        workspaceContainer.setOpaque(false);

        workspaceContainer.add(dashboardView, "DASHBOARD");
        workspaceContainer.add(portfolioView, "PORTFOLIO");
        workspaceContainer.add(goalView, "GOALS");
        workspaceContainer.add(calculatorsView, "CALCS");
        workspaceContainer.add(reportView, "REPORTS");
        workspaceContainer.add(chatbotView, "CHATBOT");
        workspaceContainer.add(adminView, "ADMIN");

        shell.add(workspaceContainer, BorderLayout.CENTER);

        // Wire Sidebar Events
        btnDash.addActionListener(e -> { setActiveNav(btnDash); showWorkspaceCard("DASHBOARD"); lblPageTitle.setText("Dashboard"); });
        btnPort.addActionListener(e -> { setActiveNav(btnPort); showWorkspaceCard("PORTFOLIO"); lblPageTitle.setText("Portfolio"); });
        btnGoal.addActionListener(e -> { setActiveNav(btnGoal); showWorkspaceCard("GOALS"); lblPageTitle.setText("Milestones"); });
        btnCalc.addActionListener(e -> { setActiveNav(btnCalc); showWorkspaceCard("CALCS"); lblPageTitle.setText("Calculators"); });
        btnRep.addActionListener(e  -> { setActiveNav(btnRep);  showWorkspaceCard("REPORTS"); lblPageTitle.setText("Statements"); });
        btnChat.addActionListener(e -> { setActiveNav(btnChat); showWorkspaceCard("CHATBOT"); lblPageTitle.setText("AI Advisor"); });
        btnAdminNav.addActionListener(e -> { setActiveNav(btnAdminNav); showWorkspaceCard("ADMIN"); lblPageTitle.setText("Admin Panel"); });

        btnTheme.addActionListener(e -> {
            ThemeManager.toggleTheme();
            SwingUtilities.updateComponentTreeUI(this);
        });

        btnLogout.addActionListener(e -> userSession.logout());

        // Set default active
        btnDash.setActive(true);

        return shell;
    }

    private NavButton createNavItem(String label, String iconType) {
        NavButton btn = new NavButton(label, iconType);
        btn.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        navButtons.add(btn);
        return btn;
    }

    private void setActiveNav(NavButton active) {
        for (NavButton btn : navButtons) {
            btn.setActive(btn == active);
        }
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
        } else if ("CHATBOT".equals(name)) {
            chatbotView.loadHistory();
        } else if ("ADMIN".equals(name)) {
            adminView.reloadAdminDiagnostics();
        }
    }

    @Override
    public void onSessionChanged(User user) {
        if (user != null) {
            // Authenticated: Initialize badges and admin views
            lblUserBadge.setText(user.getFullName() != null ? user.getFullName() + " (" + user.getUsername() + ")" : user.getUsername());

            boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
            btnAdminNav.setVisible(isAdmin);

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
