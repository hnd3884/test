package com.adventnet.client.components.property.web;

import javax.swing.event.TableModelListener;
import java.util.ArrayList;
import javax.swing.table.TableModel;

public class PropertyTableModel implements TableModel
{
    ArrayList propNames;
    ArrayList propValues;
    
    public PropertyTableModel(final ArrayList propNamesArg, final ArrayList propValuesArg) {
        this.propNames = propNamesArg;
        this.propValues = propValuesArg;
    }
    
    @Override
    public int getRowCount() {
        return 1;
    }
    
    @Override
    public int getColumnCount() {
        return this.propNames.size();
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
        return this.propNames.get(columnIndex);
    }
    
    @Override
    public Class getColumnClass(final int columnIndex) {
        final Object value = this.propValues.get(columnIndex);
        return (value == null) ? String.class : value.getClass();
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        throw new UnsupportedOperationException("isCellEditable method not supported");
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (rowIndex != 0) {
            throw new IllegalArgumentException("Row index can only be 0.But " + rowIndex + " was passed");
        }
        return this.propValues.get(columnIndex);
    }
    
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        if (rowIndex != 0) {
            throw new IllegalArgumentException("Row index can only be 0.But " + rowIndex + " was passed");
        }
        this.propValues.set(columnIndex, aValue);
    }
    
    @Override
    public void addTableModelListener(final TableModelListener l) {
        throw new UnsupportedOperationException("addTableModelListener method not supported");
    }
    
    @Override
    public void removeTableModelListener(final TableModelListener l) {
        throw new UnsupportedOperationException("removeTableModelListener method not supported");
    }
}
