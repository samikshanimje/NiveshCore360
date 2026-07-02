package com.niveshcore360.view.login;

import com.niveshcore360.controller.AuthController;
import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.LogoPainter;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Authentication view with premium two-panel split layout.
 * Left: Forest green branding panel. Right: Warm ivory auth card.
 */
@Component
public class LoginView extends JPanel {

    private final AuthController authController;
    private final CardLayout cardLayout;
    private final JPanel containerPanel;

    @Autowired
    public LoginView(AuthController authController) {
        this.authController = authController;
        this.cardLayout = new CardLayout();
        this.containerPanel = new JPanel(cardLayout);
        containerPanel.setOpaque(false);

        setLayout(new BorderLayout());
        setOpaque(false);

        // Sub-panels
        JPanel loginCard = createLoginCard();
        JPanel registerCard = createRegisterCard();
        JPanel forgotCard = createForgotCard();
        JPanel onboardingCard = createOnboardingCard();

        containerPanel.add(loginCard, "LOGIN");
        containerPanel.add(registerCard, "REGISTER");
        containerPanel.add(forgotCard, "FORGOT");
        containerPanel.add(onboardingCard, "ONBOARDING");

        add(containerPanel, BorderLayout.CENTER);
        showCard("LOGIN");
    }

    public void showCard(String name) {
        cardLayout.show(containerPanel, name);
    }

