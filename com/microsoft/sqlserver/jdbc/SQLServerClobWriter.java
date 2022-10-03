package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.io.IOException;
import java.io.Writer;

final class SQLServerClobWriter extends Writer
{
    private SQLServerClobBase parentClob;
    private long streamPos;
    
    SQLServerClobWriter(final SQLServerClobBase parentClob, final long streamPos) {
        this.parentClob = null;
        this.parentClob = parentClob;
        this.streamPos = streamPos;
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        if (null == cbuf) {
            return;
        }
        this.write(new String(cbuf));
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (null == cbuf) {
            return;
        }
        this.write(new String(cbuf, off, len));
    }
    
    @Override
    public void write(final int b) throws IOException {
        final char[] c = { (char)b };
        this.write(new String(c));
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.checkClosed();
        try {
            final int charsWritten = this.parentClob.setString(this.streamPos, str, off, len);
            this.streamPos += charsWritten;
        }
        catch (final SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void write(final String str) throws IOException {
        if (null == str) {
            return;
        }
        this.write(str, 0, str.length());
    }
    
    @Override
    public void flush() throws IOException {
        this.checkClosed();
    }
    
    @Override
    public void close() throws IOException {
        this.checkClosed();
        this.parentClob = null;
    }
    
    private void checkClosed() throws IOException {
        if (null == this.parentClob) {
            throw new IOException(SQLServerException.getErrString("R_streamIsClosed"));
        }
    }
}
