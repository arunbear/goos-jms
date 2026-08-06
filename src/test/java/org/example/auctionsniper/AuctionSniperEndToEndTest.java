package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsClient;

import javax.swing.*;

import java.util.Optional;

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
    public void sniper_joins_auction_until_auction_closes(ApplicationContext context) {

        // given
        var app = context.getBean(MainWindow.class);
        then(app.isVisible()).isTrue();
        then(app.getName()).isEqualTo("Auction Sniper Main");

        var nextStep = "Sniper shows it is joining auction";
        // when
        JLabel status = findComponentByNameAsType(app, MainWindow.SNIPER_STATUS_NAME, JLabel.class);
        then(status.getText())
            .as(nextStep)
            .isEqualTo(MainWindow.STATUS_JOINING);

        nextStep = "Got joining message from Sniper";
        // when
        var message = auctionChecksForJoiningMessageFromSniper();
        then(message).as(nextStep).isNotEmpty();

        var finalStep = "Sniper shows it has lost auction";
        // when
        auctionAnnouncesItHasClosed();
        log.info(finalStep);
        await().untilAsserted(() -> {
            // Wait for the sniper to get the message, otherwise we won't detect the status change.
            // In the book they use an external XMPP server for messaging, which introduces an actual delay.
            // We don't have that here due to using an embedded JMS broker.
            then(status.getText())
                .as(finalStep)
                .isEqualTo(MainWindow.STATUS_LOST);
        });
    }

    Optional<String> auctionChecksForJoiningMessageFromSniper() {
        return jmsClient.destination(configProperties.auction().queue())
            .withReceiveTimeout(1000)
            .receive(String.class);
    }

    void auctionAnnouncesItHasClosed() {
        jmsClient.destination(configProperties.sniper().queue()).send("");
    }

}
