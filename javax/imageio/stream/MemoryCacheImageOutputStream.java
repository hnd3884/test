package javax.imageio.stream;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryCacheImageOutputStream extends ImageOutputStreamImpl
{
    private OutputStream stream;
    private MemoryCache cache;
    
    public MemoryCacheImageOutputStream(final OutputStream stream) {
        this.cache = new MemoryCache();
        if (stream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        this.stream = stream;
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        final int read = this.cache.read(this.streamPos);
        if (read != -1) {
            ++this.streamPos;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, int n2) throws IOException {
        this.checkClosed();
        if (array == null) {
            throw new NullPointerException("b == null!");
        }
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
        }
        this.bitOffset = 0;
        if (n2 == 0) {
            return 0;
        }
        final long n3 = this.cache.getLength() - this.streamPos;
        if (n3 <= 0L) {
            return -1;
        }
        n2 = (int)Math.min(n3, n2);
        this.cache.read(array, n, n2, this.streamPos);
        this.streamPos += n2;
        return n2;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.flushBits();
        this.cache.write(n, this.streamPos);
        ++this.streamPos;
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.flushBits();
        this.cache.write(array, n, n2, this.streamPos);
        this.streamPos += n2;
    }
    
    @Override
    public long length() {
        try {
            this.checkClosed();
            return this.cache.getLength();
        }
        catch (final IOException ex) {
            return -1L;
        }
    }
    
    @Override
    public boolean isCached() {
        return true;
    }
    
    @Override
    public boolean isCachedFile() {
        return false;
    }
    
    @Override
    public boolean isCachedMemory() {
        return true;
    }
    
    @Override
    public void close() throws IOException {
        final long length = this.cache.getLength();
        this.seek(length);
        this.flushBefore(length);
        super.close();
        this.cache.reset();
        this.cache = null;
        this.stream = null;
    }
    
    @Override
    public void flushBefore(final long n) throws IOException {
        final long flushedPos = this.flushedPos;
        super.flushBefore(n);
        this.cache.writeToStream(this.stream, flushedPos, this.flushedPos - flushedPos);
        this.cache.disposeBefore(this.flushedPos);
        this.stream.flush();
    }
}
