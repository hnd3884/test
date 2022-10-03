package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;

public class MacOutputStream extends ByteArrayOutputStream
{
    private final HmacSHA1 mac;
    
    public MacOutputStream(final HmacSHA1 mac) {
        this.mac = mac;
    }
    
    public void write(final byte[] array) {
        super.write(array, 0, array.length);
        this.mac.update(array);
    }
    
    public void write(final int n) {
        super.write(n);
        this.mac.update((byte)n);
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        super.write(array, n, n2);
        this.mac.update(array, n, n2);
    }
}
