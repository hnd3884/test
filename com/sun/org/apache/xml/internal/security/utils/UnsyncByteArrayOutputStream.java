package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.OutputStream;

public class UnsyncByteArrayOutputStream extends OutputStream
{
    private static final int VM_ARRAY_INDEX_MAX_VALUE = 2147483639;
    private static final int INITIAL_SIZE = 8192;
    private byte[] buf;
    private int size;
    private int pos;
    
    public UnsyncByteArrayOutputStream() {
        this.size = 8192;
        this.buf = new byte[8192];
    }
    
    @Override
    public void write(final byte[] array) {
        if (2147483639 - this.pos < array.length) {
            throw new OutOfMemoryError();
        }
        final int pos = this.pos + array.length;
        if (pos > this.size) {
            this.expandSize(pos);
        }
        System.arraycopy(array, 0, this.buf, this.pos, array.length);
        this.pos = pos;
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        if (2147483639 - this.pos < n2) {
            throw new OutOfMemoryError();
        }
        final int pos = this.pos + n2;
        if (pos > this.size) {
            this.expandSize(pos);
        }
        System.arraycopy(array, n, this.buf, this.pos, n2);
        this.pos = pos;
    }
    
    @Override
    public void write(final int n) {
        if (2147483639 - this.pos == 0) {
            throw new OutOfMemoryError();
        }
        final int n2 = this.pos + 1;
        if (n2 > this.size) {
            this.expandSize(n2);
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
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.pos);
    }
    
    private void expandSize(final int i) {
        int size;
        for (size = this.size; i > size; size = 2147483639) {
            size <<= 1;
            if (size < 0) {}
        }
        final byte[] buf = new byte[size];
        System.arraycopy(this.buf, 0, buf, 0, this.pos);
        this.buf = buf;
        this.size = size;
    }
}
