package org.apache.axiom.blob;

import java.io.IOException;
import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;
import java.io.OutputStream;

final class MemoryBlobImpl implements MemoryBlob
{
    private MemoryBlobChunk firstChunk;
    private boolean committed;
    
    public long getSize() {
        if (this.firstChunk == null || !this.committed) {
            throw new IllegalStateException();
        }
        long size = 0L;
        for (MemoryBlobChunk chunk = this.firstChunk; chunk != null; chunk = chunk.nextChunk) {
            size += chunk.size;
        }
        return size;
    }
    
    public OutputStream getOutputStream() {
        return this.internalGetOutputStream();
    }
    
    private MemoryBlobOutputStream internalGetOutputStream() {
        if (this.firstChunk != null || this.committed) {
            throw new IllegalStateException();
        }
        return new MemoryBlobOutputStream(this, this.firstChunk = new MemoryBlobChunk(4096));
    }
    
    void commit() {
        this.committed = true;
    }
    
    public long readFrom(final InputStream in) throws StreamCopyException {
        final MemoryBlobOutputStream out = this.internalGetOutputStream();
        try {
            return out.readFrom(in, -1L);
        }
        finally {
            out.close();
        }
    }
    
    public InputStream getInputStream() {
        return this.getInputStream(true);
    }
    
    public InputStream readOnce() {
        return this.getInputStream(false);
    }
    
    public InputStream getInputStream(final boolean preserve) {
        if (this.firstChunk == null || !this.committed) {
            throw new IllegalStateException();
        }
        final InputStream in = new MemoryBlobInputStream(this.firstChunk);
        if (!preserve) {
            this.firstChunk = null;
        }
        return in;
    }
    
    public void writeTo(final OutputStream os) throws StreamCopyException {
        if (this.firstChunk == null || !this.committed) {
            throw new IllegalStateException();
        }
        MemoryBlobChunk chunk = this.firstChunk;
        try {
            while (chunk != null) {
                if (chunk.size > 0) {
                    os.write(chunk.buffer, 0, chunk.size);
                }
                chunk = chunk.nextChunk;
            }
        }
        catch (final IOException ex) {
            throw new StreamCopyException(2, ex);
        }
    }
    
    public void release() {
        this.firstChunk = null;
    }
}
