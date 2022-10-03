package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.InputStream;

class ByteBufferInputStream extends InputStream
{
    ByteBuffer bb;
    
    ByteBufferInputStream(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    @Override
    public int read() throws IOException {
        if (this.bb == null) {
            throw new IOException("read on a closed InputStream");
        }
        if (this.bb.remaining() == 0) {
            return -1;
        }
        return this.bb.get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        if (this.bb == null) {
            throw new IOException("read on a closed InputStream");
        }
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.bb == null) {
            throw new IOException("read on a closed InputStream");
        }
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        final int length = Math.min(this.bb.remaining(), len);
        if (length == 0) {
            return -1;
        }
        this.bb.get(b, off, length);
        return length;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.bb == null) {
            throw new IOException("skip on a closed InputStream");
        }
        if (n <= 0L) {
            return 0L;
        }
        final int nInt = (int)n;
        final int skip = Math.min(this.bb.remaining(), nInt);
        this.bb.position(this.bb.position() + skip);
        return nInt;
    }
    
    @Override
    public int available() throws IOException {
        if (this.bb == null) {
            throw new IOException("available on a closed InputStream");
        }
        return this.bb.remaining();
    }
    
    @Override
    public void close() throws IOException {
        this.bb = null;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
    }
    
    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
}
