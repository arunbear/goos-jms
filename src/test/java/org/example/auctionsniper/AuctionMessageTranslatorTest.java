package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import org.example.auctionsniper.AuctionEventListener.PriceSource;

@ExtendWith(MockitoExtension.class)
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
class AuctionMessageTranslatorTest {

    private static final String SNIPER_ID = "sniper";

    @Mock
    private AuctionEventListener listener;

    private AuctionMessageTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new AuctionMessageTranslator(SNIPER_ID, listener);
    }

    @Test
    void notifies_auction_closed_when_close_message_received() {
        // given
        var message = "SOLVersion: 1.1; Event: CLOSE;";

        // when
        translator.processMessage(message);

        verify(listener).auctionClosed();
    }

    @Test
    void notifies_bid_details_when_current_price_message_received_from_other_bidder() {
        var message = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;";

        translator.processMessage(message);

        verify(listener).currentPrice(192, 7, PriceSource.FROM_OTHER_BIDDER);
    }

    @Test
    void notifies_bid_details_when_current_price_message_received_from_sniper() {
        var message = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: %s;".formatted(SNIPER_ID);

        translator.processMessage(message);

        verify(listener).currentPrice(234, 5, PriceSource.FROM_SNIPER);
    }
}