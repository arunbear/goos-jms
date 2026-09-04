package org.example.auctionsniper;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsClient;
import org.springframework.test.annotation.DirtiesContext;

import javax.swing.*;

import java.awt.*;

import static org.assertj.core.api.BDDAssertions.as;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.example.auctionsniper.swing.ComponentFinder.findComponentByNameAsType;

@SpringBootTest
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
public class AuctionSniperEndToEndTest {

    @Autowired
    JmsClient jmsClient;

    @Autowired
    ConfigProperties configProperties;

    private static final Logger log = LoggerFactory.getLogger(AuctionSniperEndToEndTest.class);

    @BeforeAll
    public static void setupHeadlessMode() {
        // https://stackoverflow.com/a/52294064
        System.setProperty("java.awt.headless", "false");
    }

    @Test
    @DirtiesContext
    public void sniper_joins_auction_until_auction_closes(ApplicationContext context) {

        // given
        var app = context.getBean(MainWindow.class);
        then(app.isVisible()).isTrue();
        then(app.getName()).isEqualTo("Auction Sniper Main");

        app_has_shown_sniper_is_joining_auction(app);
        auction_has_received_joining_message_from_sniper();

        // when
        auctionAnnouncesItHasClosed();
        // then
        app_shows_sniper_has_lost_auction(app);
    }

    @Test
    @DirtiesContext
    public void sniper_makes_a_higher_bid_but_loses(ApplicationContext context) {
        // given
        var app = context.getBean(MainWindow.class);
        // then
        app_has_shown_sniper_is_joining_auction(app);
        auction_has_received_joining_message_from_sniper();

        // when
        auctionReportsPrice(1000, 98, "other bidder");
        // then
        auction_has_received_bid(1098);
        app_has_shown_sniper_is_bidding(app);

        // and when
        auctionAnnouncesItHasClosed();
        app_shows_sniper_has_lost_auction(app);
    }

    @Test
    @DirtiesContext
    void sniper_wins_an_auction_by_bidding_higher(ApplicationContext context) {
        // given
        final var sniperId = configProperties.sniper().id();
        var app = context.getBean(MainWindow.class);
          app_has_shown_sniper_is_joining_auction(app);
          auction_has_received_joining_message_from_sniper();

        var When = this;
        When.auctionReportsPrice(1000, 98, "other bidder");
          auction_has_received_bid(1098);
          app_has_shown_sniper_is_bidding(app);

        When.auctionReportsPrice(1098, 97, sniperId);
          app_has_shown_sniper_is_winning(app);

        When.auctionAnnouncesItHasClosed();
          app_shows_sniper_has_won_auction(app); // fails
    }

    private void auction_has_received_bid(int bid) {
        // when
        var message = jmsClient
            .destination(configProperties.auction().queue())
            .withReceiveTimeout(1000)
            .receive(String.class);

        then(message)
            .get(as(InstanceOfAssertFactories.STRING))
            .isEqualTo("SOLVersion: 1.1; Command: BID; Price: %d;".formatted(bid));
    }

    private void app_has_shown_sniper_is_joining_auction(Container app) {
        // when
        var status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);

        then(status.getText()).isEqualTo(MainWindow.STATUS_JOINING);
    }

    private void app_has_shown_sniper_is_winning(Container app) {
        // when
        var status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);

        await().untilAsserted(() -> {
            then(status.getText()).isEqualTo(MainWindow.STATUS_WINNING);
        });
    }

    private void app_shows_sniper_has_won_auction(Container app) {
        // when
        var status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);

        await().untilAsserted(() -> {
            then(status.getText()).isEqualTo(MainWindow.STATUS_WON);
        });
    }

    private void auction_has_received_joining_message_from_sniper() {
        // when
        var message = jmsClient.destination(configProperties.auction().queue())
            .withReceiveTimeout(1000)
            .receive(String.class);

        then(message).isNotEmpty();
    }

    private void app_shows_sniper_has_lost_auction(Container app) {
        log.info("Checking sniper status");
        JLabel status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);

        await().untilAsserted(() -> {
            // Wait for the sniper to get the message, otherwise we won't detect the status change.
            // In the book they use an external XMPP server for messaging, which introduces an actual delay.
            // We don't have that here due to using an embedded JMS broker.

            then(status.getText()).isEqualTo(MainWindow.STATUS_LOST);
        });
    }

    private void app_has_shown_sniper_is_bidding(Container app) {
        log.info("Checking sniper status");
        JLabel status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);

        then(status.getText()).isEqualTo(MainWindow.STATUS_BIDDING);
    }

    void auctionAnnouncesItHasClosed() {
        var message = "SOLVersion: 1.1; Event: CLOSE;";
        jmsClient.destination(configProperties.sniper().queue()).send(message);
    }

    private void auctionReportsPrice(int price, int increment, String bidder)  {
        jmsClient
            .destination(configProperties.sniper().queue())
            .send(
        "SOLVersion: 1.1; Event: PRICE; "
              + "CurrentPrice: %d; Increment: %d; Bidder: %s".formatted(price, increment, bidder)
            );
    }
}
