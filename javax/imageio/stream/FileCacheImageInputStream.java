package javax.imageio.stream;

import java.io.IOException;
import com.sun.imageio.stream.StreamFinalizer;
import sun.java2d.Disposer;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.sun.imageio.stream.StreamCloser;
import sun.java2d.DisposerRecord;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.InputStream;

public class FileCacheImageInputStream extends ImageInputStreamImpl
{
    private InputStream stream;
    private File cacheFile;
    private RandomAccessFile cache;
    private static final int BUFFER_LENGTH = 1024;
    private byte[] buf;
    private long length;
    private boolean foundEOF;
    private final Object disposerReferent;
    private final DisposerRecord disposerRecord;
    private final StreamCloser.CloseAction closeAction;
    
    public FileCacheImageInputStream(final InputStream stream, final File file) throws IOException {
        this.buf = new byte[1024];
        this.length = 0L;
        this.foundEOF = false;
        if (stream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        if (file != null && !file.isDirectory()) {
            throw new IllegalArgumentException("Not a directory!");
        }
        this.stream = stream;
        if (file == null) {
            this.cacheFile = Files.createTempFile("imageio", ".tmp", (FileAttribute<?>[])new FileAttribute[0]).toFile();
        }
        else {
            this.cacheFile = Files.createTempFile(file.toPath(), "imageio", ".tmp", (FileAttribute<?>[])new FileAttribute[0]).toFile();
        }
        this.cache = new RandomAccessFile(this.cacheFile, "rw");
        StreamCloser.addToQueue(this.closeAction = StreamCloser.createCloseAction(this));
        this.disposerRecord = new StreamDisposerRecord(this.cacheFile, this.cache);
        if (this.getClass() == FileCacheImageInputStream.class) {
            Disposer.addRecord(this.disposerReferent = new Object(), this.disposerRecord);
        }
        else {
            this.disposerReferent = new StreamFinalizer(this);
        }
    }
    
    private long readUntil(final long n) throws IOException {
        if (n < this.length) {
            return n;
        }
        if (this.foundEOF) {
            return this.length;
        }
        long n2 = n - this.length;
        this.cache.seek(this.length);
        while (n2 > 0L) {
            final int read = this.stream.read(this.buf, 0, (int)Math.min(n2, 1024L));
            if (read == -1) {
                this.foundEOF = true;
                return this.length;
            }
            this.cache.write(this.buf, 0, read);
            n2 -= read;
            this.length += read;
        }
        return n;
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        final long n = this.streamPos + 1L;
        if (this.readUntil(n) >= n) {
            this.cache.seek(this.streamPos++);
            return this.cache.read();
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
        n2 = (int)Math.min(n2, this.readUntil(this.streamPos + n2) - this.streamPos);
        if (n2 > 0) {
            this.cache.seek(this.streamPos);
            this.cache.readFully(array, n, n2);
            this.streamPos += n2;
            return n2;
        }
        return -1;
    }
    
    @Override
    public boolean isCached() {
        return true;
    }
    
    @Override
    public boolean isCachedFile() {
        return true;
    }
    
    @Override
    public boolean isCachedMemory() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.disposerRecord.dispose();
        this.stream = null;
        this.cache = null;
        this.cacheFile = null;
        StreamCloser.removeFromQueue(this.closeAction);
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
    
    private static class StreamDisposerRecord implements DisposerRecord
    {
        private File cacheFile;
        private RandomAccessFile cache;
        
        public StreamDisposerRecord(final File cacheFile, final RandomAccessFile cache) {
            this.cacheFile = cacheFile;
            this.cache = cache;
        }
        
        @Override
        public synchronized void dispose() {
            if (this.cache != null) {
                try {
                    this.cache.close();
                }
                catch (final IOException ex) {}
                finally {
                    this.cache = null;
                }
            }
            if (this.cacheFile != null) {
                this.cacheFile.delete();
                this.cacheFile = null;
            }
        }
    }
}
