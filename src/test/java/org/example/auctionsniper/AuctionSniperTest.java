package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
public class AuctionSniperTest {

    @Mock
    private Auction auction;

    @Mock
    private SniperListener sniperListener;

    private AuctionSniper auctionSniper;

    @BeforeEach
    void setUp() {
        auctionSniper = new AuctionSniper(auction, sniperListener);
    }

    @Test
    void reports_lost_when_auction_closes() {
        // when
        auctionSniper.auctionClosed();

        // then
        verify(sniperListener).sniperLost();
    }

    @Test
    void bids_higher_and_reports_bidding_when_new_price_arrives() {
        // given
        final int price = 1001;
        final int increment = 25;

        // when
        auctionSniper.currentPrice(price, increment);

        // then
        verify(auction).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperBidding();
    }
}
