package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import java.sql.SQLException;
import javax.sql.RowSetMetaData;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.util.BitSet;
import java.io.Serializable;

public class InsertRow extends BaseRow implements Serializable, Cloneable
{
    private BitSet colsInserted;
    private int cols;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = 1066099658102869344L;
    
    public InsertRow(final int cols) {
        this.origVals = new Object[cols];
        this.colsInserted = new BitSet(cols);
        this.cols = cols;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected void markColInserted(final int n) {
        this.colsInserted.set(n);
    }
    
    public boolean isCompleteRow(final RowSetMetaData rowSetMetaData) throws SQLException {
        for (int i = 0; i < this.cols; ++i) {
            if (!this.colsInserted.get(i) && rowSetMetaData.isNullable(i + 1) == 0) {
                return false;
            }
        }
        return true;
    }
    
    public void initInsertRow() {
        for (int i = 0; i < this.cols; ++i) {
            this.colsInserted.clear(i);
        }
    }
    
    @Override
    public Object getColumnObject(final int n) throws SQLException {
        if (!this.colsInserted.get(n - 1)) {
            throw new SQLException(this.resBundle.handleGetObject("insertrow.novalue").toString());
        }
        return this.origVals[n - 1];
    }
    
    @Override
    public void setColumnObject(final int n, final Object o) {
        this.origVals[n - 1] = o;
        this.markColInserted(n - 1);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
