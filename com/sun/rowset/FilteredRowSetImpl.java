package com.sun.rowset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import javax.sql.RowSet;
import java.util.Hashtable;
import java.sql.SQLException;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.FilteredRowSet;
import java.io.Serializable;

public class FilteredRowSetImpl extends WebRowSetImpl implements Serializable, Cloneable, FilteredRowSet
{
    private Predicate p;
    private boolean onInsertRow;
    static final long serialVersionUID = 6178454588413509360L;
    
    public FilteredRowSetImpl() throws SQLException {
        this.onInsertRow = false;
    }
    
    public FilteredRowSetImpl(final Hashtable hashtable) throws SQLException {
        super(hashtable);
        this.onInsertRow = false;
    }
    
    @Override
    public void setFilter(final Predicate p) throws SQLException {
        this.p = p;
    }
    
    @Override
    public Predicate getFilter() {
        return this.p;
    }
    
    @Override
    protected boolean internalNext() throws SQLException {
        boolean internalNext = false;
        for (int i = this.getRow(); i <= this.size(); ++i) {
            internalNext = super.internalNext();
            if (!internalNext || this.p == null) {
                return internalNext;
            }
            if (this.p.evaluate(this)) {
                break;
            }
        }
        return internalNext;
    }
    
    @Override
    protected boolean internalPrevious() throws SQLException {
        boolean internalPrevious = false;
        for (int i = this.getRow(); i > 0; --i) {
            internalPrevious = super.internalPrevious();
            if (this.p == null) {
                return internalPrevious;
            }
            if (this.p.evaluate(this)) {
                break;
            }
        }
        return internalPrevious;
    }
    
    @Override
    protected boolean internalFirst() throws SQLException {
        boolean b = super.internalFirst();
        if (this.p == null) {
            return b;
        }
        while (b && !this.p.evaluate(this)) {
            b = super.internalNext();
        }
        return b;
    }
    
    @Override
    protected boolean internalLast() throws SQLException {
        boolean b = super.internalLast();
        if (this.p == null) {
            return b;
        }
        while (b && !this.p.evaluate(this)) {
            b = super.internalPrevious();
        }
        return b;
    }
    
    @Override
    public boolean relative(final int n) throws SQLException {
        boolean internalNext = false;
        boolean internalPrevious = false;
        if (this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.relative").toString());
        }
        boolean b;
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                if (this.isAfterLast()) {
                    return false;
                }
                internalNext = this.internalNext();
            }
            b = internalNext;
        }
        else {
            for (int j = n; j < 0; ++j) {
                if (this.isBeforeFirst()) {
                    return false;
                }
                internalPrevious = this.internalPrevious();
            }
            b = internalPrevious;
        }
        if (n != 0) {
            this.notifyCursorMoved();
        }
        return b;
    }
    
    @Override
    public boolean absolute(final int n) throws SQLException {
        if (n == 0 || this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.absolute").toString());
        }
        boolean b2;
        if (n > 0) {
            boolean b = this.internalFirst();
            for (int i = 0; i < n - 1; ++i) {
                if (this.isAfterLast()) {
                    return false;
                }
                b = this.internalNext();
            }
            b2 = b;
        }
        else {
            boolean b3 = this.internalLast();
            for (int n2 = n; n2 + 1 < 0; ++n2) {
                if (this.isBeforeFirst()) {
                    return false;
                }
                b3 = this.internalPrevious();
            }
            b2 = b3;
        }
        this.notifyCursorMoved();
        return b2;
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        this.onInsertRow = true;
        super.moveToInsertRow();
    }
    
    @Override
    public void updateInt(final int n, final int n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(n2, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateInt(n, n2);
    }
    
    @Override
    public void updateInt(final String s, final int n) throws SQLException {
        this.updateInt(this.findColumn(s), n);
    }
    
    @Override
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(b, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateBoolean(n, b);
    }
    
    @Override
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        this.updateBoolean(this.findColumn(s), b);
    }
    
    @Override
    public void updateByte(final int n, final byte b) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(b, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateByte(n, b);
    }
    
    @Override
    public void updateByte(final String s, final byte b) throws SQLException {
        this.updateByte(this.findColumn(s), b);
    }
    
    @Override
    public void updateShort(final int n, final short n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(n2, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateShort(n, n2);
    }
    
    @Override
    public void updateShort(final String s, final short n) throws SQLException {
        this.updateShort(this.findColumn(s), n);
    }
    
    @Override
    public void updateLong(final int n, final long n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(n2, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateLong(n, n2);
    }
    
    @Override
    public void updateLong(final String s, final long n) throws SQLException {
        this.updateLong(this.findColumn(s), n);
    }
    
    @Override
    public void updateFloat(final int n, final float n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(n2, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateFloat(n, n2);
    }
    
    @Override
    public void updateFloat(final String s, final float n) throws SQLException {
        this.updateFloat(this.findColumn(s), n);
    }
    
    @Override
    public void updateDouble(final int n, final double n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(n2, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateDouble(n, n2);
    }
    
    @Override
    public void updateDouble(final String s, final double n) throws SQLException {
        this.updateDouble(this.findColumn(s), n);
    }
    
    @Override
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(bigDecimal, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateBigDecimal(n, bigDecimal);
    }
    
    @Override
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.updateBigDecimal(this.findColumn(s), bigDecimal);
    }
    
    @Override
    public void updateString(final int n, final String s) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(s, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateString(n, s);
    }
    
    @Override
    public void updateString(final String s, final String s2) throws SQLException {
        this.updateString(this.findColumn(s), s2);
    }
    
    @Override
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        String concat = "";
        final Byte[] array2 = new Byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
            concat = concat.concat(array2[i].toString());
        }
        if (this.onInsertRow && this.p != null && !this.p.evaluate(concat, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateBytes(n, array);
    }
    
    @Override
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        this.updateBytes(this.findColumn(s), array);
    }
    
    @Override
    public void updateDate(final int n, final Date date) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(date, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateDate(n, date);
    }
    
    @Override
    public void updateDate(final String s, final Date date) throws SQLException {
        this.updateDate(this.findColumn(s), date);
    }
    
    @Override
    public void updateTime(final int n, final Time time) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(time, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateTime(n, time);
    }
    
    @Override
    public void updateTime(final String s, final Time time) throws SQLException {
        this.updateTime(this.findColumn(s), time);
    }
    
    @Override
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(timestamp, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateTimestamp(n, timestamp);
    }
    
    @Override
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.updateTimestamp(this.findColumn(s), timestamp);
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(inputStream, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateAsciiStream(n, inputStream, n2);
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateAsciiStream(this.findColumn(s), inputStream, n);
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(reader, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateCharacterStream(n, reader, n2);
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.updateCharacterStream(this.findColumn(s), reader, n);
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(inputStream, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateBinaryStream(n, inputStream, n2);
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateBinaryStream(this.findColumn(s), inputStream, n);
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(o, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateObject(n, o);
    }
    
    @Override
    public void updateObject(final String s, final Object o) throws SQLException {
        this.updateObject(this.findColumn(s), o);
    }
    
    @Override
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        if (this.onInsertRow && this.p != null && !this.p.evaluate(o, n)) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
        }
        super.updateObject(n, o, n2);
    }
    
    @Override
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        this.updateObject(this.findColumn(s), o, n);
    }
    
    @Override
    public void insertRow() throws SQLException {
        this.onInsertRow = false;
        super.insertRow();
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
