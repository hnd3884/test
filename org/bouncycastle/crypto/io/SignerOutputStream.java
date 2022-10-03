package org.bouncycastle.crypto.io;

import java.io.IOException;
import org.bouncycastle.crypto.Signer;
import java.io.OutputStream;

public class SignerOutputStream extends OutputStream
{
    protected Signer signer;
    
    public SignerOutputStream(final Signer signer) {
        this.signer = signer;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.signer.update((byte)n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.signer.update(array, n, n2);
    }
    
    public Signer getSigner() {
        return this.signer;
    }
}
