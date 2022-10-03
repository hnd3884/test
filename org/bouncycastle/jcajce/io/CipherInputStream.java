package org.bouncycastle.jcajce.io;

import java.security.GeneralSecurityException;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Cipher;
import java.io.FilterInputStream;

public class CipherInputStream extends FilterInputStream
{
    private final Cipher cipher;
    private final byte[] inputBuffer;
    private boolean finalized;
    private byte[] buf;
    private int maxBuf;
    private int bufOff;
    
    public CipherInputStream(final InputStream inputStream, final Cipher cipher) {
        super(inputStream);
        this.inputBuffer = new byte[512];
        this.finalized = false;
        this.cipher = cipher;
    }
    
    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            final int read = this.in.read(this.inputBuffer);
            if (read == -1) {
                this.buf = this.finaliseCipher();
                if (this.buf == null || this.buf.length == 0) {
                    return -1;
                }
                return this.maxBuf = this.buf.length;
            }
            else {
                this.buf = this.cipher.update(this.inputBuffer, 0, read);
                if (this.buf == null) {
                    continue;
                }
                this.maxBuf = this.buf.length;
            }
        }
        return this.maxBuf;
    }
    
    private byte[] finaliseCipher() throws InvalidCipherTextIOException {
        try {
            this.finalized = true;
            return this.cipher.doFinal();
        }
        catch (final GeneralSecurityException ex) {
            throw new InvalidCipherTextIOException("Error finalising cipher", ex);
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        return this.buf[this.bufOff++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        final int min = Math.min(n2, this.available());
        System.arraycopy(this.buf, this.bufOff, array, n, min);
        this.bufOff += min;
        return min;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        final int n2 = (int)Math.min(n, this.available());
        this.bufOff += n2;
        return n2;
    }
    
    @Override
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        finally {
            if (!this.finalized) {
                this.finaliseCipher();
            }
        }
        final int n = 0;
        this.bufOff = n;
        this.maxBuf = n;
    }
    
    @Override
    public void mark(final int n) {
    }
    
    @Override
    public void reset() throws IOException {
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
}
