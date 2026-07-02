package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Utility manager controlling the FlatLaf window look-and-feel states at runtime.
 * Applies warm fintech palette overrides to all Swing components.
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

            applyWarmPaletteOverrides();

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

    /**
     * Apply warm fintech palette overrides to UIManager defaults.
     */
    private static void applyWarmPaletteOverrides() {
        Color bg = isDarkMode ? UIConstants.DARK_CARD : Color.WHITE;
        Color textPrimary = isDarkMode ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY;
        Color textMuted = isDarkMode ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED;
        Color border = isDarkMode ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER;
        Color baseBg = isDarkMode ? UIConstants.DARK_BG : UIConstants.LIGHT_BG;

        // Panel and general backgrounds
        UIManager.put("Panel.background", baseBg);

        // TextField styling
        UIManager.put("TextField.background", bg);
        UIManager.put("TextField.foreground", textPrimary);
        UIManager.put("TextField.selectionBackground", UIConstants.GOLD_ACCENT);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("TextField.placeholderForeground", textMuted);

        // PasswordField
        UIManager.put("PasswordField.background", bg);
        UIManager.put("PasswordField.foreground", textPrimary);
        UIManager.put("PasswordField.selectionBackground", UIConstants.GOLD_ACCENT);

        // ComboBox
        UIManager.put("ComboBox.background", bg);
        UIManager.put("ComboBox.foreground", textPrimary);
        UIManager.put("ComboBox.selectionBackground", UIConstants.GOLD_ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        // Table
        UIManager.put("Table.background", bg);
        UIManager.put("Table.foreground", textPrimary);
        UIManager.put("Table.selectionBackground", UIConstants.GOLD_SUBTLE);
        UIManager.put("Table.selectionForeground", textPrimary);
        UIManager.put("TableHeader.background", isDarkMode ? UIConstants.DARK_BG : UIConstants.LIGHT_BG);
        UIManager.put("TableHeader.foreground", textPrimary);

        // ScrollBar
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 8);

        // Focus / Accent
        UIManager.put("Component.focusColor", UIConstants.GOLD_ACCENT);
        UIManager.put("Component.focusWidth", 1);

        // TabbedPane
        UIManager.put("TabbedPane.selectedBackground", bg);
        UIManager.put("TabbedPane.underlineColor", UIConstants.GOLD_ACCENT);
        UIManager.put("TabbedPane.focusColor", UIConstants.GOLD_ACCENT);

        // ProgressBar
        UIManager.put("ProgressBar.foreground", UIConstants.GOLD_ACCENT);

        // Button
        UIManager.put("Button.foreground", textPrimary);
    }
}
