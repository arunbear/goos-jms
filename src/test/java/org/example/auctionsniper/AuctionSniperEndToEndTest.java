package org.example.auctionsniper;

import org.example.auctionsniper.swing.ComponentFinder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsClient;

import javax.swing.*;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
public class AuctionSniperEndToEndTest {

    @Autowired
    JmsClient jmsClient;

    @BeforeAll
    public static void setupHeadlessMode() {
        // https://stackoverflow.com/a/52294064
        System.setProperty("java.awt.headless", "false");
    }

    @Test
    public void sniper_joins_auction_until_auction_closes(ApplicationContext context) {
        // sniper shows it has joined auction

        // given
        var app = context.getBean(MainWindow.class);
        then(app.isVisible()).isTrue();
        then(app.getName()).isEqualTo("Auction Sniper Main");

        // when
        JLabel status = ComponentFinder.findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);
        then(status.getText()).isEqualTo(MainWindow.STATUS_JOINING);

        // verify that sniper has joined the auction
        var message = jmsClient.destination("demo-queue").withReceiveTimeout(1000).receive(String.class);
        then(message).as("Got joining message from Sniper").isNotEmpty();
    }

}
