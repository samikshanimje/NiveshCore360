package com.niveshcore360.view.login;

import com.niveshcore360.controller.AuthController;
import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Authentication view containing Login, Register, and Forgot Password card sub-panels.
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

        setLayout(new GridBagLayout());
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

        add(containerPanel);
        showCard("LOGIN");
    }

    public void showCard(String name) {
        cardLayout.show(containerPanel, name);
    }

    private JPanel createLoginCard() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 480));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Welcome back to NiveshCore360", JLabel.CENTER);
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Invest. Track. Grow. Securely.", JLabel.CENTER);
        subtitle.setFont(UIConstants.FONT_SUBTITLE);
        subtitle.setForeground(UIConstants.LIGHT_TEXT_MUTED);
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 2;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIConstants.FONT_HEADER);
        card.add(lblUser, gbc);

        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 3;
        card.add(txtUser, gbc);

        // Password
        gbc.gridy = 4;
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIConstants.FONT_HEADER);
        card.add(lblPass, gbc);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 5;
        card.add(txtPass, gbc);

        // Error message label
        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_BODY);
        gbc.gridy = 6; gbc.gridwidth = 2;
        card.add(lblError, gbc);

        // Login Button
        RoundedButton btnLogin = new RoundedButton("Login to Account");
        btnLogin.setPreferredSize(new Dimension(280, 40));
        gbc.gridy = 7; gbc.gridwidth = 2;
        card.add(btnLogin, gbc);

        // Switch panels links
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        linkPanel.setOpaque(false);
        JButton btnGoRegister = new JButton("Register");
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.setForeground(UIConstants.ACCENT_COLOR);

        JButton btnGoForgot = new JButton("Forgot Password?");
        btnGoForgot.setBorderPainted(false);
        btnGoForgot.setContentAreaFilled(false);
        btnGoForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoForgot.setForeground(UIConstants.LIGHT_TEXT_MUTED);

        linkPanel.add(btnGoForgot);
        linkPanel.add(new JLabel("|"));
        linkPanel.add(btnGoRegister);

        gbc.gridy = 8;
        card.add(linkPanel, gbc);

        // Action Handlers
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

        return card;
    }

    private JPanel createRegisterCard() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 520));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account", JLabel.CENTER);
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIConstants.FONT_HEADER);
        card.add(lblUser, gbc);

        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(280, 32));
        gbc.gridy = 2;
        card.add(txtUser, gbc);

        // Email
        gbc.gridy = 3;
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(UIConstants.FONT_HEADER);
        card.add(lblEmail, gbc);

        JTextField txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(280, 32));
        gbc.gridy = 4;
        card.add(txtEmail, gbc);

        // Full name
        gbc.gridy = 5;
        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(UIConstants.FONT_HEADER);
        card.add(lblName, gbc);

        JTextField txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(280, 32));
        gbc.gridy = 6;
        card.add(txtName, gbc);

        // Password
        gbc.gridy = 7;
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIConstants.FONT_HEADER);
        card.add(lblPass, gbc);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(280, 32));
        gbc.gridy = 8;
        card.add(txtPass, gbc);

        // Error message label
        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_BODY);
        gbc.gridy = 9; gbc.gridwidth = 2;
        card.add(lblError, gbc);

        RoundedButton btnRegister = new RoundedButton("Submit Registration");
        btnRegister.setPreferredSize(new Dimension(280, 38));
        gbc.gridy = 10;
        card.add(btnRegister, gbc);

        JButton btnBack = new JButton("Back to Login");
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridy = 11;
        card.add(btnBack, gbc);

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

        return card;
    }

    private JPanel createForgotCard() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 380));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Reset Password", JLabel.CENTER);
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIConstants.FONT_HEADER);
        card.add(lblUser, gbc);

        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 2;
        card.add(txtUser, gbc);

        gbc.gridy = 3;
        JLabel lblPass = new JLabel("New Password");
        lblPass.setFont(UIConstants.FONT_HEADER);
        card.add(lblPass, gbc);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 4;
        card.add(txtPass, gbc);

        JLabel lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(UIConstants.LOSS_RED);
        lblError.setFont(UIConstants.FONT_BODY);
        gbc.gridy = 5; gbc.gridwidth = 2;
        card.add(lblError, gbc);

        RoundedButton btnSubmit = new RoundedButton("Update Password");
        btnSubmit.setPreferredSize(new Dimension(280, 40));
        gbc.gridy = 6;
        card.add(btnSubmit, gbc);

        JButton btnBack = new JButton("Back to Login");
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setForeground(UIConstants.ACCENT_COLOR);
        gbc.gridy = 7;
        card.add(btnBack, gbc);

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

        return card;
    }

    private JPanel createOnboardingCard() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(420, 480));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        CardLayout onboardingLayout = new CardLayout();
        JPanel pagesPanel = new JPanel(onboardingLayout);
        pagesPanel.setOpaque(false);

        // Slide 1: AI Advisor
        JPanel page1 = createOnboardingPage(
            "AI Wealth Advisor",
            "Personalized wealth strategies and portfolio allocations powered by OpenAI models. Get answers to complex tax questions, asset selections, and SIP plans tailored to your risk appetite.",
            new com.niveshcore360.components.LogoPainter(80)
        );

        // Slide 2: Analytics
        JPanel page2 = createOnboardingPage(
            "Portfolio Analytics",
            "Evaluate systematically risk-adjusted performances. Access Sharpe Ratios, Betas, Alphas, and sector concentrations displayed in premium dark-themed charts.",
            new com.niveshcore360.components.LogoPainter(80)
        );

        // Slide 3: Goals
        JPanel page3 = createOnboardingPage(
            "Milestones Planner",
            "Set targets for Retirement, Education, or Custom dreams. Predict success probabilities and track compound progress using the Newton-Raphson math model.",
            new com.niveshcore360.components.LogoPainter(80)
        );

        pagesPanel.add(page1, "PAGE1");
        pagesPanel.add(page2, "PAGE2");
        pagesPanel.add(page3, "PAGE3");

        card.add(pagesPanel, BorderLayout.CENTER);

        // Navigation Footer
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnSkip = new JButton("Skip");
        btnSkip.setContentAreaFilled(false);
        btnSkip.setBorderPainted(false);
        btnSkip.setForeground(UIConstants.DARK_TEXT_MUTED);
        btnSkip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navPanel.add(btnSkip, BorderLayout.WEST);

        RoundedButton btnNext = new RoundedButton("Next Step");
        btnNext.setPreferredSize(new Dimension(140, 36));
        navPanel.add(btnNext, BorderLayout.EAST);

        card.add(navPanel, BorderLayout.SOUTH);

        // Slide logic
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
                // Reset onboarding state
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

        return card;
    }

    private JPanel createOnboardingPage(String titleText, String descText, JComponent visualComponent) {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setOpaque(false);

        page.add(Box.createRigidArea(new Dimension(0, 15)));
        visualComponent.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(visualComponent);
        page.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(new Font("sansserif", Font.BOLD, 20));
        title.setForeground(UIConstants.DARK_TEXT_PRIMARY);
        title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(title);
        page.add(Box.createRigidArea(new Dimension(0, 15)));

        JTextArea desc = new JTextArea(descText);
        desc.setFont(UIConstants.FONT_SUBTITLE);
        desc.setForeground(UIConstants.DARK_TEXT_MUTED);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFocusable(false);
        desc.setMaximumSize(new Dimension(340, 120));
        desc.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        page.add(desc);

        return page;
    }
}
