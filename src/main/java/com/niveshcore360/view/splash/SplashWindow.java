package com.niveshcore360.view.splash;

import com.niveshcore360.components.LogoPainter;
import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Splash screen showing progress while Spring Boot boots up in the background.
 */
public class SplashWindow extends JWindow {

    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public SplashWindow() {
        // Set dimensions and center on screen
        setSize(480, 360);
        setLocationRelativeTo(null);

        // Core Layout Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(UIConstants.DARK_BG);
        contentPanel.setBorder(BorderFactory.createLineBorder(UIConstants.DARK_BORDER, 2));

        // Central branding elements
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIConstants.DARK_BG);
        centerPanel.setBorder(new EmptyBorder(40, 20, 20, 20));

        // Logo
        LogoPainter logo = new LogoPainter(100);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Title
        JLabel titleLabel = new JLabel("NiveshCore360");
        titleLabel.setFont(new Font("sansserif", Font.BOLD, 26));
        titleLabel.setForeground(UIConstants.DARK_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Tagline
        JLabel taglineLabel = new JLabel("Invest. Track. Grow. Securely.");
        taglineLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
        taglineLabel.setForeground(UIConstants.ACCENT_COLOR);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(taglineLabel);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer status indicators
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBackground(UIConstants.DARK_BG);
        footerPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        statusLabel = new JLabel("Loading modules...");
        statusLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
        statusLabel.setForeground(UIConstants.DARK_TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        footerPanel.add(statusLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 6));
        progressBar.setForeground(UIConstants.ACCENT_COLOR);
        progressBar.setBackground(UIConstants.DARK_BORDER);
        progressBar.setBorderPainted(false);
        footerPanel.add(progressBar, BorderLayout.CENTER);

        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        setContentPane(contentPanel);
    }

    public void updateProgress(int value, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            statusLabel.setText(status);
        });
    }
}
