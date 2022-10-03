package org.jfree.data;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class KeyedObjects2D implements Cloneable, Serializable
{
    private static final long serialVersionUID = -1015873563138522374L;
    private List rowKeys;
    private List columnKeys;
    private List rows;
    
    public KeyedObjects2D() {
        this.rowKeys = new ArrayList();
        this.columnKeys = new ArrayList();
        this.rows = new ArrayList();
    }
    
    public int getRowCount() {
        return this.rowKeys.size();
    }
    
    public int getColumnCount() {
        return this.columnKeys.size();
    }
    
    public Object getObject(final int row, final int column) {
        Object result = null;
        final KeyedObjects rowData = this.rows.get(row);
        if (rowData != null) {
            final Comparable columnKey = this.columnKeys.get(column);
            if (columnKey != null) {
                result = rowData.getObject(columnKey);
            }
        }
        return result;
    }
    
    public Comparable getRowKey(final int row) {
        return this.rowKeys.get(row);
    }
    
    public int getRowIndex(final Comparable key) {
        return this.rowKeys.indexOf(key);
    }
    
    public List getRowKeys() {
        return Collections.unmodifiableList((List<?>)this.rowKeys);
    }
    
    public Comparable getColumnKey(final int column) {
        return this.columnKeys.get(column);
    }
    
    public int getColumnIndex(final Comparable key) {
        return this.columnKeys.indexOf(key);
    }
    
    public List getColumnKeys() {
        return Collections.unmodifiableList((List<?>)this.columnKeys);
    }
    
    public Object getObject(final Comparable rowKey, final Comparable columnKey) {
        Object result = null;
        final int row = this.rowKeys.indexOf(rowKey);
        if (row >= 0) {
            final KeyedObjects rowData = this.rows.get(row);
            result = rowData.getObject(columnKey);
        }
        return result;
    }
    
    public void addObject(final Object object, final Comparable rowKey, final Comparable columnKey) {
        this.setObject(object, rowKey, columnKey);
    }
    
    public void setObject(final Object object, final Comparable rowKey, final Comparable columnKey) {
        final int rowIndex = this.rowKeys.indexOf(rowKey);
        KeyedObjects row;
        if (rowIndex >= 0) {
            row = this.rows.get(rowIndex);
        }
        else {
            this.rowKeys.add(rowKey);
            row = new KeyedObjects();
            this.rows.add(row);
        }
        row.setObject(columnKey, object);
        final int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }
    }
    
    public void removeObject(final Comparable rowKey, final Comparable columnKey) {
        this.setObject(null, rowKey, columnKey);
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
            final KeyedObjects rowData = iterator.next();
            rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedObjects2D)) {
            return false;
        }
        final KeyedObjects2D ko2D = (KeyedObjects2D)obj;
        if (!this.getRowKeys().equals(ko2D.getRowKeys())) {
            return false;
        }
        if (!this.getColumnKeys().equals(ko2D.getColumnKeys())) {
            return false;
        }
        final int rowCount = this.getRowCount();
        if (rowCount != ko2D.getRowCount()) {
            return false;
        }
        final int colCount = this.getColumnCount();
        if (colCount != ko2D.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                final Object v1 = this.getObject(r, c);
                final Object v2 = ko2D.getObject(r, c);
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
        return super.clone();
    }
}
