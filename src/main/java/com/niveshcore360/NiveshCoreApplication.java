package com.niveshcore360;

import com.formdev.flatlaf.FlatDarkLaf;
import com.niveshcore360.view.MainFrame;
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

        // Boot Spring Application context in a Swing-compatible non-headless mode
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NiveshCoreApplication.class)
                .headless(false)
                .run(args);

        // Schedule MainFrame display on the Event Dispatch Thread (EDT) for thread-safety
        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });
    }
}
