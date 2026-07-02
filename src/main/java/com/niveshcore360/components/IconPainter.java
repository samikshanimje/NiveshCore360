package com.niveshcore360.components;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Utility class painting modern outlined vector icons (Lucide/Feather style).
 * All icons are drawn with anti-aliased strokes using the current Graphics2D foreground color.
 */
public final class IconPainter {

    private IconPainter() {}

    private static void setup(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    /** 4-square grid icon */
    public static void paintDashboard(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int gap = s / 10;
        int half = (s - gap) / 2;
        g.drawRoundRect(x, y, half, half, 3, 3);
        g.drawRoundRect(x + half + gap, y, half, half, 3, 3);
        g.drawRoundRect(x, y + half + gap, half, half, 3, 3);
        g.drawRoundRect(x + half + gap, y + half + gap, half, half, 3, 3);
        g.dispose();
    }

    /** Briefcase icon */
    public static void paintPortfolio(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int by = y + s / 4, bh = s * 3 / 4;
        g.drawRoundRect(x, by, s, bh - s / 8, 4, 4);
        int hw = s / 3, hx = x + (s - hw) / 2;
        g.drawLine(hx, by, hx, y);
        g.drawLine(hx + hw, by, hx + hw, y);
        g.drawLine(hx, y, hx + hw, y);
        g.drawLine(x, by + bh / 3, x + s, by + bh / 3);
        g.dispose();
    }

    /** Target / bullseye icon */
    public static void paintGoals(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int cx = x + s / 2, cy = y + s / 2;
        g.drawOval(x, y, s, s);
        g.drawOval(x + s / 4, y + s / 4, s / 2, s / 2);
        g.fillOval(cx - 2, cy - 2, 5, 5);
        g.dispose();
    }

    /** Calculator icon */
    public static void paintCalculator(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        g.drawRoundRect(x, y, s, s, 4, 4);
        int pad = s / 5;
        g.drawRect(x + pad, y + pad, s - pad * 2, s / 4);
        int dotS = 2;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int dx = x + pad + c * (s - pad * 2) / 3 + (s - pad * 2) / 6 - dotS;
                int dy = y + pad + s / 4 + pad / 2 + r * (s / 5) + s / 10 - dotS;
                g.fillOval(dx, dy, dotS * 2, dotS * 2);
            }
        }
        g.dispose();
    }

    /** Document / page icon */
    public static void paintStatements(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int fold = s / 4;
        Path2D doc = new Path2D.Double();
        doc.moveTo(x, y);
        doc.lineTo(x + s - fold, y);
        doc.lineTo(x + s, y + fold);
        doc.lineTo(x + s, y + s);
        doc.lineTo(x, y + s);
        doc.closePath();
        g.draw(doc);
        g.drawLine(x + s - fold, y, x + s - fold, y + fold);
        g.drawLine(x + s - fold, y + fold, x + s, y + fold);
        int lx = x + s / 5, ly = y + s / 2;
        g.drawLine(lx, ly, x + s - s / 5, ly);
        g.drawLine(lx, ly + s / 6, x + s - s / 3, ly + s / 6);
        g.dispose();
    }

    /** Sparkle / AI star icon */
    public static void paintAI(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int cx = x + s / 2, cy = y + s / 2;
        int r = s / 2;
        Path2D star = new Path2D.Double();
        star.moveTo(cx, cy - r);
        star.quadTo(cx + 2, cy - 2, cx + r, cy);
        star.quadTo(cx + 2, cy + 2, cx, cy + r);
        star.quadTo(cx - 2, cy + 2, cx - r, cy);
        star.quadTo(cx - 2, cy - 2, cx, cy - r);
        star.closePath();
        g.draw(star);
        int sr = s / 6;
        int sx = x + s - sr * 2, sy = y;
        Path2D mini = new Path2D.Double();
        mini.moveTo(sx + sr, sy);
        mini.quadTo(sx + sr + 1, sy + sr - 1, sx + sr * 2, sy + sr);
        mini.quadTo(sx + sr + 1, sy + sr + 1, sx + sr, sy + sr * 2);
        mini.quadTo(sx + sr - 1, sy + sr + 1, sx, sy + sr);
        mini.quadTo(sx + sr - 1, sy + sr - 1, sx + sr, sy);
        mini.closePath();
        g.draw(mini);
        g.dispose();
    }

    /** Shield icon */
    public static void paintAdmin(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        Path2D shield = new Path2D.Double();
        shield.moveTo(x + s / 2.0, y);
        shield.lineTo(x + s, y + s * 0.2);
        shield.lineTo(x + s, y + s * 0.55);
        shield.curveTo(x + s, y + s * 0.8, x + s / 2.0, y + s, x + s / 2.0, y + s);
        shield.curveTo(x + s / 2.0, y + s, x, y + s * 0.8, x, y + s * 0.55);
        shield.lineTo(x, y + s * 0.2);
        shield.closePath();
        g.draw(shield);
        g.dispose();
    }

    /** Gear / settings icon */
    public static void paintSettings(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int cx = x + s / 2, cy = y + s / 2;
        int innerR = s / 4, outerR = s / 2;
        g.drawOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = cx + (int) (innerR * 1.3 * Math.cos(angle));
            int y1 = cy + (int) (innerR * 1.3 * Math.sin(angle));
            int x2 = cx + (int) (outerR * Math.cos(angle));
            int y2 = cy + (int) (outerR * Math.sin(angle));
            g.drawLine(x1, y1, x2, y2);
        }
        g.dispose();
    }

    /** Logout / exit arrow icon */
    public static void paintLogout(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int doorW = s * 3 / 5;
        g.drawLine(x + doorW, y, x, y);
        g.drawLine(x, y, x, y + s);
        g.drawLine(x, y + s, x + doorW, y + s);
        int cy = y + s / 2;
        g.drawLine(x + s / 3, cy, x + s, cy);
        g.drawLine(x + s - s / 5, cy - s / 5, x + s, cy);
        g.drawLine(x + s - s / 5, cy + s / 5, x + s, cy);
        g.dispose();
    }

    /** Person / user icon */
    public static void paintUser(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int headR = s / 4;
        int cx = x + s / 2;
        g.drawOval(cx - headR, y + s / 8, headR * 2, headR * 2);
        int arcY = y + s / 2 + s / 8;
        g.drawArc(x + s / 6, arcY, s * 2 / 3, s / 2, 0, 180);
        g.dispose();
    }

    /** Magnifying glass icon */
    public static void paintSearch(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int r = s * 2 / 5;
        g.drawOval(x, y, r * 2, r * 2);
        int hx = (int) (x + r + r * Math.cos(Math.PI / 4));
        int hy = (int) (y + r + r * Math.sin(Math.PI / 4));
        g.drawLine(hx, hy, x + s, y + s);
        g.dispose();
    }

    /** Bell / notification icon */
    public static void paintNotification(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int cx = x + s / 2;
        Path2D bell = new Path2D.Double();
        bell.moveTo(x + s / 5, y + s * 0.65);
        bell.curveTo(x + s / 5, y + s / 4, cx, y, cx, y);
        bell.curveTo(cx, y, x + s * 4 / 5, y + s / 4, x + s * 4 / 5, y + s * 0.65);
        bell.lineTo(x + s / 5, y + s * 0.65);
        g.draw(bell);
        g.drawLine(x + s / 8, (int) (y + s * 0.65), x + s - s / 8, (int) (y + s * 0.65));
        g.drawArc(cx - s / 8, y + s * 3 / 4, s / 4, s / 5, 180, 180);
        g.dispose();
    }

    /** Sun / theme toggle icon */
    public static void paintTheme(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        int cx = x + s / 2, cy = y + s / 2, r = s / 4;
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        int rayLen = s / 6;
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = cx + (int) ((r + 2) * Math.cos(angle));
            int y1 = cy + (int) ((r + 2) * Math.sin(angle));
            int x2 = cx + (int) ((r + rayLen) * Math.cos(angle));
            int y2 = cy + (int) ((r + rayLen) * Math.sin(angle));
            g.drawLine(x1, y1, x2, y2);
        }
        g.dispose();
    }

    /** Upward trending line icon */
    public static void paintTrendUp(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        g.drawLine(x, y + s, x + s / 3, y + s / 2);
        g.drawLine(x + s / 3, y + s / 2, x + s * 2 / 3, y + s * 2 / 3);
        g.drawLine(x + s * 2 / 3, y + s * 2 / 3, x + s, y);
        g.drawLine(x + s - s / 4, y, x + s, y);
        g.drawLine(x + s, y, x + s, y + s / 4);
        g.dispose();
    }

    /** Downward trending line icon */
    public static void paintTrendDown(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        g.drawLine(x, y, x + s / 3, y + s / 2);
        g.drawLine(x + s / 3, y + s / 2, x + s * 2 / 3, y + s / 3);
        g.drawLine(x + s * 2 / 3, y + s / 3, x + s, y + s);
        g.drawLine(x + s - s / 4, y + s, x + s, y + s);
        g.drawLine(x + s, y + s, x + s, y + s - s / 4);
        g.dispose();
    }

    /** Paper plane / send icon */
    public static void paintSend(Graphics2D g2, int x, int y, int s) {
        Graphics2D g = (Graphics2D) g2.create();
        setup(g);
        Path2D plane = new Path2D.Double();
        plane.moveTo(x, y + s / 4);
        plane.lineTo(x + s, y + s / 2);
        plane.lineTo(x, y + s * 3 / 4);
        plane.lineTo(x + s / 4, y + s / 2);
        plane.closePath();
        g.draw(plane);
        g.drawLine(x + s / 4, y + s / 2, x + s, y + s / 2);
        g.dispose();
    }
}
