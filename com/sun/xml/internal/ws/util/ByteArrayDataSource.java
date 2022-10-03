package com.sun.xml.internal.ws.util;

import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource implements DataSource
{
    private final String contentType;
    private final byte[] buf;
    private final int start;
    private final int len;
    
    public ByteArrayDataSource(final byte[] buf, final String contentType) {
        this(buf, 0, buf.length, contentType);
    }
    
    public ByteArrayDataSource(final byte[] buf, final int length, final String contentType) {
        this(buf, 0, length, contentType);
    }
    
    public ByteArrayDataSource(final byte[] buf, final int start, final int length, final String contentType) {
        this.buf = buf;
        this.start = start;
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
        return new ByteArrayInputStream(this.buf, this.start, this.len);
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
