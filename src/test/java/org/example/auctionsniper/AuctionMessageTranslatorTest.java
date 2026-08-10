package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
class AuctionMessageTranslatorTest {

    @Mock
    private AuctionMessageListener listener;

    private AuctionMessageTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new AuctionMessageTranslator(listener);
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
    void notifies_bid_details_when_current_price_message_received() {
        // given
        var message = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;";

        // when
        translator.processMessage(message);

        verify(listener).currentPrice(192, 7);
    }
}