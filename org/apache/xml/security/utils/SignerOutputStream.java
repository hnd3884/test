package org.apache.xml.security.utils;

import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import java.io.ByteArrayOutputStream;

public class SignerOutputStream extends ByteArrayOutputStream
{
    static final byte[] none;
    final SignatureAlgorithm sa;
    
    public SignerOutputStream(final SignatureAlgorithm sa) {
        this.sa = sa;
    }
    
    public byte[] toByteArray() {
        return SignerOutputStream.none;
    }
    
    public void write(final byte[] array) {
        try {
            this.sa.update(array);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    public void write(final int n) {
        try {
            this.sa.update((byte)n);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        try {
            this.sa.update(array, n, n2);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    static {
        none = "error".getBytes();
    }
}
