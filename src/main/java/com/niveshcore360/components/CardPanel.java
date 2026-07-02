package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable card container with rounded corners, soft drop shadows,
 * and optional hover-lift animation for premium fintech aesthetic.
 */
public class CardPanel extends JPanel {

    private int cornerRadius = UIConstants.RADIUS_LG;
    private boolean hoverLiftEnabled = true;
    private boolean hovered = false;

    public CardPanel() {
        this(new BorderLayout());
    }

    public CardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initHover();
    }

    public CardPanel(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padding, padding, padding, padding));
        initHover();
    }

    private void initHover() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverLiftEnabled) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverLiftEnabled) {
                    hovered = false;
                    repaint();
                }
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setHoverLiftEnabled(boolean enabled) {
        this.hoverLiftEnabled = enabled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean dark = ThemeManager.isDarkMode();
        Color bg = dark ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD;
        Color border = dark ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER;

        int w = getWidth();
        int h = getHeight();

        // Draw shadow
        if (hovered && hoverLiftEnabled) {
            g2.setColor(UIConstants.SHADOW_LG);
            g2.fillRoundRect(3, 5, w - 6, h - 6, cornerRadius, cornerRadius);
        } else {
            g2.setColor(UIConstants.SHADOW_MD);
            g2.fillRoundRect(2, 3, w - 4, h - 4, cornerRadius, cornerRadius);
        }

        // Draw card background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 2, h - 3, cornerRadius, cornerRadius);

        // Draw border
        g2.setColor(border);
        g2.drawRoundRect(0, 0, w - 2, h - 3, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
