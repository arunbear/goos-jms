package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDAssertions.then;
import static org.example.auctionsniper.MainWindow.STATUS_BIDDING;

@ExtendWith(MockitoExtension.class)
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
class SnipersTableModelTest {

    private SnipersTableModel model;

    @BeforeEach
    void setUp() {
        model = new SnipersTableModel();
    }

    @Test
    void has_enough_columns() {
        then(model.getColumnCount())
            .isEqualTo(Column.values().length); // fails
    }

    @Test
    void sets_sniper_values_in_columns() {
        // when
        model.sniperStatusChanged(new SniperState("item-id", 123, 456), STATUS_BIDDING);
    }

}