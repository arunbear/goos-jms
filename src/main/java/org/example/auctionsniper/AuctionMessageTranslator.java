package org.example.auctionsniper;

public class AuctionMessageTranslator {
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    public void processMessage(String message) {
        listener.auctionClosed();
    }
}
