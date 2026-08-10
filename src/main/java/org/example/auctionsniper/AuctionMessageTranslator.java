package org.example.auctionsniper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuctionMessageTranslator {
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    public void processMessage(String message) {
        Map<String, String> event = unpackEventFrom(message);
        String type = event.get("Event");

        switch (type) {
            case "CLOSE" -> listener.auctionClosed();
            case null, default -> {
            }
        }
    }

    private Map<String, String> unpackEventFrom(String message) {
        return Stream
            .of(message.split(";"))
            .map(element -> element.split(":"))
            .collect(Collectors.toMap(pair -> pair[0].trim(), pair -> pair[1].trim()));
    }
}
