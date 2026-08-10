package org.example.auctionsniper;

public class AuctionMessageTranslator {
    private final AuctionMessageListener listener;

    public AuctionMessageTranslator(AuctionMessageListener listener) {
        this.listener = listener;
    }

    public void processMessage(String message) {
        listener.auctionClosed();
    }
}
