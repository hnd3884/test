package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class UnsyncBufferedOutputStream extends FilterOutputStream
{
    protected byte[] buffer;
    protected int count;
    
    public UnsyncBufferedOutputStream(final OutputStream outputStream) {
        super(outputStream);
        this.buffer = new byte[8192];
    }
    
    public UnsyncBufferedOutputStream(final OutputStream outputStream, final int n) {
        super(outputStream);
        if (n <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        this.buffer = new byte[n];
    }
    
    @Override
    public void flush() throws IOException {
        this.flushInternal();
        this.out.flush();
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 >= this.buffer.length) {
            this.flushInternal();
            this.out.write(array, n, n2);
            return;
        }
        if (n2 >= this.buffer.length - this.count) {
            this.flushInternal();
        }
        System.arraycopy(array, n, this.buffer, this.count, n2);
        this.count += n2;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.count == this.buffer.length) {
            this.out.write(this.buffer, 0, this.count);
            this.count = 0;
        }
        this.buffer[this.count++] = (byte)n;
    }
    
    private void flushInternal() throws IOException {
        if (this.count > 0) {
            this.out.write(this.buffer, 0, this.count);
            this.count = 0;
        }
    }
}
