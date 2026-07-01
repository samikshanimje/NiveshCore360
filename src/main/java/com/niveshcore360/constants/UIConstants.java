package com.niveshcore360.constants;

import java.awt.Color;
import java.awt.Font;

/**
 * Constants defining the design system, colors, and typography of NiveshCore360.
 */
public class UIConstants {

    // Typography (uses system-mapped clean sans-serif like Inter, fallback to Dialog)
    public static final Font FONT_TITLE = new Font("sansserif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("sansserif", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("sansserif", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("sansserif", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("sansserif", Font.BOLD, 12);
    public static final Font FONT_BUTTON = new Font("sansserif", Font.BOLD, 12);

    // Accent Theme Colors (Royal Purple & Highlight Green)
    public static final Color ACCENT_COLOR = new Color(139, 92, 246);     // Purple 500 (Royal Purple)
    public static final Color ACCENT_HOVER = new Color(124, 58, 237);     // Purple 600
    public static final Color ACCENT_SECONDARY = new Color(16, 185, 129); // Emerald 500 (Neon Green)
    
    // Status Indicators
    public static final Color PROFIT_GREEN = new Color(16, 185, 129);     // Emerald 500
    public static final Color LOSS_RED = new Color(239, 68, 68);          // Red 500
 
    // Light Theme Specific Colors
    public static final Color LIGHT_BG = new Color(248, 250, 252);        // Slate 50
    public static final Color LIGHT_CARD = new Color(255, 255, 255);      // White
    public static final Color LIGHT_TEXT_PRIMARY = new Color(15, 23, 42);  // Slate 900
    public static final Color LIGHT_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color LIGHT_BORDER = new Color(226, 232, 240);     // Slate 200
 
    // Premium Dark Theme Specific Colors (Apple / Groww inspired)
    public static final Color DARK_BG = new Color(2, 6, 23);              // Slate 950 (Deep Navy)
    public static final Color DARK_CARD = new Color(15, 23, 42);          // Slate 900 (Midnight Blue)
    public static final Color DARK_TEXT_PRIMARY = new Color(248, 250, 252); // Slate 50
    public static final Color DARK_TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    public static final Color DARK_BORDER = new Color(30, 41, 59);        // Slate 800
}
