package org.example.auctionsniper;

import javax.swing.table.AbstractTableModel;

class SnipersTableModel extends AbstractTableModel {

    private String statusText = MainWindow.STATUS_JOINING;
    private int columnCount = 1; // todo remove when all e2e tests pass

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
        return statusText;
    }

    public void setStatusText(String newStatus) {
        statusText = newStatus;
    }

    public void setStatusText(SniperState sniperState, String newStatus) {
        statusText = newStatus;
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {

    }
}
