package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener sniperListener;
    private final Auction auction;

    private static final Logger logger = LoggerFactory.getLogger(AuctionSniper.class);

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.sniperListener = sniperListener;
        this.auction = auction;
    }

    @Override
    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FROM_OTHER_BIDDER -> {
                auction.bid(price + increment);
                sniperListener.sniperBidding();
            }
            case FROM_SNIPER -> sniperListener.sniperWinning();
        }
    }
}
