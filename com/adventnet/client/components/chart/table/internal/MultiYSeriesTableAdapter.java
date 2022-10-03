package com.adventnet.client.components.chart.table.internal;

import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

public class MultiYSeriesTableAdapter extends AbstractTableModel
{
    protected TableModel model;
    protected int[] yAxisIdx;
    int rc;
    int cc;
    int[] colIdx;
    
    public MultiYSeriesTableAdapter(final TableModel model, final String[] yAxisColumns) {
        this.rc = 0;
        this.cc = 0;
        this.colIdx = null;
        this.model = model;
        final int size = yAxisColumns.length;
        this.rc = model.getRowCount() * size;
        this.cc = model.getColumnCount() - size + 2;
        (this.colIdx = new int[this.cc])[0] = -2;
        this.colIdx[1] = -1;
        this.yAxisIdx = new int[size];
        for (int i = 0; i < size; ++i) {
            this.yAxisIdx[i] = getIndex(model, yAxisColumns[i]);
        }
        final int actSize = model.getColumnCount();
        int counter = 2;
        int j = 0;
    Label_0129:
        while (j < actSize) {
            final String actColName = model.getColumnName(j);
            while (true) {
                for (int k = 0; k < size; ++k) {
                    if (actColName.equalsIgnoreCase(yAxisColumns[k])) {
                        ++j;
                        continue Label_0129;
                    }
                }
                this.colIdx[counter++] = getIndex(model, actColName);
                continue;
            }
        }
    }
    
    public MultiYSeriesTableAdapter(final TableModel model, final int[] yAxisColumns) {
        this.rc = 0;
        this.cc = 0;
        this.colIdx = null;
        this.model = model;
        final int size = yAxisColumns.length;
        this.rc = model.getRowCount() * size;
        this.cc = model.getColumnCount() - size + 2;
        (this.colIdx = new int[this.cc])[0] = -2;
        this.colIdx[1] = -1;
        this.yAxisIdx = yAxisColumns;
        int counter = 2;
        for (int j = 0; j < size; ++j) {
            this.colIdx[counter++] = yAxisColumns[j];
        }
    }
    
    public static int getIndex(final TableModel tableModel, final String column) {
        for (int colCount = tableModel.getColumnCount(), i = 0; i < colCount; ++i) {
            if (tableModel.getColumnName(i).equals(column)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int getRowCount() {
        return this.rc;
    }
    
    @Override
    public int getColumnCount() {
        return this.cc;
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final int actRowIndex = rowIndex % this.model.getRowCount();
        final int range = rowIndex / this.model.getRowCount();
        switch (columnIndex) {
            case 0: {
                return this.model.getValueAt(actRowIndex, this.yAxisIdx[range]);
            }
            case 1: {
                return this.model.getColumnName(this.yAxisIdx[range]);
            }
            default: {
                return this.model.getValueAt(actRowIndex, this.colIdx[columnIndex]);
            }
        }
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return "SP_YAXIS";
            }
            case 1: {
                return "SP_SERIES";
            }
            default: {
                return this.model.getColumnName(this.colIdx[columnIndex]);
            }
        }
    }
    
    @Override
    public Class getColumnClass(final int aColumn) {
        return this.model.getColumnClass(this.yAxisIdx[0]);
    }
    
    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }
    
    @Override
    public String toString() {
        return FilterUtil.getTableModelAsString(this);
    }
}
