package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream
{
    private static final int EOF = -1;
    private final long max;
    private final InputStream in;
    private long pos;
    
    public BoundedInputStream(final long max, final InputStream in) {
        this.max = max;
        this.in = in;
    }
    
    @Override
    public int read() throws IOException {
        if (this.max >= 0L && this.pos >= this.max) {
            return -1;
        }
        final int result = this.in.read();
        ++this.pos;
        return result;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.max >= 0L && this.pos >= this.max) {
            return -1;
        }
        final long maxRead = (this.max >= 0L) ? Math.min(len, this.max - this.pos) : len;
        final int bytesRead = this.in.read(b, off, (int)maxRead);
        if (bytesRead == -1) {
            return -1;
        }
        this.pos += bytesRead;
        return bytesRead;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long toSkip = (this.max >= 0L) ? Math.min(n, this.max - this.pos) : n;
        final long skippedBytes = this.in.skip(toSkip);
        this.pos += skippedBytes;
        return skippedBytes;
    }
    
    @Override
    public void reset() throws IOException {
        this.in.reset();
        this.pos = 0L;
    }
    
    @Override
    public void mark(final int readLimit) {
        this.in.mark(readLimit);
    }
    
    public boolean hasHitBound() {
        return this.pos >= this.max;
    }
}
