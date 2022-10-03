package com.microsoft.sqlserver.jdbc;

import java.io.Writer;
import java.io.OutputStream;
import java.sql.Clob;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.sql.SQLException;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.io.Closeable;
import java.util.ArrayList;

abstract class SQLServerClobBase extends SQLServerLob
{
    private static final long serialVersionUID = 8691072211054430124L;
    protected String value;
    private final SQLCollation sqlCollation;
    private boolean isClosed;
    protected final TypeInfo typeInfo;
    private ArrayList<Closeable> activeStreams;
    transient SQLServerConnection con;
    private final Logger logger;
    private final String traceID;
    private static final AtomicInteger BASE_ID;
    private Charset defaultCharset;
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    private static int nextInstanceID() {
        return SQLServerClobBase.BASE_ID.incrementAndGet();
    }
    
    abstract JDBCType getJdbcType();
    
    private String getDisplayClassName() {
        final String fullClassName = this.getJdbcType().className();
        return fullClassName.substring(1 + fullClassName.lastIndexOf(46));
    }
    
    SQLServerClobBase(final SQLServerConnection connection, final Object data, final SQLCollation collation, final Logger logger, final TypeInfo typeInfo) {
        this.isClosed = false;
        this.activeStreams = new ArrayList<Closeable>(1);
        this.traceID = this.getClass().getName().substring(1 + this.getClass().getName().lastIndexOf(46)) + ":" + nextInstanceID();
        this.defaultCharset = null;
        this.con = connection;
        if (data instanceof BaseInputStream) {
            this.activeStreams.add((Closeable)data);
        }
        else {
            this.value = (String)data;
        }
        this.sqlCollation = collation;
        this.logger = logger;
        this.typeInfo = typeInfo;
        if (logger.isLoggable(Level.FINE)) {
            final String loggingInfo = (null != connection) ? connection.toString() : "null connection";
            logger.fine(this.toString() + " created by (" + loggingInfo + ")");
        }
    }
    
    public void free() throws SQLException {
        if (!this.isClosed) {
            if (null != this.activeStreams) {
                for (final Closeable stream : this.activeStreams) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ioException) {
                        this.logger.fine(this.toString() + " ignored IOException closing stream " + stream + ": " + ioException.getMessage());
                    }
                }
                this.activeStreams = null;
            }
            this.value = null;
            this.isClosed = true;
        }
    }
    
    private void checkClosed() throws SQLServerException {
        if (this.isClosed) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[] { this.getDisplayClassName() }), null, true);
        }
    }
    
    public InputStream getAsciiStream() throws SQLException {
        this.checkClosed();
        if (null != this.sqlCollation && !this.sqlCollation.supportsAsciiConversion()) {
            DataTypes.throwConversionError(this.getDisplayClassName(), "AsciiStream");
        }
        InputStream getterStream = null;
        if (null == this.value && !this.activeStreams.isEmpty()) {
            final InputStream inputStream = this.activeStreams.get(0);
            try {
                inputStream.reset();
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            getterStream = new BufferedInputStream(inputStream);
        }
        else if (null != this.value) {
            getterStream = new ByteArrayInputStream(this.value.getBytes(StandardCharsets.US_ASCII));
        }
        this.activeStreams.add(getterStream);
        return getterStream;
    }
    
    public Reader getCharacterStream() throws SQLException {
        this.checkClosed();
        Reader getterStream = null;
        if (null == this.value && !this.activeStreams.isEmpty()) {
            final InputStream inputStream = this.activeStreams.get(0);
            try {
                inputStream.reset();
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            final Charset cs = (this.defaultCharset == null) ? this.typeInfo.getCharset() : this.defaultCharset;
            getterStream = new BufferedReader(new InputStreamReader(inputStream, cs));
        }
        else {
            getterStream = new StringReader(this.value);
        }
        this.activeStreams.add(getterStream);
        return getterStream;
    }
    
    public Reader getCharacterStream(final long pos, final long length) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return null;
    }
    
    public String getSubString(long pos, int length) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (pos < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { pos };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (length < 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { length };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        --pos;
        if (pos > this.value.length()) {
            pos = this.value.length();
        }
        if (length > this.value.length() - pos) {
            length = (int)(this.value.length() - pos);
        }
        return this.value.substring((int)pos, (int)pos + length);
    }
    
    public long length() throws SQLException {
        this.checkClosed();
        if (null == this.value && this.activeStreams.get(0) instanceof BaseInputStream) {
            final int length = this.activeStreams.get(0).payloadLength;
            if (null != this.typeInfo) {
                final String columnTypeName = this.typeInfo.getSSTypeName();
                return ("nvarchar".equalsIgnoreCase(columnTypeName) || "ntext".equalsIgnoreCase(columnTypeName)) ? (length / 2) : ((long)length);
            }
            return length;
        }
        else {
            if (null == this.value) {
                return 0L;
            }
            return this.value.length();
        }
    }
    
    @Override
    void fillFromStream() throws SQLException {
        if (!this.isClosed) {
            this.getStringFromStream();
        }
    }
    
    private void getStringFromStream() throws SQLServerException {
        if (null == this.value && !this.activeStreams.isEmpty()) {
            final BaseInputStream stream = this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            final Charset cs = (this.defaultCharset == null) ? this.typeInfo.getCharset() : this.defaultCharset;
            this.value = new String(stream.getBytes(), cs);
        }
    }
    
    public long position(final Clob searchstr, final long start) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (start < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { start };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == searchstr) {
            return -1L;
        }
        return this.position(searchstr.getSubString(1L, (int)searchstr.length()), start);
    }
    
    public long position(final String searchstr, final long start) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (start < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { start };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == searchstr) {
            return -1L;
        }
        final int pos = this.value.indexOf(searchstr, (int)(start - 1L));
        if (-1 != pos) {
            return pos + 1L;
        }
        return -1L;
    }
    
    public void truncate(final long len) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (len < 0L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { len };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len <= 2147483647L && this.value.length() > len) {
            this.value = this.value.substring(0, (int)len);
        }
    }
    
    public OutputStream setAsciiStream(final long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { pos };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return new SQLServerClobAsciiOutputStream(this, pos);
    }
    
    public Writer setCharacterStream(final long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { pos };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return new SQLServerClobWriter(this, pos);
    }
    
    public int setString(final long pos, final String s) throws SQLException {
        this.checkClosed();
        if (null == s) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        return this.setString(pos, s, 0, s.length());
    }
    
    public int setString(long pos, final String str, final int offset, final int len) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (null == str) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        if (offset < 0 || offset > str.length()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidOffset"));
            final Object[] msgArgs = { offset };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len < 0 || len > str.length() - offset) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { len };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (pos < 1L || pos > this.value.length() + 1) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { pos };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        --pos;
        if (len >= this.value.length() - pos) {
            DataTypes.getCheckedLength(this.con, this.getJdbcType(), pos + len, false);
            assert pos + len <= 2147483647L;
            final StringBuilder sb = new StringBuilder((int)pos + len);
            sb.append(this.value.substring(0, (int)pos));
            sb.append(str.substring(offset, offset + len));
            this.value = sb.toString();
        }
        else {
            final StringBuilder sb = new StringBuilder(this.value.length());
            sb.append(this.value.substring(0, (int)pos));
            sb.append(str.substring(offset, offset + len));
            sb.append(this.value.substring((int)pos + len));
            this.value = sb.toString();
        }
        return len;
    }
    
    protected void setDefaultCharset(final Charset c) {
        this.defaultCharset = c;
    }
    
    static {
        BASE_ID = new AtomicInteger(0);
    }
}
