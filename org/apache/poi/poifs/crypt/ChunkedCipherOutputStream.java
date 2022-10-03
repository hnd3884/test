package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;
import java.io.InputStream;
import org.apache.poi.util.LittleEndian;
import java.io.FileInputStream;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.FileOutputStream;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.IOUtils;
import java.io.OutputStream;
import javax.crypto.Cipher;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.File;
import com.zaxxer.sparsebits.SparseBitSet;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;
import java.io.FilterOutputStream;

@Internal
public abstract class ChunkedCipherOutputStream extends FilterOutputStream
{
    private static final POILogger LOG;
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final int STREAMING = -1;
    private final int chunkSize;
    private final int chunkBits;
    private final byte[] chunk;
    private final SparseBitSet plainByteFlags;
    private final File fileOut;
    private final DirectoryNode dir;
    private long pos;
    private long totalPos;
    private long written;
    private Cipher cipher;
    private boolean isClosed;
    
    public ChunkedCipherOutputStream(final DirectoryNode dir, final int chunkSize) throws IOException, GeneralSecurityException {
        super(null);
        this.chunkSize = chunkSize;
        final int cs = (chunkSize == -1) ? 4096 : chunkSize;
        this.chunk = IOUtils.safelyAllocate(cs, 100000);
        this.plainByteFlags = new SparseBitSet(cs);
        this.chunkBits = Integer.bitCount(cs - 1);
        (this.fileOut = TempFile.createTempFile("encrypted_package", "crypt")).deleteOnExit();
        this.out = new FileOutputStream(this.fileOut);
        this.dir = dir;
        this.cipher = this.initCipherForBlock(null, 0, false);
    }
    
    public ChunkedCipherOutputStream(final OutputStream stream, final int chunkSize) throws IOException, GeneralSecurityException {
        super(stream);
        this.chunkSize = chunkSize;
        final int cs = (chunkSize == -1) ? 4096 : chunkSize;
        this.chunk = IOUtils.safelyAllocate(cs, 100000);
        this.plainByteFlags = new SparseBitSet(cs);
        this.chunkBits = Integer.bitCount(cs - 1);
        this.fileOut = null;
        this.dir = null;
        this.cipher = this.initCipherForBlock(null, 0, false);
    }
    
    public final Cipher initCipherForBlock(final int block, final boolean lastChunk) throws IOException, GeneralSecurityException {
        return this.initCipherForBlock(this.cipher, block, lastChunk);
    }
    
    @Internal
    protected Cipher initCipherForBlockNoFlush(final Cipher existing, final int block, final boolean lastChunk) throws IOException, GeneralSecurityException {
        return this.initCipherForBlock(this.cipher, block, lastChunk);
    }
    
    protected abstract Cipher initCipherForBlock(final Cipher p0, final int p1, final boolean p2) throws IOException, GeneralSecurityException;
    
    protected abstract void calculateChecksum(final File p0, final int p1) throws GeneralSecurityException, IOException;
    
    protected abstract void createEncryptionInfoEntry(final DirectoryNode p0, final File p1) throws IOException, GeneralSecurityException;
    
    @Override
    public void write(final int b) throws IOException {
        this.write(new byte[] { (byte)b });
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.write(b, off, len, false);
    }
    
    public void writePlain(final byte[] b, final int off, final int len) throws IOException {
        this.write(b, off, len, true);
    }
    
    protected void write(final byte[] b, int off, int len, final boolean writePlain) throws IOException {
        if (len == 0) {
            return;
        }
        if (len < 0 || b.length < off + len) {
            throw new IOException("not enough bytes in your input buffer");
        }
        final int chunkMask = this.getChunkMask();
        while (len > 0) {
            final int posInChunk = (int)(this.pos & (long)chunkMask);
            final int nextLen = Math.min(this.chunk.length - posInChunk, len);
            System.arraycopy(b, off, this.chunk, posInChunk, nextLen);
            if (writePlain) {
                this.plainByteFlags.set(posInChunk, posInChunk + nextLen);
            }
            this.pos += nextLen;
            this.totalPos += nextLen;
            off += nextLen;
            len -= nextLen;
            if ((this.pos & (long)chunkMask) == 0x0L) {
                this.writeChunk(len > 0);
            }
        }
    }
    
    protected int getChunkMask() {
        return this.chunk.length - 1;
    }
    
