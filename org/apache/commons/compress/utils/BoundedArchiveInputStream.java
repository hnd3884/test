package org.apache.commons.compress.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.InputStream;

public abstract class BoundedArchiveInputStream extends InputStream
{
    private final long end;
    private ByteBuffer singleByteBuffer;
    private long loc;
    
    public BoundedArchiveInputStream(final long start, final long remaining) {
        this.end = start + remaining;
        if (this.end < start) {
            throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
        }
        this.loc = start;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.loc >= this.end) {
            return -1;
        }
        if (this.singleByteBuffer == null) {
            this.singleByteBuffer = ByteBuffer.allocate(1);
        }
        else {
            this.singleByteBuffer.rewind();
        }
        final int read = this.read(this.loc, this.singleByteBuffer);
        if (read < 1) {
            return -1;
        }
        ++this.loc;
        return this.singleByteBuffer.get() & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.loc >= this.end) {
            return -1;
        }
        final long maxLen = Math.min(len, this.end - this.loc);
        if (maxLen <= 0L) {
            return 0;
        }
        if (off < 0 || off > b.length || maxLen > b.length - off) {
            throw new IndexOutOfBoundsException("offset or len are out of bounds");
        }
        final ByteBuffer buf = ByteBuffer.wrap(b, off, (int)maxLen);
        final int ret = this.read(this.loc, buf);
        if (ret > 0) {
            this.loc += ret;
        }
        return ret;
    }
    
    protected abstract int read(final long p0, final ByteBuffer p1) throws IOException;
}
