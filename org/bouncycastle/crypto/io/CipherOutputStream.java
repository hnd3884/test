package org.bouncycastle.crypto.io;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import java.io.FilterOutputStream;

public class CipherOutputStream extends FilterOutputStream
{
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private final byte[] oneByte;
    private byte[] buf;
    
    public CipherOutputStream(final OutputStream outputStream, final BufferedBlockCipher bufferedBlockCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.bufferedBlockCipher = bufferedBlockCipher;
    }
    
    public CipherOutputStream(final OutputStream outputStream, final StreamCipher streamCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.streamCipher = streamCipher;
    }
    
    public CipherOutputStream(final OutputStream outputStream, final AEADBlockCipher aeadBlockCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.aeadBlockCipher = aeadBlockCipher;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.oneByte[0] = (byte)n;
        if (this.streamCipher != null) {
            this.out.write(this.streamCipher.returnByte((byte)n));
        }
        else {
            this.write(this.oneByte, 0, 1);
        }
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.ensureCapacity(n2, false);
        if (this.bufferedBlockCipher != null) {
            final int processBytes = this.bufferedBlockCipher.processBytes(array, n, n2, this.buf, 0);
            if (processBytes != 0) {
                this.out.write(this.buf, 0, processBytes);
            }
        }
        else if (this.aeadBlockCipher != null) {
            final int processBytes2 = this.aeadBlockCipher.processBytes(array, n, n2, this.buf, 0);
            if (processBytes2 != 0) {
                this.out.write(this.buf, 0, processBytes2);
            }
        }
        else {
            this.streamCipher.processBytes(array, n, n2, this.buf, 0);
            this.out.write(this.buf, 0, n2);
        }
    }
    
    private void ensureCapacity(final int n, final boolean b) {
        int n2 = n;
        if (b) {
            if (this.bufferedBlockCipher != null) {
                n2 = this.bufferedBlockCipher.getOutputSize(n);
            }
            else if (this.aeadBlockCipher != null) {
                n2 = this.aeadBlockCipher.getOutputSize(n);
            }
        }
        else if (this.bufferedBlockCipher != null) {
            n2 = this.bufferedBlockCipher.getUpdateOutputSize(n);
        }
        else if (this.aeadBlockCipher != null) {
            n2 = this.aeadBlockCipher.getUpdateOutputSize(n);
        }
        if (this.buf == null || this.buf.length < n2) {
            this.buf = new byte[n2];
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.ensureCapacity(0, true);
        IOException ex = null;
        try {
            if (this.bufferedBlockCipher != null) {
                final int doFinal = this.bufferedBlockCipher.doFinal(this.buf, 0);
                if (doFinal != 0) {
                    this.out.write(this.buf, 0, doFinal);
                }
            }
            else if (this.aeadBlockCipher != null) {
                final int doFinal2 = this.aeadBlockCipher.doFinal(this.buf, 0);
                if (doFinal2 != 0) {
                    this.out.write(this.buf, 0, doFinal2);
                }
            }
            else if (this.streamCipher != null) {
                this.streamCipher.reset();
            }
        }
        catch (final InvalidCipherTextException ex2) {
            ex = new InvalidCipherTextIOException("Error finalising cipher data", ex2);
        }
        catch (final Exception ex3) {
            ex = new CipherIOException("Error closing stream: ", ex3);
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
