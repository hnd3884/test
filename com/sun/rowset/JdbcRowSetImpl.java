package com.sun.rowset;

import java.io.ObjectInputStream;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ParameterMetaData;
import java.sql.DatabaseMetaData;
import javax.sql.rowset.RowSetWarning;
import java.net.URL;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.sql.Statement;
import java.sql.SQLWarning;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.DriverManager;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.sql.RowSetMetaData;
import java.util.Map;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Vector;
import java.sql.ResultSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.BaseRowSet;

public class JdbcRowSetImpl extends BaseRowSet implements JdbcRowSet, Joinable
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private RowSetMetaDataImpl rowsMD;
    private ResultSetMetaData resMD;
    private Vector<Integer> iMatchColumns;
    private Vector<String> strMatchColumns;
    protected transient JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = -3591946023893483003L;
    
    public JdbcRowSetImpl() {
        this.conn = null;
        this.ps = null;
        this.rs = null;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        this.initParams();
        try {
            this.setShowDeleted(false);
        }
        catch (final SQLException ex2) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setshowdeleted").toString() + ex2.getLocalizedMessage());
        }
        try {
            this.setQueryTimeout(0);
        }
        catch (final SQLException ex3) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + ex3.getLocalizedMessage());
        }
        try {
            this.setMaxRows(0);
        }
        catch (final SQLException ex4) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + ex4.getLocalizedMessage());
        }
        try {
            this.setMaxFieldSize(0);
        }
        catch (final SQLException ex5) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + ex5.getLocalizedMessage());
        }
        try {
            this.setEscapeProcessing(true);
        }
        catch (final SQLException ex6) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + ex6.getLocalizedMessage());
        }
        try {
            this.setConcurrency(1008);
        }
        catch (final SQLException ex7) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setconcurrency").toString() + ex7.getLocalizedMessage());
        }
        this.setTypeMap(null);
        try {
            this.setType(1004);
        }
        catch (final SQLException ex8) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settype").toString() + ex8.getLocalizedMessage());
        }
        this.setReadOnly(true);
        try {
            this.setTransactionIsolation(2);
        }
        catch (final SQLException ex9) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settransactionisolation").toString() + ex9.getLocalizedMessage());
        }
        this.iMatchColumns = new Vector<Integer>(10);
        for (int i = 0; i < 10; ++i) {
            this.iMatchColumns.add(i, -1);
        }
        this.strMatchColumns = new Vector<String>(10);
        for (int j = 0; j < 10; ++j) {
            this.strMatchColumns.add(j, null);
        }
    }
    
    public JdbcRowSetImpl(final Connection conn) throws SQLException {
        this.conn = conn;
        this.ps = null;
        this.rs = null;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        this.initParams();
        this.setShowDeleted(false);
        this.setQueryTimeout(0);
        this.setMaxRows(0);
        this.setMaxFieldSize(0);
        this.setParams();
        this.setReadOnly(true);
        this.setTransactionIsolation(2);
        this.setEscapeProcessing(true);
        this.setTypeMap(null);
        this.iMatchColumns = new Vector<Integer>(10);
        for (int i = 0; i < 10; ++i) {
            this.iMatchColumns.add(i, -1);
        }
        this.strMatchColumns = new Vector<String>(10);
        for (int j = 0; j < 10; ++j) {
            this.strMatchColumns.add(j, null);
        }
    }
    
    public JdbcRowSetImpl(final String url, final String username, final String password) throws SQLException {
        this.conn = null;
        this.ps = null;
        this.rs = null;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        this.initParams();
        this.setUsername(username);
        this.setPassword(password);
        this.setUrl(url);
        this.setShowDeleted(false);
        this.setQueryTimeout(0);
        this.setMaxRows(0);
        this.setMaxFieldSize(0);
        this.setParams();
        this.setReadOnly(true);
        this.setTransactionIsolation(2);
        this.setEscapeProcessing(true);
        this.setTypeMap(null);
        this.iMatchColumns = new Vector<Integer>(10);
        for (int i = 0; i < 10; ++i) {
            this.iMatchColumns.add(i, -1);
        }
        this.strMatchColumns = new Vector<String>(10);
        for (int j = 0; j < 10; ++j) {
            this.strMatchColumns.add(j, null);
        }
    }
    
    public JdbcRowSetImpl(final ResultSet rs) throws SQLException {
        this.conn = null;
        this.ps = null;
        this.rs = rs;
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        this.initParams();
        this.setShowDeleted(false);
        this.setQueryTimeout(0);
        this.setMaxRows(0);
        this.setMaxFieldSize(0);
        this.setParams();
        this.setReadOnly(true);
        this.setTransactionIsolation(2);
        this.setEscapeProcessing(true);
        this.setTypeMap(null);
        this.resMD = this.rs.getMetaData();
        this.initMetaData(this.rowsMD = new RowSetMetaDataImpl(), this.resMD);
        this.iMatchColumns = new Vector<Integer>(10);
        for (int i = 0; i < 10; ++i) {
            this.iMatchColumns.add(i, -1);
        }
        this.strMatchColumns = new Vector<String>(10);
        for (int j = 0; j < 10; ++j) {
            this.strMatchColumns.add(j, null);
        }
    }
    
    protected void initMetaData(final RowSetMetaData rowSetMetaData, final ResultSetMetaData resultSetMetaData) throws SQLException {
        final int columnCount = resultSetMetaData.getColumnCount();
        rowSetMetaData.setColumnCount(columnCount);
        for (int i = 1; i <= columnCount; ++i) {
            rowSetMetaData.setAutoIncrement(i, resultSetMetaData.isAutoIncrement(i));
            rowSetMetaData.setCaseSensitive(i, resultSetMetaData.isCaseSensitive(i));
            rowSetMetaData.setCurrency(i, resultSetMetaData.isCurrency(i));
            rowSetMetaData.setNullable(i, resultSetMetaData.isNullable(i));
            rowSetMetaData.setSigned(i, resultSetMetaData.isSigned(i));
            rowSetMetaData.setSearchable(i, resultSetMetaData.isSearchable(i));
            rowSetMetaData.setColumnDisplaySize(i, resultSetMetaData.getColumnDisplaySize(i));
            rowSetMetaData.setColumnLabel(i, resultSetMetaData.getColumnLabel(i));
            rowSetMetaData.setColumnName(i, resultSetMetaData.getColumnName(i));
            rowSetMetaData.setSchemaName(i, resultSetMetaData.getSchemaName(i));
            rowSetMetaData.setPrecision(i, resultSetMetaData.getPrecision(i));
            rowSetMetaData.setScale(i, resultSetMetaData.getScale(i));
            rowSetMetaData.setTableName(i, resultSetMetaData.getTableName(i));
            rowSetMetaData.setCatalogName(i, resultSetMetaData.getCatalogName(i));
            rowSetMetaData.setColumnType(i, resultSetMetaData.getColumnType(i));
            rowSetMetaData.setColumnTypeName(i, resultSetMetaData.getColumnTypeName(i));
        }
    }
    
    protected void checkState() throws SQLException {
        if (this.conn == null && this.ps == null && this.rs == null) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.invalstate").toString());
        }
    }
    
    @Override
    public void execute() throws SQLException {
        this.prepare();
        this.setProperties(this.ps);
        this.decodeParams(this.getParams(), this.ps);
        this.rs = this.ps.executeQuery();
        this.notifyRowSetChanged();
    }
    
    protected void setProperties(final PreparedStatement preparedStatement) throws SQLException {
        try {
            preparedStatement.setEscapeProcessing(this.getEscapeProcessing());
        }
        catch (final SQLException ex) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + ex.getLocalizedMessage());
        }
        try {
            preparedStatement.setMaxFieldSize(this.getMaxFieldSize());
        }
        catch (final SQLException ex2) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + ex2.getLocalizedMessage());
        }
        try {
            preparedStatement.setMaxRows(this.getMaxRows());
        }
        catch (final SQLException ex3) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + ex3.getLocalizedMessage());
        }
        try {
            preparedStatement.setQueryTimeout(this.getQueryTimeout());
        }
        catch (final SQLException ex4) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + ex4.getLocalizedMessage());
        }
    }
    
    private Connection connect() throws SQLException {
        if (this.conn != null) {
            return this.conn;
        }
        if (this.getDataSourceName() != null) {
            try {
                final DataSource dataSource = (DataSource)new InitialContext().lookup(this.getDataSourceName());
                if (this.getUsername() != null && !this.getUsername().equals("")) {
                    return dataSource.getConnection(this.getUsername(), this.getPassword());
                }
                return dataSource.getConnection();
            }
            catch (final NamingException ex) {
                throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.connect").toString());
            }
        }
        if (this.getUrl() != null) {
            return DriverManager.getConnection(this.getUrl(), this.getUsername(), this.getPassword());
        }
        return null;
    }
    
    protected PreparedStatement prepare() throws SQLException {
        this.conn = this.connect();
        try {
            final Map<String, Class<?>> typeMap = this.getTypeMap();
            if (typeMap != null) {
                this.conn.setTypeMap(typeMap);
            }
            this.ps = this.conn.prepareStatement(this.getCommand(), 1004, 1008);
        }
        catch (final SQLException ex) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.prepare").toString() + ex.getLocalizedMessage());
            if (this.ps != null) {
                this.ps.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
            throw new SQLException(ex.getMessage());
        }
        return this.ps;
    }
    
    private void decodeParams(final Object[] array, final PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof Object[]) {
                final Object[] array2 = (Object[])array[i];
                if (array2.length == 2) {
                    if (array2[0] == null) {
                        preparedStatement.setNull(i + 1, (int)array2[1]);
                    }
                    else if (array2[0] instanceof Date || array2[0] instanceof Time || array2[0] instanceof Timestamp) {
                        System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detecteddate"));
                        if (!(array2[1] instanceof Calendar)) {
                            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                        }
                        System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detectedcalendar"));
                        preparedStatement.setDate(i + 1, (Date)array2[0], (Calendar)array2[1]);
                    }
                    else if (array2[0] instanceof Reader) {
                        preparedStatement.setCharacterStream(i + 1, (Reader)array2[0], (int)array2[1]);
                    }
                    else if (array2[1] instanceof Integer) {
                        preparedStatement.setObject(i + 1, array2[0], (int)array2[1]);
                    }
                }
                else if (array2.length == 3) {
                    if (array2[0] == null) {
                        preparedStatement.setNull(i + 1, (int)array2[1], (String)array2[2]);
                    }
                    else {
                        if (array2[0] instanceof InputStream) {
                            switch ((int)array2[2]) {
                                case 0: {
                                    preparedStatement.setUnicodeStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                case 1: {
                                    preparedStatement.setBinaryStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                case 2: {
                                    preparedStatement.setAsciiStream(i + 1, (InputStream)array2[0], (int)array2[1]);
                                    break;
                                }
                                default: {
                                    throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                                }
                            }
                        }
                        if (!(array2[1] instanceof Integer) || !(array2[2] instanceof Integer)) {
                            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
                        }
                        preparedStatement.setObject(i + 1, array2[0], (int)array2[1], (int)array2[2]);
                    }
                }
                else {
                    preparedStatement.setObject(i + 1, array[i]);
                }
            }
            else {
                preparedStatement.setObject(i + 1, array[i]);
            }
        }
    }
    
    @Override
    public boolean next() throws SQLException {
        this.checkState();
        final boolean next = this.rs.next();
        this.notifyCursorMoved();
        return next;
    }
    
    @Override
    public void close() throws SQLException {
        if (this.rs != null) {
            this.rs.close();
        }
        if (this.ps != null) {
            this.ps.close();
        }
        if (this.conn != null) {
            this.conn.close();
        }
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        this.checkState();
        return this.rs.wasNull();
    }
    
    @Override
    public String getString(final int n) throws SQLException {
        this.checkState();
        return this.rs.getString(n);
    }
    
    @Override
    public boolean getBoolean(final int n) throws SQLException {
        this.checkState();
        return this.rs.getBoolean(n);
    }
    
    @Override
    public byte getByte(final int n) throws SQLException {
        this.checkState();
        return this.rs.getByte(n);
    }
    
    @Override
    public short getShort(final int n) throws SQLException {
        this.checkState();
        return this.rs.getShort(n);
    }
    
    @Override
    public int getInt(final int n) throws SQLException {
        this.checkState();
        return this.rs.getInt(n);
    }
    
    @Override
    public long getLong(final int n) throws SQLException {
        this.checkState();
        return this.rs.getLong(n);
    }
    
    @Override
    public float getFloat(final int n) throws SQLException {
        this.checkState();
        return this.rs.getFloat(n);
    }
    
    @Override
    public double getDouble(final int n) throws SQLException {
        this.checkState();
        return this.rs.getDouble(n);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        this.checkState();
        return this.rs.getBigDecimal(n, n2);
    }
    
    @Override
    public byte[] getBytes(final int n) throws SQLException {
        this.checkState();
        return this.rs.getBytes(n);
    }
    
    @Override
    public Date getDate(final int n) throws SQLException {
        this.checkState();
        return this.rs.getDate(n);
    }
    
    @Override
    public Time getTime(final int n) throws SQLException {
        this.checkState();
        return this.rs.getTime(n);
    }
    
    @Override
    public Timestamp getTimestamp(final int n) throws SQLException {
        this.checkState();
        return this.rs.getTimestamp(n);
    }
    
    @Override
    public InputStream getAsciiStream(final int n) throws SQLException {
        this.checkState();
        return this.rs.getAsciiStream(n);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int n) throws SQLException {
        this.checkState();
        return this.rs.getUnicodeStream(n);
    }
    
    @Override
    public InputStream getBinaryStream(final int n) throws SQLException {
        this.checkState();
        return this.rs.getBinaryStream(n);
    }
    
    @Override
    public String getString(final String s) throws SQLException {
        return this.getString(this.findColumn(s));
    }
    
    @Override
    public boolean getBoolean(final String s) throws SQLException {
        return this.getBoolean(this.findColumn(s));
    }
    
    @Override
    public byte getByte(final String s) throws SQLException {
        return this.getByte(this.findColumn(s));
    }
    
    @Override
    public short getShort(final String s) throws SQLException {
        return this.getShort(this.findColumn(s));
    }
    
    @Override
    public int getInt(final String s) throws SQLException {
        return this.getInt(this.findColumn(s));
    }
    
    @Override
    public long getLong(final String s) throws SQLException {
        return this.getLong(this.findColumn(s));
    }
    
    @Override
    public float getFloat(final String s) throws SQLException {
        return this.getFloat(this.findColumn(s));
    }
    
    @Override
    public double getDouble(final String s) throws SQLException {
        return this.getDouble(this.findColumn(s));
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        return this.getBigDecimal(this.findColumn(s), n);
    }
    
    @Override
    public byte[] getBytes(final String s) throws SQLException {
        return this.getBytes(this.findColumn(s));
    }
    
    @Override
    public Date getDate(final String s) throws SQLException {
        return this.getDate(this.findColumn(s));
    }
    
    @Override
    public Time getTime(final String s) throws SQLException {
        return this.getTime(this.findColumn(s));
    }
    
    @Override
    public Timestamp getTimestamp(final String s) throws SQLException {
        return this.getTimestamp(this.findColumn(s));
    }
    
    @Override
    public InputStream getAsciiStream(final String s) throws SQLException {
        return this.getAsciiStream(this.findColumn(s));
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String s) throws SQLException {
        return this.getUnicodeStream(this.findColumn(s));
    }
    
    @Override
    public InputStream getBinaryStream(final String s) throws SQLException {
        return this.getBinaryStream(this.findColumn(s));
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkState();
        return this.rs.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.checkState();
        this.rs.clearWarnings();
    }
    
    @Override
    public String getCursorName() throws SQLException {
        this.checkState();
        return this.rs.getCursorName();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkState();
        try {
            this.checkState();
        }
        catch (final SQLException ex) {
            this.prepare();
            return this.ps.getMetaData();
        }
        return this.rs.getMetaData();
    }
    
    @Override
    public Object getObject(final int n) throws SQLException {
        this.checkState();
        return this.rs.getObject(n);
    }
    
    @Override
    public Object getObject(final String s) throws SQLException {
        return this.getObject(this.findColumn(s));
    }
    
    @Override
    public int findColumn(final String s) throws SQLException {
        this.checkState();
        return this.rs.findColumn(s);
    }
    
    @Override
    public Reader getCharacterStream(final int n) throws SQLException {
        this.checkState();
        return this.rs.getCharacterStream(n);
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
        return this.getCharacterStream(this.findColumn(s));
    }
    
    @Override
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        this.checkState();
        return this.rs.getBigDecimal(n);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        return this.getBigDecimal(this.findColumn(s));
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        this.checkState();
        return this.rs.isBeforeFirst();
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        this.checkState();
        return this.rs.isAfterLast();
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        this.checkState();
        return this.rs.isFirst();
    }
    
    @Override
    public boolean isLast() throws SQLException {
        this.checkState();
        return this.rs.isLast();
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        this.checkState();
        this.rs.beforeFirst();
        this.notifyCursorMoved();
    }
    
    @Override
    public void afterLast() throws SQLException {
        this.checkState();
        this.rs.afterLast();
        this.notifyCursorMoved();
    }
    
    @Override
    public boolean first() throws SQLException {
        this.checkState();
        final boolean first = this.rs.first();
        this.notifyCursorMoved();
        return first;
    }
    
    @Override
    public boolean last() throws SQLException {
        this.checkState();
        final boolean last = this.rs.last();
        this.notifyCursorMoved();
        return last;
    }
    
    @Override
    public int getRow() throws SQLException {
        this.checkState();
        return this.rs.getRow();
    }
    
    @Override
    public boolean absolute(final int n) throws SQLException {
        this.checkState();
        final boolean absolute = this.rs.absolute(n);
        this.notifyCursorMoved();
        return absolute;
    }
    
    @Override
    public boolean relative(final int n) throws SQLException {
        this.checkState();
        final boolean relative = this.rs.relative(n);
        this.notifyCursorMoved();
        return relative;
    }
    
    @Override
    public boolean previous() throws SQLException {
        this.checkState();
        final boolean previous = this.rs.previous();
        this.notifyCursorMoved();
        return previous;
    }
    
    @Override
    public void setFetchDirection(final int fetchDirection) throws SQLException {
        this.checkState();
        this.rs.setFetchDirection(fetchDirection);
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            this.checkState();
        }
        catch (final SQLException ex) {
            super.getFetchDirection();
        }
        return this.rs.getFetchDirection();
    }
    
    @Override
    public void setFetchSize(final int fetchSize) throws SQLException {
        this.checkState();
        this.rs.setFetchSize(fetchSize);
    }
    
    @Override
    public int getType() throws SQLException {
        try {
            this.checkState();
        }
        catch (final SQLException ex) {
            return super.getType();
        }
        if (this.rs == null) {
            return super.getType();
        }
        return this.rs.getType();
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        try {
            this.checkState();
        }
        catch (final SQLException ex) {
            super.getConcurrency();
        }
        return this.rs.getConcurrency();
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        this.checkState();
        return this.rs.rowUpdated();
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        this.checkState();
        return this.rs.rowInserted();
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        this.checkState();
        return this.rs.rowDeleted();
    }
    
    @Override
    public void updateNull(final int n) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateNull(n);
    }
    
    @Override
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateBoolean(n, b);
    }
    
    @Override
    public void updateByte(final int n, final byte b) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateByte(n, b);
    }
    
    @Override
    public void updateShort(final int n, final short n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateShort(n, n2);
    }
    
    @Override
    public void updateInt(final int n, final int n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateInt(n, n2);
    }
    
    @Override
    public void updateLong(final int n, final long n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateLong(n, n2);
    }
    
    @Override
    public void updateFloat(final int n, final float n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateFloat(n, n2);
    }
    
    @Override
    public void updateDouble(final int n, final double n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateDouble(n, n2);
    }
    
    @Override
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateBigDecimal(n, bigDecimal);
    }
    
    @Override
    public void updateString(final int n, final String s) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateString(n, s);
    }
    
    @Override
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateBytes(n, array);
    }
    
    @Override
    public void updateDate(final int n, final Date date) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateDate(n, date);
    }
    
    @Override
    public void updateTime(final int n, final Time time) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateTime(n, time);
    }
    
    @Override
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateTimestamp(n, timestamp);
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateAsciiStream(n, inputStream, n2);
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateBinaryStream(n, inputStream, n2);
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateCharacterStream(n, reader, n2);
    }
    
    @Override
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateObject(n, o, n2);
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        this.checkState();
        this.checkTypeConcurrency();
        this.rs.updateObject(n, o);
    }
    
    @Override
    public void updateNull(final String s) throws SQLException {
        this.updateNull(this.findColumn(s));
    }
    
    @Override
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        this.updateBoolean(this.findColumn(s), b);
    }
    
    @Override
    public void updateByte(final String s, final byte b) throws SQLException {
        this.updateByte(this.findColumn(s), b);
    }
    
    @Override
    public void updateShort(final String s, final short n) throws SQLException {
        this.updateShort(this.findColumn(s), n);
    }
    
    @Override
    public void updateInt(final String s, final int n) throws SQLException {
        this.updateInt(this.findColumn(s), n);
    }
    
    @Override
    public void updateLong(final String s, final long n) throws SQLException {
        this.updateLong(this.findColumn(s), n);
    }
    
    @Override
    public void updateFloat(final String s, final float n) throws SQLException {
        this.updateFloat(this.findColumn(s), n);
    }
    
    @Override
    public void updateDouble(final String s, final double n) throws SQLException {
        this.updateDouble(this.findColumn(s), n);
    }
    
    @Override
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.updateBigDecimal(this.findColumn(s), bigDecimal);
    }
    
    @Override
    public void updateString(final String s, final String s2) throws SQLException {
        this.updateString(this.findColumn(s), s2);
    }
    
    @Override
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        this.updateBytes(this.findColumn(s), array);
    }
    
    @Override
    public void updateDate(final String s, final Date date) throws SQLException {
        this.updateDate(this.findColumn(s), date);
    }
    
    @Override
    public void updateTime(final String s, final Time time) throws SQLException {
        this.updateTime(this.findColumn(s), time);
    }
    
    @Override
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.updateTimestamp(this.findColumn(s), timestamp);
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateAsciiStream(this.findColumn(s), inputStream, n);
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.updateBinaryStream(this.findColumn(s), inputStream, n);
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.updateCharacterStream(this.findColumn(s), reader, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        this.updateObject(this.findColumn(s), o, n);
    }
    
    @Override
    public void updateObject(final String s, final Object o) throws SQLException {
        this.updateObject(this.findColumn(s), o);
    }
    
    @Override
    public void insertRow() throws SQLException {
        this.checkState();
        this.rs.insertRow();
        this.notifyRowChanged();
    }
    
    @Override
    public void updateRow() throws SQLException {
        this.checkState();
        this.rs.updateRow();
        this.notifyRowChanged();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this.checkState();
        this.rs.deleteRow();
        this.notifyRowChanged();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        this.checkState();
        this.rs.refreshRow();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        this.checkState();
        this.rs.cancelRowUpdates();
        this.notifyRowChanged();
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        this.checkState();
        this.rs.moveToInsertRow();
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        this.checkState();
        this.rs.moveToCurrentRow();
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        if (this.rs != null) {
            return this.rs.getStatement();
        }
        return null;
    }
    
    @Override
    public Object getObject(final int n, final Map<String, Class<?>> map) throws SQLException {
        this.checkState();
        return this.rs.getObject(n, map);
    }
    
    @Override
    public Ref getRef(final int n) throws SQLException {
        this.checkState();
        return this.rs.getRef(n);
    }
    
    @Override
    public Blob getBlob(final int n) throws SQLException {
        this.checkState();
        return this.rs.getBlob(n);
    }
    
    @Override
    public Clob getClob(final int n) throws SQLException {
        this.checkState();
        return this.rs.getClob(n);
    }
    
    @Override
    public Array getArray(final int n) throws SQLException {
        this.checkState();
        return this.rs.getArray(n);
    }
    
    @Override
    public Object getObject(final String s, final Map<String, Class<?>> map) throws SQLException {
        return this.getObject(this.findColumn(s), map);
    }
    
    @Override
    public Ref getRef(final String s) throws SQLException {
        return this.getRef(this.findColumn(s));
    }
    
    @Override
    public Blob getBlob(final String s) throws SQLException {
        return this.getBlob(this.findColumn(s));
    }
    
    @Override
    public Clob getClob(final String s) throws SQLException {
        return this.getClob(this.findColumn(s));
    }
    
    @Override
    public Array getArray(final String s) throws SQLException {
        return this.getArray(this.findColumn(s));
    }
    
    @Override
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        this.checkState();
        return this.rs.getDate(n, calendar);
    }
    
    @Override
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        return this.getDate(this.findColumn(s), calendar);
    }
    
    @Override
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        this.checkState();
        return this.rs.getTime(n, calendar);
    }
    
    @Override
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        return this.getTime(this.findColumn(s), calendar);
    }
    
    @Override
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        this.checkState();
        return this.rs.getTimestamp(n, calendar);
    }
    
    @Override
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        return this.getTimestamp(this.findColumn(s), calendar);
    }
    
    @Override
    public void updateRef(final int n, final Ref ref) throws SQLException {
        this.checkState();
        this.rs.updateRef(n, ref);
    }
    
    @Override
    public void updateRef(final String s, final Ref ref) throws SQLException {
        this.updateRef(this.findColumn(s), ref);
    }
    
    @Override
    public void updateClob(final int n, final Clob clob) throws SQLException {
        this.checkState();
        this.rs.updateClob(n, clob);
    }
    
    @Override
    public void updateClob(final String s, final Clob clob) throws SQLException {
        this.updateClob(this.findColumn(s), clob);
    }
    
    @Override
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        this.checkState();
        this.rs.updateBlob(n, blob);
    }
    
    @Override
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        this.updateBlob(this.findColumn(s), blob);
    }
    
    @Override
    public void updateArray(final int n, final Array array) throws SQLException {
        this.checkState();
        this.rs.updateArray(n, array);
    }
    
    @Override
    public void updateArray(final String s, final Array array) throws SQLException {
        this.updateArray(this.findColumn(s), array);
    }
    
    @Override
    public URL getURL(final int n) throws SQLException {
        this.checkState();
        return this.rs.getURL(n);
    }
    
    @Override
    public URL getURL(final String s) throws SQLException {
        return this.getURL(this.findColumn(s));
    }
    
    @Override
    public RowSetWarning getRowSetWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void unsetMatchColumn(final int[] array) throws SQLException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != Integer.parseInt(this.iMatchColumns.get(i).toString())) {
                throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
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
                throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
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
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
        }
        this.strMatchColumns.copyInto(array);
        return array;
    }
    
    @Override
    public int[] getMatchColumnIndexes() throws SQLException {
        final Integer[] array = new Integer[this.iMatchColumns.size()];
        final int[] array2 = new int[this.iMatchColumns.size()];
        if (this.iMatchColumns.get(0) == -1) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
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
                throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
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
                throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
            }
        }
        for (int j = 0; j < array.length; ++j) {
            this.strMatchColumns.add(j, array[j]);
        }
    }
    
    @Override
    public void setMatchColumn(final int n) throws SQLException {
        if (n < 0) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
        }
        this.iMatchColumns.set(0, n);
    }
    
    @Override
    public void setMatchColumn(String trim) throws SQLException {
        if (trim == null || (trim = trim.trim()).equals("")) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
        }
        this.strMatchColumns.set(0, trim);
    }
    
    @Override
    public void unsetMatchColumn(final int n) throws SQLException {
        if (!this.iMatchColumns.get(0).equals(n)) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
        }
        if (this.strMatchColumns.get(0) != null) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolname").toString());
        }
        this.iMatchColumns.set(0, -1);
    }
    
    @Override
    public void unsetMatchColumn(String trim) throws SQLException {
        trim = trim.trim();
        if (!this.strMatchColumns.get(0).equals(trim)) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
        }
        if (this.iMatchColumns.get(0) > 0) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolid").toString());
        }
        this.strMatchColumns.set(0, null);
    }
    
    public DatabaseMetaData getDatabaseMetaData() throws SQLException {
        return this.connect().getMetaData();
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.prepare();
        return this.ps.getParameterMetaData();
    }
    
    @Override
    public void commit() throws SQLException {
        this.conn.commit();
        if (this.conn.getHoldability() != 1) {
            this.rs = null;
        }
    }
    
    @Override
    public void setAutoCommit(final boolean b) throws SQLException {
        if (this.conn != null) {
            this.conn.setAutoCommit(b);
        }
        else {
            (this.conn = this.connect()).setAutoCommit(b);
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.conn.getAutoCommit();
    }
    
    @Override
    public void rollback() throws SQLException {
        this.conn.rollback();
        this.rs = null;
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.conn.rollback(savepoint);
    }
    
    protected void setParams() throws SQLException {
        if (this.rs == null) {
            this.setType(1004);
            this.setConcurrency(1008);
        }
        else {
            this.setType(this.rs.getType());
            this.setConcurrency(this.rs.getConcurrency());
        }
    }
    
    private void checkTypeConcurrency() throws SQLException {
        if (this.rs.getType() == 1003 || this.rs.getConcurrency() == 1007) {
            throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.resnotupd").toString());
        }
    }
    
    protected Connection getConnection() {
        return this.conn;
    }
    
    protected void setConnection(final Connection conn) {
        this.conn = conn;
    }
    
    protected PreparedStatement getPreparedStatement() {
        return this.ps;
    }
    
    protected void setPreparedStatement(final PreparedStatement ps) {
        this.ps = ps;
    }
    
    protected ResultSet getResultSet() throws SQLException {
        this.checkState();
        return this.rs;
    }
    
    protected void setResultSet(final ResultSet rs) {
        this.rs = rs;
    }
    
    @Override
    public void setCommand(final String s) throws SQLException {
        if (this.getCommand() != null) {
            if (!this.getCommand().equals(s)) {
                super.setCommand(s);
                this.ps = null;
                this.rs = null;
            }
        }
        else {
            super.setCommand(s);
        }
    }
    
    @Override
    public void setDataSourceName(final String s) throws SQLException {
        if (this.getDataSourceName() != null) {
            if (!this.getDataSourceName().equals(s)) {
                super.setDataSourceName(s);
                this.conn = null;
                this.ps = null;
                this.rs = null;
            }
        }
        else {
            super.setDataSourceName(s);
        }
    }
    
    @Override
    public void setUrl(final String s) throws SQLException {
        if (this.getUrl() != null) {
            if (!this.getUrl().equals(s)) {
                super.setUrl(s);
                this.conn = null;
                this.ps = null;
                this.rs = null;
            }
        }
        else {
            super.setUrl(s);
        }
    }
    
    @Override
    public void setUsername(final String s) {
        if (this.getUsername() != null) {
            if (!this.getUsername().equals(s)) {
                super.setUsername(s);
                this.conn = null;
                this.ps = null;
                this.rs = null;
            }
        }
        else {
            super.setUsername(s);
        }
    }
    
    @Override
    public void setPassword(final String s) {
        if (this.getPassword() != null) {
            if (!this.getPassword().equals(s)) {
                super.setPassword(s);
                this.conn = null;
                this.ps = null;
                this.rs = null;
            }
        }
        else {
            super.setPassword(s);
        }
    }
    
    @Override
    public void setType(final int type) throws SQLException {
        int type2;
        try {
            type2 = this.getType();
        }
        catch (final SQLException ex) {
            type2 = 0;
        }
        if (type2 != type) {
            super.setType(type);
        }
    }
    
    @Override
    public void setConcurrency(final int concurrency) throws SQLException {
        int concurrency2;
        try {
            concurrency2 = this.getConcurrency();
        }
        catch (final NullPointerException ex) {
            concurrency2 = 0;
        }
        if (concurrency2 != concurrency) {
            super.setConcurrency(concurrency);
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public SQLXML getSQLXML(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public RowId getRowId(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public RowId getRowId(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateRowId(final int n, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateRowId(final String s, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNString(final int n, final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public NClob getNClob(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public NClob getNClob(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
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
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setRowId(final String s, final RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNString(final int n, final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public Reader getNCharacterStream(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public Reader getNCharacterStream(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public String getNString(final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public String getNString(final String s) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBlob(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBinaryStream(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void updateCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setURL(final int n, final URL url) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTimestamp(final String s, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Reader reader, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Clob clob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDate(final String s, final Date date) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDate(final String s, final Date date, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTime(final String s, final Time time) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTime(final String s, final Time time, final Calendar calendar) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final int n, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final Blob blob) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o, final int n, final int n2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setObject(final String s, final Object o) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setCharacterStream(final String s, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setString(final String s, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBytes(final String s, final byte[] array) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNull(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setNull(final String s, final int n, final String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setBoolean(final String s, final boolean b) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setByte(final String s, final byte b) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setShort(final String s, final short n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setInt(final String s, final int n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setLong(final String s, final long n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setFloat(final String s, final float n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    @Override
    public void setDouble(final String s, final double n) throws SQLException {
        throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {}
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
