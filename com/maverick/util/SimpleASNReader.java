package com.maverick.util;

import java.io.IOException;

public class SimpleASNReader
{
    private byte[] b;
    private int c;
    
    public SimpleASNReader(final byte[] b) {
        this.b = b;
        this.c = 0;
    }
    
    public void assertByte(final int n) throws IOException {
        final int byte1 = this.getByte();
        if (byte1 != n) {
            throw new IOException("Assertion failed, next byte value is " + Integer.toHexString(byte1) + " instead of asserted " + Integer.toHexString(n));
        }
    }
    
    public int getByte() {
        return this.b[this.c++] & 0xFF;
    }
    
    public byte[] getData() {
        return this.b(this.getLength());
    }
    
    public int getLength() {
        final int n = this.b[this.c++] & 0xFF;
        if ((n & 0x80) != 0x0) {
            int n2 = 0;
            for (int i = n & 0x7F; i > 0; --i) {
                n2 = (n2 << 8 | (this.b[this.c++] & 0xFF));
            }
            return n2;
        }
        return n;
    }
    
    private byte[] b(final int n) {
        final byte[] array = new byte[n];
        System.arraycopy(this.b, this.c, array, 0, n);
        this.c += n;
        return array;
    }
    
    public boolean hasMoreData() {
        return this.c < this.b.length;
    }
}
