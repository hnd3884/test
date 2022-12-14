package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Digest;
import java.io.FilterInputStream;

public class DigestInputStream extends FilterInputStream
{
    protected Digest digest;
    
    public DigestInputStream(final InputStream inputStream, final Digest digest) {
        super(inputStream);
        this.digest = digest;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read >= 0) {
            this.digest.update((byte)read);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.in.read(array, n, n2);
        if (read > 0) {
            this.digest.update(array, n, read);
        }
        return read;
    }
    
    public Digest getDigest() {
        return this.digest;
    }
}
