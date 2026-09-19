package org.example.auctionsniper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionSniper implements AuctionEventListener {
    private final String itemId;
    private final SniperListener sniperListener;
    private final Auction auction;
    private boolean isWinning;

    private static final Logger logger = LoggerFactory.getLogger(AuctionSniper.class);

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.itemId = itemId;
        this.sniperListener = sniperListener;
        this.auction = auction;
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        }
        else {
            sniperListener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FROM_SNIPER;

        if (isWinning) {
            sniperListener.sniperWinning();
        }
        else {
            int bid = price + increment;
            auction.bid(bid);
            sniperListener.sniperBidding(new SniperState(itemId, price, bid));
        }
    }
}
