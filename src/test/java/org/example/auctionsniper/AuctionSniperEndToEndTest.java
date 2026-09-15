package org.example.auctionsniper;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsClient;
import org.springframework.test.annotation.DirtiesContext;

import javax.swing.*;

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

    private MainWindow app;

    @BeforeAll
    public static void setupHeadlessMode() {
        // https://stackoverflow.com/a/52294064
        System.setProperty("java.awt.headless", "false");
    }

    @BeforeEach
    public void setUp(ApplicationContext context) {
        app = context.getBean(MainWindow.class);
    }

    @Test
    @DirtiesContext
    public void sniper_joins_auction_until_auction_closes() {

        then(app.isVisible()).isTrue();
        then(app.getName()).isEqualTo("Auction Sniper Main");

        app_has_shown_sniper_is_joining_auction();
        auction_has_received_joining_message_from_sniper();

        // when
        auctionAnnouncesItHasClosed();
        // then
        app_shows_sniper_has_lost_auction();
    }

    @Test
    @DirtiesContext
    public void sniper_makes_a_higher_bid_but_loses() {
        // then
        app_has_shown_sniper_is_joining_auction();
        auction_has_received_joining_message_from_sniper();

        // when
        auctionReportsPrice(1000, 98, "other bidder");
        // then
        auction_has_received_bid(1098);
        app_has_shown_sniper_is_bidding();

        // and when
        auctionAnnouncesItHasClosed();
        app_shows_sniper_has_lost_auction();
    }

    @Test
    @DirtiesContext
    void sniper_wins_an_auction_by_bidding_higher() {
        // given
        final var sniperId = configProperties.sniper().id();
          app_has_shown_sniper_is_joining_auction();
          auction_has_received_joining_message_from_sniper();

        var When = this;
        When.auctionReportsPrice(1000, 98, "other bidder");
          auction_has_received_bid(1098);
          app_has_shown_sniper_is_bidding();

        When.auctionReportsPrice(1098, 97, sniperId);
          app_has_shown_sniper_is_winning();

        When.auctionAnnouncesItHasClosed();
          app_shows_sniper_has_won_auction();
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

    private void app_has_shown_sniper_is_joining_auction() {
        shows_sniper_status(MainWindow.STATUS_JOINING);
    }

    private void app_has_shown_sniper_is_winning() {
        shows_sniper_status(MainWindow.STATUS_WINNING);
    }

    private void app_shows_sniper_has_won_auction() {
        shows_sniper_status(MainWindow.STATUS_WON);
    }

    private void auction_has_received_joining_message_from_sniper() {
        // when
        var message = jmsClient.destination(configProperties.auction().queue())
            .withReceiveTimeout(1000)
            .receive(String.class);

        then(message).isNotEmpty();
    }

    private void app_shows_sniper_has_lost_auction() {
        shows_sniper_status(MainWindow.STATUS_LOST);
    }

    private void app_has_shown_sniper_is_bidding() {
        shows_sniper_status(MainWindow.STATUS_BIDDING);
    }

    private void shows_sniper_status(String expectedStatus) {
        // when
        var table = findComponentByNameAsType(this.app, MainWindow.SNIPERS_TABLE_NAME, JTable.class);

        await().untilAsserted(() -> {
            // Wait for the sniper to get the message, otherwise we won't detect the status change.
            // In the book they use an external XMPP server for messaging, which introduces a longer delay
            // than we have here due to using an embedded JMS broker.

            final int row = 0, column = 0;
            then(table.getValueAt(row, column)).isEqualTo(expectedStatus);
        });
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
