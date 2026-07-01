package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Reusable panel container providing smooth rounded corners, paddings, and theme borders.
 */
public class CardPanel extends JPanel {

    private int cornerRadius = 14;

    public CardPanel() {
        this(new BorderLayout());
    }

    public CardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public CardPanel(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padding, padding, padding, padding));
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fetch theme colors dynamically
        Color bg = ThemeManager.isDarkMode() ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD;
        Color border = ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER;

        // Draw filled card background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Draw smooth border
        g2.setColor(border);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
