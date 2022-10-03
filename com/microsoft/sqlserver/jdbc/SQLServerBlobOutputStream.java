package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.io.IOException;
import java.io.OutputStream;

final class SQLServerBlobOutputStream extends OutputStream
{
    private SQLServerBlob parentBlob;
    private long currentPos;
    
    SQLServerBlobOutputStream(final SQLServerBlob parentBlob, final long startPos) {
        this.parentBlob = null;
        this.parentBlob = parentBlob;
        this.currentPos = startPos;
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
        try {
            final int bytesWritten = this.parentBlob.setBytes(this.currentPos, b, off, len);
            this.currentPos += bytesWritten;
        }
        catch (final SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        final byte[] bTemp = { (byte)(b & 0xFF) };
        this.write(bTemp, 0, bTemp.length);
    }
}
