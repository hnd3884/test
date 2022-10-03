package com.adventnet.client.components.chart.table.internal;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

public class SameXModelAdapter extends AbstractTableModel
{
    protected int[] yAxisIdx;
    int rc;
    int cc;
    int[] colIdx;
    String[] seriesNames;
    Object[][] data;
    
    public SameXModelAdapter(final TableModel model, final String xAxis, final String yAxis, final String series) {
        this.rc = 0;
        this.cc = 0;
        this.colIdx = null;
        this.seriesNames = null;
        this.data = null;
        final int xIndex = FilterUtil.getFirstColumnIndex(model, xAxis);
        final int yIndex = FilterUtil.getFirstColumnIndex(model, yAxis);
        final int seriesIndex = FilterUtil.getFirstColumnIndex(model, series);
        final Collection xVal = FilterUtil.getDistinctValue(model, xIndex);
        final ArrayList xValues = new ArrayList(xVal.size());
        Iterator iterator = xVal.iterator();
        while (iterator.hasNext()) {
            xValues.add(iterator.next());
        }
        Collections.sort((List<Comparable>)xValues);
        final Collection seriesValues = FilterUtil.getDistinctValue(model, seriesIndex);
        (this.seriesNames = new String[seriesValues.size() + 1])[0] = model.getColumnName(xIndex);
        final HashMap seriesPos = new HashMap();
        this.cc = seriesValues.size() + 1;
        iterator = seriesValues.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            final Object value = iterator.next();
            this.seriesNames[++counter] = value.toString();
            seriesPos.put(value, new Integer(counter));
        }
        final HashMap indexedXPos = new HashMap();
        this.rc = xValues.size();
        this.data = new Object[this.rc][this.cc];
        for (int i = 0; i < this.rc; ++i) {
            final Object value2 = xValues.get(i);
            indexedXPos.put(value2, new Integer(i));
            this.data[i][0] = value2;
        }
        for (int rowCount = model.getRowCount(), j = 0; j < rowCount; ++j) {
            final Object xValue = model.getValueAt(j, xIndex);
            final Object yValue = model.getValueAt(j, yIndex);
            final Object seriesValue = model.getValueAt(j, seriesIndex);
            final int xPos = indexedXPos.get(xValue);
            final int serPos = seriesPos.get(seriesValue);
            this.data[xPos][serPos] = yValue;
        }
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
        return this.data[rowIndex][columnIndex];
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
        return this.seriesNames[columnIndex];
    }
    
    @Override
    public Class getColumnClass(final int aColumn) {
        return Object.class;
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
