package com.sun.rowset.internal;

import java.sql.SQLException;
import java.util.BitSet;
import java.io.Serializable;

public class Row extends BaseRow implements Serializable, Cloneable
{
    static final long serialVersionUID = 5047859032611314762L;
    private Object[] currentVals;
    private BitSet colsChanged;
    private boolean deleted;
    private boolean updated;
    private boolean inserted;
    private int numCols;
    
    public Row(final int numCols) {
        this.origVals = new Object[numCols];
        this.currentVals = new Object[numCols];
        this.colsChanged = new BitSet(numCols);
        this.numCols = numCols;
    }
    
    public Row(final int numCols, final Object[] array) {
        System.arraycopy(array, 0, this.origVals = new Object[numCols], 0, numCols);
        this.currentVals = new Object[numCols];
        this.colsChanged = new BitSet(numCols);
        this.numCols = numCols;
    }
    
    public void initColumnObject(final int n, final Object o) {
        this.origVals[n - 1] = o;
    }
    
    @Override
    public void setColumnObject(final int n, final Object o) {
        this.currentVals[n - 1] = o;
        this.setColUpdated(n - 1);
    }
    
    @Override
    public Object getColumnObject(final int n) throws SQLException {
        if (this.getColUpdated(n - 1)) {
            return this.currentVals[n - 1];
        }
        return this.origVals[n - 1];
    }
    
    public boolean getColUpdated(final int n) {
        return this.colsChanged.get(n);
    }
    
    public void setDeleted() {
        this.deleted = true;
    }
    
    public boolean getDeleted() {
        return this.deleted;
    }
    
    public void clearDeleted() {
        this.deleted = false;
    }
    
    public void setInserted() {
        this.inserted = true;
    }
    
    public boolean getInserted() {
        return this.inserted;
    }
    
    public void clearInserted() {
        this.inserted = false;
    }
    
    public boolean getUpdated() {
        return this.updated;
    }
    
    public void setUpdated() {
        for (int i = 0; i < this.numCols; ++i) {
            if (this.getColUpdated(i)) {
                this.updated = true;
                return;
            }
        }
    }
    
    private void setColUpdated(final int n) {
        this.colsChanged.set(n);
    }
    
    public void clearUpdated() {
        this.updated = false;
        for (int i = 0; i < this.numCols; ++i) {
            this.currentVals[i] = null;
            this.colsChanged.clear(i);
        }
    }
    
    public void moveCurrentToOrig() {
        for (int i = 0; i < this.numCols; ++i) {
            if (this.getColUpdated(i)) {
                this.origVals[i] = this.currentVals[i];
                this.currentVals[i] = null;
                this.colsChanged.clear(i);
            }
        }
        this.updated = false;
    }
    
    public BaseRow getCurrentRow() {
        return null;
    }
}
