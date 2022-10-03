package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;

public class ContentLengthInputStream extends InputStream
{
    private long contentLength;
    private long pos;
    private boolean closed;
    private InputStream wrappedStream;
    
    public ContentLengthInputStream(final InputStream in, final int contentLength) {
        this(in, (long)contentLength);
    }
    
    public ContentLengthInputStream(final InputStream in, final long contentLength) {
        this.pos = 0L;
        this.closed = false;
        this.wrappedStream = null;
        this.wrappedStream = in;
        this.contentLength = contentLength;
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            try {
                ChunkedInputStream.exhaustInputStream(this);
            }
            finally {
                this.closed = true;
            }
        }
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        ++this.pos;
        return this.wrappedStream.read();
    }
    
    public int read(final byte[] b, final int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        if (this.pos + len > this.contentLength) {
            len = (int)(this.contentLength - this.pos);
        }
        final int count = this.wrappedStream.read(b, off, len);
        this.pos += count;
        return count;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public long skip(final long n) throws IOException {
        long length = Math.min(n, this.contentLength - this.pos);
        length = this.wrappedStream.skip(length);
        if (length > 0L) {
            this.pos += length;
        }
        return length;
    }
    
    public int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        int avail = this.wrappedStream.available();
        if (this.pos + avail > this.contentLength) {
            avail = (int)(this.contentLength - this.pos);
        }
        return avail;
    }
}
