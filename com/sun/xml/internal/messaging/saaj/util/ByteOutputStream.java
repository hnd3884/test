package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteOutputStream extends OutputStream
{
    protected byte[] buf;
    protected int count;
    
    public ByteOutputStream() {
        this(1024);
    }
    
    public ByteOutputStream(final int size) {
        this.count = 0;
        this.buf = new byte[size];
    }
    
    public void write(final InputStream in) throws IOException {
        if (in instanceof ByteArrayInputStream) {
            final int size = in.available();
            this.ensureCapacity(size);
            this.count += in.read(this.buf, this.count, size);
            return;
        }
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
            this.ensureCapacity(this.count);
        }
    }
    
    @Override
    public void write(final int b) {
        this.ensureCapacity(1);
        this.buf[this.count] = (byte)b;
        ++this.count;
    }
    
    private void ensureCapacity(final int space) {
        final int newcount = space + this.count;
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        this.ensureCapacity(len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }
    
    @Override
    public void write(final byte[] b) {
        this.write(b, 0, b.length);
    }
    
    public void writeAsAscii(final String s) {
        final int len = s.length();
        this.ensureCapacity(len);
        int ptr = this.count;
        for (int i = 0; i < len; ++i) {
            this.buf[ptr++] = (byte)s.charAt(i);
        }
        this.count = ptr;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }
    
    public void reset() {
        this.count = 0;
    }
    
    @Deprecated
    public byte[] toByteArray() {
        final byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }
    
    public int size() {
        return this.count;
    }
    
    public ByteInputStream newInputStream() {
        return new ByteInputStream(this.buf, this.count);
    }
    
    @Override
    public String toString() {
        return new String(this.buf, 0, this.count);
    }
    
    @Override
    public void close() {
    }
    
    public byte[] getBytes() {
        return this.buf;
    }
    
    public int getCount() {
        return this.count;
    }
}
