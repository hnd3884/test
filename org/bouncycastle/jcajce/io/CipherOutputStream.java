package org.bouncycastle.jcajce.io;

import java.security.GeneralSecurityException;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;
import java.io.FilterOutputStream;

public class CipherOutputStream extends FilterOutputStream
{
    private final Cipher cipher;
    private final byte[] oneByte;
    
    public CipherOutputStream(final OutputStream outputStream, final Cipher cipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.cipher = cipher;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.oneByte[0] = (byte)n;
        this.write(this.oneByte, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        final byte[] update = this.cipher.update(array, n, n2);
        if (update != null) {
            this.out.write(update);
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        IOException ex = null;
        try {
            final byte[] doFinal = this.cipher.doFinal();
            if (doFinal != null) {
                this.out.write(doFinal);
            }
        }
        catch (final GeneralSecurityException ex2) {
            ex = new InvalidCipherTextIOException("Error during cipher finalisation", ex2);
        }
        catch (final Exception ex3) {
            ex = new IOException("Error closing stream: " + ex3);
        }
        try {
            this.flush();
            this.out.close();
        }
        catch (final IOException ex4) {
            if (ex == null) {
                ex = ex4;
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
