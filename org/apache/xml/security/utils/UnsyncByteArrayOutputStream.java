package org.apache.xml.security.utils;

import java.io.OutputStream;

public class UnsyncByteArrayOutputStream extends OutputStream
{
    private static ThreadLocal bufCahce;
    byte[] buf;
    int size;
    int pos;
    
    public UnsyncByteArrayOutputStream() {
        this.size = 8192;
        this.pos = 0;
        this.buf = UnsyncByteArrayOutputStream.bufCahce.get();
    }
    
    public void write(final byte[] array) {
        final int pos = this.pos + array.length;
        if (pos > this.size) {
            this.expandSize();
        }
        System.arraycopy(array, 0, this.buf, this.pos, array.length);
        this.pos = pos;
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        final int pos = this.pos + n2;
        if (pos > this.size) {
            this.expandSize();
        }
        System.arraycopy(array, n, this.buf, this.pos, n2);
        this.pos = pos;
    }
    
    public void write(final int n) {
        if (this.pos >= this.size) {
            this.expandSize();
        }
        this.buf[this.pos++] = (byte)n;
    }
    
    public byte[] toByteArray() {
        final byte[] array = new byte[this.pos];
        System.arraycopy(this.buf, 0, array, 0, this.pos);
        return array;
    }
    
    public void reset() {
        this.pos = 0;
    }
    
    void expandSize() {
        final int size = this.size << 2;
        final byte[] buf = new byte[size];
        System.arraycopy(this.buf, 0, buf, 0, this.pos);
        this.buf = buf;
        this.size = size;
    }
    
    static {
        UnsyncByteArrayOutputStream.bufCahce = new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new byte[8192];
            }
        };
    }
}
