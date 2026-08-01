package org.example.auctionsniper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sniper.demo")
public record ConfigProperties(String queue) {
}
