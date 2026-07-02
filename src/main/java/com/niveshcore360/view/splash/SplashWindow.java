package com.niveshcore360.view.splash;

import com.niveshcore360.components.LogoPainter;
import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Premium splash screen with forest-green background, gold accents,
 * and smooth progress bar during Spring Boot initialization.
 */
public class SplashWindow extends JWindow {

    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public SplashWindow() {
        setSize(500, 380);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient: forest primary to darker
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.FOREST_PRIMARY,
                        0, getHeight(), new Color(15, 30, 22));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createLineBorder(UIConstants.FOREST_LIGHT, 1));

        // Central branding elements
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(50, 20, 20, 20));

        // Logo
        LogoPainter logo = new LogoPainter(100);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title
        JLabel titleLabel = new JLabel("NiveshCore360");
        titleLabel.setFont(UIConstants.FONT_DISPLAY);
        titleLabel.setForeground(UIConstants.GOLD_ACCENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Tagline
        JLabel taglineLabel = new JLabel("Invest. Track. Grow. Securely.");
        taglineLabel.setFont(UIConstants.FONT_BODY);
        taglineLabel.setForeground(UIConstants.WARM_IVORY);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(taglineLabel);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer status indicators
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 50, 36, 50));

        statusLabel = new JLabel("Initializing modules...");
        statusLabel.setFont(UIConstants.FONT_CAPTION);
        statusLabel.setForeground(UIConstants.CHAMPAGNE);
        statusLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
        footerPanel.add(statusLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 5));
        progressBar.setForeground(UIConstants.GOLD_ACCENT);
        progressBar.setBackground(UIConstants.FOREST_LIGHT);
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
