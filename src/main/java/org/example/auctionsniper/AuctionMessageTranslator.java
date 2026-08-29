package org.example.auctionsniper;

import org.example.auctionsniper.AuctionEventListener.PriceSource;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class AuctionMessageTranslator {
    private final String sniperId;
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    public void processMessage(String message) {
        var event = AuctionEvent.from(message);

        switch (event.type()) {
            case "CLOSE" -> listener.auctionClosed();
            case "PRICE" -> {
                listener.currentPrice(event.currentPrice(), event.increment(), priceSourceOf(event));
            }
            default -> {
            }
        }
    }

    private PriceSource priceSourceOf(AuctionEvent event) {
        return event.bidder().equals(sniperId)
                ? PriceSource.FROM_SNIPER
                : PriceSource.FROM_OTHER_BIDDER;
    }

    private static class AuctionEvent {
        private final Map<String, String> fields;

        public static AuctionEvent from(String message) {
            return new AuctionEvent(message);
        }

        public String type() {
            return Optional.ofNullable(fields.get("Event")).orElseThrow();
        }

        public int currentPrice() {
            return Integer.parseInt(fields.get("CurrentPrice"));
        }

        public int increment() {
            return Integer.parseInt(fields.get("Increment"));
        }

        public String bidder() {
            return Optional.ofNullable(fields.get("Bidder")).orElseThrow();
        }

        private AuctionEvent(String source) {
            fields = unpackEventFrom(source);
        }

        private Map<String, String> unpackEventFrom(String message) {
            return Stream
                .of(message.split(";"))
                .map(element -> element.split(":"))
                .collect(
                    toMap(
                    pair -> pair[0].trim(),
                    pair -> pair[1].trim()));
        }
    }
}
