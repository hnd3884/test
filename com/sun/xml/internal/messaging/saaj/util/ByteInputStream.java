package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;

public class ByteInputStream extends ByteArrayInputStream
{
    private static final byte[] EMPTY_ARRAY;
    
    public ByteInputStream() {
        this(ByteInputStream.EMPTY_ARRAY, 0);
    }
    
    public ByteInputStream(final byte[] buf, final int length) {
        super(buf, 0, length);
    }
    
    public ByteInputStream(final byte[] buf, final int offset, final int length) {
        super(buf, offset, length);
    }
    
    public byte[] getBytes() {
        return this.buf;
    }
    
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void close() throws IOException {
        this.reset();
    }
    
    public void setBuf(final byte[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }
    
    static {
        EMPTY_ARRAY = new byte[0];
    }
}
