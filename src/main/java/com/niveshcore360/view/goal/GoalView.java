package com.niveshcore360.view.goal;

import com.niveshcore360.components.CardPanel;
import com.niveshcore360.components.RoundedButton;
import com.niveshcore360.components.ThemeManager;
import com.niveshcore360.constants.UIConstants;
import com.niveshcore360.controller.GoalController;
import com.niveshcore360.controller.PortfolioController;
import com.niveshcore360.dto.GoalDTO;
import com.niveshcore360.entity.GoalStatus;
import com.niveshcore360.entity.Portfolio;
import com.niveshcore360.security.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Goal View visualizing savings milestones using card progress lists.
 */
@Component
public class GoalView extends JPanel {

    private final GoalController goalController;
    private final PortfolioController portfolioController;
    private final UserSession userSession;

    private JComboBox<PortfolioItem> comboPortfolios;
    private JPanel cardsGridPanel;

    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public GoalView(GoalController goalController,
                    PortfolioController portfolioController,
                    UserSession userSession) {
        this.goalController = goalController;
        this.portfolioController = portfolioController;
        this.userSession = userSession;

        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setOpaque(false);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Financial Milestones Tracker");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(180, 36));
        comboPortfolios.addActionListener(e -> reloadGoals());
        controls.add(comboPortfolios);

        RoundedButton btnAddGoal = new RoundedButton("New Goal");
        btnAddGoal.setPreferredSize(new Dimension(100, 36));
        btnAddGoal.addActionListener(e -> openGoalFormDialog(null));
        controls.add(btnAddGoal);

        headerPanel.add(controls, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Scroll pane for cards
        cardsGridPanel = new JPanel(new GridBagLayout());
        cardsGridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setupGoalViewData() {
        if (!userSession.isLoggedIn()) return;
        comboPortfolios.removeAllItems();
        List<Portfolio> list = portfolioController.getPortfolios();
        for (Portfolio p : list) {
            comboPortfolios.addItem(new PortfolioItem(p.getId(), p.getName()));
        }
        reloadGoals();
    }

    private void reloadGoals() {
        cardsGridPanel.removeAll();
        PortfolioItem selected = (PortfolioItem) comboPortfolios.getSelectedItem();
        if (selected == null) {
            cardsGridPanel.revalidate();
            cardsGridPanel.repaint();
            return;
        }

        try {
            List<GoalDTO> goals = goalController.getGoals(selected.id);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            int row = 0;
            if (goals.isEmpty()) {
                JLabel emptyLbl = new JLabel("No financial goals configured for this portfolio.", JLabel.CENTER);
                emptyLbl.setFont(UIConstants.FONT_BODY);
                emptyLbl.setForeground(UIConstants.LIGHT_TEXT_MUTED);
                gbc.gridx = 0; gbc.gridy = 0;
                cardsGridPanel.add(emptyLbl, gbc);
            } else {
                for (GoalDTO goal : goals) {
                    gbc.gridx = 0; gbc.gridy = row++;
                    cardsGridPanel.add(createGoalCard(goal), gbc);
                }
                
                // Add vertical push space
                gbc.gridy = row;
                gbc.weighty = 1.0;
                cardsGridPanel.add(Box.createGlue(), gbc);
            }
            
            cardsGridPanel.revalidate();
            cardsGridPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load goals: " + ex.getMessage());
        }
    }

    private JPanel createGoalCard(GoalDTO goal) {
        CardPanel panel = new CardPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 150));

        // Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        detailsPanel.setOpaque(false);

        JLabel lblName = new JLabel(goal.getName());
        lblName.setFont(UIConstants.FONT_HEADER);
        lblName.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);

        JLabel lblMeta = new JLabel("Target: " + currencyFormatter.format(goal.getTargetAmount()) + 
                "  |  Target Date: " + goal.getTargetDate().format(dateFormatter) + 
                "  |  Remaining: " + goal.getDaysRemaining() + " days");
        lblMeta.setFont(UIConstants.FONT_BODY);
        lblMeta.setForeground(UIConstants.LIGHT_TEXT_MUTED);

