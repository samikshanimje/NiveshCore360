package com.niveshcore360.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Custom Swing component that paints the vectorized NiveshCore360 logo.
 * Integrates Rupee, Growth Arrow, Security Shield, and AI Sparkles.
 */
public class LogoPainter extends JComponent {
    
    private final int size;

    public LogoPainter(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw Shield (Security Base Layer)
        g2.setColor(new Color(139, 92, 246, 30)); // 2FA Transparent Purple
        Path2D shield = new Path2D.Double();
        shield.moveTo(w * 0.5, h * 0.1);
        shield.curveTo(w * 0.8, h * 0.1, w * 0.9, h * 0.2, w * 0.85, h * 0.5);
        shield.curveTo(w * 0.8, h * 0.8, w * 0.5, h * 0.95, w * 0.5, h * 0.95);
        shield.curveTo(w * 0.5, h * 0.95, w * 0.2, h * 0.8, w * 0.15, h * 0.5);
        shield.curveTo(w * 0.1, h * 0.2, w * 0.2, h * 0.1, w * 0.5, h * 0.1);
        shield.closePath();
        g2.fill(shield);

        g2.setColor(new Color(139, 92, 246, 220)); // Purple boundary lines
        g2.setStroke(new BasicStroke(3.0f));
        g2.draw(shield);

        // 2. Draw Growth Trend (Graph line)
        g2.setColor(new Color(16, 185, 129)); // Neon Emerald Green
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D graph = new Path2D.Double();
        graph.moveTo(w * 0.28, h * 0.65);
        graph.lineTo(w * 0.45, h * 0.50);
        graph.lineTo(w * 0.58, h * 0.55);
        graph.lineTo(w * 0.72, h * 0.35); // Trending arrow apex
        g2.draw(graph);

        // Trend arrowhead
        Path2D arrowhead = new Path2D.Double();
        arrowhead.moveTo(w * 0.62, h * 0.35);
        arrowhead.lineTo(w * 0.72, h * 0.35);
        arrowhead.lineTo(w * 0.72, h * 0.45);
        g2.draw(arrowhead);

        // 3. Draw Rupee Symbol in Center
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("sansserif", Font.BOLD, (int) (size * 0.25)));
        FontMetrics fm = g2.getFontMetrics();
        String rupee = "₹";
        int rx = (w - fm.stringWidth(rupee)) / 2;
        int ry = (h + fm.getAscent() - fm.getDescent()) / 2 - (int) (size * 0.03);
        g2.drawString(rupee, rx, ry);

        // 4. Draw AI Sparkles (Micro Stars)
        g2.setColor(new Color(251, 191, 36)); // Golden Amber sparkles
        drawSparkle(g2, (int) (w * 0.76), (int) (h * 0.22));
        drawSparkle(g2, (int) (w * 0.24), (int) (h * 0.38));

        g2.dispose();
    }

    private void drawSparkle(Graphics2D g2, int cx, int cy) {
        int r = (int) (size * 0.035);
        Path2D star = new Path2D.Double();
        star.moveTo(cx, cy - r);
        star.quadTo(cx, cy, cx + r, cy);
        star.quadTo(cx, cy, cx, cy + r);
        star.quadTo(cx, cy, cx - r, cy);
        star.quadTo(cx, cy, cx, cy - r);
        star.closePath();
        g2.fill(star);
    }
}
