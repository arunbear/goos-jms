package org.example.auctionsniper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * This CommandLineRunner fires off at runtime and boots up our GUI.
 */
@Component
public class SpringBootSwingCommandLineRunner implements CommandLineRunner {
    private final MainWindow controller;

    @Autowired
    public SpringBootSwingCommandLineRunner(MainWindow controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) {
        //This boots up the GUI.
        EventQueue.invokeLater(() -> {
            controller.setVisible(true);
        });
    }
}
