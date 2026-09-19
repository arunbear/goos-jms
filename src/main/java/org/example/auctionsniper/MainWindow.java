package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

@Controller
public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String STATUS_JOINING = "JOINING";
    public static final String STATUS_LOST = "LOST";
    public static final String STATUS_BIDDING = "BIDDING";
    public static final String STATUS_WINNING = "WINNING";
    public static final String STATUS_WON = "WON";
    public static final String SNIPERS_TABLE_NAME = "Snipers";

    private final SnipersTableModel snipers = new SnipersTableModel();

    private final Auction auction;
    private final AuctionMessageTranslator messageTranslator;
    private final ConfigProperties properties;

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    public MainWindow(Auction auction, ConfigProperties properties) throws HeadlessException {
        super("Auction Sniper");
        this.auction = auction;
        this.properties = properties;
        auction.join();

        messageTranslator = new AuctionMessageTranslator(
            properties.sniper().id(),
            new AuctionSniper(auction, new SniperStateDisplayer())
        );
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @JmsListener(destination = "${messaging.sniper.queue}")
    public void receiveMessage(@Payload(required = false) String message) {
        logger.info("Received a message: {}", message);
        messageTranslator.processMessage(message);
    }

    private void fillContentPane(JTable snipersTable) {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public class SniperStateDisplayer implements SniperListener {

        @Override
        public void sniperLost() {
            showStatus(STATUS_LOST);
        }

        @Override
        public void sniperBidding(SniperState sniperState) {
            showStatus(STATUS_BIDDING);
        }

        @Override
        public void sniperWinning() {
            showStatus(STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(STATUS_WON);
        }

        private void showStatus(String status) {
            snipers.setStatusText(status);
        }
    }

    static class SnipersTableModel extends AbstractTableModel {

        private String statusText = STATUS_JOINING;

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return statusText;
        }

        public void setStatusText(String newStatus) {
             statusText = newStatus;
        }
    }
}
