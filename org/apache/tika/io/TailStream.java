package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TailStream extends FilterInputStream
{
    private static final int SKIP_SIZE = 4096;
    private final byte[] tailBuffer;
    private final int tailSize;
    private byte[] markBuffer;
    private long bytesRead;
    private long markBytesRead;
    private int currentIndex;
    private int markIndex;
    
    public TailStream(final InputStream in, final int size) {
        super(in);
        this.tailSize = size;
        this.tailBuffer = new byte[size];
    }
    
    @Override
    public int read() throws IOException {
        final int c = super.read();
        if (c != -1) {
            this.appendByte((byte)c);
        }
        return c;
    }
    
    @Override
    public int read(final byte[] buf) throws IOException {
        final int read = super.read(buf);
        if (read > 0) {
            this.appendBuf(buf, 0, read);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] buf, final int ofs, final int length) throws IOException {
        final int read = super.read(buf, ofs, length);
        if (read > 0) {
            this.appendBuf(buf, ofs, read);
        }
        return read;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final int bufSize = (int)Math.min(n, 4096L);
        final byte[] buf = new byte[bufSize];
        long bytesSkipped;
        int bytesRead;
        int len;
        for (bytesSkipped = 0L, bytesRead = 0; bytesSkipped < n && bytesRead != -1; bytesSkipped += bytesRead) {
            len = (int)Math.min(bufSize, n - bytesSkipped);
            bytesRead = this.read(buf, 0, len);
            if (bytesRead != -1) {}
        }
        return (bytesRead < 0 && bytesSkipped == 0L) ? -1L : bytesSkipped;
    }
    
    @Override
    public void mark(final int limit) {
        this.markBuffer = new byte[this.tailSize];
        System.arraycopy(this.tailBuffer, 0, this.markBuffer, 0, this.tailSize);
        this.markIndex = this.currentIndex;
        this.markBytesRead = this.bytesRead;
    }
    
    @Override
    public void reset() {
        if (this.markBuffer != null) {
            System.arraycopy(this.markBuffer, 0, this.tailBuffer, 0, this.tailSize);
            this.currentIndex = this.markIndex;
            this.bytesRead = this.markBytesRead;
        }
    }
    
    public byte[] getTail() {
        final int size = (int)Math.min(this.tailSize, this.bytesRead);
        final byte[] result = new byte[size];
        System.arraycopy(this.tailBuffer, this.currentIndex, result, 0, size - this.currentIndex);
        System.arraycopy(this.tailBuffer, 0, result, size - this.currentIndex, this.currentIndex);
        return result;
    }
    
    private void appendByte(final byte b) {
        this.tailBuffer[this.currentIndex++] = b;
        if (this.currentIndex >= this.tailSize) {
            this.currentIndex = 0;
        }
        ++this.bytesRead;
    }
    
    private void appendBuf(final byte[] buf, final int ofs, final int length) {
        if (length >= this.tailSize) {
            this.replaceTailBuffer(buf, ofs, length);
        }
        else {
            this.copyToTailBuffer(buf, ofs, length);
        }
        this.bytesRead += length;
    }
    
    private void replaceTailBuffer(final byte[] buf, final int ofs, final int length) {
        System.arraycopy(buf, ofs + length - this.tailSize, this.tailBuffer, 0, this.tailSize);
        this.currentIndex = 0;
    }
    
    private void copyToTailBuffer(final byte[] buf, final int ofs, final int length) {
        final int remaining = this.tailSize - this.currentIndex;
        final int size1 = Math.min(remaining, length);
        System.arraycopy(buf, ofs, this.tailBuffer, this.currentIndex, size1);
        System.arraycopy(buf, ofs + size1, this.tailBuffer, 0, length - size1);
        this.currentIndex = (this.currentIndex + length) % this.tailSize;
    }
}
