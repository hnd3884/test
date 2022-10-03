package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.InputStream;

final class CloseShieldInputStream extends InputStream
{
    private final InputStream parent;
    
    public CloseShieldInputStream(final InputStream parent) {
        this.parent = parent;
    }
    
    @Override
    public int available() throws IOException {
        return this.parent.available();
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void mark(final int readlimit) {
        this.parent.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.parent.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        return this.parent.read();
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.parent.read(b, off, len);
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.parent.read(b);
    }
    
    @Override
    public void reset() throws IOException {
        this.parent.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.parent.skip(n);
    }
}
