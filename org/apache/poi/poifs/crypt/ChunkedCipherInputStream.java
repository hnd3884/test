package org.apache.poi.poifs.crypt;

import java.io.EOFException;
import org.apache.poi.EncryptedDocumentException;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public abstract class ChunkedCipherInputStream extends LittleEndianInputStream
{
    private static final int MAX_RECORD_LENGTH = 100000;
    private final int chunkSize;
    private final int chunkBits;
    private final long size;
    private final byte[] chunk;
    private final byte[] plain;
    private final Cipher cipher;
    private int lastIndex;
    private long pos;
    private boolean chunkIsValid;
    
    public ChunkedCipherInputStream(final InputStream stream, final long size, final int chunkSize) throws GeneralSecurityException {
        this(stream, size, chunkSize, 0);
    }
    
    public ChunkedCipherInputStream(final InputStream stream, final long size, final int chunkSize, final int initialPos) throws GeneralSecurityException {
        super(stream);
        this.size = size;
        this.pos = initialPos;
        this.chunkSize = chunkSize;
        final int cs = (chunkSize == -1) ? 4096 : chunkSize;
        this.chunk = IOUtils.safelyAllocate(cs, 100000);
        this.plain = IOUtils.safelyAllocate(cs, 100000);
        this.chunkBits = Integer.bitCount(this.chunk.length - 1);
        this.lastIndex = (int)(this.pos >> this.chunkBits);
        this.cipher = this.initCipherForBlock(null, this.lastIndex);
    }
    
    public final Cipher initCipherForBlock(final int block) throws IOException, GeneralSecurityException {
        if (this.chunkSize != -1) {
            throw new GeneralSecurityException("the cipher block can only be set for streaming encryption, e.g. CryptoAPI...");
        }
        this.chunkIsValid = false;
        return this.initCipherForBlock(this.cipher, block);
    }
    
    protected abstract Cipher initCipherForBlock(final Cipher p0, final int p1) throws GeneralSecurityException;
    
    @Override
    public int read() throws IOException {
        final byte[] b = { 0 };
        return (this.read(b) == 1) ? (b[0] & 0xFF) : -1;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.read(b, off, len, false);
    }
    
    private int read(final byte[] b, int off, int len, final boolean readPlain) throws IOException {
        int total = 0;
        if (this.remainingBytes() <= 0) {
            return -1;
        }
        final int chunkMask = this.getChunkMask();
        while (len > 0) {
            if (!this.chunkIsValid) {
                try {
                    this.nextChunk();
                    this.chunkIsValid = true;
                }
                catch (final GeneralSecurityException e) {
                    throw new EncryptedDocumentException(e.getMessage(), e);
                }
            }
            int count = (int)(this.chunk.length - (this.pos & (long)chunkMask));
            final int avail = this.remainingBytes();
            if (avail == 0) {
                return total;
            }
            count = Math.min(avail, Math.min(count, len));
            System.arraycopy(readPlain ? this.plain : this.chunk, (int)(this.pos & (long)chunkMask), b, off, count);
            off += count;
            len -= count;
            this.pos += count;
            if ((this.pos & (long)chunkMask) == 0x0L) {
                this.chunkIsValid = false;
            }
            total += count;
        }
        return total;
    }
    
    @Override
    public long skip(final long n) {
        final long start = this.pos;
        final long skip = Math.min(this.remainingBytes(), n);
        if (((this.pos + skip ^ start) & (long)~this.getChunkMask()) != 0x0L) {
            this.chunkIsValid = false;
        }
        this.pos += skip;
        return skip;
    }
    
    @Override
    public int available() {
        return this.remainingBytes();
    }
    
    private int remainingBytes() {
        return (int)(this.size - this.pos);
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException();
    }
    
    protected int getChunkMask() {
        return this.chunk.length - 1;
    }
    
    private void nextChunk() throws GeneralSecurityException, IOException {
        if (this.chunkSize != -1) {
            final int index = (int)(this.pos >> this.chunkBits);
            this.initCipherForBlock(this.cipher, index);
            if (this.lastIndex != index) {
                final long skipN = index - this.lastIndex << this.chunkBits;
                if (super.skip(skipN) < skipN) {
                    throw new EOFException("buffer underrun");
                }
            }
            this.lastIndex = index + 1;
        }
        final int todo = (int)Math.min(this.size, this.chunk.length);
        int totalBytes = 0;
        int readBytes;
        do {
            readBytes = super.read(this.plain, totalBytes, todo - totalBytes);
            totalBytes += Math.max(0, readBytes);
        } while (readBytes != -1 && totalBytes < todo);
        if (readBytes == -1 && this.pos + totalBytes < this.size && this.size < 2147483647L) {
            throw new EOFException("buffer underrun");
        }
        System.arraycopy(this.plain, 0, this.chunk, 0, totalBytes);
        final int totalBytes2;
        this.invokeCipher(totalBytes2, (totalBytes2 = totalBytes) == this.chunkSize);
    }
    
    protected int invokeCipher(final int totalBytes, final boolean doFinal) throws GeneralSecurityException {
        if (doFinal) {
            return this.cipher.doFinal(this.chunk, 0, totalBytes, this.chunk);
        }
        return this.cipher.update(this.chunk, 0, totalBytes, this.chunk);
    }
    
    @Override
    public void readPlain(final byte[] b, final int off, final int len) {
        if (len <= 0) {
            return;
        }
        try {
            int total = 0;
            int readBytes;
            do {
                readBytes = this.read(b, off, len, true);
                total += Math.max(0, readBytes);
            } while (readBytes > -1 && total < len);
            if (total < len) {
                throw new EOFException("buffer underrun");
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setNextRecordSize(final int recordSize) {
    }
    
    protected byte[] getChunk() {
        return this.chunk;
    }
    
    protected byte[] getPlain() {
        return this.plain;
    }
    
    public long getPos() {
        return this.pos;
    }
}