    // ─── LOGIN CARD (Two-Panel Split) ────────────────────────────────
    private JPanel createLoginCard() {
        JPanel split = new JPanel(new GridLayout(1, 2));
        split.setOpaque(false);

        // LEFT: Branding panel
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Forest green background
                g2.setColor(UIConstants.FOREST_PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative subtle growth lines
                g2.setColor(UIConstants.FOREST_LIGHT);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                Path2D line1 = new Path2D.Double();
                line1.moveTo(w * 0.1, h * 0.85);
                line1.lineTo(w * 0.3, h * 0.7);
                line1.lineTo(w * 0.5, h * 0.75);
                line1.lineTo(w * 0.7, h * 0.55);
                line1.lineTo(w * 0.9, h * 0.4);
                g2.draw(line1);
                Path2D line2 = new Path2D.Double();
                line2.moveTo(w * 0.1, h * 0.92);
                line2.lineTo(w * 0.35, h * 0.8);
                line2.lineTo(w * 0.55, h * 0.82);
                line2.lineTo(w * 0.75, h * 0.65);
                line2.lineTo(w * 0.9, h * 0.5);
                g2.draw(line2);
                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);

        LogoPainter logo = new LogoPainter(90);
        logo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        brandContent.add(logo);
        brandContent.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel brandName = new JLabel("NiveshCore360");
        brandName.setFont(UIConstants.FONT_DISPLAY);
        brandName.setForeground(UIConstants.GOLD_ACCENT);
        brandName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        brandContent.add(brandName);
        brandContent.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel tagline = new JLabel("Your AI-powered wealth companion");
        tagline.setFont(UIConstants.FONT_BODY);
        tagline.setForeground(UIConstants.WARM_IVORY);
        tagline.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        brandContent.add(tagline);
        brandContent.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel features = new JLabel("● AI Advisory    ● Risk Analytics    ● Goal Planner");
        features.setFont(UIConstants.FONT_CAPTION);
        features.setForeground(UIConstants.CHAMPAGNE);
        features.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        brandContent.add(features);

        leftPanel.add(brandContent);

        // RIGHT: Login form
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.WARM_IVORY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setOpaque(false);

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(0, 40, 0, 40));
        formWrapper.setMaximumSize(new Dimension(380, 500));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UIConstants.FONT_DISPLAY);
        title.setForeground(UIConstants.LIGHT_TEXT_PRIMARY);
        title.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(title);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel subtitle = new JLabel("Invest smarter. Grow consistently.");
        subtitle.setFont(UIConstants.FONT_CAPTION);
        subtitle.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        subtitle.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(subtitle);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 28)));

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIConstants.FONT_CAPTION);
        lblUser.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        lblUser.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(lblUser);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 4)));

        JTextField txtUser = new JTextField();
        txtUser.setFont(UIConstants.FONT_BODY);
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUser.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(txtUser);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 16)));

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIConstants.FONT_CAPTION);
        lblPass.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        lblPass.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(lblPass);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 4)));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(UIConstants.FONT_BODY);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(txtPass);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        // Remember me + Forgot
        JPanel rememberRow = new JPanel(new BorderLayout());
        rememberRow.setOpaque(false);
        rememberRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        rememberRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JCheckBox chkRemember = new JCheckBox("Remember me");
        chkRemember.setFont(UIConstants.FONT_CAPTION);
        chkRemember.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        chkRemember.setOpaque(false);
        rememberRow.add(chkRemember, BorderLayout.WEST);

        JButton btnGoForgot = new JButton("Forgot Password?");
        btnGoForgot.setBorderPainted(false);
        btnGoForgot.setContentAreaFilled(false);
        btnGoForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoForgot.setFont(UIConstants.FONT_CAPTION);
        btnGoForgot.setForeground(UIConstants.GOLD_ACCENT);
        rememberRow.add(btnGoForgot, BorderLayout.EAST);

        formWrapper.add(rememberRow);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        // Error label
        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_CAPTION);
        lblError.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(lblError);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 8)));

        // Login button
        RoundedButton btnLogin = new RoundedButton("Sign In");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        formWrapper.add(btnLogin);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create account link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);
        linkPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JLabel linkText = new JLabel("Don't have an account?");
        linkText.setFont(UIConstants.FONT_CAPTION);
        linkText.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        JButton btnGoRegister = new JButton("Create Account");
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.setFont(UIConstants.FONT_CAPTION);
        btnGoRegister.setForeground(UIConstants.GOLD_ACCENT);
        linkPanel.add(linkText);
        linkPanel.add(btnGoRegister);
        formWrapper.add(linkPanel);

        rightPanel.add(formWrapper);

        // Action Handlers (preserved exactly)
        btnGoRegister.addActionListener(e -> showCard("REGISTER"));
        btnGoForgot.addActionListener(e -> showCard("FORGOT"));
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please enter username and password.");
                return;
            }
            try {
                authController.login(user, pass);
                lblError.setText("");
                txtUser.setText("");
                txtPass.setText("");
            } catch (Exception ex) {
                lblError.setText(ex.getMessage());
            }
        });

        split.add(leftPanel);
        split.add(rightPanel);
        return split;
    }

    // ─── REGISTER CARD ─────────────────────────────────────────────
    private JPanel createRegisterCard() {
        JPanel split = new JPanel(new GridLayout(1, 2));
        split.setOpaque(false);

        // Left branding (same as login)
        JPanel leftPanel = createBrandingPanel();
        split.add(leftPanel);

        // Right form
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.WARM_IVORY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setOpaque(false);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(0, 40, 0, 40));
        form.setMaximumSize(new Dimension(380, 600));

        JLabel title = new JLabel("Create Your Account");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.LIGHT_TEXT_PRIMARY);
        title.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        JTextField txtUser = addFormField(form, "Username");
        JTextField txtEmail = addFormField(form, "Email Address");
        JTextField txtName = addFormField(form, "Full Name");
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(UIConstants.FONT_BODY);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIConstants.FONT_CAPTION);
        passLabel.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        passLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(passLabel);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(txtPass);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_CAPTION);
        lblError.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(lblError);
        form.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedButton btnRegister = new RoundedButton("Create Account");
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnRegister.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(btnRegister);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnBack = new JButton("← Back to Sign In");
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setFont(UIConstants.FONT_CAPTION);
        btnBack.setForeground(UIConstants.GOLD_ACCENT);
        btnBack.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(btnBack);

        rightPanel.add(form);
        split.add(rightPanel);

        // Action handlers (preserved)
        btnBack.addActionListener(e -> showCard("LOGIN"));
        btnRegister.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String email = txtEmail.getText().trim();
            String name = txtName.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (user.isEmpty() || email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please fill out all fields.");
                return;
            }
            try {
                authController.register(user, email, name, pass);
                JOptionPane.showMessageDialog(this, "Registration successful! Let's show you around.");
                lblError.setText("");
                showCard("ONBOARDING");
            } catch (Exception ex) {
                lblError.setText(ex.getMessage());
            }
        });

        return split;
    }

    // ─── FORGOT CARD ───────────────────────────────────────────────
    private JPanel createForgotCard() {
        JPanel split = new JPanel(new GridLayout(1, 2));
        split.setOpaque(false);

        split.add(createBrandingPanel());

        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.WARM_IVORY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setOpaque(false);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(0, 40, 0, 40));
        form.setMaximumSize(new Dimension(380, 500));

        JLabel title = new JLabel("Reset Password");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.LIGHT_TEXT_PRIMARY);
        title.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        JLabel sub = new JLabel("Enter your username and new password.");
        sub.setFont(UIConstants.FONT_CAPTION);
        sub.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        sub.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(sub);
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        JTextField txtUser = addFormField(form, "Username");

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(UIConstants.FONT_BODY);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JLabel passLbl = new JLabel("New Password");
        passLbl.setFont(UIConstants.FONT_CAPTION);
        passLbl.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        passLbl.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(passLbl);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(txtPass);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_CAPTION);
        lblError.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(lblError);
        form.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedButton btnSubmit = new RoundedButton("Update Password");
        btnSubmit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnSubmit.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(btnSubmit);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnBack = new JButton("← Back to Sign In");
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setFont(UIConstants.FONT_CAPTION);
        btnBack.setForeground(UIConstants.GOLD_ACCENT);
        btnBack.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(btnBack);

        rightPanel.add(form);
        split.add(rightPanel);

        // Action handlers (preserved)
        btnBack.addActionListener(e -> showCard("LOGIN"));
        btnSubmit.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please enter username and new password.");
                return;
            }
            try {
                authController.forgotPassword(user, pass);
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                lblError.setText("");
                showCard("LOGIN");
            } catch (Exception ex) {
                lblError.setText(ex.getMessage());
            }
        });

        return split;
    }

    // ─── ONBOARDING CARD ───────────────────────────────────────────
    private JPanel createOnboardingCard() {
        JPanel wrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.FOREST_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setOpaque(false);

        CardPanel card = new CardPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(480, 420));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        CardLayout onboardingLayout = new CardLayout();
        JPanel pagesPanel = new JPanel(onboardingLayout);
        pagesPanel.setOpaque(false);

        JPanel page1 = createOnboardingPage("AI Wealth Advisor",
                "Personalized wealth strategies and portfolio allocations powered by OpenAI. Get answers to complex tax questions, asset selections, and SIP plans.",
                new LogoPainter(70));
        JPanel page2 = createOnboardingPage("Portfolio Analytics",
                "Evaluate risk-adjusted performances. Access Sharpe Ratios, Betas, Alphas, and sector concentrations displayed in premium dark-themed charts.",
                new LogoPainter(70));
        JPanel page3 = createOnboardingPage("Milestones Planner",
                "Set targets for Retirement, Education, or Custom dreams. Predict success probabilities and track compound progress.",
                new LogoPainter(70));

        pagesPanel.add(page1, "PAGE1");
        pagesPanel.add(page2, "PAGE2");
        pagesPanel.add(page3, "PAGE3");
        card.add(pagesPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnSkip = new JButton("Skip");
        btnSkip.setContentAreaFilled(false);
        btnSkip.setBorderPainted(false);
        btnSkip.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        btnSkip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSkip.setFont(UIConstants.FONT_CAPTION);
        navPanel.add(btnSkip, BorderLayout.WEST);

        RoundedButton btnNext = new RoundedButton("Next Step");
        btnNext.setPreferredSize(new Dimension(140, 38));
        navPanel.add(btnNext, BorderLayout.EAST);

        card.add(navPanel, BorderLayout.SOUTH);
        wrapper.add(card);

        // Slide logic (preserved)
        final int[] currentPage = {1};
        btnNext.addActionListener(e -> {
            if (currentPage[0] == 1) {
                onboardingLayout.show(pagesPanel, "PAGE2");
                currentPage[0] = 2;
            } else if (currentPage[0] == 2) {
                onboardingLayout.show(pagesPanel, "PAGE3");
                btnNext.setText("Get Started");
                currentPage[0] = 3;
            } else {
                onboardingLayout.show(pagesPanel, "PAGE1");
                btnNext.setText("Next Step");
                currentPage[0] = 1;
                showCard("LOGIN");
            }
        });

        btnSkip.addActionListener(e -> {
            onboardingLayout.show(pagesPanel, "PAGE1");
            btnNext.setText("Next Step");
            currentPage[0] = 1;
            showCard("LOGIN");
        });

        return wrapper;
    }

    // ─── HELPERS ──────────────────────────────────────────────────
    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConstants.FOREST_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        LogoPainter logo = new LogoPainter(80);
        logo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel brand = new JLabel("NiveshCore360");
        brand.setFont(UIConstants.FONT_HEADING);
        brand.setForeground(UIConstants.GOLD_ACCENT);
        brand.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        content.add(brand);
        content.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel tag = new JLabel("Your AI-powered wealth companion");
        tag.setFont(UIConstants.FONT_CAPTION);
        tag.setForeground(UIConstants.WARM_IVORY);
        tag.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        content.add(tag);

        panel.add(content);
        return panel;
    }

    private JTextField addFormField(JPanel form, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_CAPTION);
        lbl.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        lbl.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(lbl);
        form.add(Box.createRigidArea(new Dimension(0, 4)));

        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(field);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        return field;
    }

    private JPanel createOnboardingPage(String titleText, String descText, JComponent visual) {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setOpaque(false);

        page.add(Box.createRigidArea(new Dimension(0, 15)));
        visual.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(visual);
        page.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.LIGHT_TEXT_PRIMARY);
        title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(title);
        page.add(Box.createRigidArea(new Dimension(0, 15)));

        JTextArea desc = new JTextArea(descText);
        desc.setFont(UIConstants.FONT_BODY);
        desc.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFocusable(false);
        desc.setMaximumSize(new Dimension(380, 120));
        desc.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(desc);

        return page;
    }
}
