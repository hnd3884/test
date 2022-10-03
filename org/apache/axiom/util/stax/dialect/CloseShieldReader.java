package org.apache.axiom.util.stax.dialect;

import java.nio.CharBuffer;
import java.io.IOException;
import java.io.Reader;

final class CloseShieldReader extends Reader
{
    private final Reader parent;
    
    public CloseShieldReader(final Reader parent) {
        this.parent = parent;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        this.parent.mark(readAheadLimit);
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
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        return this.parent.read(cbuf, off, len);
    }
    
    @Override
    public int read(final char[] cbuf) throws IOException {
        return this.parent.read(cbuf);
    }
    
    @Override
    public int read(final CharBuffer target) throws IOException {
        return this.parent.read(target);
    }
    
    @Override
    public boolean ready() throws IOException {
        return this.parent.ready();
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
