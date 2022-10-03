package com.sun.rowset;

import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import javax.sql.RowSetEvent;
import java.sql.Savepoint;
import java.net.URL;
import javax.sql.rowset.serial.SerialRef;
import java.util.Arrays;
import java.util.Calendar;
import java.sql.Ref;
import java.sql.Statement;
import javax.sql.RowSetMetaData;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLInput;
import javax.sql.rowset.serial.SQLInputImpl;
import sun.reflect.misc.ReflectUtil;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.DateFormat;
import java.sql.Timestamp;
import java.sql.Date;
import java.math.BigDecimal;
import com.sun.rowset.internal.BaseRow;
import java.util.TreeMap;
import java.util.Collection;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import com.sun.rowset.internal.CachedRowSetWriter;
import javax.sql.rowset.spi.SyncProviderException;
import java.util.Map;
import javax.sql.rowset.serial.SerialArray;
import java.sql.Array;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLData;
import javax.sql.rowset.serial.SerialStruct;
import java.sql.Struct;
import com.sun.rowset.internal.Row;
import java.util.Hashtable;
import java.sql.SQLException;
import com.sun.rowset.providers.RIOptimisticProvider;
import javax.sql.rowset.spi.SyncFactory;
import java.io.IOException;
import javax.sql.rowset.spi.TransactionalWriter;
import com.sun.rowset.internal.CachedRowSetReader;
import java.sql.ResultSet;
import javax.sql.rowset.RowSetWarning;
import java.sql.SQLWarning;
import com.sun.rowset.internal.InsertRow;
import java.util.Vector;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import javax.sql.RowSetWriter;
import javax.sql.RowSetReader;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.CachedRowSet;
import java.io.Serializable;
import javax.sql.RowSetInternal;
import javax.sql.RowSet;
import javax.sql.rowset.BaseRowSet;

public class CachedRowSetImpl extends BaseRowSet implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet
{
    private SyncProvider provider;
    private RowSetReader rowSetReader;
    private RowSetWriter rowSetWriter;
    private transient Connection conn;
    private transient ResultSetMetaData RSMD;
    private RowSetMetaDataImpl RowSetMD;
    private int[] keyCols;
    private String tableName;
    private Vector<Object> rvh;
    private int cursorPos;
    private int absolutePos;
    private int numDeleted;
    private int numRows;
    private InsertRow insertRow;
    private boolean onInsertRow;
    private int currentRow;
    private boolean lastValueNull;
    private SQLWarning sqlwarn;
    private String strMatchColumn;
    private int iMatchColumn;
    private RowSetWarning rowsetWarning;
    private String DEFAULT_SYNC_PROVIDER;
    private boolean dbmslocatorsUpdateCopy;
    private transient ResultSet resultSet;
    private int endPos;
    private int prevEndPos;
    private int startPos;
    private int startPrev;
    private int pageSize;
    private int maxRowsreached;
    private boolean pagenotend;
    private boolean onFirstPage;
    private boolean onLastPage;
    private int populatecallcount;
    private int totalRows;
    private boolean callWithCon;
    private CachedRowSetReader crsReader;
    private Vector<Integer> iMatchColumns;
    private Vector<String> strMatchColumns;
    private boolean tXWriter;
    private TransactionalWriter tWriter;
    protected transient JdbcRowSetResourceBundle resBundle;
    private boolean updateOnInsert;
    static final long serialVersionUID = 1884577171200622428L;
    
    public CachedRowSetImpl() throws SQLException {
        this.strMatchColumn = "";
        this.iMatchColumn = -1;
        this.DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
        this.pagenotend = true;
        this.tXWriter = false;
        this.tWriter = null;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        this.provider = SyncFactory.getInstance(this.DEFAULT_SYNC_PROVIDER);
        if (!(this.provider instanceof RIOptimisticProvider)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidp").toString());
        }
        this.rowSetReader = this.provider.getRowSetReader();
        this.rowSetWriter = this.provider.getRowSetWriter();
        this.initParams();
        this.initContainer();
        this.initProperties();
        this.onInsertRow = false;
        this.insertRow = null;
        this.sqlwarn = new SQLWarning();
        this.rowsetWarning = new RowSetWarning();
    }
    
    public CachedRowSetImpl(final Hashtable hashtable) throws SQLException {
        this.strMatchColumn = "";
        this.iMatchColumn = -1;
        this.DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
        this.pagenotend = true;
        this.tXWriter = false;
        this.tWriter = null;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        if (hashtable == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nullhash").toString());
        }
        this.provider = SyncFactory.getInstance(hashtable.get("rowset.provider.classname"));
        this.rowSetReader = this.provider.getRowSetReader();
        this.rowSetWriter = this.provider.getRowSetWriter();
        this.initParams();
        this.initContainer();
        this.initProperties();
    }
    
    private void initContainer() {
        this.rvh = new Vector<Object>(100);
        this.cursorPos = 0;
        this.absolutePos = 0;
        this.numRows = 0;
        this.numDeleted = 0;
    }
    
