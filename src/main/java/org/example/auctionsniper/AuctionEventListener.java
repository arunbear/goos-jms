package org.example.auctionsniper;

public interface AuctionEventListener {
    enum PriceSource {
        FROM_OTHER_BIDDER
    }
    void auctionClosed();

    void currentPrice(int currentPrice, int increment, PriceSource fromOtherBidder);
}
