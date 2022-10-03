package com.adventnet.client.components.chart.table.internal;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class FilterTableModel extends AbstractTableModel implements TableModelListener
{
    protected ArrayList indexList;
    protected FilterObject filter;
    protected int[] index;
    protected TableModel model;
    
    public FilterTableModel(final TableModel model) {
        this.indexList = new ArrayList(0);
        this.filter = null;
        this.setModel(model);
    }
    
    private TableModel getModel() {
        return this.model;
    }
    
    private void setModel(final TableModel model) {
        (this.model = model).addTableModelListener(this);
    }
    
    public void setFilter(final FilterObject filter, final int[] index) {
        this.filter = filter;
        this.index = index;
        this.reallocateIndexes();
        this.fireTableDataChanged();
    }
    
    public FilterObject getFilter() {
        return this.filter;
    }
    
    @Override
    public int getRowCount() {
        return this.indexList.size();
    }
    
    @Override
    public int getColumnCount() {
        return this.model.getColumnCount();
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final int newIndex = this.indexList.get(rowIndex);
        return this.model.getValueAt(newIndex, columnIndex);
    }
    
    @Override
    public String getColumnName(final int aColumn) {
        return this.model.getColumnName(aColumn);
    }
    
    @Override
    public Class getColumnClass(final int aColumn) {
        return this.model.getColumnClass(aColumn);
    }
    
    @Override
    public boolean isCellEditable(final int row, final int column) {
        return this.model.isCellEditable(row, column);
    }
    
    public void reallocateIndexes() {
        final int rowCount = this.model.getRowCount();
        this.indexList.clear();
        for (int i = 0; i < rowCount; ++i) {
            final int len = this.index.length;
            final Object[] data = new Object[len];
            for (int j = 0; j < len; ++j) {
                final Object value = this.model.getValueAt(i, this.index[j]);
                data[j] = value;
            }
            final FilterObject toCMP = new FilterObject(data);
            if (toCMP.equals(this.filter)) {
                this.indexList.add(new Integer(i));
            }
        }
    }
    
    @Override
    public void tableChanged(final TableModelEvent e) {
        this.reallocateIndexes();
        this.fireTableChanged(e);
    }
}
