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
            .isEqualTo(Column.values().length);
    }

    @Test
    void sets_sniper_values_in_columns() {
        // when
        model.sniperStatusChanged(new SniperState("item-id", 555, 666), STATUS_BIDDING);

        // then
        assertColumnEquals(Column.ITEM_IDENTIFIER, "item-id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATUS, STATUS_BIDDING);
    }

    private void assertColumnEquals(Column column, Object value) {
        // given
        int rowIndex = 0;
        int columnIndex = column.ordinal();

        then(model.getValueAt(rowIndex, columnIndex)).isEqualTo(value);
    }
}