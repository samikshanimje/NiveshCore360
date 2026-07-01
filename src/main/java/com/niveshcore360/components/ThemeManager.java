package com.niveshcore360.components;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

/**
 * Utility manager controlling the FlatLaf window look-and-feel states at runtime.
 */
public class ThemeManager {
    
    private static boolean isDarkMode = true; // Defaults to Dark Theme

    /**
     * Checks if current look-and-feel is set to dark mode.
     */
    public static boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Set window look-and-feel theme.
     */
    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
        try {
            if (isDarkMode) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            // Repaint and update active Swing components hierarchy
            FlatLaf.updateUI();
        } catch (Exception ex) {
            System.err.println("Failed to update FlatLaf look-and-feel: " + ex.getMessage());
        }
    }

    /**
     * Toggles between Dark and Light mode.
     */
    public static void toggleTheme() {
        setDarkMode(!isDarkMode);
    }
}
