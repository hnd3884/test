package org.apache.axiom.blob;

import java.io.IOException;
import java.io.InputStream;

final class MemoryBlobInputStream extends InputStream
{
    private MemoryBlobChunk chunk;
    private int index;
    private MemoryBlobChunk markChunk;
    private int markIndex;
    
    MemoryBlobInputStream(final MemoryBlobChunk firstChunk) {
        this.chunk = firstChunk;
    }
    
    private void updateChunk() {
        while (this.chunk != null && this.index == this.chunk.size) {
            this.chunk = this.chunk.nextChunk;
            this.index = 0;
        }
    }
    
    @Override
    public int read(final byte[] buffer, int off, int len) throws IOException {
        int read = 0;
        while (len > 0) {
            this.updateChunk();
            if (this.chunk == null) {
                if (read == 0) {
                    return -1;
                }
                break;
            }
            else {
                final int c = Math.min(len, this.chunk.size - this.index);
                System.arraycopy(this.chunk.buffer, this.index, buffer, off, c);
                this.index += c;
                off += c;
                len -= c;
                read += c;
            }
        }
        return read;
    }
    
    @Override
    public int read() throws IOException {
        this.updateChunk();
        if (this.chunk == null) {
            return -1;
        }
        return this.chunk.buffer[this.index++] & 0xFF;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.markChunk = this.chunk;
        this.markIndex = this.index;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.chunk = this.markChunk;
        this.index = this.markIndex;
    }
    
    @Override
    public long skip(long n) throws IOException {
        long skipped = 0L;
        while (n > 0L) {
            this.updateChunk();
            if (this.chunk == null) {
                break;
            }
            final int c = (int)Math.min(n, this.chunk.size - this.index);
            this.index += c;
            skipped += c;
            n -= c;
        }
        return skipped;
    }
    
    @Override
    public int available() throws IOException {
        if (this.chunk == null) {
            return 0;
        }
        long available = this.chunk.size - this.index;
        for (MemoryBlobChunk chunk = this.chunk.nextChunk; chunk != null; chunk = chunk.nextChunk) {
            available += chunk.size;
            if (available > 2147483647L) {
                return Integer.MAX_VALUE;
            }
        }
        return (int)available;
    }
    
    @Override
    public void close() throws IOException {
        this.chunk = null;
    }
}
