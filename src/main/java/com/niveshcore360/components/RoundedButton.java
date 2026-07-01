package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable rounded button component supporting accent coloring, mouse hover effects, and antialiased edges.
 */
public class RoundedButton extends JButton {

    private boolean hover = false;
    private final int radius = 10;

    public RoundedButton(String text) {
        super(text);
        setFont(UIConstants.FONT_BUTTON);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render filled container background
        Color bg = hover ? UIConstants.ACCENT_HOVER : UIConstants.ACCENT_COLOR;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        
        // Let standard Swing draw the text centered above the background
        super.paintComponent(g);
    }
}
