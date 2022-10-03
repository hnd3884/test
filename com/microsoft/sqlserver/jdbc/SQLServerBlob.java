package com.microsoft.sqlserver.jdbc;

import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.sql.SQLException;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.io.Serializable;
import java.sql.Blob;

public final class SQLServerBlob extends SQLServerLob implements Blob, Serializable
{
    private static final long serialVersionUID = -3526170228097889085L;
    private static final String R_CANT_SET_NULL = "R_cantSetNull";
    private static final String R_INVALID_POSITION_INDEX = "R_invalidPositionIndex";
    private static final String R_INVALID_LENGTH = "R_invalidLength";
    private static final Logger _LOGGER;
    private static final AtomicInteger BASE_ID;
    private byte[] value;
    private transient SQLServerConnection con;
    private boolean isClosed;
    ArrayList<Closeable> activeStreams;
    private final String traceID;
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    private static int nextInstanceID() {
        return SQLServerBlob.BASE_ID.incrementAndGet();
    }
    
    @Deprecated
    public SQLServerBlob(final SQLServerConnection connection, final byte[] data) {
        this.isClosed = false;
        this.activeStreams = new ArrayList<Closeable>(1);
        this.traceID = this.getClass().getSimpleName() + nextInstanceID();
        this.con = connection;
        if (null == data) {
            throw new NullPointerException(SQLServerException.getErrString("R_cantSetNull"));
        }
        this.value = data;
        if (SQLServerBlob._LOGGER.isLoggable(Level.FINE)) {
            final String loggingInfo = (null != connection) ? connection.toString() : "null connection";
            SQLServerBlob._LOGGER.fine(this.toString() + " created by (" + loggingInfo + ")");
        }
    }
    
    SQLServerBlob(final SQLServerConnection connection) {
        this.isClosed = false;
        this.activeStreams = new ArrayList<Closeable>(1);
        this.traceID = this.getClass().getSimpleName() + nextInstanceID();
        this.con = connection;
        this.value = new byte[0];
        if (SQLServerBlob._LOGGER.isLoggable(Level.FINE)) {
            SQLServerBlob._LOGGER.fine(this.toString() + " created by (" + connection.toString() + ")");
        }
    }
    
    SQLServerBlob(final BaseInputStream stream) {
        this.isClosed = false;
        this.activeStreams = new ArrayList<Closeable>(1);
        this.traceID = this.getClass().getSimpleName() + nextInstanceID();
        this.activeStreams.add(stream);
        if (SQLServerBlob._LOGGER.isLoggable(Level.FINE)) {
            SQLServerBlob._LOGGER.fine(this.toString() + " created by (null connection)");
        }
    }
    
    @Override
    public void free() throws SQLException {
        if (!this.isClosed) {
            if (null != this.activeStreams) {
                for (final Closeable stream : this.activeStreams) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ioException) {
                        SQLServerBlob._LOGGER.fine(this.toString() + " ignored IOException closing stream " + stream + ": " + ioException.getMessage());
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
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[] { "Blob" }), null, true);
        }
    }
    
    @Override
    public InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        if (null == this.value && !this.activeStreams.isEmpty()) {
            final InputStream stream = this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (final IOException e) {
                throw new SQLServerException(e.getMessage(), null, 0, e);
            }
            return this.activeStreams.get(0);
        }
        if (this.value == null) {
            throw new SQLServerException("Unexpected Error: blob value is null while all streams are closed.", (Throwable)null);
        }
        return this.getBinaryStreamInternal(0, this.value.length);
    }
    
    @Override
    public InputStream getBinaryStream(final long pos, final long length) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return null;
    }
    
    private InputStream getBinaryStreamInternal(final int pos, final int length) {
        assert null != this.value;
        assert pos >= 0;
        assert 0 <= length && length <= this.value.length - pos;
        assert null != this.activeStreams;
        final InputStream getterStream = new ByteArrayInputStream(this.value, pos, length);
        this.activeStreams.add(getterStream);
        return getterStream;
    }
    
    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
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
        if (pos > this.value.length) {
            pos = this.value.length;
        }
        if (length > this.value.length - pos) {
            length = (int)(this.value.length - pos);
        }
        final byte[] bTemp = new byte[length];
        System.arraycopy(this.value, (int)pos, bTemp, 0, length);
        return bTemp;
    }
    
    @Override
    public long length() throws SQLException {
        this.checkClosed();
        if (this.value == null && this.activeStreams.get(0) instanceof BaseInputStream) {
            return this.activeStreams.get(0).payloadLength;
        }
        this.getBytesFromStream();
        return this.value.length;
    }
    
    @Override
    void fillFromStream() throws SQLException {
        if (!this.isClosed) {
            this.getBytesFromStream();
        }
    }
    
    private void getBytesFromStream() throws SQLServerException {
        if (null == this.value) {
            final BaseInputStream stream = this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (final IOException e) {
                throw new SQLServerException(e.getMessage(), null, 0, e);
            }
            this.value = stream.getBytes();
        }
    }
    
    @Override
    public long position(final Blob pattern, final long start) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (start < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { start };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == pattern) {
            return -1L;
        }
        return this.position(pattern.getBytes(1L, (int)pattern.length()), start);
    }
    
    @Override
    public long position(final byte[] bPattern, long start) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (start < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { start };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == bPattern) {
            return -1L;
        }
        --start;
        for (int pos = (int)start; pos <= this.value.length - bPattern.length; ++pos) {
            boolean match = true;
            for (int i = 0; i < bPattern.length; ++i) {
                if (this.value[pos + i] != bPattern[i]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return pos + 1L;
            }
        }
        return -1L;
    }
    
    @Override
    public void truncate(final long len) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (len < 0L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { len };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (this.value.length > len) {
            final byte[] bNew = new byte[(int)len];
            System.arraycopy(this.value, 0, bNew, 0, (int)len);
            this.value = bNew;
        }
    }
    
    @Override
    public OutputStream setBinaryStream(final long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[] { pos }), null, true);
        }
        return new SQLServerBlobOutputStream(this, pos);
    }
    
    @Override
    public int setBytes(final long pos, final byte[] bytes) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (null == bytes) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        return this.setBytes(pos, bytes, 0, bytes.length);
    }
    
    @Override
    public int setBytes(long pos, final byte[] bytes, final int offset, final int len) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (null == bytes) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        if (offset < 0 || offset > bytes.length) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidOffset"));
            final Object[] msgArgs = { offset };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len < 0 || len > bytes.length - offset) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { len };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (pos <= 0L || pos > this.value.length + 1) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            final Object[] msgArgs = { pos };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        --pos;
        if (len >= this.value.length - pos) {
            DataTypes.getCheckedLength(this.con, JDBCType.BLOB, pos + len, false);
            final byte[] combinedValue = new byte[(int)pos + len];
            System.arraycopy(this.value, 0, combinedValue, 0, (int)pos);
            System.arraycopy(bytes, offset, combinedValue, (int)pos, len);
            this.value = combinedValue;
        }
        else {
            System.arraycopy(bytes, offset, this.value, (int)pos, len);
        }
        return len;
    }
    
    static {
        _LOGGER = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerBlob");
        BASE_ID = new AtomicInteger(0);
    }
}
