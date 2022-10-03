package com.zoho.cp;

import java.io.IOException;
import java.io.InputStream;

public class MeteredInputStream extends InputStream
{
    private InputStream is;
    private long bytesRead;
    
    public MeteredInputStream(final InputStream is) {
        this.is = is;
    }
    
    public long getBytesRead() {
        return this.bytesRead;
    }
    
    @Override
    public int available() throws IOException {
        return this.is.available();
    }
    
    @Override
    public void close() throws IOException {
        this.is.close();
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return this.is.equals(arg0);
    }
    
    @Override
    public int hashCode() {
        return this.is.hashCode();
    }
    
    @Override
    public void mark(final int readlimit) {
        this.is.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.is.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        final int no = this.is.read();
        if (no != -1) {
            ++this.bytesRead;
        }
        return no;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytes = this.is.read(b, off, len);
        if (bytes != -1) {
            this.bytesRead += bytes;
        }
        return bytes;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        final int bytes = this.is.read(b);
        if (bytes != -1) {
            this.bytesRead += bytes;
        }
        return bytes;
    }
    
    @Override
    public void reset() throws IOException {
        this.is.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.is.skip(n);
    }
    
    @Override
    public String toString() {
        return this.is.toString();
    }
}
