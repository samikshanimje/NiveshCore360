package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Sidebar navigation button with icon, label, active/hover states.
 * Designed for the forest-green sidebar with gold accent highlights.
 */
public class NavButton extends JButton {

    private final String iconType;
    private boolean active = false;
    private boolean hovered = false;

    public NavButton(String label, String iconType) {
        super(label);
        this.iconType = iconType;

        setFont(UIConstants.FONT_BODY);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 32, 42));
        setBorder(new EmptyBorder(0, 16, 0, 12));
        setHorizontalAlignment(SwingConstants.LEFT);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Background fill
        if (active) {
            g2.setColor(UIConstants.GOLD_SUBTLE);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);
            // Gold left-edge indicator bar
            g2.setColor(UIConstants.GOLD_ACCENT);
            g2.fillRoundRect(0, 6, 3, h - 12, 2, 2);
        } else if (hovered) {
            g2.setColor(new Color(UIConstants.FOREST_LIGHT.getRed(),
                    UIConstants.FOREST_LIGHT.getGreen(),
                    UIConstants.FOREST_LIGHT.getBlue(), 80));
            g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);
        }

        // Determine text/icon color
        Color textColor;
        if (active) {
            textColor = UIConstants.GOLD_ACCENT;
        } else if (hovered) {
            textColor = UIConstants.WARM_IVORY;
        } else {
            textColor = new Color(UIConstants.WARM_IVORY.getRed(),
                    UIConstants.WARM_IVORY.getGreen(),
                    UIConstants.WARM_IVORY.getBlue(), 200);
        }

        // Draw icon
        int iconSize = 18;
        int iconX = 20;
        int iconY = (h - iconSize) / 2;
        g2.setColor(textColor);
        paintIcon(g2, iconX, iconY, iconSize);

        // Draw label text
        g2.setFont(getFont());
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        int textX = iconX + iconSize + 14;
        int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    private void paintIcon(Graphics2D g2, int x, int y, int size) {
        switch (iconType) {
            case "dashboard"   -> IconPainter.paintDashboard(g2, x, y, size);
            case "portfolio"   -> IconPainter.paintPortfolio(g2, x, y, size);
            case "goals"       -> IconPainter.paintGoals(g2, x, y, size);
            case "calculator"  -> IconPainter.paintCalculator(g2, x, y, size);
            case "statements"  -> IconPainter.paintStatements(g2, x, y, size);
            case "ai"          -> IconPainter.paintAI(g2, x, y, size);
            case "admin"       -> IconPainter.paintAdmin(g2, x, y, size);
            case "settings"    -> IconPainter.paintSettings(g2, x, y, size);
            case "logout"      -> IconPainter.paintLogout(g2, x, y, size);
            case "theme"       -> IconPainter.paintTheme(g2, x, y, size);
            case "user"        -> IconPainter.paintUser(g2, x, y, size);
            case "search"      -> IconPainter.paintSearch(g2, x, y, size);
            case "notification"-> IconPainter.paintNotification(g2, x, y, size);
            default -> {}
        }
    }
}