    private void initProperties() throws SQLException {
        if (this.resBundle == null) {
            try {
                this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
            }
            catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.setShowDeleted(false);
        this.setQueryTimeout(0);
        this.setMaxRows(0);
        this.setMaxFieldSize(0);
        this.setType(1004);
        this.setConcurrency(1008);
        if (this.rvh.size() > 0 && !this.isReadOnly()) {
            this.setReadOnly(false);
        }
        else {
            this.setReadOnly(true);
        }
        this.setTransactionIsolation(2);
        this.setEscapeProcessing(true);
        this.checkTransactionalWriter();
        this.iMatchColumns = new Vector<Integer>(10);
        for (int i = 0; i < 10; ++i) {
            this.iMatchColumns.add(i, -1);
        }
        this.strMatchColumns = new Vector<String>(10);
        for (int j = 0; j < 10; ++j) {
            this.strMatchColumns.add(j, null);
        }
    }
    
    private void checkTransactionalWriter() {
        if (this.rowSetWriter != null) {
            final Class<? extends RowSetWriter> class1 = this.rowSetWriter.getClass();
            if (class1 != null) {
                final Class<?>[] interfaces = class1.getInterfaces();
                for (int i = 0; i < interfaces.length; ++i) {
                    if (interfaces[i].getName().indexOf("TransactionalWriter") > 0) {
                        this.tXWriter = true;
                        this.establishTransactionalWriter();
                    }
                }
            }
        }
    }
    
    private void establishTransactionalWriter() {
        this.tWriter = (TransactionalWriter)this.provider.getRowSetWriter();
    }
    
    @Override
    public void setCommand(final String command) throws SQLException {
        super.setCommand(command);
        if (!this.buildTableName(command).equals("")) {
            this.setTableName(this.buildTableName(command));
        }
    }
    
    @Override
    public void populate(final ResultSet resultSet) throws SQLException {
        final Map<String, Class<?>> typeMap = this.getTypeMap();
        if (resultSet == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
        }
        this.resultSet = resultSet;
        this.RSMD = resultSet.getMetaData();
        this.initMetaData(this.RowSetMD = new RowSetMetaDataImpl(), this.RSMD);
        this.RSMD = null;
        final int columnCount = this.RowSetMD.getColumnCount();
        final int maxRows = this.getMaxRows();
        int numRows = 0;
        while (resultSet.next()) {
            final Row row = new Row(columnCount);
            if (numRows > maxRows && maxRows > 0) {
                this.rowsetWarning.setNextWarning(new RowSetWarning("Populating rows setting has exceeded max row setting"));
            }
            for (int i = 1; i <= columnCount; ++i) {
                Object o;
                if (typeMap == null || typeMap.isEmpty()) {
                    o = resultSet.getObject(i);
                }
                else {
                    o = resultSet.getObject(i, typeMap);
                }
                if (o instanceof Struct) {
                    o = new SerialStruct((Struct)o, typeMap);
                }
                else if (o instanceof SQLData) {
                    o = new SerialStruct((SQLData)o, typeMap);
                }
                else if (o instanceof Blob) {
                    o = new SerialBlob((Blob)o);
                }
                else if (o instanceof Clob) {
                    o = new SerialClob((Clob)o);
                }
                else if (o instanceof Array) {
                    if (typeMap != null) {
                        o = new SerialArray((Array)o, typeMap);
                    }
                    else {
                        o = new SerialArray((Array)o);
                    }
                }
                row.initColumnObject(i, o);
            }
            ++numRows;
            this.rvh.add(row);
        }
        this.numRows = numRows;
        this.notifyRowSetChanged();
    }
    
    private void initMetaData(final RowSetMetaDataImpl rowSetMetaDataImpl, final ResultSetMetaData resultSetMetaData) throws SQLException {
        final int columnCount = resultSetMetaData.getColumnCount();
        rowSetMetaDataImpl.setColumnCount(columnCount);
        for (int i = 1; i <= columnCount; ++i) {
            rowSetMetaDataImpl.setAutoIncrement(i, resultSetMetaData.isAutoIncrement(i));
            if (resultSetMetaData.isAutoIncrement(i)) {
                this.updateOnInsert = true;
            }
            rowSetMetaDataImpl.setCaseSensitive(i, resultSetMetaData.isCaseSensitive(i));
            rowSetMetaDataImpl.setCurrency(i, resultSetMetaData.isCurrency(i));
            rowSetMetaDataImpl.setNullable(i, resultSetMetaData.isNullable(i));
            rowSetMetaDataImpl.setSigned(i, resultSetMetaData.isSigned(i));
            rowSetMetaDataImpl.setSearchable(i, resultSetMetaData.isSearchable(i));
            int columnDisplaySize = resultSetMetaData.getColumnDisplaySize(i);
            if (columnDisplaySize < 0) {
                columnDisplaySize = 0;
            }
            rowSetMetaDataImpl.setColumnDisplaySize(i, columnDisplaySize);
            rowSetMetaDataImpl.setColumnLabel(i, resultSetMetaData.getColumnLabel(i));
            rowSetMetaDataImpl.setColumnName(i, resultSetMetaData.getColumnName(i));
            rowSetMetaDataImpl.setSchemaName(i, resultSetMetaData.getSchemaName(i));
            int precision = resultSetMetaData.getPrecision(i);
            if (precision < 0) {
                precision = 0;
            }
            rowSetMetaDataImpl.setPrecision(i, precision);
            int scale = resultSetMetaData.getScale(i);
            if (scale < 0) {
                scale = 0;
            }
            rowSetMetaDataImpl.setScale(i, scale);
            rowSetMetaDataImpl.setTableName(i, resultSetMetaData.getTableName(i));
            rowSetMetaDataImpl.setCatalogName(i, resultSetMetaData.getCatalogName(i));
            rowSetMetaDataImpl.setColumnType(i, resultSetMetaData.getColumnType(i));
            rowSetMetaDataImpl.setColumnTypeName(i, resultSetMetaData.getColumnTypeName(i));
        }
        if (this.conn != null) {
            this.dbmslocatorsUpdateCopy = this.conn.getMetaData().locatorsUpdateCopy();
        }
    }
    
    @Override
    public void execute(final Connection connection) throws SQLException {
        this.setConnection(connection);
        if (this.getPageSize() != 0) {
            (this.crsReader = (CachedRowSetReader)this.provider.getRowSetReader()).setStartPosition(1);
            this.callWithCon = true;
            this.crsReader.readData(this);
        }
        else {
            this.rowSetReader.readData(this);
        }
        this.RowSetMD = (RowSetMetaDataImpl)this.getMetaData();
        if (connection != null) {
            this.dbmslocatorsUpdateCopy = connection.getMetaData().locatorsUpdateCopy();
        }
    }
    
    private void setConnection(final Connection conn) {
        this.conn = conn;
    }
    
    @Override
    public void acceptChanges() throws SyncProviderException {
        if (this.onInsertRow) {
            throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        final int cursorPos = this.cursorPos;
        boolean b = false;
        boolean writeData = false;
        try {
            if (this.rowSetWriter != null) {
                final int cursorPos2 = this.cursorPos;
                writeData = this.rowSetWriter.writeData(this);
                this.cursorPos = cursorPos2;
            }
            if (this.tXWriter) {
                if (!writeData) {
                    (this.tWriter = (TransactionalWriter)this.rowSetWriter).rollback();
                    b = false;
                }
                else {
                    this.tWriter = (TransactionalWriter)this.rowSetWriter;
                    if (this.tWriter instanceof CachedRowSetWriter) {
                        ((CachedRowSetWriter)this.tWriter).commit(this, this.updateOnInsert);
                    }
                    else {
                        this.tWriter.commit();
                    }
                    b = true;
                }
            }
            if (b) {
                this.setOriginal();
            }
            else if (!b) {
                throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.accfailed").toString());
            }
        }
        catch (final SyncProviderException ex) {
            throw ex;
        }
        catch (final SQLException ex2) {
            ex2.printStackTrace();
            throw new SyncProviderException(ex2.getMessage());
        }
        catch (final SecurityException ex3) {
            throw new SyncProviderException(ex3.getMessage());
        }
    }
    
    @Override
    public void acceptChanges(final Connection connection) throws SyncProviderException {
        this.setConnection(connection);
        this.acceptChanges();
    }
    
    @Override
    public void restoreOriginal() throws SQLException {
        final Iterator<Object> iterator = this.rvh.iterator();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            if (row.getInserted()) {
                iterator.remove();
                --this.numRows;
            }
            else {
                if (row.getDeleted()) {
                    row.clearDeleted();
                }
                if (!row.getUpdated()) {
                    continue;
                }
                row.clearUpdated();
            }
        }
        this.cursorPos = 0;
        this.notifyRowSetChanged();
    }
    
    @Override
    public void release() throws SQLException {
        this.initContainer();
        this.notifyRowSetChanged();
    }
    
    @Override
    public void undoDelete() throws SQLException {
        if (!this.getShowDeleted()) {
            return;
        }
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        final Row row = (Row)this.getCurrentRow();
        if (row.getDeleted()) {
            row.clearDeleted();
            --this.numDeleted;
            this.notifyRowChanged();
        }
    }
    
    @Override
    public void undoInsert() throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        if (((Row)this.getCurrentRow()).getInserted()) {
            this.rvh.remove(this.cursorPos - 1);
            --this.numRows;
            this.notifyRowChanged();
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.illegalop").toString());
    }
    
    @Override
    public void undoUpdate() throws SQLException {
        this.moveToCurrentRow();
        this.undoDelete();
        this.undoInsert();
    }
    
    @Override
    public RowSet createShared() throws SQLException {
        RowSet set;
        try {
            set = (RowSet)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new SQLException(ex.getMessage());
        }
        return set;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public CachedRowSet createCopy() throws SQLException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(byteArrayOutputStream).writeObject(this);
        }
        catch (final IOException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex.getMessage()));
        }
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        }
        catch (final StreamCorruptedException ex2) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex2.getMessage()));
        }
        catch (final IOException ex3) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex3.getMessage()));
        }
        try {
            final CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)objectInputStream.readObject();
            cachedRowSetImpl.resBundle = this.resBundle;
            return cachedRowSetImpl;
        }
        catch (final ClassNotFoundException ex4) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex4.getMessage()));
        }
        catch (final OptionalDataException ex5) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex5.getMessage()));
        }
        catch (final IOException ex6) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), ex6.getMessage()));
        }
    }
    
    @Override
    public CachedRowSet createCopySchema() throws SQLException {
        final int numRows = this.numRows;
        this.numRows = 0;
        final CachedRowSet copy = this.createCopy();
        this.numRows = numRows;
        return copy;
    }
    
    @Override
    public CachedRowSet createCopyNoConstraints() throws SQLException {
        final CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)this.createCopy();
        cachedRowSetImpl.initProperties();
        try {
            cachedRowSetImpl.unsetMatchColumn(cachedRowSetImpl.getMatchColumnIndexes());
        }
        catch (final SQLException ex) {}
        try {
            cachedRowSetImpl.unsetMatchColumn(cachedRowSetImpl.getMatchColumnNames());
        }
        catch (final SQLException ex2) {}
        return cachedRowSetImpl;
    }
    
    @Override
    public Collection<?> toCollection() throws SQLException {
        final TreeMap treeMap = new TreeMap();
        for (int i = 0; i < this.numRows; ++i) {
            treeMap.put(i, this.rvh.get(i));
        }
        return treeMap.values();
    }
    
    @Override
    public Collection<?> toCollection(final int n) throws SQLException {
        int i = this.numRows;
        final Vector vector = new Vector<Object>(i);
        final CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)this.createCopy();
        while (i != 0) {
            cachedRowSetImpl.next();
            vector.add(cachedRowSetImpl.getObject(n));
            --i;
        }
        return vector;
    }
    
    @Override
    public Collection<?> toCollection(final String s) throws SQLException {
        return this.toCollection(this.getColIdxByName(s));
    }
    
    @Override
    public SyncProvider getSyncProvider() throws SQLException {
        return this.provider;
    }
    
    @Override
    public void setSyncProvider(final String s) throws SQLException {
        this.provider = SyncFactory.getInstance(s);
        this.rowSetReader = this.provider.getRowSetReader();
        this.rowSetWriter = this.provider.getRowSetWriter();
    }
    
    @Override
    public void execute() throws SQLException {
        this.execute(null);
    }
    
    @Override
    public boolean next() throws SQLException {
        if (this.cursorPos < 0 || this.cursorPos >= this.numRows + 1) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        final boolean internalNext = this.internalNext();
        this.notifyCursorMoved();
        return internalNext;
    }
    
    protected boolean internalNext() throws SQLException {
        boolean b = false;
        do {
            if (this.cursorPos < this.numRows) {
                ++this.cursorPos;
                b = true;
            }
            else {
                if (this.cursorPos == this.numRows) {
                    ++this.cursorPos;
                    b = false;
                    break;
                }
                continue;
            }
        } while (!this.getShowDeleted() && this.rowDeleted());
        if (b) {
            ++this.absolutePos;
        }
        else {
            this.absolutePos = 0;
        }
        return b;
    }
    
    @Override
    public void close() throws SQLException {
        this.cursorPos = 0;
        this.absolutePos = 0;
        this.numRows = 0;
        this.numDeleted = 0;
        this.initProperties();
        this.rvh.clear();
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this.lastValueNull;
    }
    
    private void setLastValueNull(final boolean lastValueNull) {
        this.lastValueNull = lastValueNull;
    }
    
    private void checkIndex(final int n) throws SQLException {
        if (n < 1 || n > this.RowSetMD.getColumnCount()) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString());
        }
    }
    
    private void checkCursor() throws SQLException {
        if (this.isAfterLast() || this.isBeforeFirst()) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
    }
    
    private int getColIdxByName(final String s) throws SQLException {
        this.RowSetMD = (RowSetMetaDataImpl)this.getMetaData();
        for (int columnCount = this.RowSetMD.getColumnCount(), i = 1; i <= columnCount; ++i) {
            final String columnName = this.RowSetMD.getColumnName(i);
            if (columnName != null && s.equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalcolnm").toString());
    }
    
    protected BaseRow getCurrentRow() {
        if (this.onInsertRow) {
            return this.insertRow;
        }
        return this.rvh.get(this.cursorPos - 1);
    }
    
    protected void removeCurrentRow() {
        ((Row)this.getCurrentRow()).setDeleted();
        this.rvh.remove(this.cursorPos - 1);
        --this.numRows;
    }
    
    @Override
    public String getString(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        return columnObject.toString();
    }
    
    @Override
    public boolean getBoolean(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return false;
        }
        if (columnObject instanceof Boolean) {
            return (boolean)columnObject;
        }
        try {
            return Double.compare(Double.parseDouble(columnObject.toString()), 0.0) != 0;
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.boolfail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public byte getByte(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0;
        }
        try {
            return Byte.valueOf(columnObject.toString());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.bytefail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public short getShort(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0;
        }
        try {
            return Short.valueOf(columnObject.toString().trim());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.shortfail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public int getInt(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0;
        }
        try {
            return Integer.valueOf(columnObject.toString().trim());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.intfail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public long getLong(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0L;
        }
        try {
            return Long.valueOf(columnObject.toString().trim());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.longfail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public float getFloat(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0.0f;
        }
        try {
            return new Float(columnObject.toString());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.floatfail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public double getDouble(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return 0.0;
        }
        try {
            return new Double(columnObject.toString().trim());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int n, final int scale) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        if (this.getCurrentRow().getColumnObject(n) == null) {
            this.setLastValueNull(true);
            return new BigDecimal(0);
        }
        return this.getBigDecimal(n).setScale(scale);
    }
    
    @Override
    public byte[] getBytes(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        return (byte[])this.getCurrentRow().getColumnObject(n);
    }
    
    @Override
    public Date getDate(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        switch (this.RowSetMD.getColumnType(n)) {
            case 91: {
                return new Date(((Date)columnObject).getTime());
            }
            case 93: {
                return new Date(((Timestamp)columnObject).getTime());
            }
            case -1:
            case 1:
            case 12: {
                try {
                    return (Date)DateFormat.getDateInstance().parse(columnObject.toString());
                }
                catch (final ParseException ex) {
                    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), columnObject.toString().trim(), n));
                }
                break;
            }
        }
        throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), columnObject.toString().trim(), n));
    }
    
    @Override
    public Time getTime(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        switch (this.RowSetMD.getColumnType(n)) {
            case 92: {
                return (Time)columnObject;
            }
            case 93: {
                return new Time(((Timestamp)columnObject).getTime());
            }
            case -1:
            case 1:
            case 12: {
                try {
                    return (Time)DateFormat.getTimeInstance().parse(columnObject.toString());
                }
                catch (final ParseException ex) {
                    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), columnObject.toString().trim(), n));
                }
                break;
            }
        }
        throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), columnObject.toString().trim(), n));
    }
    
    @Override
    public Timestamp getTimestamp(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        switch (this.RowSetMD.getColumnType(n)) {
            case 93: {
                return (Timestamp)columnObject;
            }
            case 92: {
                return new Timestamp(((Time)columnObject).getTime());
            }
            case 91: {
                return new Timestamp(((Date)columnObject).getTime());
            }
            case -1:
            case 1:
            case 12: {
                try {
                    return (Timestamp)DateFormat.getTimeInstance().parse(columnObject.toString());
                }
                catch (final ParseException ex) {
                    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), columnObject.toString().trim(), n));
                }
                break;
            }
        }
        throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), columnObject.toString().trim(), n));
    }
    
    @Override
    public InputStream getAsciiStream(final int n) throws SQLException {
        this.asciiStream = null;
        this.checkIndex(n);
        this.checkCursor();
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.lastValueNull = true;
            return null;
        }
        try {
            if (!this.isString(this.RowSetMD.getColumnType(n))) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            }
            this.asciiStream = new ByteArrayInputStream(((String)columnObject).getBytes("ASCII"));
        }
        catch (final UnsupportedEncodingException ex) {
            throw new SQLException(ex.getMessage());
        }
        return this.asciiStream;
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int n) throws SQLException {
        this.unicodeStream = null;
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isBinary(this.RowSetMD.getColumnType(n)) && !this.isString(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.lastValueNull = true;
            return null;
        }
        return this.unicodeStream = new StringBufferInputStream(columnObject.toString());
    }
    
    @Override
    public InputStream getBinaryStream(final int n) throws SQLException {
        this.binaryStream = null;
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.lastValueNull = true;
            return null;
        }
        return this.binaryStream = new ByteArrayInputStream((byte[])columnObject);
    }
    
    @Override
    public String getString(final String s) throws SQLException {
        return this.getString(this.getColIdxByName(s));
    }
    
    @Override
    public boolean getBoolean(final String s) throws SQLException {
        return this.getBoolean(this.getColIdxByName(s));
    }
    
    @Override
    public byte getByte(final String s) throws SQLException {
        return this.getByte(this.getColIdxByName(s));
    }
    
    @Override
    public short getShort(final String s) throws SQLException {
        return this.getShort(this.getColIdxByName(s));
    }
    
    @Override
    public int getInt(final String s) throws SQLException {
        return this.getInt(this.getColIdxByName(s));
    }
    
    @Override
    public long getLong(final String s) throws SQLException {
        return this.getLong(this.getColIdxByName(s));
    }
    
    @Override
    public float getFloat(final String s) throws SQLException {
        return this.getFloat(this.getColIdxByName(s));
    }
    
    @Override
    public double getDouble(final String s) throws SQLException {
        return this.getDouble(this.getColIdxByName(s));
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        return this.getBigDecimal(this.getColIdxByName(s), n);
    }
    
    @Override
    public byte[] getBytes(final String s) throws SQLException {
        return this.getBytes(this.getColIdxByName(s));
    }
    
    @Override
    public Date getDate(final String s) throws SQLException {
        return this.getDate(this.getColIdxByName(s));
    }
    
    @Override
    public Time getTime(final String s) throws SQLException {
        return this.getTime(this.getColIdxByName(s));
    }
    
    @Override
    public Timestamp getTimestamp(final String s) throws SQLException {
        return this.getTimestamp(this.getColIdxByName(s));
    }
    
    @Override
    public InputStream getAsciiStream(final String s) throws SQLException {
        return this.getAsciiStream(this.getColIdxByName(s));
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String s) throws SQLException {
        return this.getUnicodeStream(this.getColIdxByName(s));
    }
    
    @Override
    public InputStream getBinaryStream(final String s) throws SQLException {
        return this.getBinaryStream(this.getColIdxByName(s));
    }
    
    @Override
    public SQLWarning getWarnings() {
        return this.sqlwarn;
    }
    
    @Override
    public void clearWarnings() {
        this.sqlwarn = null;
    }
    
    @Override
    public String getCursorName() throws SQLException {
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.posupdate").toString());
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.RowSetMD;
    }
    
    @Override
    public Object getObject(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        if (columnObject instanceof Struct) {
            final Struct struct = (Struct)columnObject;
            final Map<String, Class<?>> typeMap = this.getTypeMap();
            final Class clazz = typeMap.get(struct.getSQLTypeName());
            if (clazz != null) {
                SQLData sqlData;
                try {
                    sqlData = (SQLData)ReflectUtil.newInstance(clazz);
                }
                catch (final Exception ex) {
                    throw new SQLException("Unable to Instantiate: ", ex);
                }
                sqlData.readSQL(new SQLInputImpl(struct.getAttributes(typeMap), typeMap), struct.getSQLTypeName());
                return sqlData;
            }
        }
        return columnObject;
    }
    
    @Override
    public Object getObject(final String s) throws SQLException {
        return this.getObject(this.getColIdxByName(s));
    }
    
    @Override
    public int findColumn(final String s) throws SQLException {
        return this.getColIdxByName(s);
    }
    
    @Override
    public Reader getCharacterStream(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.isBinary(this.RowSetMD.getColumnType(n))) {
            final Object columnObject = this.getCurrentRow().getColumnObject(n);
            if (columnObject == null) {
                this.lastValueNull = true;
                return null;
            }
            this.charStream = new InputStreamReader(new ByteArrayInputStream((byte[])columnObject));
        }
        else {
            if (!this.isString(this.RowSetMD.getColumnType(n))) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
            }
            final Object columnObject2 = this.getCurrentRow().getColumnObject(n);
            if (columnObject2 == null) {
                this.lastValueNull = true;
                return null;
            }
            this.charStream = new StringReader(columnObject2.toString());
        }
        return this.charStream;
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
        return this.getCharacterStream(this.getColIdxByName(s));
    }
    
    @Override
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        try {
            return new BigDecimal(columnObject.toString().trim());
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), columnObject.toString().trim(), n));
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        return this.getBigDecimal(this.getColIdxByName(s));
    }
    
    @Override
    public int size() {
        return this.numRows;
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.cursorPos == 0 && this.numRows > 0;
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        return this.cursorPos == this.numRows + 1 && this.numRows > 0;
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        final int cursorPos = this.cursorPos;
        final int absolutePos = this.absolutePos;
        this.internalFirst();
        if (this.cursorPos == cursorPos) {
            return true;
        }
        this.cursorPos = cursorPos;
        this.absolutePos = absolutePos;
        return false;
    }
    
    @Override
    public boolean isLast() throws SQLException {
        final int cursorPos = this.cursorPos;
        final int absolutePos = this.absolutePos;
        final boolean showDeleted = this.getShowDeleted();
        this.setShowDeleted(true);
        this.internalLast();
        if (this.cursorPos == cursorPos) {
            this.setShowDeleted(showDeleted);
            return true;
        }
        this.setShowDeleted(showDeleted);
        this.cursorPos = cursorPos;
        this.absolutePos = absolutePos;
        return false;
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        if (this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.beforefirst").toString());
        }
        this.cursorPos = 0;
        this.absolutePos = 0;
        this.notifyCursorMoved();
    }
    
    @Override
    public void afterLast() throws SQLException {
        if (this.numRows > 0) {
            this.cursorPos = this.numRows + 1;
            this.absolutePos = 0;
            this.notifyCursorMoved();
        }
    }
    
    @Override
    public boolean first() throws SQLException {
        if (this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.first").toString());
        }
        final boolean internalFirst = this.internalFirst();
        this.notifyCursorMoved();
        return internalFirst;
    }
    
    protected boolean internalFirst() throws SQLException {
        boolean b = false;
        if (this.numRows > 0) {
            this.cursorPos = 1;
            b = (this.getShowDeleted() || !this.rowDeleted() || this.internalNext());
        }
        if (b) {
            this.absolutePos = 1;
        }
        else {
            this.absolutePos = 0;
        }
        return b;
    }
    
    @Override
    public boolean last() throws SQLException {
        if (this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
        }
        final boolean internalLast = this.internalLast();
        this.notifyCursorMoved();
        return internalLast;
    }
    
    protected boolean internalLast() throws SQLException {
        boolean b = false;
        if (this.numRows > 0) {
            this.cursorPos = this.numRows;
            b = (this.getShowDeleted() || !this.rowDeleted() || this.internalPrevious());
        }
        if (b) {
            this.absolutePos = this.numRows - this.numDeleted;
        }
        else {
            this.absolutePos = 0;
        }
        return b;
    }
    
    @Override
    public int getRow() throws SQLException {
        if (this.numRows > 0 && this.cursorPos > 0 && this.cursorPos < this.numRows + 1 && !this.getShowDeleted() && !this.rowDeleted()) {
            return this.absolutePos;
        }
        if (this.getShowDeleted()) {
            return this.cursorPos;
        }
        return 0;
    }
    
    @Override
    public boolean absolute(final int n) throws SQLException {
        if (n == 0 || this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.absolute").toString());
        }
        if (n > 0) {
            if (n > this.numRows) {
                this.afterLast();
                return false;
            }
            if (this.absolutePos <= 0) {
                this.internalFirst();
            }
        }
        else {
            if (this.cursorPos + n < 0) {
                this.beforeFirst();
                return false;
            }
            if (this.absolutePos >= 0) {
                this.internalLast();
            }
        }
        while (this.absolutePos != n) {
            if (this.absolutePos < n) {
                if (!this.internalNext()) {
                    break;
                }
                continue;
            }
            else {
                if (!this.internalPrevious()) {
                    break;
                }
                continue;
            }
        }
        this.notifyCursorMoved();
        return !this.isAfterLast() && !this.isBeforeFirst();
    }
    
    @Override
    public boolean relative(final int n) throws SQLException {
        if (this.numRows == 0 || this.isBeforeFirst() || this.isAfterLast() || this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.relative").toString());
        }
        if (n == 0) {
            return true;
        }
        if (n > 0) {
            if (this.cursorPos + n > this.numRows) {
                this.afterLast();
            }
            else {
                for (int n2 = 0; n2 < n && this.internalNext(); ++n2) {}
            }
        }
        else if (this.cursorPos + n < 0) {
            this.beforeFirst();
        }
        else {
            for (int i = n; i < 0; ++i) {
                if (!this.internalPrevious()) {
                    break;
                }
            }
        }
        this.notifyCursorMoved();
        return !this.isAfterLast() && !this.isBeforeFirst();
    }
    
    @Override
    public boolean previous() throws SQLException {
        if (this.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString());
        }
        if (this.cursorPos < 0 || this.cursorPos > this.numRows + 1) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        final boolean internalPrevious = this.internalPrevious();
        this.notifyCursorMoved();
        return internalPrevious;
    }
    
    protected boolean internalPrevious() throws SQLException {
        boolean b = false;
        do {
            if (this.cursorPos > 1) {
                --this.cursorPos;
                b = true;
            }
            else {
                if (this.cursorPos == 1) {
                    --this.cursorPos;
                    b = false;
                    break;
                }
                continue;
            }
        } while (!this.getShowDeleted() && this.rowDeleted());
        if (b) {
            --this.absolutePos;
        }
        else {
            this.absolutePos = 0;
        }
        return b;
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        return ((Row)this.getCurrentRow()).getUpdated();
    }
    
    @Override
    public boolean columnUpdated(final int n) throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        return ((Row)this.getCurrentRow()).getColUpdated(n - 1);
    }
    
    @Override
    public boolean columnUpdated(final String s) throws SQLException {
        return this.columnUpdated(this.getColIdxByName(s));
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        return ((Row)this.getCurrentRow()).getInserted();
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        if (this.isAfterLast() || this.isBeforeFirst() || this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        return ((Row)this.getCurrentRow()).getDeleted();
    }
    
    private boolean isNumeric(final int n) {
        switch (n) {
            case -7:
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isString(final int n) {
        switch (n) {
            case -1:
            case 1:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isBinary(final int n) {
        switch (n) {
            case -4:
            case -3:
            case -2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isTemporal(final int n) {
        switch (n) {
            case 91:
            case 92:
            case 93: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isBoolean(final int n) {
        switch (n) {
            case -7:
            case 16: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private Object convertNumeric(final Object o, final int n, final int n2) throws SQLException {
        if (n == n2) {
            return o;
        }
        if (!this.isNumeric(n2) && !this.isString(n2)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + n2);
        }
        try {
            switch (n2) {
                case -7: {
                    return Integer.valueOf(o.toString().trim()).equals(0) ? false : true;
                }
                case -6: {
                    return Byte.valueOf(o.toString().trim());
                }
                case 5: {
                    return Short.valueOf(o.toString().trim());
                }
                case 4: {
                    return Integer.valueOf(o.toString().trim());
                }
                case -5: {
                    return Long.valueOf(o.toString().trim());
                }
                case 2:
                case 3: {
                    return new BigDecimal(o.toString().trim());
                }
                case 6:
                case 7: {
                    return new Float(o.toString().trim());
                }
                case 8: {
                    return new Double(o.toString().trim());
                }
                case -1:
                case 1:
                case 12: {
                    return o.toString();
                }
                default: {
                    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + n2);
                }
            }
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + n2);
        }
    }
    
    private Object convertTemporal(final Object o, final int n, final int n2) throws SQLException {
        if (n == n2) {
            return o;
        }
        if (this.isNumeric(n2) || (!this.isString(n2) && !this.isTemporal(n2))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        try {
            switch (n2) {
                case 91: {
                    if (n == 93) {
                        return new Date(((Timestamp)o).getTime());
                    }
                    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
                }
                case 93: {
                    if (n == 92) {
                        return new Timestamp(((Time)o).getTime());
                    }
                    return new Timestamp(((Date)o).getTime());
                }
                case 92: {
                    if (n == 93) {
                        return new Time(((Timestamp)o).getTime());
                    }
                    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
                }
                case -1:
                case 1:
                case 12: {
                    return o.toString();
                }
                default: {
                    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
                }
            }
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
    }
    
    private Object convertBoolean(final Object o, final int n, final int n2) throws SQLException {
        if (n == n2) {
            return o;
        }
        if (this.isNumeric(n2) || (!this.isString(n2) && !this.isBoolean(n2))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        try {
            switch (n2) {
                case -7: {
                    return Integer.valueOf(o.toString().trim()).equals(0) ? false : true;
                }
                case 16: {
                    return Boolean.valueOf(o.toString().trim());
                }
                default: {
                    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + n2);
                }
            }
        }
        catch (final NumberFormatException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + n2);
        }
    }
    
    @Override
    public void updateNull(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, null);
    }
    
    @Override
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertBoolean(b, -7, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateByte(final int n, final byte b) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(b, -6, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateShort(final int n, final short n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(n2, 5, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateInt(final int n, final int n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(n2, 4, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateLong(final int n, final long n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(n2, -5, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateFloat(final int n, final float n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(n2, 7, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateDouble(final int n, final double n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(n2, 8, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertNumeric(bigDecimal, 2, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateString(final int n, final String s) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, s);
    }
    
    @Override
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.getCurrentRow().setColumnObject(n, array);
    }
    
    @Override
    public void updateDate(final int n, final Date date) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertTemporal(date, 91, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateTime(final int n, final Time time) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertTemporal(time, 92, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, this.convertTemporal(timestamp, 93, this.RowSetMD.getColumnType(n)));
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isString(this.RowSetMD.getColumnType(n)) && !this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        final byte[] array = new byte[n2];
        try {
            int i = 0;
            do {
                i += inputStream.read(array, i, n2 - i);
            } while (i != n2);
        }
        catch (final IOException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.asciistream").toString());
        }
        this.getCurrentRow().setColumnObject(n, new String(array));
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        final byte[] array = new byte[n2];
        try {
            int i = 0;
            do {
                i += inputStream.read(array, i, n2 - i);
            } while (i != -1);
        }
        catch (final IOException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
        }
        this.getCurrentRow().setColumnObject(n, array);
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (!this.isString(this.RowSetMD.getColumnType(n)) && !this.isBinary(this.RowSetMD.getColumnType(n))) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        final char[] array = new char[n2];
        try {
            int i = 0;
            do {
                i += reader.read(array, i, n2 - i);
            } while (i != n2);
        }
        catch (final IOException ex) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
        }
        this.getCurrentRow().setColumnObject(n, new String(array));
    }
    
    @Override
    public void updateObject(final int n, final Object o, final int scale) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        final int columnType = this.RowSetMD.getColumnType(n);
        if (columnType == 3 || columnType == 2) {
            ((BigDecimal)o).setScale(scale);
        }
        this.getCurrentRow().setColumnObject(n, o);
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, o);
    }
    
    @Override
    public void updateNull(final String s) throws SQLException {
        this.updateNull(this.getColIdxByName(s));
    }
    
    @Override
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        this.updateBoolean(this.getColIdxByName(s), b);
    }
    
    @Override
    public void updateByte(final String s, final byte b) throws SQLException {
        this.updateByte(this.getColIdxByName(s), b);
    }
    
    @Override
    public void updateShort(final String s, final short n) throws SQLException {
        this.updateShort(this.getColIdxByName(s), n);
    }
    
    @Override
    public void updateInt(final String s, final int n) throws SQLException {
        this.updateInt(this.getColIdxByName(s), n);
    }
    
    @Override
    public void updateLong(final String s, final long n) throws SQLException {
        this.updateLong(this.getColIdxByName(s), n);
    }
    
    @Override
    public void updateFloat(final String s, final float n) throws SQLException {
        this.updateFloat(this.getColIdxByName(s), n);
    }
    
    @Override
    public void updateDouble(final String s, final double n) throws SQLException {
        this.updateDouble(this.getColIdxByName(s), n);
    }
    
    @Override
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.updateBigDecimal(this.getColIdxByName(s), bigDecimal);
    }
    
    @Override
    public void updateString(final String s, final String s2) throws SQLException {
        this.updateString(this.getColIdxByName(s), s2);
    }
    
    @Override
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        this.updateBytes(this.getColIdxByName(s), array);
    }
    
    @Override
    public void updateDate(final String s, final Date date) throws SQLException {
        this.updateDate(this.getColIdxByName(s), date);
    }
    
    @Override
    public void updateTime(final String s, final Time time) throws SQLException {
        this.updateTime(this.getColIdxByName(s), time);
    }
    
    @Override
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.updateTimestamp(this.getColIdxByName(s), timestamp);
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateAsciiStream(this.getColIdxByName(s), inputStream, n);
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateBinaryStream(this.getColIdxByName(s), inputStream, n);
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.updateCharacterStream(this.getColIdxByName(s), reader, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        this.updateObject(this.getColIdxByName(s), o, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o) throws SQLException {
        this.updateObject(this.getColIdxByName(s), o);
    }
    
    @Override
    public void insertRow() throws SQLException {
        if (!this.onInsertRow || !this.insertRow.isCompleteRow(this.RowSetMD)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.failedins").toString());
        }
        final Object[] params = this.getParams();
        for (int i = 0; i < params.length; ++i) {
            this.insertRow.setColumnObject(i + 1, params[i]);
        }
        final Row row = new Row(this.RowSetMD.getColumnCount(), this.insertRow.getOrigRow());
        row.setInserted();
        int n;
        if (this.currentRow >= this.numRows || this.currentRow < 0) {
            n = this.numRows;
        }
        else {
            n = this.currentRow;
        }
        this.rvh.add(n, row);
        ++this.numRows;
        this.notifyRowChanged();
    }
    
    @Override
    public void updateRow() throws SQLException {
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.updateins").toString());
        }
        ((Row)this.getCurrentRow()).setUpdated();
        this.notifyRowChanged();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this.checkCursor();
        ((Row)this.getCurrentRow()).setDeleted();
        ++this.numDeleted;
        this.notifyRowChanged();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        ((Row)this.getCurrentRow()).clearUpdated();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
        }
        final Row row = (Row)this.getCurrentRow();
        if (row.getUpdated()) {
            row.clearUpdated();
            this.notifyRowChanged();
        }
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        if (this.getConcurrency() == 1007) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins").toString());
        }
        if (this.insertRow == null) {
            if (this.RowSetMD == null) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins1").toString());
            }
            final int columnCount = this.RowSetMD.getColumnCount();
            if (columnCount <= 0) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins2").toString());
            }
            this.insertRow = new InsertRow(columnCount);
        }
        this.onInsertRow = true;
        this.currentRow = this.cursorPos;
        this.cursorPos = -1;
        this.insertRow.initInsertRow();
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        if (!this.onInsertRow) {
            return;
        }
        this.cursorPos = this.currentRow;
        this.onInsertRow = false;
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return null;
    }
    
    @Override
    public Object getObject(final int n, final Map<String, Class<?>> map) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        if (columnObject instanceof Struct) {
            final Struct struct = (Struct)columnObject;
            final Class clazz = map.get(struct.getSQLTypeName());
            if (clazz != null) {
                SQLData sqlData;
                try {
                    sqlData = (SQLData)ReflectUtil.newInstance(clazz);
                }
                catch (final Exception ex) {
                    throw new SQLException("Unable to Instantiate: ", ex);
                }
                sqlData.readSQL(new SQLInputImpl(struct.getAttributes(map), map), struct.getSQLTypeName());
                return sqlData;
            }
        }
        return columnObject;
    }
    
    @Override
    public Ref getRef(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.RowSetMD.getColumnType(n) != 2006) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.setLastValueNull(false);
        final Ref ref = (Ref)this.getCurrentRow().getColumnObject(n);
        if (ref == null) {
            this.setLastValueNull(true);
            return null;
        }
        return ref;
    }
    
    @Override
    public Blob getBlob(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.RowSetMD.getColumnType(n) != 2004) {
            System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), this.RowSetMD.getColumnType(n)));
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.setLastValueNull(false);
        final Blob blob = (Blob)this.getCurrentRow().getColumnObject(n);
        if (blob == null) {
            this.setLastValueNull(true);
            return null;
        }
        return blob;
    }
    
    @Override
    public Clob getClob(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.RowSetMD.getColumnType(n) != 2005) {
            System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), this.RowSetMD.getColumnType(n)));
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.setLastValueNull(false);
        final Clob clob = (Clob)this.getCurrentRow().getColumnObject(n);
        if (clob == null) {
            this.setLastValueNull(true);
            return null;
        }
        return clob;
    }
    
    @Override
    public Array getArray(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.RowSetMD.getColumnType(n) != 2003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.setLastValueNull(false);
        final Array array = (Array)this.getCurrentRow().getColumnObject(n);
        if (array == null) {
            this.setLastValueNull(true);
            return null;
        }
        return array;
    }
    
    @Override
    public Object getObject(final String s, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(this.getColIdxByName(s), map);
    }
    
    @Override
    public Ref getRef(final String s) throws SQLException {
        return this.getRef(this.getColIdxByName(s));
    }
    
    @Override
    public Blob getBlob(final String s) throws SQLException {
        return this.getBlob(this.getColIdxByName(s));
    }
    
    @Override
    public Clob getClob(final String s) throws SQLException {
        return this.getClob(this.getColIdxByName(s));
    }
    
    @Override
    public Array getArray(final String s) throws SQLException {
        return this.getArray(this.getColIdxByName(s));
    }
    
    @Override
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        final Object convertTemporal = this.convertTemporal(columnObject, this.RowSetMD.getColumnType(n), 91);
        final Calendar instance = Calendar.getInstance();
        instance.setTime((java.util.Date)convertTemporal);
        calendar.set(1, instance.get(1));
        calendar.set(2, instance.get(2));
        calendar.set(5, instance.get(5));
        return new Date(calendar.getTime().getTime());
    }
    
    @Override
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        return this.getDate(this.getColIdxByName(s), calendar);
    }
    
    @Override
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        final Object convertTemporal = this.convertTemporal(columnObject, this.RowSetMD.getColumnType(n), 92);
        final Calendar instance = Calendar.getInstance();
        instance.setTime((java.util.Date)convertTemporal);
        calendar.set(11, instance.get(11));
        calendar.set(12, instance.get(12));
        calendar.set(13, instance.get(13));
        return new Time(calendar.getTime().getTime());
    }
    
    @Override
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        return this.getTime(this.getColIdxByName(s), calendar);
    }
    
    @Override
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.setLastValueNull(false);
        final Object columnObject = this.getCurrentRow().getColumnObject(n);
        if (columnObject == null) {
            this.setLastValueNull(true);
            return null;
        }
        final Object convertTemporal = this.convertTemporal(columnObject, this.RowSetMD.getColumnType(n), 93);
        final Calendar instance = Calendar.getInstance();
        instance.setTime((java.util.Date)convertTemporal);
        calendar.set(1, instance.get(1));
        calendar.set(2, instance.get(2));
        calendar.set(5, instance.get(5));
        calendar.set(11, instance.get(11));
        calendar.set(12, instance.get(12));
        calendar.set(13, instance.get(13));
        return new Timestamp(calendar.getTime().getTime());
    }
    
    @Override
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        return this.getTimestamp(this.getColIdxByName(s), calendar);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.conn;
    }
    
    @Override
    public void setMetaData(final RowSetMetaData rowSetMetaData) throws SQLException {
        this.RowSetMD = (RowSetMetaDataImpl)rowSetMetaData;
    }
    
    @Override
    public ResultSet getOriginal() throws SQLException {
        final CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
        cachedRowSetImpl.RowSetMD = this.RowSetMD;
        cachedRowSetImpl.numRows = this.numRows;
        cachedRowSetImpl.cursorPos = 0;
        final int columnCount = this.RowSetMD.getColumnCount();
        final Iterator<Object> iterator = this.rvh.iterator();
        while (iterator.hasNext()) {
            cachedRowSetImpl.rvh.add(new Row(columnCount, iterator.next().getOrigRow()));
        }
        return cachedRowSetImpl;
    }
    
    @Override
    public ResultSet getOriginalRow() throws SQLException {
        final CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
        cachedRowSetImpl.RowSetMD = this.RowSetMD;
        cachedRowSetImpl.numRows = 1;
        cachedRowSetImpl.cursorPos = 0;
        cachedRowSetImpl.setTypeMap(this.getTypeMap());
        cachedRowSetImpl.rvh.add(new Row(this.RowSetMD.getColumnCount(), this.getCurrentRow().getOrigRow()));
        return cachedRowSetImpl;
    }
    
    @Override
    public void setOriginalRow() throws SQLException {
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        final Row row = (Row)this.getCurrentRow();
        this.makeRowOriginal(row);
        if (row.getDeleted()) {
            this.removeCurrentRow();
        }
    }
    
    private void makeRowOriginal(final Row row) {
        if (row.getInserted()) {
            row.clearInserted();
        }
        if (row.getUpdated()) {
            row.moveCurrentToOrig();
        }
    }
    
    public void setOriginal() throws SQLException {
        final Iterator<Object> iterator = this.rvh.iterator();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            this.makeRowOriginal(row);
            if (row.getDeleted()) {
                iterator.remove();
                --this.numRows;
            }
        }
        this.numDeleted = 0;
        this.notifyRowSetChanged();
    }
    
    @Override
    public String getTableName() throws SQLException {
        return this.tableName;
    }
    
    @Override
    public void setTableName(final String tableName) throws SQLException {
        if (tableName == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.tablename").toString());
        }
        this.tableName = tableName;
    }
    
    @Override
    public int[] getKeyColumns() throws SQLException {
        final int[] keyCols = this.keyCols;
        return (int[])((keyCols == null) ? null : Arrays.copyOf(keyCols, keyCols.length));
    }
    
    @Override
    public void setKeyColumns(final int[] array) throws SQLException {
        int columnCount = 0;
        if (this.RowSetMD != null) {
            columnCount = this.RowSetMD.getColumnCount();
            if (array.length > columnCount) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.keycols").toString());
            }
        }
        this.keyCols = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (this.RowSetMD != null && (array[i] <= 0 || array[i] > columnCount)) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString() + array[i]);
            }
            this.keyCols[i] = array[i];
        }
    }
    
    @Override
    public void updateRef(final int n, final Ref ref) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, new SerialRef(ref));
    }
    
    @Override
    public void updateRef(final String s, final Ref ref) throws SQLException {
        this.updateRef(this.getColIdxByName(s), ref);
    }
    
    @Override
    public void updateClob(final int n, final Clob clob) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.dbmslocatorsUpdateCopy) {
            this.getCurrentRow().setColumnObject(n, new SerialClob(clob));
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    }
    
    @Override
    public void updateClob(final String s, final Clob clob) throws SQLException {
        this.updateClob(this.getColIdxByName(s), clob);
    }
    
    @Override
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.dbmslocatorsUpdateCopy) {
            this.getCurrentRow().setColumnObject(n, new SerialBlob(blob));
            return;
        }
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        this.updateBlob(this.getColIdxByName(s), blob);
    }
    
    @Override
    public void updateArray(final int n, final Array array) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        this.getCurrentRow().setColumnObject(n, new SerialArray(array));
    }
    
    @Override
    public void updateArray(final String s, final Array array) throws SQLException {
        this.updateArray(this.getColIdxByName(s), array);
    }
    
    @Override
    public URL getURL(final int n) throws SQLException {
        this.checkIndex(n);
        this.checkCursor();
        if (this.RowSetMD.getColumnType(n) != 70) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        }
        this.setLastValueNull(false);
        final URL url = (URL)this.getCurrentRow().getColumnObject(n);
        if (url == null) {
            this.setLastValueNull(true);
            return null;
        }
        return url;
    }
    
    @Override
    public URL getURL(final String s) throws SQLException {
        return this.getURL(this.getColIdxByName(s));
    }
    
    @Override
    public RowSetWarning getRowSetWarnings() {
        try {
            this.notifyCursorMoved();
        }
        catch (final SQLException ex) {}
        return this.rowsetWarning;
    }
    
    private String buildTableName(String trim) throws SQLException {
        String s = "";
        trim = trim.trim();
        if (trim.toLowerCase().startsWith("select")) {
            final int index = trim.toLowerCase().indexOf("from");
            if (trim.indexOf(",", index) == -1) {
                String s2 = trim.substring(index + "from".length(), trim.length()).trim();
                final int index2 = s2.toLowerCase().indexOf("where");
                if (index2 != -1) {
                    s2 = s2.substring(0, index2).trim();
                }
                s = s2;
            }
        }
        else if (!trim.toLowerCase().startsWith("insert")) {
            if (trim.toLowerCase().startsWith("update")) {}
        }
        return s;
    }
    
    @Override
    public void commit() throws SQLException {
        this.conn.commit();
    }
    
    @Override
    public void rollback() throws SQLException {
        this.conn.rollback();
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.conn.rollback(savepoint);
    }
    
    @Override
    public void unsetMatchColumn(final int[] array) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != Integer.parseInt(this.iMatchColumns.get(i).toString())) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
            }
        }
        for (int j = 0; j < array.length; ++j) {
            this.iMatchColumns.set(j, -1);
        }
    }
    
    @Override
    public void unsetMatchColumn(final String[] array) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equals(this.strMatchColumns.get(i))) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
            }
        }
        for (int j = 0; j < array.length; ++j) {
            this.strMatchColumns.set(j, null);
        }
    }
    
    @Override
    public String[] getMatchColumnNames() throws SQLException {
        final String[] array = new String[this.strMatchColumns.size()];
        if (this.strMatchColumns.get(0) == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
        }
        this.strMatchColumns.copyInto(array);
        return array;
    }
    
    @Override
    public int[] getMatchColumnIndexes() throws SQLException {
        final Integer[] array = new Integer[this.iMatchColumns.size()];
        final int[] array2 = new int[this.iMatchColumns.size()];
        if (this.iMatchColumns.get(0) == -1) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
        }
        this.iMatchColumns.copyInto(array);
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    @Override
    public void setMatchColumn(final int[] array) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] < 0) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
            }
        }
        for (int j = 0; j < array.length; ++j) {
            this.iMatchColumns.add(j, array[j]);
        }
    }
    
    @Override
    public void setMatchColumn(final String[] array) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null || array[i].equals("")) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
            }
        }
        for (int j = 0; j < array.length; ++j) {
            this.strMatchColumns.add(j, array[j]);
        }
    }
    
    @Override
    public void setMatchColumn(final int n) throws SQLException {
        if (n < 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
        }
        this.iMatchColumns.set(0, n);
    }
    
    @Override
    public void setMatchColumn(String trim) throws SQLException {
        if (trim == null || (trim = trim.trim()).equals("")) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
        }
        this.strMatchColumns.set(0, trim);
    }
    
    @Override
    public void unsetMatchColumn(final int n) throws SQLException {
        if (!this.iMatchColumns.get(0).equals(n)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
        }
        if (this.strMatchColumns.get(0) != null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch1").toString());
        }
        this.iMatchColumns.set(0, -1);
    }
    
    @Override
    public void unsetMatchColumn(String trim) throws SQLException {
        trim = trim.trim();
        if (!this.strMatchColumns.get(0).equals(trim)) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
        }
        if (this.iMatchColumns.get(0) > 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch2").toString());
        }
        this.strMatchColumns.set(0, null);
    }
    
    @Override
    public void rowSetPopulated(RowSetEvent rowSetEvent, final int n) throws SQLException {
        if (n < 0 || n < this.getFetchSize()) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.numrows").toString());
        }
        if (this.size() % n == 0) {
            rowSetEvent = new RowSetEvent(this);
            this.notifyRowSetChanged();
        }
    }
    
    @Override
    public void populate(final ResultSet resultSet, final int startPos) throws SQLException {
        final Map<String, Class<?>> typeMap = this.getTypeMap();
        this.cursorPos = 0;
        if (this.populatecallcount == 0) {
            if (startPos < 0) {
                throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.startpos").toString());
            }
            if (this.getMaxRows() == 0) {
                resultSet.absolute(startPos);
                while (resultSet.next()) {
                    ++this.totalRows;
                }
                ++this.totalRows;
            }
            this.startPos = startPos;
        }
        ++this.populatecallcount;
        this.resultSet = resultSet;
        if (this.endPos - this.startPos >= this.getMaxRows() && this.getMaxRows() > 0) {
            this.endPos = this.prevEndPos;
            this.pagenotend = false;
            return;
        }
        if ((this.maxRowsreached != this.getMaxRows() || this.maxRowsreached != this.totalRows) && this.pagenotend) {
            this.startPrev = startPos - this.getPageSize();
        }
        if (this.pageSize == 0) {
            this.prevEndPos = this.endPos;
            this.endPos = startPos + this.getMaxRows();
        }
        else {
            this.prevEndPos = this.endPos;
            this.endPos = startPos + this.getPageSize();
        }
        if (startPos == 1) {
            this.resultSet.beforeFirst();
        }
        else {
            this.resultSet.absolute(startPos - 1);
        }
        if (this.pageSize == 0) {
            this.rvh = new Vector<Object>(this.getMaxRows());
        }
        else {
            this.rvh = new Vector<Object>(this.getPageSize());
        }
        if (resultSet == null) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
        }
        this.RSMD = resultSet.getMetaData();
        this.initMetaData(this.RowSetMD = new RowSetMetaDataImpl(), this.RSMD);
        this.RSMD = null;
        final int columnCount = this.RowSetMD.getColumnCount();
        final int maxRows = this.getMaxRows();
        int numRows = 0;
        if (!resultSet.next() && maxRows == 0) {
            this.endPos = this.prevEndPos;
            this.pagenotend = false;
            return;
        }
        resultSet.previous();
        while (resultSet.next()) {
            final Row row = new Row(columnCount);
            if (this.pageSize == 0) {
                if (numRows >= maxRows && maxRows > 0) {
                    this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
                    break;
                }
            }
            else if (numRows >= this.pageSize || (this.maxRowsreached >= maxRows && maxRows > 0)) {
                this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
                break;
            }
            for (int i = 1; i <= columnCount; ++i) {
                Object o;
                if (typeMap == null) {
                    o = resultSet.getObject(i);
                }
                else {
                    o = resultSet.getObject(i, typeMap);
                }
                if (o instanceof Struct) {
                    o = new SerialStruct((Struct)o, typeMap);
                }
                else if (o instanceof SQLData) {
                    o = new SerialStruct((SQLData)o, typeMap);
                }
                else if (o instanceof Blob) {
                    o = new SerialBlob((Blob)o);
                }
                else if (o instanceof Clob) {
                    o = new SerialClob((Clob)o);
                }
                else if (o instanceof Array) {
                    o = new SerialArray((Array)o, typeMap);
                }
                row.initColumnObject(i, o);
            }
            ++numRows;
            ++this.maxRowsreached;
            this.rvh.add(row);
        }
        this.numRows = numRows;
        this.notifyRowSetChanged();
    }
    
    @Override
    public boolean nextPage() throws SQLException {
        if (this.populatecallcount == 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
        }
        this.onFirstPage = false;
        if (this.callWithCon) {
            this.crsReader.setStartPosition(this.endPos);
            this.crsReader.readData(this);
            this.resultSet = null;
        }
        else {
            this.populate(this.resultSet, this.endPos);
        }
        return this.pagenotend;
    }
    
    @Override
    public void setPageSize(final int pageSize) throws SQLException {
        if (pageSize < 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize").toString());
        }
        if (pageSize > this.getMaxRows() && this.getMaxRows() != 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize1").toString());
        }
        this.pageSize = pageSize;
    }
    
    @Override
    public int getPageSize() {
        return this.pageSize;
    }
    
    @Override
    public boolean previousPage() throws SQLException {
        final int pageSize = this.getPageSize();
        final int maxRowsreached = this.maxRowsreached;
        if (this.populatecallcount == 0) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
        }
        if (!this.callWithCon && this.resultSet.getType() == 1003) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.fwdonly").toString());
        }
        this.pagenotend = true;
        if (this.startPrev < this.startPos) {
            this.onFirstPage = true;
            return false;
        }
        if (this.onFirstPage) {
            return false;
        }
        final int n = maxRowsreached % pageSize;
        if (n == 0) {
            this.maxRowsreached -= 2 * pageSize;
            if (this.callWithCon) {
                this.crsReader.setStartPosition(this.startPrev);
                this.crsReader.readData(this);
                this.resultSet = null;
            }
            else {
                this.populate(this.resultSet, this.startPrev);
            }
            return true;
        }
        this.maxRowsreached -= pageSize + n;
        if (this.callWithCon) {
            this.crsReader.setStartPosition(this.startPrev);
            this.crsReader.readData(this);
            this.resultSet = null;
        }
        else {
            this.populate(this.resultSet, this.startPrev);
        }
        return true;
    }
    
    public void setRowInserted(final boolean b) throws SQLException {
        this.checkCursor();
        if (this.onInsertRow) {
            throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
        }
        if (b) {
            ((Row)this.getCurrentRow()).setInserted();
        }
        else {
            ((Row)this.getCurrentRow()).clearInserted();
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public SQLXML getSQLXML(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public RowId getRowId(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public RowId getRowId(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateRowId(final int n, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateRowId(final String s, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNString(final int n, final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public NClob getNClob(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public NClob getNClob(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return false;
    }
    
    @Override
    public void setSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void setSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void setRowId(final String s, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public Reader getNCharacterStream(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public Reader getNCharacterStream(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public String getNString(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public String getNString(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final long n) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final long n) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream) throws SQLException {
    }
    
    @Override
    public void setURL(final int n, final URL url) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNString(final int n, final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTimestamp(final String s, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Clob clob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDate(final String s, final Date date) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDate(final String s, final Date date, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTime(final String s, final Time time) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTime(final String s, final Time time, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final Blob blob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o, final int n, final int n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBytes(final String s, final byte[] array) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNull(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNull(final String s, final int n, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBoolean(final String s, final boolean b) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setByte(final String s, final byte b) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setShort(final String s, final short n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setInt(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setLong(final String s, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setFloat(final String s, final float n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDouble(final String s, final double n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
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
    
    @Override
    public <T> T getObject(final int n, final Class<T> clazz) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported yet.");
    }
    
    @Override
    public <T> T getObject(final String s, final Class<T> clazz) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported yet.");
    }
}
