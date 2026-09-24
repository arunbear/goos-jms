package org.example.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.assertj.core.api.BDDAssertions.then;
import static org.example.auctionsniper.MainWindow.STATUS_BIDDING;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@IndicativeSentencesGeneration(
    separator = " -> ",
    generator = DisplayNameGenerator.ReplaceUnderscores.class
)
class SnipersTableModelTest {

    private SnipersTableModel model;

    @Mock
    private TableModelListener listener;

    @BeforeEach
    void setUp() {
        model = new SnipersTableModel();
        model.addTableModelListener(listener);
    }

    @Test
    void has_enough_columns() {
        then(model.getColumnCount())
            .isEqualTo(Column.values().length); // fails
    }

    @Test
    void sets_sniper_values_in_columns() {
        // when
        model.sniperStatusChanged(new SniperState("item-id", 555, 666), STATUS_BIDDING);
        assertColumnEquals(Column.ITEM_IDENTIFIER, "item-id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATUS, STATUS_BIDDING);

        verify(listener).tableChanged(refEq(new TableModelEvent(model, 0)));
    }

    private void assertColumnEquals(Column column, Object value) {
        // given
        int rowIndex = 0;
        int columnIndex = column.ordinal();

        then(model.getValueAt(rowIndex, columnIndex)).isEqualTo(value);
    }
}