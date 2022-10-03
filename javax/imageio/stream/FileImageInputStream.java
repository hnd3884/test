package javax.imageio.stream;

import com.sun.imageio.stream.StreamFinalizer;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.io.Closeable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import com.sun.imageio.stream.CloseableDisposerRecord;
import java.io.RandomAccessFile;

public class FileImageInputStream extends ImageInputStreamImpl
{
    private RandomAccessFile raf;
    private final Object disposerReferent;
    private final CloseableDisposerRecord disposerRecord;
    
    public FileImageInputStream(final File file) throws FileNotFoundException, IOException {
        this((file == null) ? null : new RandomAccessFile(file, "r"));
    }
    
    public FileImageInputStream(final RandomAccessFile raf) {
        if (raf == null) {
            throw new IllegalArgumentException("raf == null!");
        }
        this.raf = raf;
        this.disposerRecord = new CloseableDisposerRecord(raf);
        if (this.getClass() == FileImageInputStream.class) {
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
        final int read = this.raf.read();
        if (read != -1) {
            ++this.streamPos;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        this.checkClosed();
        this.bitOffset = 0;
        final int read = this.raf.read(array, n, n2);
        if (read != -1) {
            this.streamPos += read;
        }
        return read;
    }
    
    @Override
    public long length() {
        try {
            this.checkClosed();
            return this.raf.length();
        }
        catch (final IOException ex) {
            return -1L;
        }
    }
    
    @Override
    public void seek(final long n) throws IOException {
        this.checkClosed();
        if (n < this.flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }
        this.bitOffset = 0;
        this.raf.seek(n);
        this.streamPos = this.raf.getFilePointer();
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.disposerRecord.dispose();
        this.raf = null;
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
}
