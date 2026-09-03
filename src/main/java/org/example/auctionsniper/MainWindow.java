package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
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
    public static final String STATUS_BIDDING = "BIDDING";
    public static final String STATUS_WINNING = "WINNING";
    public static final String STATUS_WON = "WON";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    private final Auction auction;
    private final AuctionMessageTranslator messageTranslator;

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    public MainWindow(Auction auction) throws HeadlessException {
        super("Auction Sniper");
        this.auction = auction;
        auction.join();

        final var sniperId = "sniper-1"; // todo fix hard coding
        messageTranslator = new AuctionMessageTranslator(
            sniperId,
            new AuctionSniper(auction, new SniperStateDisplayer())
        );
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @JmsListener(destination = "${messaging.sniper.queue}")
    public void receiveMessage(@Payload(required = false) String message) {
        logger.info("Received a message: {}", message);
        messageTranslator.processMessage(message);
    }

    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public class SniperStateDisplayer implements SniperListener {

        @Override
        public void sniperLost() {
            showStatus(STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(STATUS_BIDDING);
        }

        @Override
        public void sniperWinning() {
            showStatus(STATUS_WINNING);
        }

        private void showStatus(String status) {
            sniperStatus.setText(status);
        }
    }
}
