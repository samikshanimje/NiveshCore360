package com.niveshcore360;

import com.formdev.flatlaf.FlatDarkLaf;
import com.niveshcore360.view.MainFrame;
import com.niveshcore360.view.splash.SplashWindow;
import com.niveshcore360.service.MarketService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import java.awt.EventQueue;

/**
 * Main launcher class for NiveshCore360 Desktop Application.
 */
@SpringBootApplication
public class NiveshCoreApplication {

    public static void main(String[] args) {
        // Set Look and Feel to FlatDarkLaf for premium dark theme styling
        FlatDarkLaf.setup();

        // 1. Instantly display splash screen
        SplashWindow splash = new SplashWindow();
        splash.setVisible(true);
        splash.updateProgress(15, "Initializing Java Runtime...");

        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}

        splash.updateProgress(35, "Configuring Spring Context Boot...");

        // 2. Boot Spring Application context in a Swing-compatible non-headless mode
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NiveshCoreApplication.class)
                .headless(false)
                .run(args);

        splash.updateProgress(65, "Initializing Database & Seeding Assets...");

        // Dynamically invoke market database seeding
        try {
            MarketService marketService = context.getBean(MarketService.class);
            marketService.seedMarketAssets();
        } catch (Exception e) {
            System.err.println("Market seeding failure: " + e.getMessage());
        }

        splash.updateProgress(85, "Building Premium Workspaces...");
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}

        splash.updateProgress(100, "Starting NiveshCore360!");

        // 3. Schedule MainFrame display on the Event Dispatch Thread (EDT) and dispose splash
        EventQueue.invokeLater(() -> {
            splash.setVisible(false);
            splash.dispose();
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });
    }
}
