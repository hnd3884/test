package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class Latin1Reader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    
    public Latin1Reader(final InputStream inputStream) {
        this(inputStream, 2048);
    }
    
    public Latin1Reader(final InputStream inputStream, final int n) {
        this(inputStream, new byte[n]);
    }
    
    public Latin1Reader(final InputStream fInputStream, final byte[] fBuffer) {
        this.fInputStream = fInputStream;
        this.fBuffer = fBuffer;
    }
    
    public int read() throws IOException {
        return this.fInputStream.read();
    }
    
    public int read(final char[] array, final int n, int length) throws IOException {
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        final int read = this.fInputStream.read(this.fBuffer, 0, length);
        for (int i = 0; i < read; ++i) {
            array[n + i] = (char)(this.fBuffer[i] & 0xFF);
        }
        return read;
    }
    
    public long skip(final long n) throws IOException {
        return this.fInputStream.skip(n);
    }
    
    public boolean ready() throws IOException {
        return false;
    }
    
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }
    
    public void mark(final int n) throws IOException {
        this.fInputStream.mark(n);
    }
    
    public void reset() throws IOException {
        this.fInputStream.reset();
    }
    
    public void close() throws IOException {
        this.fInputStream.close();
    }
}
