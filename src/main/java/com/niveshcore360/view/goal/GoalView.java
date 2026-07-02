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
 * Goal View with circular progress rings, status chips, and premium goal cards.
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

        setLayout(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_MD));
        setBorder(new EmptyBorder(UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG, UIConstants.SPACE_LG));
        setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Financial Milestones Tracker");
        lblTitle.setFont(UIConstants.FONT_DISPLAY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        comboPortfolios = new JComboBox<>();
        comboPortfolios.setPreferredSize(new Dimension(180, 36));
        comboPortfolios.addActionListener(e -> reloadGoals());
        controls.add(comboPortfolios);

        RoundedButton btnAddGoal = new RoundedButton("New Goal");
        btnAddGoal.setPreferredSize(new Dimension(110, 36));
        btnAddGoal.addActionListener(e -> openGoalFormDialog(null));
        controls.add(btnAddGoal);

        headerPanel.add(controls, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Cards grid
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
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            int row = 0;
            if (goals.isEmpty()) {
                JLabel emptyLbl = new JLabel("No financial goals configured. Create your first milestone!", JLabel.CENTER);
                emptyLbl.setFont(UIConstants.FONT_BODY);
                emptyLbl.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
                emptyLbl.setBorder(new EmptyBorder(40, 0, 0, 0));
                gbc.gridx = 0; gbc.gridy = 0;
                cardsGridPanel.add(emptyLbl, gbc);
            } else {
                for (GoalDTO goal : goals) {
                    gbc.gridx = 0; gbc.gridy = row++;
                    cardsGridPanel.add(createGoalCard(goal), gbc);
                }

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
        CardPanel panel = new CardPanel(new BorderLayout(UIConstants.SPACE_MD, UIConstants.SPACE_SM), UIConstants.SPACE_LG);
        panel.setPreferredSize(new Dimension(600, 140));

        // Left: Progress Ring
        double progress = goal.getProgressPercentage();
        JPanel ringPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 70, pad = 10;
                int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;

                // Track
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(ThemeManager.isDarkMode() ? UIConstants.DARK_BORDER : UIConstants.LIGHT_BORDER);
                g2.drawArc(x, y, size, size, 0, 360);

                // Progress arc
                Color arcColor = progress >= 100 ? UIConstants.GOLD_ACCENT :
                                 progress >= 50 ? UIConstants.PROFIT_GREEN : UIConstants.WARN_AMBER;
                g2.setColor(arcColor);
                int angle = (int) (progress * 3.6);
                g2.drawArc(x, y, size, size, 90, -angle);

                // Center text
                g2.setFont(UIConstants.FONT_BUTTON);
                g2.setColor(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_PRIMARY : UIConstants.LIGHT_TEXT_PRIMARY);
                String pctText = String.format("%.0f%%", progress);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth(pctText)) / 2;
                int ty = y + (size + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(pctText, tx, ty);

                g2.dispose();
            }
        };
        ringPanel.setOpaque(false);
        ringPanel.setPreferredSize(new Dimension(100, 100));
        panel.add(ringPanel, BorderLayout.WEST);

        // Center: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel lblName = new JLabel(goal.getName());
        lblName.setFont(UIConstants.FONT_SUBHEADING);
        lblName.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        detailsPanel.add(lblName);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblTarget = new JLabel("Target: " + currencyFormatter.format(goal.getTargetAmount()));
        lblTarget.setFont(UIConstants.FONT_BODY);
        lblTarget.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        lblTarget.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        detailsPanel.add(lblTarget);

        JLabel lblCurrent = new JLabel("Current: " + currencyFormatter.format(goal.getCurrentAmount()));
        lblCurrent.setFont(UIConstants.FONT_BODY);
        lblCurrent.setForeground(UIConstants.PROFIT_GREEN);
        lblCurrent.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        detailsPanel.add(lblCurrent);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        // Status chip + days remaining
        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chipRow.setOpaque(false);
        chipRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        String statusText = progress >= 100 ? "Achieved" : progress >= 50 ? "On Track" : "Behind";
        Color chipColor = progress >= 100 ? UIConstants.GOLD_ACCENT : progress >= 50 ? UIConstants.PROFIT_GREEN : UIConstants.WARN_AMBER;

        JLabel chip = new JLabel(statusText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(chipColor.getRed(), chipColor.getGreen(), chipColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(UIConstants.FONT_CAPTION);
        chip.setForeground(chipColor);
        chip.setBorder(new EmptyBorder(2, 8, 2, 8));
        chip.setOpaque(false);
        chipRow.add(chip);

        JLabel daysLbl = new JLabel(goal.getDaysRemaining() + " days remaining");
        daysLbl.setFont(UIConstants.FONT_CAPTION);
        daysLbl.setForeground(ThemeManager.isDarkMode() ? UIConstants.DARK_TEXT_MUTED : UIConstants.LIGHT_TEXT_MUTED);
        chipRow.add(daysLbl);

        detailsPanel.add(chipRow);
        panel.add(detailsPanel, BorderLayout.CENTER);

        // Right: Action buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        RoundedButton btnAddFunds = new RoundedButton("Add Funds");
        btnAddFunds.setPreferredSize(new Dimension(100, 30));
        btnAddFunds.setMaximumSize(new Dimension(110, 32));
        btnAddFunds.setFont(UIConstants.FONT_CAPTION);
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
        btnPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        RoundedButton btnEdit = RoundedButton.secondary("Edit");
        btnEdit.setPreferredSize(new Dimension(100, 30));
        btnEdit.setMaximumSize(new Dimension(110, 32));
        btnEdit.setFont(UIConstants.FONT_CAPTION);
        btnEdit.addActionListener(e -> openGoalFormDialog(goal));
        btnPanel.add(btnEdit);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        RoundedButton btnDelete = RoundedButton.danger("Remove");
        btnDelete.setPreferredSize(new Dimension(100, 30));
        btnDelete.setMaximumSize(new Dimension(110, 32));
        btnDelete.setFont(UIConstants.FONT_CAPTION);
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
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Goal Name:");
        l1.setFont(UIConstants.FONT_BODY);
        dialog.add(l1, gbc);
        JTextField txtName = new JTextField();
        txtName.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Target Amount:");
        l2.setFont(UIConstants.FONT_BODY);
        dialog.add(l2, gbc);
        JTextField txtTarget = new JTextField();
        txtTarget.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtTarget, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("Current Saved:");
        l3.setFont(UIConstants.FONT_BODY);
        dialog.add(l3, gbc);
        JTextField txtCurrent = new JTextField("0.00");
        txtCurrent.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtCurrent, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel l4 = new JLabel("Target Date (YYYY-MM-DD):");
        l4.setFont(UIConstants.FONT_BODY);
        dialog.add(l4, gbc);
        JTextField txtDate = new JTextField(LocalDate.now().plusYears(1).format(dateFormatter));
        txtDate.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1;
        dialog.add(txtDate, gbc);

        if (editDto != null) {
            txtName.setText(editDto.getName());
            txtTarget.setText(editDto.getTargetAmount().toString());
            txtCurrent.setText(editDto.getCurrentAmount().toString());
            txtDate.setText(editDto.getTargetDate().format(dateFormatter));
        }

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 16, 8, 16);
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
