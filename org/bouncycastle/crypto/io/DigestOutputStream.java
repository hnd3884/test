package org.bouncycastle.crypto.io;

import java.io.IOException;
import org.bouncycastle.crypto.Digest;
import java.io.OutputStream;

public class DigestOutputStream extends OutputStream
{
    protected Digest digest;
    
    public DigestOutputStream(final Digest digest) {
        this.digest = digest;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.digest.update((byte)n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.digest.update(array, n, n2);
    }
    
    public byte[] getDigest() {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        return array;
    }
}
