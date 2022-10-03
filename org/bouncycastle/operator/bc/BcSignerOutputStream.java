package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.CryptoException;
import java.io.IOException;
import org.bouncycastle.crypto.Signer;
import java.io.OutputStream;

public class BcSignerOutputStream extends OutputStream
{
    private Signer sig;
    
    BcSignerOutputStream(final Signer sig) {
        this.sig = sig;
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.sig.update(array, n, n2);
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.sig.update(array, 0, array.length);
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.sig.update((byte)n);
    }
    
    byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }
    
    boolean verify(final byte[] array) {
        return this.sig.verifySignature(array);
    }
}
