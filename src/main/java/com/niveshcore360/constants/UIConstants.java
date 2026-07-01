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

    // Accent Theme Colors
    public static final Color ACCENT_COLOR = new Color(99, 102, 241);     // Indigo 500
    public static final Color ACCENT_HOVER = new Color(79, 70, 229);     // Indigo 600
    
    // Status Indicators
    public static final Color PROFIT_GREEN = new Color(16, 185, 129);     // Emerald 500 (Green)
    public static final Color LOSS_RED = new Color(239, 68, 68);          // Red 500 (Red)

    // Light Theme Specific Colors
    public static final Color LIGHT_BG = new Color(248, 250, 252);        // Slate 50
    public static final Color LIGHT_CARD = new Color(255, 255, 255);      // White
    public static final Color LIGHT_TEXT_PRIMARY = new Color(15, 23, 42);  // Slate 900
    public static final Color LIGHT_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color LIGHT_BORDER = new Color(226, 232, 240);     // Slate 200

    // Dark Theme Specific Colors
    public static final Color DARK_BG = new Color(15, 23, 42);            // Slate 900
    public static final Color DARK_CARD = new Color(30, 41, 59);          // Slate 800
    public static final Color DARK_TEXT_PRIMARY = new Color(248, 250, 252); // Slate 50
    public static final Color DARK_TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    public static final Color DARK_BORDER = new Color(51, 65, 85);        // Slate 700
}
