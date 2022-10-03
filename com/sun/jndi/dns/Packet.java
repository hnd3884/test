package com.sun.jndi.dns;

class Packet
{
    byte[] buf;
    
    Packet(final int n) {
        this.buf = new byte[n];
    }
    
    Packet(final byte[] array, final int n) {
        System.arraycopy(array, 0, this.buf = new byte[n], 0, n);
    }
    
    void putInt(final int n, final int n2) {
        this.buf[n2 + 0] = (byte)(n >> 24);
        this.buf[n2 + 1] = (byte)(n >> 16);
        this.buf[n2 + 2] = (byte)(n >> 8);
        this.buf[n2 + 3] = (byte)n;
    }
    
    void putShort(final int n, final int n2) {
        this.buf[n2 + 0] = (byte)(n >> 8);
        this.buf[n2 + 1] = (byte)n;
    }
    
    void putByte(final int n, final int n2) {
        this.buf[n2] = (byte)n;
    }
    
    void putBytes(final byte[] array, final int n, final int n2, final int n3) {
        System.arraycopy(array, n, this.buf, n2, n3);
    }
    
    int length() {
        return this.buf.length;
    }
    
    byte[] getData() {
        return this.buf;
    }
}
