package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener sniperListener;

    private static final Logger logger = LoggerFactory.getLogger(AuctionSniper.class);

    public AuctionSniper(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }

    @Override
    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int currentPrice, int increment) {
        logger.warn("not implemented yet");
    }
}
