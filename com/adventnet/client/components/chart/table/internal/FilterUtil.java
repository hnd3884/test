package com.adventnet.client.components.chart.table.internal;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collection;
import javax.swing.table.TableModel;

public class FilterUtil
{
    public static int getFirstColumnIndex(final TableModel tm, String columnName) {
        columnName = columnName.trim();
        for (int count = tm.getColumnCount(), i = 0; i < count; ++i) {
            final String column = tm.getColumnName(i);
            if (column.trim().equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("There is no such column named :" + columnName + " in passed table model" + tm);
    }
    
    public static int getIndex(final TableModel tm, final Object values) {
        if (values instanceof Integer) {
            final Integer data = (Integer)values;
            final int result = data;
            return result;
        }
        if (values instanceof String) {
            final String data2 = (String)values;
            final int result = getFirstColumnIndex(tm, data2);
            return result;
        }
        throw new IllegalArgumentException(values.getClass().getName() + " type is not supported. check the argument ");
    }
    
    public static int[] getSeriesIndex(final TableModel tm, final Object values) {
        if (values instanceof Integer[]) {
            final Integer[] data = (Integer[])values;
            final int[] result = new int[data.length];
            for (int i = 0; i < data.length; ++i) {
                result[i] = data[i];
            }
            return result;
        }
        if (values instanceof Integer) {
            final Integer data2 = (Integer)values;
            final int[] result = { data2 };
            return result;
        }
        if (values instanceof String[]) {
            final String[] data3 = (String[])values;
            final int[] result = new int[data3.length];
            for (int i = 0; i < data3.length; ++i) {
                result[i] = getFirstColumnIndex(tm, data3[i]);
            }
            return result;
        }
        if (values instanceof String) {
            final String data4 = (String)values;
            final int[] result = { getFirstColumnIndex(tm, data4) };
            return result;
        }
        throw new IllegalArgumentException(values.getClass().getName() + " type is not supported. check the argument ");
    }
    
    public static Collection getDistinctValue(final TableModel tm, final int colIndex) {
        final int rowCount = tm.getRowCount();
        final LinkedHashSet set = new LinkedHashSet();
        for (int i = 0; i < rowCount; ++i) {
            final Object value = tm.getValueAt(i, colIndex);
            if (value != null) {
                set.add(value);
            }
        }
        return set;
    }
    
    public static Collection applyGroupBy(final TableModel tm, final int[] seriesHeader) {
        final Set set = new LinkedHashSet();
        final int rowCount = tm.getRowCount();
        final int len = seriesHeader.length;
        for (int i = 0; i < rowCount; ++i) {
            final Object[] data = new Object[len];
            for (int j = 0; j < len; ++j) {
                data[j] = tm.getValueAt(i, seriesHeader[j]);
            }
            set.add(new FilterObject(data));
        }
        return set;
    }
    
    public static List getGroupedTableModel(final TableModel tm, final int[] seriesHeader) {
        final Collection seriesFilter = applyGroupBy(tm, seriesHeader);
        final Iterator iterator = seriesFilter.iterator();
        final List tables = new ArrayList(seriesFilter.size() + 2);
        final int counter = 0;
        while (iterator.hasNext()) {
            final FilterObject filter = iterator.next();
            final FilterTableModel tableModel = new FilterTableModel(tm);
            tableModel.setFilter(filter, seriesHeader);
            tables.add(new Object[] { filter, tableModel });
        }
        return tables;
    }
    
    public static String getTableModelAsString(final TableModel tm) {
        final StringBuffer sbf = new StringBuffer();
        final int rc = tm.getRowCount();
        final int cc = tm.getColumnCount();
        sbf.append("\n Rowcount :" + rc + " columnCount " + cc + " ColumnNames \n");
        for (int i = 0; i < cc; ++i) {
            sbf.append(tm.getColumnName(i) + "\t");
        }
        for (int i = 0; i < rc; ++i) {
            sbf.append("\n");
            for (int j = 0; j < cc; ++j) {
                sbf.append(tm.getValueAt(i, j) + "\t");
            }
        }
        return sbf.toString();
    }
}
