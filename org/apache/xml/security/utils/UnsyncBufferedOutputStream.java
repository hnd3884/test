package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.OutputStream;

public class UnsyncBufferedOutputStream extends OutputStream
{
    final OutputStream out;
    final byte[] buf;
    static final int size = 8192;
    private static ThreadLocal bufCahce;
    int pointer;
    
    public UnsyncBufferedOutputStream(final OutputStream out) {
        this.pointer = 0;
        this.buf = UnsyncBufferedOutputStream.bufCahce.get();
        this.out = out;
    }
    
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        int pointer = this.pointer + n2;
        if (pointer > 8192) {
            this.flushBuffer();
            if (n2 > 8192) {
                this.out.write(array, n, n2);
                return;
            }
            pointer = n2;
        }
        System.arraycopy(array, n, this.buf, this.pointer, n2);
        this.pointer = pointer;
    }
    
    private final void flushBuffer() throws IOException {
        if (this.pointer > 0) {
            this.out.write(this.buf, 0, this.pointer);
        }
        this.pointer = 0;
    }
    
    public void write(final int n) throws IOException {
        if (this.pointer >= 8192) {
            this.flushBuffer();
        }
        this.buf[this.pointer++] = (byte)n;
    }
    
    public void flush() throws IOException {
        this.flushBuffer();
        this.out.flush();
    }
    
    public void close() throws IOException {
        this.flush();
    }
    
    static {
        UnsyncBufferedOutputStream.bufCahce = new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new byte[8192];
            }
        };
    }
}
