package org.apache.lucene.store;

import java.io.EOFException;
import java.io.IOException;

public class RAMInputStream extends IndexInput implements Cloneable
{
    private final RAMFile file;
    private final long length;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int bufferPosition;
    private int bufferLength;
    
    public RAMInputStream(final String name, final RAMFile f) throws IOException {
        this(name, f, f.length);
    }
    
    RAMInputStream(final String name, final RAMFile f, final long length) throws IOException {
        super("RAMInputStream(name=" + name + ")");
        this.file = f;
        this.length = length;
        if (length / 1024L >= 2147483647L) {
            throw new IOException("RAMInputStream too large length=" + length + ": " + name);
        }
        this.setCurrentBuffer();
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public long length() {
        return this.length;
    }
    
    @Override
    public byte readByte() throws IOException {
        if (this.bufferPosition == this.bufferLength) {
            this.nextBuffer();
        }
        return this.currentBuffer[this.bufferPosition++];
    }
    
    @Override
    public void readBytes(final byte[] b, int offset, int len) throws IOException {
        while (len > 0) {
            if (this.bufferPosition == this.bufferLength) {
                this.nextBuffer();
            }
            final int remainInBuffer = this.bufferLength - this.bufferPosition;
            final int bytesToCopy = (len < remainInBuffer) ? len : remainInBuffer;
            System.arraycopy(this.currentBuffer, this.bufferPosition, b, offset, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            this.bufferPosition += bytesToCopy;
        }
    }
    
    @Override
    public long getFilePointer() {
        return this.currentBufferIndex * 1024L + this.bufferPosition;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        final int newBufferIndex = (int)(pos / 1024L);
        if (newBufferIndex != this.currentBufferIndex) {
            this.currentBufferIndex = newBufferIndex;
            this.setCurrentBuffer();
        }
        this.bufferPosition = (int)(pos % 1024L);
        if (this.getFilePointer() > this.length()) {
            throw new EOFException("seek beyond EOF: pos=" + this.getFilePointer() + " vs length=" + this.length() + ": " + this);
        }
    }
    
    private void nextBuffer() throws IOException {
        if (this.getFilePointer() >= this.length()) {
            throw new EOFException("cannot read another byte at EOF: pos=" + this.getFilePointer() + " vs length=" + this.length() + ": " + this);
        }
        ++this.currentBufferIndex;
        this.setCurrentBuffer();
        assert this.currentBuffer != null;
        this.bufferPosition = 0;
    }
    
    private final void setCurrentBuffer() throws IOException {
        if (this.currentBufferIndex < this.file.numBuffers()) {
            this.currentBuffer = this.file.getBuffer(this.currentBufferIndex);
            assert this.currentBuffer != null;
            final long bufferStart = 1024L * this.currentBufferIndex;
            this.bufferLength = (int)Math.min(1024L, this.length - bufferStart);
        }
        else {
            this.currentBuffer = null;
        }
    }
    
    @Override
    public IndexInput slice(final String sliceDescription, final long offset, final long sliceLength) throws IOException {
        if (offset < 0L || sliceLength < 0L || offset + sliceLength > this.length) {
            throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + this);
        }
        return new RAMInputStream(this.getFullSliceDescription(sliceDescription), this.file, offset + sliceLength) {
            {
                this.seek(0L);
            }
            
            @Override
            public void seek(final long pos) throws IOException {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this);
                }
                super.seek(pos + offset);
            }
            
            @Override
            public long getFilePointer() {
                return super.getFilePointer() - offset;
            }
            
            @Override
            public long length() {
                return sliceLength;
            }
            
            @Override
            public IndexInput slice(final String sliceDescription, final long ofs, final long len) throws IOException {
                return super.slice(sliceDescription, offset + ofs, len);
            }
        };
    }
}
