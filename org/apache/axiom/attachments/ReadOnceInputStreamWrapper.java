package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;

class ReadOnceInputStreamWrapper extends InputStream
{
    private final PartImpl part;
    private InputStream in;
    
    ReadOnceInputStreamWrapper(final PartImpl part, final InputStream in) {
        this.part = part;
        this.in = in;
    }
    
    @Override
    public int available() throws IOException {
        return (this.in == null) ? 0 : this.in.available();
    }
    
    @Override
    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        final int result = this.in.read();
        if (result == -1) {
            this.close();
        }
        return result;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.in == null) {
            return -1;
        }
        final int result = this.in.read(b, off, len);
        if (result == -1) {
            this.close();
        }
        return result;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        if (this.in == null) {
            return -1;
        }
        final int result = this.in.read(b);
        if (result == -1) {
            this.close();
        }
        return result;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return (this.in == null) ? 0L : this.in.skip(n);
    }
    
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.part.releaseContent();
            this.in = null;
        }
    }
}
