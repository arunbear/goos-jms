package org.example.auctionsniper;

import org.example.auctionsniper.AuctionEventListener.PriceSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
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
    void reports_lost_if_auction_closes_immediately() {
        // when
        auctionSniper.auctionClosed();

        // then
        verify(sniperListener).sniperLost();
    }

    @Test
    void reports_lost_if_auction_closes_when_bidding() {
        auctionSniper.currentPrice(123, 45, PriceSource.FROM_OTHER_BIDDER);
        auctionSniper.auctionClosed();

        verify(sniperListener).sniperBidding(any(SniperState.class));
        verify(sniperListener).sniperLost();
    }

    @Test
    void bids_higher_and_reports_bidding_when_new_price_arrives() {
        // given
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        // when
        auctionSniper.currentPrice(price, increment, PriceSource.FROM_OTHER_BIDDER);

        // then
        verify(auction).bid(bid);
        verify(sniperListener, atLeastOnce()).sniperBidding(new SniperState("item-123", price, bid));
    }

    @Test
    void reports_winning_when_current_price_comes_from_sniper() {
        final int price = 1001;
        final int increment = 25;

        auctionSniper.currentPrice(price, increment, PriceSource.FROM_SNIPER);

        verify(sniperListener).sniperWinning();
    }

    @Test
    void reports_won_if_auction_closes_when_winning() {
        auctionSniper.currentPrice(123, 45, PriceSource.FROM_SNIPER);
        auctionSniper.auctionClosed();

        verify(sniperListener).sniperWinning();
        verify(sniperListener).sniperWon();
    }
}
