package com.sun.istack.internal;

import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource implements DataSource
{
    private final String contentType;
    private final byte[] buf;
    private final int len;
    
    public ByteArrayDataSource(final byte[] buf, final String contentType) {
        this(buf, buf.length, contentType);
    }
    
    public ByteArrayDataSource(final byte[] buf, final int length, final String contentType) {
        this.buf = buf;
        this.len = length;
        this.contentType = contentType;
    }
    
    @Override
    public String getContentType() {
        if (this.contentType == null) {
            return "application/octet-stream";
        }
        return this.contentType;
    }
    
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.len);
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }
}
