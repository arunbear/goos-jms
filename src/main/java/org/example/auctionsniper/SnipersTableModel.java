package org.example.auctionsniper;

import javax.swing.table.AbstractTableModel;

class SnipersTableModel extends AbstractTableModel {

    private static final SniperState STARTING_UP = new SniperState("", 0, 0);

    private String statusText = MainWindow.STATUS_JOINING;
    private int columnCount = 1; // todo remove when all e2e tests pass
    private SniperState sniperState = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnCount == 1) {
            return statusText;
        }

        if (columnIndex == Column.ITEM_IDENTIFIER.ordinal()) {
            return sniperState.itemId();
        }
        else if (columnIndex == Column.LAST_PRICE.ordinal()) {
            return sniperState.lastPrice();
        }
        else if (columnIndex == Column.LAST_BID.ordinal()) {
            return sniperState.lastBid();
        }
        else if (columnIndex == Column.SNIPER_STATUS.ordinal()) {
            return statusText;
        }
        throw new IllegalArgumentException("Invalid column index: %d".formatted(columnIndex));
    }

    public void setStatusText(String newStatus) {
        statusText = newStatus;
    }

    public void setStatusText(SniperState sniperState, String newStatus) {
        statusText = newStatus;
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
        this.columnCount = Column.values().length;
        this.sniperState = newSniperState;
        this.statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
