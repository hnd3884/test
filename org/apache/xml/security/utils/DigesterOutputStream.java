package org.apache.xml.security.utils;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import java.io.ByteArrayOutputStream;

public class DigesterOutputStream extends ByteArrayOutputStream
{
    static final byte[] none;
    final MessageDigestAlgorithm mda;
    
    public DigesterOutputStream(final MessageDigestAlgorithm mda) {
        this.mda = mda;
    }
    
    public byte[] toByteArray() {
        return DigesterOutputStream.none;
    }
    
    public void write(final byte[] array) {
        this.mda.update(array);
    }
    
    public void write(final int n) {
        this.mda.update((byte)n);
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        this.mda.update(array, n, n2);
    }
    
    public byte[] getDigestValue() {
        return this.mda.digest();
    }
    
    static {
        none = "error".getBytes();
    }
}
