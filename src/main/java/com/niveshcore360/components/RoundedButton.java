package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium rounded button with gold accent, hover/pressed states, and subtle shadow.
 * Supports PRIMARY (default), SECONDARY (outlined), and DANGER (crimson) variants.
 */
public class RoundedButton extends JButton {

    private boolean hover = false;
    private boolean pressed = false;
    private final int radius = UIConstants.RADIUS_MD;

    private Color bgNormal = UIConstants.GOLD_ACCENT;
    private Color bgHover = UIConstants.GOLD_HOVER;
    private Color bgPressed = new Color(160, 130, 35);
    private Color fgColor = Color.WHITE;
    private boolean outlined = false;

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
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    /** Create a secondary (outlined) button variant */
    public static RoundedButton secondary(String text) {
        RoundedButton btn = new RoundedButton(text);
        btn.outlined = true;
        btn.bgNormal = new Color(0, 0, 0, 0);
        btn.bgHover = UIConstants.GOLD_SUBTLE;
        btn.bgPressed = UIConstants.GOLD_SUBTLE;
        btn.fgColor = UIConstants.GOLD_ACCENT;
        btn.setForeground(UIConstants.GOLD_ACCENT);
        return btn;
    }

    /** Create a danger (crimson) button variant */
    public static RoundedButton danger(String text) {
        RoundedButton btn = new RoundedButton(text);
        btn.bgNormal = UIConstants.LOSS_RED;
        btn.bgHover = new Color(170, 45, 35);
        btn.bgPressed = new Color(150, 40, 30);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Subtle shadow
        if (!outlined) {
            g2.setColor(UIConstants.SHADOW_SM);
            g2.fillRoundRect(1, 2, w - 2, h - 1, radius, radius);
        }

        // Background
        Color bg = pressed ? bgPressed : (hover ? bgHover : bgNormal);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, radius, radius);

        // Outlined border
        if (outlined) {
            g2.setColor(UIConstants.GOLD_ACCENT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }

        g2.dispose();

        // Set foreground for text
        setForeground(outlined ? fgColor : Color.WHITE);
        super.paintComponent(g);
    }
}
