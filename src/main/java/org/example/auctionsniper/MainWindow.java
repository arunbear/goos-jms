package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsClient;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

@Controller
public class MainWindow extends JFrame implements SniperListener {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String STATUS_JOINING = "JOINING";
    public static final String STATUS_LOST = "LOST";
    public static final String STATUS_BIDDING = "BIDDING";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    private final ConfigProperties properties;
    private final JmsClient jmsClient;

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    public MainWindow(ConfigProperties properties, JmsClient jmsClient) throws HeadlessException {
        this.properties = properties;
        this.jmsClient = jmsClient;

        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void joinAuction() {
        jmsClient.destination(properties.auction().queue()).send("");
    }

    @JmsListener(destination = "${messaging.sniper.queue}")
    public void receiveMessage(@Payload(required = false) String message) {
        logger.info("Received a message: {}", message);
        var messageTranslator =
            new AuctionMessageTranslator(
                new AuctionSniper(this, _ -> { })
            );
        messageTranslator.processMessage(message);
    }

    @Override
    public void sniperLost() {
        showStatus(STATUS_LOST);
    }

    @Override
    public void sniperBidding() {
        logger.warn("not implemented yet");
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
