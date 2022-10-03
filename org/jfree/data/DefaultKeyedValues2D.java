package org.jfree.data;

import org.jfree.util.ObjectUtilities;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class DefaultKeyedValues2D implements KeyedValues2D, PublicCloneable, Cloneable, Serializable
{
    private static final long serialVersionUID = -5514169970951994748L;
    private List rowKeys;
    private List columnKeys;
    private List rows;
    private boolean sortRowKeys;
    
    public DefaultKeyedValues2D() {
        this(false);
    }
    
    public DefaultKeyedValues2D(final boolean sortRowKeys) {
        this.rowKeys = new ArrayList();
        this.columnKeys = new ArrayList();
        this.rows = new ArrayList();
        this.sortRowKeys = sortRowKeys;
    }
    
    public int getRowCount() {
        return this.rowKeys.size();
    }
    
    public int getColumnCount() {
        return this.columnKeys.size();
    }
    
    public Number getValue(final int row, final int column) {
        Number result = null;
        final DefaultKeyedValues rowData = this.rows.get(row);
        if (rowData != null) {
            final Comparable columnKey = this.columnKeys.get(column);
            final int index = rowData.getIndex(columnKey);
            if (index >= 0) {
                result = rowData.getValue(index);
            }
        }
        return result;
    }
    
    public Comparable getRowKey(final int row) {
        return this.rowKeys.get(row);
    }
    
    public int getRowIndex(final Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (this.sortRowKeys) {
            return Collections.binarySearch(this.rowKeys, key);
        }
        return this.rowKeys.indexOf(key);
    }
    
    public List getRowKeys() {
        return Collections.unmodifiableList((List<?>)this.rowKeys);
    }
    
    public Comparable getColumnKey(final int column) {
        return this.columnKeys.get(column);
    }
    
    public int getColumnIndex(final Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.columnKeys.indexOf(key);
    }
    
    public List getColumnKeys() {
        return Collections.unmodifiableList((List<?>)this.columnKeys);
    }
    
    public Number getValue(final Comparable rowKey, final Comparable columnKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        final int row = this.getRowIndex(rowKey);
        if (row >= 0) {
            final DefaultKeyedValues rowData = this.rows.get(row);
            return rowData.getValue(columnKey);
        }
        throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
    }
    
    public void addValue(final Number value, final Comparable rowKey, final Comparable columnKey) {
        this.setValue(value, rowKey, columnKey);
    }
    
    public void setValue(final Number value, final Comparable rowKey, final Comparable columnKey) {
        int rowIndex = this.getRowIndex(rowKey);
        DefaultKeyedValues row;
        if (rowIndex >= 0) {
            row = this.rows.get(rowIndex);
        }
        else {
            row = new DefaultKeyedValues();
            if (this.sortRowKeys) {
                rowIndex = -rowIndex - 1;
                this.rowKeys.add(rowIndex, rowKey);
                this.rows.add(rowIndex, row);
            }
            else {
                this.rowKeys.add(rowKey);
                this.rows.add(row);
            }
        }
        row.setValue(columnKey, value);
        final int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }
    }
    
    public void removeValue(final Comparable rowKey, final Comparable columnKey) {
        this.setValue(null, rowKey, columnKey);
        boolean allNull = true;
        final int rowIndex = this.getRowIndex(rowKey);
        DefaultKeyedValues row = this.rows.get(rowIndex);
        for (int item = 0, itemCount = row.getItemCount(); item < itemCount; ++item) {
            if (row.getValue(item) != null) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }
        allNull = true;
        final int columnIndex = this.getColumnIndex(columnKey);
        for (int item2 = 0, itemCount2 = this.rows.size(); item2 < itemCount2; ++item2) {
            row = this.rows.get(item2);
            if (row.getValue(columnIndex) != null) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            for (int item2 = 0, itemCount2 = this.rows.size(); item2 < itemCount2; ++item2) {
                row = this.rows.get(item2);
                row.removeValue(columnIndex);
            }
            this.columnKeys.remove(columnIndex);
        }
    }
    
    public void removeRow(final int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }
    
    public void removeRow(final Comparable rowKey) {
        this.removeRow(this.getRowIndex(rowKey));
    }
    
    public void removeColumn(final int columnIndex) {
        final Comparable columnKey = this.getColumnKey(columnIndex);
        this.removeColumn(columnKey);
    }
    
    public void removeColumn(final Comparable columnKey) {
        final Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            final DefaultKeyedValues rowData = iterator.next();
            rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }
    
    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyedValues2D)) {
            return false;
        }
        final KeyedValues2D kv2D = (KeyedValues2D)o;
        if (!this.getRowKeys().equals(kv2D.getRowKeys())) {
            return false;
        }
        if (!this.getColumnKeys().equals(kv2D.getColumnKeys())) {
            return false;
        }
        final int rowCount = this.getRowCount();
        if (rowCount != kv2D.getRowCount()) {
            return false;
        }
        final int colCount = this.getColumnCount();
        if (colCount != kv2D.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                final Number v1 = this.getValue(r, c);
                final Number v2 = kv2D.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                }
                else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int hashCode() {
        int result = this.rowKeys.hashCode();
        result = 29 * result + this.columnKeys.hashCode();
        result = 29 * result + this.rows.hashCode();
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultKeyedValues2D clone = (DefaultKeyedValues2D)super.clone();
        clone.columnKeys = new ArrayList(this.columnKeys);
        clone.rowKeys = new ArrayList(this.rowKeys);
        clone.rows = (List)ObjectUtilities.deepClone((Collection)this.rows);
        return clone;
    }
}
