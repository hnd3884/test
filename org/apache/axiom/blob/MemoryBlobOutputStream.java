package org.apache.axiom.blob;

import java.io.IOException;
import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;
import org.apache.axiom.ext.io.ReadFromSupport;
import java.io.OutputStream;

final class MemoryBlobOutputStream extends OutputStream implements ReadFromSupport
{
    private final MemoryBlobImpl blob;
    private MemoryBlobChunk chunk;
    
    MemoryBlobOutputStream(final MemoryBlobImpl blob, final MemoryBlobChunk firstChunk) {
        this.blob = blob;
        this.chunk = firstChunk;
    }
    
    private void updateChunk() {
        if (this.chunk.size == this.chunk.buffer.length) {
            this.chunk = this.chunk.allocateNextChunk();
        }
    }
    
    @Override
    public void write(final byte[] b, int off, final int len) {
        if (this.chunk == null) {
            throw new IllegalStateException();
        }
        int c;
        for (int total = 0; total < len; total += c, off += c) {
            this.updateChunk();
            c = Math.min(len - total, this.chunk.buffer.length - this.chunk.size);
            System.arraycopy(b, off, this.chunk.buffer, this.chunk.size, c);
            final MemoryBlobChunk chunk = this.chunk;
            chunk.size += c;
        }
    }
    
    @Override
    public void write(final int b) {
        if (this.chunk == null) {
            throw new IllegalStateException();
        }
        this.updateChunk();
        this.chunk.buffer[this.chunk.size++] = (byte)b;
    }
    
    public long readFrom(final InputStream in, final long length) throws StreamCopyException {
        if (this.chunk == null) {
            throw new IllegalStateException();
        }
        long read = 0L;
        int c;
        for (long toRead = (length == -1L) ? Long.MAX_VALUE : length; toRead > 0L; toRead -= c) {
            this.updateChunk();
            try {
                c = in.read(this.chunk.buffer, this.chunk.size, (int)Math.min(toRead, this.chunk.buffer.length - this.chunk.size));
            }
            catch (final IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (c == -1) {
                break;
            }
            final MemoryBlobChunk chunk = this.chunk;
            chunk.size += c;
            read += c;
        }
        return read;
    }
    
    @Override
    public void close() {
        this.blob.commit();
        this.chunk = null;
    }
}
