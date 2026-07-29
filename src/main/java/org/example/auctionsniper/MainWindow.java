package org.example.auctionsniper;

import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

@Controller
public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String STATUS_JOINING = "JOINING";
    public static final String STATUS_LOST = "LOST";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    public MainWindow() throws HeadlessException {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public void showStatus(String status) {
        sniperStatus.setText(status);
    }
}
