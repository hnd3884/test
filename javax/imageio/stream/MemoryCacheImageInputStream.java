package javax.imageio.stream;

import java.io.IOException;
import com.sun.imageio.stream.StreamFinalizer;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import java.io.InputStream;

public class MemoryCacheImageInputStream extends ImageInputStreamImpl
{
    private InputStream stream;
    private MemoryCache cache;
    private final Object disposerReferent;
    private final DisposerRecord disposerRecord;
    
    public MemoryCacheImageInputStream(final InputStream stream) {
        this.cache = new MemoryCache();
        if (stream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        this.stream = stream;
        this.disposerRecord = new StreamDisposerRecord(this.cache);
        if (this.getClass() == MemoryCacheImageInputStream.class) {
            Disposer.addRecord(this.disposerReferent = new Object(), this.disposerRecord);
        }
        else {
            this.disposerReferent = new StreamFinalizer(this);
        }
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        if (this.cache.loadFromStream(this.stream, this.streamPos + 1L) >= this.streamPos + 1L) {
            return this.cache.read(this.streamPos++);
        }
        return -1;
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
        n2 = (int)(this.cache.loadFromStream(this.stream, this.streamPos + n2) - this.streamPos);
        if (n2 > 0) {
            this.cache.read(array, n, n2, this.streamPos);
            this.streamPos += n2;
            return n2;
        }
        return -1;
    }
    
    @Override
    public void flushBefore(final long n) throws IOException {
        super.flushBefore(n);
        this.cache.disposeBefore(n);
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
        super.close();
        this.disposerRecord.dispose();
        this.stream = null;
        this.cache = null;
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
    
    private static class StreamDisposerRecord implements DisposerRecord
    {
        private MemoryCache cache;
        
        public StreamDisposerRecord(final MemoryCache cache) {
            this.cache = cache;
        }
        
        @Override
        public synchronized void dispose() {
            if (this.cache != null) {
                this.cache.reset();
                this.cache = null;
            }
        }
    }
}
