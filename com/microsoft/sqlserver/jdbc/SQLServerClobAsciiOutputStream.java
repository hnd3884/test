package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;

final class SQLServerClobAsciiOutputStream extends OutputStream
{
    private SQLServerClobBase parentClob;
    private long streamPos;
    private byte[] bSingleByte;
    
    SQLServerClobAsciiOutputStream(final SQLServerClobBase parentClob, final long streamPos) {
        this.parentClob = null;
        this.bSingleByte = new byte[1];
        this.parentClob = parentClob;
        this.streamPos = streamPos;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        if (null == b) {
            return;
        }
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (null == b) {
            return;
        }
        try {
            final String s = new String(b, off, len, StandardCharsets.US_ASCII);
            final int charsWritten = this.parentClob.setString(this.streamPos, s);
            this.streamPos += charsWritten;
        }
        catch (final SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.bSingleByte[0] = (byte)(b & 0xFF);
        this.write(this.bSingleByte, 0, this.bSingleByte.length);
    }
}
