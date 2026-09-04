package org.example.auctionsniper;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties(prefix = "messaging")
public record ConfigProperties(Auction auction, Sniper sniper) {
    public record Auction(String queue) {

    }
    public record Sniper(String queue, String id) {
        public Sniper {
            requireNonNull(queue, "queue is required");
            requireNonNull(id, "id is required");
        }
    }
}
