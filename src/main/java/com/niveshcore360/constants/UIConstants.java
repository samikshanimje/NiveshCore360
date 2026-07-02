package com.niveshcore360.constants;

import java.awt.Color;
import java.awt.Font;

/**
 * Design system constants for NiveshCore360.
 * Premium warm fintech palette inspired by CRED, Groww, Stripe, and Linear.
 */
public class UIConstants {

    // ─── Typography ─────────────────────────────────────────────────────
    // Uses Dialog (system sans-serif) with careful weight/size differentiation.
    public static final Font FONT_DISPLAY    = new Font("Dialog", Font.BOLD, 28);
    public static final Font FONT_HEADING    = new Font("Dialog", Font.BOLD, 20);
    public static final Font FONT_SUBHEADING = new Font("Dialog", Font.BOLD, 16);
    public static final Font FONT_BODY       = new Font("Dialog", Font.PLAIN, 14);
    public static final Font FONT_CAPTION    = new Font("Dialog", Font.PLAIN, 12);
    public static final Font FONT_BUTTON     = new Font("Dialog", Font.BOLD, 13);
    public static final Font FONT_MONO       = new Font("Monospaced", Font.BOLD, 15);

    // Legacy aliases (referenced across existing views)
    public static final Font FONT_TITLE    = FONT_DISPLAY;
    public static final Font FONT_HEADER   = FONT_SUBHEADING;
    public static final Font FONT_SUBTITLE = FONT_CAPTION;
    public static final Font FONT_BOLD     = new Font("Dialog", Font.BOLD, 14);

    // ─── Primary Brand Colors ───────────────────────────────────────────
    public static final Color FOREST_PRIMARY  = new Color(27, 58, 45);     // #1B3A2D Deep Forest Green
    public static final Color FOREST_LIGHT    = new Color(40, 75, 58);     // Lighter forest for hovers
    public static final Color CHARCOAL        = new Color(28, 28, 30);     // #1C1C1E Matte Black
    public static final Color WARM_IVORY      = new Color(250, 248, 245);  // #FAF8F5 Warm Off-White

    // ─── Accent Colors ──────────────────────────────────────────────────
    public static final Color GOLD_ACCENT     = new Color(201, 168, 76);   // #C9A84C Champagne Gold
    public static final Color GOLD_HOVER      = new Color(184, 148, 47);   // #B8942F Darker Gold
    public static final Color GOLD_SUBTLE     = new Color(201, 168, 76, 25); // 10% Gold tint
    public static final Color CHAMPAGNE       = new Color(245, 230, 200);  // #F5E6C8 Champagne highlight
    public static final Color COPPER          = new Color(184, 115, 51);   // #B87333 Copper accent
    public static final Color SAGE_SOFT       = new Color(168, 181, 160);  // #A8B5A0 Soft Sage

    // Legacy aliases
    public static final Color ACCENT_COLOR     = GOLD_ACCENT;
    public static final Color ACCENT_HOVER     = GOLD_HOVER;
    public static final Color ACCENT_SECONDARY = new Color(45, 139, 85);   // Emerald

    // ─── Status Indicators ──────────────────────────────────────────────
    public static final Color PROFIT_GREEN   = new Color(45, 139, 85);     // #2D8B55 Emerald
    public static final Color LOSS_RED       = new Color(192, 57, 43);     // #C0392B Muted Crimson
    public static final Color WARN_AMBER     = new Color(212, 160, 23);    // #D4A017 Soft Amber

    // ─── Light Theme ────────────────────────────────────────────────────
    public static final Color LIGHT_BG           = WARM_IVORY;
    public static final Color LIGHT_CARD         = new Color(255, 255, 255);  // Pure White
    public static final Color LIGHT_TEXT_PRIMARY  = new Color(41, 37, 36);    // Stone 800
    public static final Color LIGHT_TEXT_MUTED    = new Color(120, 113, 108); // Stone 500
    public static final Color LIGHT_BORDER        = new Color(232, 229, 224); // Warm border

    // ─── Dark Theme ─────────────────────────────────────────────────────
    public static final Color DARK_BG            = CHARCOAL;
    public static final Color DARK_CARD          = new Color(44, 44, 46);     // #2C2C2E
    public static final Color DARK_TEXT_PRIMARY   = new Color(250, 248, 245); // Warm Ivory
    public static final Color DARK_TEXT_MUTED     = new Color(162, 155, 148); // Warm Stone 400
    public static final Color DARK_BORDER         = new Color(58, 58, 60);    // #3A3A3C

    // ─── Shadows (Alpha-channel colors for painting) ────────────────────
    public static final Color SHADOW_SM  = new Color(0, 0, 0, 15);   // Very subtle
    public static final Color SHADOW_MD  = new Color(0, 0, 0, 25);   // Card shadow
    public static final Color SHADOW_LG  = new Color(0, 0, 0, 40);   // Elevated shadow

    // ─── Spacing ────────────────────────────────────────────────────────
    public static final int SPACE_XS  = 4;
    public static final int SPACE_SM  = 8;
    public static final int SPACE_MD  = 16;
    public static final int SPACE_LG  = 24;
    public static final int SPACE_XL  = 32;
    public static final int SPACE_2XL = 48;

    // ─── Corner Radii ───────────────────────────────────────────────────
    public static final int RADIUS_SM  = 8;
    public static final int RADIUS_MD  = 12;
    public static final int RADIUS_LG  = 16;
    public static final int RADIUS_XL  = 24;

    // ─── Layout Constants ───────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH = 260;
    public static final int HEADER_HEIGHT = 60;

    // ─── Chart Palette (for JFreeChart pie/line segments) ───────────────
    public static final Color[] CHART_PALETTE = {
        GOLD_ACCENT,
        PROFIT_GREEN,
        COPPER,
        SAGE_SOFT,
        CHAMPAGNE,
        new Color(107, 142, 126),   // Muted teal
        new Color(166, 134, 93),    // Warm tan
        new Color(139, 164, 133),   // Soft olive
    };
}
