package com.adventnet.tools.update.installer;

import javax.swing.table.DefaultTableModel;

public class NonEditableTableModel extends DefaultTableModel
{
    public NonEditableTableModel(final int row, final int column) {
        super(row, column);
    }
    
    public NonEditableTableModel(final String[] headers, final int row) {
        super(headers, row);
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex != 1;
    }
}
