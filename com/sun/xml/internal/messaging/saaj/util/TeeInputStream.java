package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class TeeInputStream extends InputStream
{
    protected InputStream source;
    protected OutputStream copySink;
    
    public TeeInputStream(final InputStream source, final OutputStream sink) {
        this.copySink = sink;
        this.source = source;
    }
    
    @Override
    public int read() throws IOException {
        final int result = this.source.read();
        this.copySink.write(result);
        return result;
    }
    
    @Override
    public int available() throws IOException {
        return this.source.available();
    }
    
    @Override
    public void close() throws IOException {
        this.source.close();
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.source.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.source.markSupported();
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int result = this.source.read(b, off, len);
        this.copySink.write(b, off, len);
        return result;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        final int result = this.source.read(b);
        this.copySink.write(b);
        return result;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.source.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.source.skip(n);
    }
}
