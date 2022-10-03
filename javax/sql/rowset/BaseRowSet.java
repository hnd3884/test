package javax.sql.rowset;

import java.net.URL;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLXML;
import java.util.Calendar;
import javax.sql.rowset.serial.SerialArray;
import java.sql.Array;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialRef;
import java.sql.Ref;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.Iterator;
import javax.sql.RowSetEvent;
import java.sql.SQLException;
import javax.sql.RowSet;
import java.util.Hashtable;
import javax.sql.RowSetListener;
import java.util.Vector;
import java.util.Map;
import java.io.Reader;
import java.io.InputStream;
import java.io.Serializable;

public abstract class BaseRowSet implements Serializable, Cloneable
{
    public static final int UNICODE_STREAM_PARAM = 0;
    public static final int BINARY_STREAM_PARAM = 1;
    public static final int ASCII_STREAM_PARAM = 2;
    protected InputStream binaryStream;
    protected InputStream unicodeStream;
    protected InputStream asciiStream;
    protected Reader charStream;
    private String command;
    private String URL;
    private String dataSource;
    private transient String username;
    private transient String password;
    private int rowSetType;
    private boolean showDeleted;
    private int queryTimeout;
    private int maxRows;
    private int maxFieldSize;
    private int concurrency;
    private boolean readOnly;
    private boolean escapeProcessing;
    private int isolation;
    private int fetchDir;
    private int fetchSize;
    private Map<String, Class<?>> map;
    private Vector<RowSetListener> listeners;
    private Hashtable<Integer, Object> params;
    static final long serialVersionUID = 4886719666485113312L;
    
    public BaseRowSet() {
        this.rowSetType = 1004;
        this.showDeleted = false;
        this.queryTimeout = 0;
        this.maxRows = 0;
        this.maxFieldSize = 0;
        this.concurrency = 1008;
        this.escapeProcessing = true;
        this.fetchDir = 1000;
        this.fetchSize = 0;
        this.listeners = new Vector<RowSetListener>();
    }
    
    protected void initParams() {
        this.params = new Hashtable<Integer, Object>();
    }
    
    public void addRowSetListener(final RowSetListener rowSetListener) {
        this.listeners.add(rowSetListener);
    }
    
    public void removeRowSetListener(final RowSetListener rowSetListener) {
        this.listeners.remove(rowSetListener);
    }
    
    private void checkforRowSetInterface() throws SQLException {
        if (!(this instanceof RowSet)) {
            throw new SQLException("The class extending abstract class BaseRowSet must implement javax.sql.RowSet or one of it's sub-interfaces.");
        }
    }
    
