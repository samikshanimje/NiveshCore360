package com.niveshcore360.components;

import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.view.MainFrame;
import com.niveshcore360.view.dialog.RiskProfilingDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern floating Command Palette dialog (Cmd + K / Ctrl + K) for rapid workspace navigation,
 * action commands execution, and interactive stock explainer lookups.
 */
public class CommandPaletteDialog extends JDialog {

    private final JTextField searchField;
    private final DefaultListModel<PaletteItem> listModel;
    private final JList<PaletteItem> resultsList;
    private final List<PaletteItem> allItems = new ArrayList<>();
    private final MainFrame mainFrame;

    public CommandPaletteDialog(Frame parent, MainFrame mainFrame) {
        super(parent, true);
        this.mainFrame = mainFrame;
        
        setUndecorated(true);
        setSize(500, 320);
        setLocationRelativeTo(parent);
        
        // Premium rounded panel border
        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean dark = ThemeManager.isDarkMode();
                g2.setColor(dark ? UIConstants.DARK_CARD : UIConstants.LIGHT_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(dark ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        rootPanel.setOpaque(false);
        rootPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(rootPanel);

        // Input field
        searchField = new JTextField();
        searchField.setFont(UIConstants.FONT_SUBHEADING);
        searchField.setPreferredSize(new Dimension(0, 42));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        rootPanel.add(searchField, BorderLayout.NORTH);

        // List model
        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setFont(UIConstants.FONT_BODY);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setFixedCellHeight(36);
        resultsList.setOpaque(false);

        // Custom renderer for premium look
        resultsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                PaletteItem item = (PaletteItem) value;
                setText(item.title + "   —   " + item.subtitle);
                if (isSelected) {
                    setBackground(UIConstants.GOLD_SUBTLE);
                    setForeground(UIConstants.GOLD_ACCENT);
                } else {
                    setBackground(new Color(0, 0, 0, 0));
                    setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        // Seed default action mappings
        initializeActions();
        filterItems("");

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    resultsList.requestFocus();
                    resultsList.setSelectedIndex(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                } else {
                    filterItems(searchField.getText().trim());
                }
            }
        });

        resultsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeSelected();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        resultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeSelected();
                }
            }
        });
    }

    private void initializeActions() {
        allItems.add(new PaletteItem("Go to Dashboard", "Navigate to home workspace panel", () -> triggerNav(0, "DASHBOARD", "Dashboard")));
        allItems.add(new PaletteItem("Go to Portfolio", "Navigate to holdings management", () -> triggerNav(1, "PORTFOLIO", "Portfolio")));
        allItems.add(new PaletteItem("Go to Milestones", "Track financial goals progress", () -> triggerNav(2, "GOALS", "Milestones")));
        allItems.add(new PaletteItem("Go to Calculators", "Open financial projection tools", () -> triggerNav(3, "CALCS", "Calculators")));
        allItems.add(new PaletteItem("Go to Statements", "Export PDF/Excel wealth statements", () -> triggerNav(4, "REPORTS", "Statements")));
        allItems.add(new PaletteItem("Go to AI Advisor", "Interact with chatbot advisor", () -> triggerNav(5, "CHATBOT", "AI Advisor")));
        allItems.add(new PaletteItem("Trigger AI Risk Profiler Wizard", "Start 15-question risk mapping", () -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                RiskProfilingDialog riskWizard = new RiskProfilingDialog(mainFrame);
                riskWizard.setVisible(true);
            });
        }));
        allItems.add(new PaletteItem("Switch Visual Theme Mode", "Toggle Light & Dark theme look", () -> {
            dispose();
            ThemeManager.toggleTheme();
            SwingUtilities.updateComponentTreeUI(mainFrame);
        }));
        allItems.add(new PaletteItem("Explain Stock: TCS", "Get AI business summary & metrics", () -> showStockDetails("TCS", "Tata Consultancy Services Ltd", "15% growth, high dividend yield", "3800")));
        allItems.add(new PaletteItem("Explain Stock: INFY", "Get AI business summary & metrics", () -> showStockDetails("INFY", "Infosys Ltd", "Solid cloud contracts pipeline", "1450")));
        allItems.add(new PaletteItem("Explain Stock: Reliance", "Get AI business summary & metrics", () -> showStockDetails("RELIANCE", "Reliance Industries Ltd", "Energy transition investments driving value", "2480")));
    }

    private void filterItems(String query) {
        listModel.clear();
        String q = query.toLowerCase();
        for (PaletteItem item : allItems) {
            if (item.title.toLowerCase().contains(q) || item.subtitle.toLowerCase().contains(q)) {
                listModel.addElement(item);
            }
        }
        if (listModel.size() > 0) {
            resultsList.setSelectedIndex(0);
        }
    }

    private void executeSelected() {
        PaletteItem selected = resultsList.getSelectedValue();
        if (selected != null) {
            selected.action.run();
        }
    }

    private void triggerNav(int navIdx, String cardName, String pageTitle) {
        dispose();
        // Invoke reflection/methods on MainFrame directly
        try {
            java.lang.reflect.Method setNav = MainFrame.class.getDeclaredMethod("setActiveNav", com.niveshcore360.components.NavButton.class);
            setNav.setAccessible(true);
            java.lang.reflect.Field buttonsField = MainFrame.class.getDeclaredField("navButtons");
            buttonsField.setAccessible(true);
            List<NavButton> list = (List<NavButton>) buttonsField.get(mainFrame);
            if (navIdx < list.size()) {
                setNav.invoke(mainFrame, list.get(navIdx));
            }
            java.lang.reflect.Method showCard = MainFrame.class.getDeclaredMethod("showWorkspaceCard", String.class);
            showCard.setAccessible(true);
            showCard.invoke(mainFrame, cardName);

            java.lang.reflect.Field pageTitleField = MainFrame.class.getDeclaredField("lblPageTitle");
            pageTitleField.setAccessible(true);
            JLabel lbl = (JLabel) pageTitleField.get(mainFrame);
            lbl.setText(pageTitle);
        } catch (Exception ex) {
            System.err.println("Reflective navigation failure in palette: " + ex.getMessage());
        }
    }

    private void showStockDetails(String symbol, String name, String details, String price) {
        dispose();
        JOptionPane.showMessageDialog(mainFrame,
            "<html><body>" +
            "<h2>Stock Explainer: " + symbol + " (" + name + ")</h2>" +
            "<p><b>Price:</b> ₹" + price + "</p>" +
            "<p><b>Strengths:</b> Industry-leading ROE, global footprint, solid margins.</p>" +
            "<p><b>Risks:</b> Global IT demand slowdown, FX headwind exposure.</p>" +
            "<p><b>Outlook:</b> " + details + "</p>" +
            "</body></html>",
            "Stock explainer: " + symbol,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static class PaletteItem {
        final String title;
        final String subtitle;
        final Runnable action;

        PaletteItem(String title, String subtitle, Runnable action) {
            this.title = title;
            this.subtitle = subtitle;
            this.action = action;
        }
    }
}
