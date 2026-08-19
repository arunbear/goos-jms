package org.example.auctionsniper;

import org.springframework.jms.core.JmsClient;

class JMSAuction implements Auction {
    private final JmsClient jmsClient;
    private final ConfigProperties properties;

    public JMSAuction(JmsClient jmsClient, ConfigProperties properties) {
        this.jmsClient = jmsClient;
        this.properties = properties;
    }

    @Override
    public void bid(int amount) {
        var bidMessage = "SOLVersion: 1.1; Command: BID; Price: %d;".formatted(amount);
        jmsClient.destination(properties.auction().queue()).send(bidMessage);
    }

    @Override
    public void join() {
        jmsClient.destination(properties.auction().queue()).send("");
    }
}
