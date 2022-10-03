package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Signer;
import java.io.FilterInputStream;

public class SignerInputStream extends FilterInputStream
{
    protected Signer signer;
    
    public SignerInputStream(final InputStream inputStream, final Signer signer) {
        super(inputStream);
        this.signer = signer;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read >= 0) {
            this.signer.update((byte)read);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.in.read(array, n, n2);
        if (read > 0) {
            this.signer.update(array, n, read);
        }
        return read;
    }
    
    public Signer getSigner() {
        return this.signer;
    }
}
