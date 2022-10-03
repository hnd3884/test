package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import javax.sql.RowSetEvent;
import java.sql.Savepoint;
import javax.sql.rowset.RowSetWarning;
import java.net.URL;
import java.util.Calendar;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.util.Map;
import java.sql.Statement;
import java.io.Reader;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import javax.sql.rowset.spi.SyncProvider;
import java.util.Collection;
import javax.sql.RowSet;
import java.sql.ResultSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.RowSetInternal;
import javax.sql.rowset.spi.SyncProviderException;
import java.sql.SQLException;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.util.ArrayList;
import javax.sql.rowset.spi.SyncResolver;
import com.sun.rowset.CachedRowSetImpl;

public class SyncResolverImpl extends CachedRowSetImpl implements SyncResolver
{
    private CachedRowSetImpl crsRes;
    private CachedRowSetImpl crsSync;
    private ArrayList<?> stats;
    private CachedRowSetWriter crw;
    private int rowStatus;
    private int sz;
    private transient Connection con;
    private CachedRowSet row;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = -3345004441725080251L;
    
    public SyncResolverImpl() throws SQLException {
        try {
            this.crsSync = new CachedRowSetImpl();
            this.crsRes = new CachedRowSetImpl();
            this.crw = new CachedRowSetWriter();
            this.row = new CachedRowSetImpl();
            this.rowStatus = 1;
            try {
                this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
            }
            catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (final SQLException ex2) {}
    }
    
    @Override
    public int getStatus() {
        return (int)this.stats.get(this.rowStatus - 1);
    }
    
    @Override
    public Object getConflictValue(final int n) throws SQLException {
        try {
            return this.crsRes.getObject(n);
        }
        catch (final SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    @Override
    public Object getConflictValue(final String s) throws SQLException {
        try {
            return this.crsRes.getObject(s);
        }
        catch (final SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    @Override
    public void setResolvedValue(final int n, final Object o) throws SQLException {
        try {
            if (n <= 0 || n > this.crsSync.getMetaData().getColumnCount()) {
                throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.indexval").toString() + n);
            }
            if (this.crsRes.getObject(n) == null) {
                throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.noconflict").toString());
            }
        }
        catch (final SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
        try {
            boolean b = true;
            if (this.crsSync.getObject(n).toString().equals(o.toString()) || this.crsRes.getObject(n).toString().equals(o.toString())) {
                this.crsRes.updateNull(n);
                this.crsRes.updateRow();
                if (this.row.size() != 1) {
                    this.row = this.buildCachedRow();
                }
                this.row.updateObject(n, o);
                this.row.updateRow();
                for (int i = 1; i < this.crsRes.getMetaData().getColumnCount(); ++i) {
                    if (this.crsRes.getObject(i) != null) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    try {
                        this.writeData(this.row);
                        return;
                    }
                    catch (final SyncProviderException ex2) {
                        throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.syncnotpos").toString());
                    }
                    throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.valtores").toString());
                }
                return;
            }
            throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.valtores").toString());
        }
        catch (final SQLException ex3) {
            throw new SQLException(ex3.getMessage());
        }
    }
    
    private void writeData(final CachedRowSet set) throws SQLException {
        this.crw.updateResolvedConflictToDB(set, this.crw.getReader().connect(this.crsSync));
    }
    
    private CachedRowSet buildCachedRow() throws SQLException {
        final CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
        final RowSetMetaDataImpl rowSetMetaDataImpl = new RowSetMetaDataImpl();
        final RowSetMetaDataImpl rowSetMetaDataImpl2 = (RowSetMetaDataImpl)this.crsSync.getMetaData();
        final RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
        final int columnCount = rowSetMetaDataImpl2.getColumnCount();
        metaData.setColumnCount(columnCount);
        for (int i = 1; i <= columnCount; ++i) {
            metaData.setColumnType(i, rowSetMetaDataImpl2.getColumnType(i));
            metaData.setColumnName(i, rowSetMetaDataImpl2.getColumnName(i));
            metaData.setNullable(i, 2);
            try {
                metaData.setCatalogName(i, rowSetMetaDataImpl2.getCatalogName(i));
                metaData.setSchemaName(i, rowSetMetaDataImpl2.getSchemaName(i));
            }
            catch (final SQLException ex) {
                ex.printStackTrace();
            }
        }
        cachedRowSetImpl.setMetaData(metaData);
        cachedRowSetImpl.moveToInsertRow();
        for (int j = 1; j <= this.crsSync.getMetaData().getColumnCount(); ++j) {
            cachedRowSetImpl.updateObject(j, this.crsSync.getObject(j));
        }
        cachedRowSetImpl.insertRow();
        cachedRowSetImpl.moveToCurrentRow();
        cachedRowSetImpl.absolute(1);
        cachedRowSetImpl.setOriginalRow();
        try {
            cachedRowSetImpl.setUrl(this.crsSync.getUrl());
        }
        catch (final SQLException ex2) {}
        try {
            cachedRowSetImpl.setDataSourceName(this.crsSync.getCommand());
        }
        catch (final SQLException ex3) {}
        try {
            if (this.crsSync.getTableName() != null) {
                cachedRowSetImpl.setTableName(this.crsSync.getTableName());
            }
        }
        catch (final SQLException ex4) {}
        try {
            if (this.crsSync.getCommand() != null) {
                cachedRowSetImpl.setCommand(this.crsSync.getCommand());
            }
        }
        catch (final SQLException ex5) {}
        try {
            cachedRowSetImpl.setKeyColumns(this.crsSync.getKeyColumns());
        }
        catch (final SQLException ex6) {}
        return cachedRowSetImpl;
    }
    
    @Override
    public void setResolvedValue(final String s, final Object o) throws SQLException {
    }
    
    void setCachedRowSet(final CachedRowSet set) {
        this.crsSync = (CachedRowSetImpl)set;
    }
    
    void setCachedRowSetResolver(final CachedRowSet set) {
        try {
            (this.crsRes = (CachedRowSetImpl)set).afterLast();
            this.sz = this.crsRes.size();
        }
        catch (final SQLException ex) {}
    }
    
    void setStatus(final ArrayList stats) {
        this.stats = stats;
    }
    
    void setCachedRowSetWriter(final CachedRowSetWriter crw) {
        this.crw = crw;
    }
    
    @Override
    public boolean nextConflict() throws SQLException {
        boolean b = false;
        this.crsSync.setShowDeleted(true);
        while (this.crsSync.next()) {
            this.crsRes.previous();
            ++this.rowStatus;
            if (this.rowStatus - 1 >= this.stats.size()) {
                b = false;
                break;
            }
            if ((int)this.stats.get(this.rowStatus - 1) == 3) {
                continue;
            }
            b = true;
            break;
        }
        this.crsSync.setShowDeleted(false);
        return b;
    }
    
    @Override
    public boolean previousConflict() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setCommand(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void populate(final ResultSet set) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void execute(final Connection connection) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void acceptChanges() throws SyncProviderException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void acceptChanges(final Connection connection) throws SyncProviderException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void restoreOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void release() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void undoDelete() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void undoInsert() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void undoUpdate() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RowSet createShared() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public CachedRowSet createCopy() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public CachedRowSet createCopySchema() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public CachedRowSet createCopyNoConstraints() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection toCollection() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection toCollection(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection toCollection(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SyncProvider getSyncProvider() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setSyncProvider(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void execute() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean next() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean internalNext() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected BaseRow getCurrentRow() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void removeCurrentRow() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getString(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean getBoolean(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte getByte(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public short getShort(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getInt(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long getLong(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public float getFloat(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public double getDouble(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte[] getBytes(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getDate(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Time getTime(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Timestamp getTimestamp(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getAsciiStream(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getBinaryStream(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getString(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean getBoolean(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte getByte(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public short getShort(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getInt(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long getLong(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public float getFloat(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public double getDouble(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte[] getBytes(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getDate(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Time getTime(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Timestamp getTimestamp(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getAsciiStream(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getBinaryStream(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SQLWarning getWarnings() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clearWarnings() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getObject(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getObject(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int findColumn(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader getCharacterStream(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean internalFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean internalLast() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getRow() throws SQLException {
        return this.crsSync.getRow();
    }
    
    @Override
    public boolean absolute(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean relative(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean internalPrevious() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean columnUpdated(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean columnUpdated(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateNull(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateByte(final int n, final byte b) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateShort(final int n, final short n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateInt(final int n, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateLong(final int n, final long n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateFloat(final int n, final float n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateDouble(final int n, final double n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateString(final int n, final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateDate(final int n, final Date date) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateTime(final int n, final Time time) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateNull(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateByte(final String s, final byte b) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateShort(final String s, final short n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateInt(final String s, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateLong(final String s, final long n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateFloat(final String s, final float n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateDouble(final String s, final double n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateString(final String s, final String s2) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateDate(final String s, final Date date) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateTime(final String s, final Time time) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateObject(final String s, final Object o) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getObject(final int n, final Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Ref getRef(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Blob getBlob(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Clob getClob(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Array getArray(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object getObject(final String s, final Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Ref getRef(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Blob getBlob(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Clob getClob(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Array getArray(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMetaData(final RowSetMetaData rowSetMetaData) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ResultSet getOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ResultSet getOriginalRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setOriginalRow() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setOriginal() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getTableName() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setTableName(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int[] getKeyColumns() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setKeyColumns(final int[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateRef(final int n, final Ref ref) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateRef(final String s, final Ref ref) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateClob(final int n, final Clob clob) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateClob(final String s, final Clob clob) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateArray(final int n, final Array array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateArray(final String s, final Array array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public URL getURL(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public URL getURL(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RowSetWarning getRowSetWarnings() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void commit() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unsetMatchColumn(final int[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unsetMatchColumn(final String[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String[] getMatchColumnNames() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int[] getMatchColumnIndexes() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMatchColumn(final int[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMatchColumn(final String[] array) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMatchColumn(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMatchColumn(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unsetMatchColumn(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void unsetMatchColumn(final String s) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void rowSetPopulated(final RowSetEvent rowSetEvent, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void populate(final ResultSet set, final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean nextPage() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setPageSize(final int n) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean previousPage() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    public void updateNCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        throw new UnsupportedOperationException("Operation not yet supported");
    }
    
    public void updateNCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw new UnsupportedOperationException("Operation not yet supported");
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
