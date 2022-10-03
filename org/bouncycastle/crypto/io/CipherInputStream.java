package org.bouncycastle.crypto.io;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.SkippingCipher;
import java.io.FilterInputStream;

public class CipherInputStream extends FilterInputStream
{
    private static final int INPUT_BUF_SIZE = 2048;
    private SkippingCipher skippingCipher;
    private byte[] inBuf;
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private byte[] buf;
    private byte[] markBuf;
    private int bufOff;
    private int maxBuf;
    private boolean finalized;
    private long markPosition;
    private int markBufOff;
    
    public CipherInputStream(final InputStream inputStream, final BufferedBlockCipher bufferedBlockCipher) {
        this(inputStream, bufferedBlockCipher, 2048);
    }
    
    public CipherInputStream(final InputStream inputStream, final StreamCipher streamCipher) {
        this(inputStream, streamCipher, 2048);
    }
    
    public CipherInputStream(final InputStream inputStream, final AEADBlockCipher aeadBlockCipher) {
        this(inputStream, aeadBlockCipher, 2048);
    }
    
    public CipherInputStream(final InputStream inputStream, final BufferedBlockCipher bufferedBlockCipher, final int n) {
        super(inputStream);
        this.bufferedBlockCipher = bufferedBlockCipher;
        this.inBuf = new byte[n];
        this.skippingCipher = ((bufferedBlockCipher instanceof SkippingCipher) ? bufferedBlockCipher : null);
    }
    
    public CipherInputStream(final InputStream inputStream, final StreamCipher streamCipher, final int n) {
        super(inputStream);
        this.streamCipher = streamCipher;
        this.inBuf = new byte[n];
        this.skippingCipher = ((streamCipher instanceof SkippingCipher) ? streamCipher : null);
    }
    
    public CipherInputStream(final InputStream inputStream, final AEADBlockCipher aeadBlockCipher, final int n) {
        super(inputStream);
        this.aeadBlockCipher = aeadBlockCipher;
        this.inBuf = new byte[n];
        this.skippingCipher = ((aeadBlockCipher instanceof SkippingCipher) ? aeadBlockCipher : null);
    }
    
    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            final int read = this.in.read(this.inBuf);
            if (read == -1) {
                this.finaliseCipher();
                if (this.maxBuf == 0) {
                    return -1;
                }
                return this.maxBuf;
            }
            else {
                try {
                    this.ensureCapacity(read, false);
                    if (this.bufferedBlockCipher != null) {
                        this.maxBuf = this.bufferedBlockCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                    }
                    else if (this.aeadBlockCipher != null) {
                        this.maxBuf = this.aeadBlockCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                    }
                    else {
                        this.streamCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                        this.maxBuf = read;
                    }
                }
                catch (final Exception ex) {
                    throw new CipherIOException("Error processing stream ", ex);
                }
            }
        }
        return this.maxBuf;
    }
    
    private void finaliseCipher() throws IOException {
        try {
            this.ensureCapacity(0, this.finalized = true);
            if (this.bufferedBlockCipher != null) {
                this.maxBuf = this.bufferedBlockCipher.doFinal(this.buf, 0);
            }
            else if (this.aeadBlockCipher != null) {
                this.maxBuf = this.aeadBlockCipher.doFinal(this.buf, 0);
            }
            else {
                this.maxBuf = 0;
            }
        }
        catch (final InvalidCipherTextException ex) {
            throw new InvalidCipherTextIOException("Error finalising cipher", ex);
        }
        catch (final Exception ex2) {
            throw new IOException("Error finalising cipher " + ex2);
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
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
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
        if (this.skippingCipher == null) {
            final int n2 = (int)Math.min(n, this.available());
            this.bufOff += n2;
            return n2;
        }
        final int available = this.available();
        if (n <= available) {
            this.bufOff += (int)n;
            return n;
        }
        this.bufOff = this.maxBuf;
        final long skip = this.in.skip(n - available);
        if (skip != this.skippingCipher.skip(skip)) {
            throw new IOException("Unable to skip cipher " + skip + " bytes.");
        }
        return skip + available;
    }
    
    @Override
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
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
        this.markBufOff = 0;
        this.markPosition = 0L;
        if (this.markBuf != null) {
            Arrays.fill(this.markBuf, (byte)0);
            this.markBuf = null;
        }
        if (this.buf != null) {
            Arrays.fill(this.buf, (byte)0);
            this.buf = null;
        }
        Arrays.fill(this.inBuf, (byte)0);
    }
    
    @Override
    public void mark(final int n) {
        this.in.mark(n);
        if (this.skippingCipher != null) {
            this.markPosition = this.skippingCipher.getPosition();
        }
        if (this.buf != null) {
            this.markBuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, this.markBuf, 0, this.buf.length);
        }
        this.markBufOff = this.bufOff;
    }
    
    @Override
    public void reset() throws IOException {
        if (this.skippingCipher == null) {
            throw new IOException("cipher must implement SkippingCipher to be used with reset()");
        }
        this.in.reset();
        this.skippingCipher.seekTo(this.markPosition);
        if (this.markBuf != null) {
            this.buf = this.markBuf;
        }
        this.bufOff = this.markBufOff;
    }
    
    @Override
    public boolean markSupported() {
        return this.skippingCipher != null && this.in.markSupported();
    }
}
