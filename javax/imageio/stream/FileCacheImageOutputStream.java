package javax.imageio.stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.sun.imageio.stream.StreamCloser;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.OutputStream;

public class FileCacheImageOutputStream extends ImageOutputStreamImpl
{
    private OutputStream stream;
    private File cacheFile;
    private RandomAccessFile cache;
    private long maxStreamPos;
    private final StreamCloser.CloseAction closeAction;
    
    public FileCacheImageOutputStream(final OutputStream stream, final File file) throws IOException {
        this.maxStreamPos = 0L;
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
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        final int read = this.cache.read();
        if (read != -1) {
            ++this.streamPos;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
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
        final int read = this.cache.read(array, n, n2);
        if (read != -1) {
            this.streamPos += read;
        }
        return read;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.flushBits();
        this.cache.write(n);
        ++this.streamPos;
        this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.flushBits();
        this.cache.write(array, n, n2);
        this.streamPos += n2;
        this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
    }
    
    @Override
    public long length() {
        try {
            this.checkClosed();
            return this.cache.length();
        }
        catch (final IOException ex) {
            return -1L;
        }
    }
    
    @Override
    public void seek(final long n) throws IOException {
        this.checkClosed();
        if (n < this.flushedPos) {
            throw new IndexOutOfBoundsException();
        }
        this.cache.seek(n);
        this.streamPos = this.cache.getFilePointer();
        this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
        this.bitOffset = 0;
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
        this.seek(this.maxStreamPos = this.cache.length());
        this.flushBefore(this.maxStreamPos);
        super.close();
        this.cache.close();
        this.cache = null;
        this.cacheFile.delete();
        this.cacheFile = null;
        this.stream.flush();
        this.stream = null;
        StreamCloser.removeFromQueue(this.closeAction);
    }
    
    @Override
    public void flushBefore(final long n) throws IOException {
        final long flushedPos = this.flushedPos;
        super.flushBefore(n);
        long n2 = this.flushedPos - flushedPos;
        if (n2 > 0L) {
            final int n3 = 512;
            final byte[] array = new byte[n3];
            this.cache.seek(flushedPos);
            while (n2 > 0L) {
                final int n4 = (int)Math.min(n2, n3);
                this.cache.readFully(array, 0, n4);
                this.stream.write(array, 0, n4);
                n2 -= n4;
            }
            this.stream.flush();
        }
    }
}
