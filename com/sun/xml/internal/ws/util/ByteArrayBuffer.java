package com.sun.xml.internal.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayBuffer extends OutputStream
{
    protected byte[] buf;
    private int count;
    private static final int CHUNK_SIZE = 4096;
    
    public ByteArrayBuffer() {
        this(32);
    }
    
    public ByteArrayBuffer(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        this.buf = new byte[size];
    }
    
    public ByteArrayBuffer(final byte[] data) {
        this(data, data.length);
    }
    
    public ByteArrayBuffer(final byte[] data, final int length) {
        this.buf = data;
        this.count = length;
    }
    
    public final void write(final InputStream in) throws IOException {
        while (true) {
            final int cap = this.buf.length - this.count;
            final int sz = in.read(this.buf, this.count, cap);
            if (sz < 0) {
                break;
            }
            this.count += sz;
            if (cap != sz) {
                continue;
            }
            this.ensureCapacity(this.buf.length * 2);
        }
    }
    
    @Override
    public final void write(final int b) {
        final int newcount = this.count + 1;
        this.ensureCapacity(newcount);
        this.buf[this.count] = (byte)b;
        this.count = newcount;
    }
    
    @Override
    public final void write(final byte[] b, final int off, final int len) {
        final int newcount = this.count + len;
        this.ensureCapacity(newcount);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count = newcount;
    }
    
    private void ensureCapacity(final int newcount) {
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }
    
    public final void writeTo(final OutputStream out) throws IOException {
        int chunk;
        for (int remaining = this.count, off = 0; remaining > 0; remaining -= chunk, off += chunk) {
            chunk = ((remaining > 4096) ? 4096 : remaining);
            out.write(this.buf, off, chunk);
        }
    }
    
    public final void reset() {
        this.count = 0;
    }
    
    @Deprecated
    public final byte[] toByteArray() {
        final byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }
    
    public final int size() {
        return this.count;
    }
    
    public final byte[] getRawData() {
        return this.buf;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public final InputStream newInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }
    
    public final InputStream newInputStream(final int start, final int length) {
        return new ByteArrayInputStream(this.buf, start, length);
    }
    
    @Override
    public String toString() {
        return new String(this.buf, 0, this.count);
    }
}