    protected void writeChunk(final boolean continued) throws IOException {
        if (this.pos == 0L || this.totalPos == this.written) {
            return;
        }
        int posInChunk = (int)(this.pos & (long)this.getChunkMask());
        int index = (int)(this.pos >> this.chunkBits);
        boolean lastChunk;
        if (posInChunk == 0) {
            --index;
            posInChunk = this.chunk.length;
            lastChunk = false;
        }
        else {
            lastChunk = true;
        }
        int ciLen;
        try {
            boolean doFinal = true;
            final long oldPos = this.pos;
            this.pos = 0L;
            if (this.chunkSize == -1) {
                if (continued) {
                    doFinal = false;
                }
            }
            else {
                this.cipher = this.initCipherForBlock(this.cipher, index, lastChunk);
                this.pos = oldPos;
            }
            ciLen = this.invokeCipher(posInChunk, doFinal);
        }
        catch (final GeneralSecurityException e) {
            throw new IOException("can't re-/initialize cipher", e);
        }
        this.out.write(this.chunk, 0, ciLen);
        this.plainByteFlags.clear();
        this.written += ciLen;
    }
    
    protected int invokeCipher(int posInChunk, final boolean doFinal) throws GeneralSecurityException, IOException {
        final byte[] plain = (byte[])(this.plainByteFlags.isEmpty() ? null : ((byte[])this.chunk.clone()));
        final int ciLen = doFinal ? this.cipher.doFinal(this.chunk, 0, posInChunk, this.chunk) : this.cipher.update(this.chunk, 0, posInChunk, this.chunk);
        if (doFinal && "IBMJCE".equals(this.cipher.getProvider().getName()) && "RC4".equals(this.cipher.getAlgorithm())) {
            int index = (int)(this.pos >> this.chunkBits);
            boolean lastChunk;
            if (posInChunk == 0) {
                --index;
                posInChunk = this.chunk.length;
                lastChunk = false;
            }
            else {
                lastChunk = true;
            }
            this.cipher = this.initCipherForBlockNoFlush(this.cipher, index, lastChunk);
        }
        if (plain != null) {
            for (int i = this.plainByteFlags.nextSetBit(0); i >= 0 && i < posInChunk; i = this.plainByteFlags.nextSetBit(i + 1)) {
                this.chunk[i] = plain[i];
            }
        }
        return ciLen;
    }
    
    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            ChunkedCipherOutputStream.LOG.log(1, "ChunkedCipherOutputStream was already closed - ignoring");
            return;
        }
        this.isClosed = true;
        try {
            this.writeChunk(false);
            super.close();
            if (this.fileOut != null) {
                final int oleStreamSize = (int)(this.fileOut.length() + 8L);
                this.calculateChecksum(this.fileOut, (int)this.pos);
                this.dir.createDocument("EncryptedPackage", oleStreamSize, new EncryptedPackageWriter());
                this.createEncryptionInfoEntry(this.dir, this.fileOut);
            }
        }
        catch (final GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
    
    protected byte[] getChunk() {
        return this.chunk;
    }
    
    protected SparseBitSet getPlainByteFlags() {
        return this.plainByteFlags;
    }
    
    protected long getPos() {
        return this.pos;
    }
    
    protected long getTotalPos() {
        return this.totalPos;
    }
    
    public void setNextRecordSize(final int recordSize, final boolean isPlain) {
    }
    
    static {
        LOG = POILogFactory.getLogger(ChunkedCipherOutputStream.class);
    }
    
    private class EncryptedPackageWriter implements POIFSWriterListener
    {
        @Override
        public void processPOIFSWriterEvent(final POIFSWriterEvent event) {
            try {
                try (final OutputStream os = event.getStream();
                     final FileInputStream fis = new FileInputStream(ChunkedCipherOutputStream.this.fileOut)) {
                    final byte[] buf = new byte[8];
                    LittleEndian.putLong(buf, 0, ChunkedCipherOutputStream.this.pos);
                    os.write(buf);
                    IOUtils.copy(fis, os);
                }
                if (!ChunkedCipherOutputStream.this.fileOut.delete()) {
                    ChunkedCipherOutputStream.LOG.log(7, "Can't delete temporary encryption file: " + ChunkedCipherOutputStream.this.fileOut);
                }
            }
            catch (final IOException e) {
                throw new EncryptedDocumentException(e);
            }
        }
    }
}
