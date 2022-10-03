package org.jcp.xml.dsig.internal;

import java.security.SignatureException;
import java.security.Signature;
import java.io.ByteArrayOutputStream;

public class SignerOutputStream extends ByteArrayOutputStream
{
    private final Signature sig;
    
    public SignerOutputStream(final Signature sig) {
        this.sig = sig;
    }
    
    public void write(final byte[] array) {
        super.write(array, 0, array.length);
        try {
            this.sig.update(array);
        }
        catch (final SignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    public void write(final int n) {
        super.write(n);
        try {
            this.sig.update((byte)n);
        }
        catch (final SignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        super.write(array, n, n2);
        try {
            this.sig.update(array, n, n2);
        }
        catch (final SignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
}