    protected void notifyCursorMoved() throws SQLException {
        this.checkforRowSetInterface();
        if (!this.listeners.isEmpty()) {
            final RowSetEvent rowSetEvent = new RowSetEvent((RowSet)this);
            final Iterator<RowSetListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().cursorMoved(rowSetEvent);
            }
        }
    }
    
    protected void notifyRowChanged() throws SQLException {
        this.checkforRowSetInterface();
        if (!this.listeners.isEmpty()) {
            final RowSetEvent rowSetEvent = new RowSetEvent((RowSet)this);
            final Iterator<RowSetListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().rowChanged(rowSetEvent);
            }
        }
    }
    
    protected void notifyRowSetChanged() throws SQLException {
        this.checkforRowSetInterface();
        if (!this.listeners.isEmpty()) {
            final RowSetEvent rowSetEvent = new RowSetEvent((RowSet)this);
            final Iterator<RowSetListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().rowSetChanged(rowSetEvent);
            }
        }
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) throws SQLException {
        if (command == null) {
            this.command = null;
        }
        else {
            if (command.length() == 0) {
                throw new SQLException("Invalid command string detected. Cannot be of length less than 0");
            }
            if (this.params == null) {
                throw new SQLException("Set initParams() before setCommand");
            }
            this.params.clear();
            this.command = command;
        }
    }
    
    public String getUrl() throws SQLException {
        return this.URL;
    }
    
    public void setUrl(final String url) throws SQLException {
        if (url != null) {
            if (url.length() < 1) {
                throw new SQLException("Invalid url string detected. Cannot be of length less than 1");
            }
            this.URL = url;
        }
        this.dataSource = null;
    }
    
    public String getDataSourceName() {
        return this.dataSource;
    }
    
    public void setDataSourceName(final String dataSource) throws SQLException {
        if (dataSource == null) {
            this.dataSource = null;
        }
        else {
            if (dataSource.equals("")) {
                throw new SQLException("DataSource name cannot be empty string");
            }
            this.dataSource = dataSource;
        }
        this.URL = null;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        if (username == null) {
            this.username = null;
        }
        else {
            this.username = username;
        }
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        if (password == null) {
            this.password = null;
        }
        else {
            this.password = password;
        }
    }
    
    public void setType(final int rowSetType) throws SQLException {
        if (rowSetType != 1003 && rowSetType != 1004 && rowSetType != 1005) {
            throw new SQLException("Invalid type of RowSet set. Must be either ResultSet.TYPE_FORWARD_ONLY or ResultSet.TYPE_SCROLL_INSENSITIVE or ResultSet.TYPE_SCROLL_SENSITIVE.");
        }
        this.rowSetType = rowSetType;
    }
    
    public int getType() throws SQLException {
        return this.rowSetType;
    }
    
    public void setConcurrency(final int concurrency) throws SQLException {
        if (concurrency != 1007 && concurrency != 1008) {
            throw new SQLException("Invalid concurrency set. Must be either ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE.");
        }
        this.concurrency = concurrency;
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public int getTransactionIsolation() {
        return this.isolation;
    }
    
    public void setTransactionIsolation(final int isolation) throws SQLException {
        if (isolation != 0 && isolation != 2 && isolation != 1 && isolation != 4 && isolation != 8) {
            throw new SQLException("Invalid transaction isolation set. Must be either Connection.TRANSACTION_NONE or Connection.TRANSACTION_READ_UNCOMMITTED or Connection.TRANSACTION_READ_COMMITTED or Connection.RRANSACTION_REPEATABLE_READ or Connection.TRANSACTION_SERIALIZABLE");
        }
        this.isolation = isolation;
    }
    
    public Map<String, Class<?>> getTypeMap() {
        return this.map;
    }
    
    public void setTypeMap(final Map<String, Class<?>> map) {
        this.map = map;
    }
    
    public int getMaxFieldSize() throws SQLException {
        return this.maxFieldSize;
    }
    
    public void setMaxFieldSize(final int maxFieldSize) throws SQLException {
        if (maxFieldSize < 0) {
            throw new SQLException("Invalid max field size set. Cannot be of value: " + maxFieldSize);
        }
        this.maxFieldSize = maxFieldSize;
    }
    
    public int getMaxRows() throws SQLException {
        return this.maxRows;
    }
    
    public void setMaxRows(final int maxRows) throws SQLException {
        if (maxRows < 0) {
            throw new SQLException("Invalid max row size set. Cannot be of value: " + maxRows);
        }
        if (maxRows < this.getFetchSize()) {
            throw new SQLException("Invalid max row size set. Cannot be less than the fetchSize.");
        }
        this.maxRows = maxRows;
    }
    
    public void setEscapeProcessing(final boolean escapeProcessing) throws SQLException {
        this.escapeProcessing = escapeProcessing;
    }
    
    public int getQueryTimeout() throws SQLException {
        return this.queryTimeout;
    }
    
    public void setQueryTimeout(final int queryTimeout) throws SQLException {
        if (queryTimeout < 0) {
            throw new SQLException("Invalid query timeout value set. Cannot be of value: " + queryTimeout);
        }
        this.queryTimeout = queryTimeout;
    }
    
    public boolean getShowDeleted() throws SQLException {
        return this.showDeleted;
    }
    
    public void setShowDeleted(final boolean showDeleted) throws SQLException {
        this.showDeleted = showDeleted;
    }
    
    public boolean getEscapeProcessing() throws SQLException {
        return this.escapeProcessing;
    }
    
    public void setFetchDirection(final int fetchDir) throws SQLException {
        if ((this.getType() == 1003 && fetchDir != 1000) || (fetchDir != 1000 && fetchDir != 1001 && fetchDir != 1002)) {
            throw new SQLException("Invalid Fetch Direction");
        }
        this.fetchDir = fetchDir;
    }
    
    public int getFetchDirection() throws SQLException {
        return this.fetchDir;
    }
    
    public void setFetchSize(final int n) throws SQLException {
        if (this.getMaxRows() == 0 && n >= 0) {
            this.fetchSize = n;
            return;
        }
        if (n < 0 || n > this.getMaxRows()) {
            throw new SQLException("Invalid fetch size set. Cannot be of value: " + n);
        }
        this.fetchSize = n;
    }
    
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }
    
    public int getConcurrency() throws SQLException {
        return this.concurrency;
    }
    
    private void checkParamIndex(final int n) throws SQLException {
        if (n < 1) {
            throw new SQLException("Invalid Parameter Index");
        }
    }
    
    public void setNull(final int n, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { null, n2 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setNull");
        }
        this.params.put(n - 1, array);
    }
    
    public void setNull(final int n, final int n2, final String s) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { null, n2, s };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setNull");
        }
        this.params.put(n - 1, array);
    }
    
    public void setBoolean(final int n, final boolean b) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setNull");
        }
        this.params.put(n - 1, b);
    }
    
    public void setByte(final int n, final byte b) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setByte");
        }
        this.params.put(n - 1, b);
    }
    
    public void setShort(final int n, final short n2) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setShort");
        }
        this.params.put(n - 1, n2);
    }
    
    public void setInt(final int n, final int n2) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setInt");
        }
        this.params.put(n - 1, n2);
    }
    
    public void setLong(final int n, final long n2) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setLong");
        }
        this.params.put(n - 1, n2);
    }
    
    public void setFloat(final int n, final float n2) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setFloat");
        }
        this.params.put(n - 1, n2);
    }
    
    public void setDouble(final int n, final double n2) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setDouble");
        }
        this.params.put(n - 1, n2);
    }
    
    public void setBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setBigDecimal");
        }
        this.params.put(n - 1, bigDecimal);
    }
    
    public void setString(final int n, final String s) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setString");
        }
        this.params.put(n - 1, s);
    }
    
    public void setBytes(final int n, final byte[] array) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setBytes");
        }
        this.params.put(n - 1, array);
    }
    
    public void setDate(final int n, final Date date) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setDate");
        }
        this.params.put(n - 1, date);
    }
    
    public void setTime(final int n, final Time time) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setTime");
        }
        this.params.put(n - 1, time);
    }
    
    public void setTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setTimestamp");
        }
        this.params.put(n - 1, timestamp);
    }
    
    public void setAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { inputStream, n2, 2 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setAsciiStream");
        }
        this.params.put(n - 1, array);
    }
    
    public void setAsciiStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { inputStream, n2, 1 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setBinaryStream");
        }
        this.params.put(n - 1, array);
    }
    
    public void setBinaryStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    @Deprecated
    public void setUnicodeStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { inputStream, n2, 0 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setUnicodeStream");
        }
        this.params.put(n - 1, array);
    }
    
    public void setCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { reader, n2 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setCharacterStream");
        }
        this.params.put(n - 1, array);
    }
    
    public void setCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setObject(final int n, final Object o, final int n2, final int n3) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { o, n2, n3 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setObject");
        }
        this.params.put(n - 1, array);
    }
    
    public void setObject(final int n, final Object o, final int n2) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { o, n2 };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setObject");
        }
        this.params.put(n - 1, array);
    }
    
    public void setObject(final int n, final Object o) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setObject");
        }
        this.params.put(n - 1, o);
    }
    
    public void setRef(final int n, final Ref ref) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setRef");
        }
        this.params.put(n - 1, new SerialRef(ref));
    }
    
    public void setBlob(final int n, final Blob blob) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setBlob");
        }
        this.params.put(n - 1, new SerialBlob(blob));
    }
    
    public void setClob(final int n, final Clob clob) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setClob");
        }
        this.params.put(n - 1, new SerialClob(clob));
    }
    
    public void setArray(final int n, final Array array) throws SQLException {
        this.checkParamIndex(n);
        if (this.params == null) {
            throw new SQLException("Set initParams() before setArray");
        }
        this.params.put(n - 1, new SerialArray(array));
    }
    
    public void setDate(final int n, final Date date, final Calendar calendar) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { date, calendar };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setDate");
        }
        this.params.put(n - 1, array);
    }
    
    public void setTime(final int n, final Time time, final Calendar calendar) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { time, calendar };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setTime");
        }
        this.params.put(n - 1, array);
    }
    
    public void setTimestamp(final int n, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        this.checkParamIndex(n);
        final Object[] array = { timestamp, calendar };
        if (this.params == null) {
            throw new SQLException("Set initParams() before setTimestamp");
        }
        this.params.put(n - 1, array);
    }
    
    public void clearParameters() throws SQLException {
        this.params.clear();
    }
    
    public Object[] getParams() throws SQLException {
        if (this.params == null) {
            this.initParams();
            return new Object[this.params.size()];
        }
        final Object[] array = new Object[this.params.size()];
        for (int i = 0; i < this.params.size(); ++i) {
            array[i] = this.params.get(i);
            if (array[i] == null) {
                throw new SQLException("missing parameter: " + (i + 1));
            }
        }
        return array;
    }
    
    public void setNull(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNull(final String s, final int n, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBoolean(final String s, final boolean b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setByte(final String s, final byte b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setShort(final String s, final short n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setInt(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setLong(final String s, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setFloat(final String s, final float n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setDouble(final String s, final double n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBytes(final String s, final byte[] array) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setObject(final String s, final Object o, final int n, final int n2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setObject(final String s, final Object o, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setObject(final String s, final Object o) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBlob(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBlob(final String s, final Blob blob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setBlob(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setClob(final String s, final Clob clob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setDate(final String s, final Date date) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setDate(final String s, final Date date, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setTime(final String s, final Time time) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setTime(final String s, final Time time, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setTimestamp(final String s, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setRowId(final String s, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNString(final int n, final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final String s, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setNClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
    
    public void setURL(final int n, final URL url) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }
}