        detailsPanel.add(lblName);
        detailsPanel.add(lblMeta);
        panel.add(detailsPanel, BorderLayout.NORTH);

        // Progress Section
        JPanel progressPanel = new JPanel(new BorderLayout(6, 6));
        progressPanel.setOpaque(false);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) goal.getProgressPercentage());
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 22));
        progressBar.setForeground(UIConstants.PROFIT_GREEN);
        
        JLabel lblProgressVal = new JLabel("Current: " + currencyFormatter.format(goal.getCurrentAmount()) + 
                " (" + String.format("%.1f", goal.getProgressPercentage()) + "%)");
        lblProgressVal.setFont(UIConstants.FONT_BODY);
        lblProgressVal.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(lblProgressVal, BorderLayout.SOUTH);
        panel.add(progressPanel, BorderLayout.CENTER);

        // Action Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnAddFunds = new RoundedButton("Add Funds");
        btnAddFunds.setPreferredSize(new Dimension(100, 32));
        btnAddFunds.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter savings amount to deposit:", "Add Funds to Milestone", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    BigDecimal deposit = new BigDecimal(input.trim());
                    if (deposit.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount must be positive.");
                    goalController.updateGoal(goal.getId(), goal.getName(), goal.getTargetAmount(), goal.getCurrentAmount().add(deposit), goal.getTargetDate());
                    reloadGoals();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Input Error: " + ex.getMessage());
                }
            }
        });
        btnPanel.add(btnAddFunds);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setPreferredSize(new Dimension(70, 32));
        btnEdit.addActionListener(e -> openGoalFormDialog(goal));
        btnPanel.add(btnEdit);

        JButton btnDelete = new JButton("Remove");
        btnDelete.setPreferredSize(new Dimension(85, 32));
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this goal?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    goalController.deleteGoal(goal.getId());
                    reloadGoals();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
                }
            }
        });
        btnPanel.add(btnDelete);

        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    private void openGoalFormDialog(GoalDTO editDto) {
        PortfolioItem selected = (PortfolioItem) comboPortfolios.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an active portfolio first.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editDto == null ? "Configure Goal Milestone" : "Edit Goal Details", true);
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Goal Name:"), gbc);

        JTextField txtName = new JTextField();
        gbc.gridx = 1;
        dialog.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Target Amount:"), gbc);

        JTextField txtTarget = new JTextField();
        gbc.gridx = 1;
        dialog.add(txtTarget, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Current Saved:"), gbc);

        JTextField txtCurrent = new JTextField("0.00");
        gbc.gridx = 1;
        dialog.add(txtCurrent, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Target Date (YYYY-MM-DD):"), gbc);

        JTextField txtDate = new JTextField(LocalDate.now().plusYears(1).format(dateFormatter));
        gbc.gridx = 1;
        dialog.add(txtDate, gbc);

        if (editDto != null) {
            txtName.setText(editDto.getName());
            txtTarget.setText(editDto.getTargetAmount().toString());
            txtCurrent.setText(editDto.getCurrentAmount().toString());
            txtDate.setText(editDto.getTargetDate().format(dateFormatter));
        }

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 12, 6, 12);
        RoundedButton btnSave = new RoundedButton("Save Goal");
        dialog.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                BigDecimal target = new BigDecimal(txtTarget.getText().trim());
                BigDecimal current = new BigDecimal(txtCurrent.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), dateFormatter);

                if (name.isEmpty()) throw new IllegalArgumentException("Goal name cannot be empty.");
                if (target.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Target must be greater than zero.");
                if (current.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Current savings cannot be negative.");

                if (editDto == null) {
                    goalController.createGoal(selected.id, name, target, current, date);
                } else {
                    goalController.updateGoal(editDto.getId(), name, target, current, date);
                }

                dialog.dispose();
                reloadGoals();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private static class PortfolioItem {
        final Long id;
        final String name;

        PortfolioItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
