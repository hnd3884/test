package sun.net.www;

import sun.net.www.http.ChunkedInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.net.ProgressSource;
import java.io.FilterInputStream;

public class MeteredStream extends FilterInputStream
{
    protected boolean closed;
    protected long expected;
    protected long count;
    protected long markedCount;
    protected int markLimit;
    protected ProgressSource pi;
    
    public MeteredStream(final InputStream inputStream, final ProgressSource pi, final long expected) {
        super(inputStream);
        this.closed = false;
        this.count = 0L;
        this.markedCount = 0L;
        this.markLimit = -1;
        this.pi = pi;
        this.expected = expected;
        if (pi != null) {
            pi.updateProgress(0L, expected);
        }
    }
    
    private final void justRead(final long n) throws IOException {
        if (n == -1L) {
            if (!this.isMarked()) {
                this.close();
            }
            return;
        }
        this.count += n;
        if (this.count - this.markedCount > this.markLimit) {
            this.markLimit = -1;
        }
        if (this.pi != null) {
            this.pi.updateProgress(this.count, this.expected);
        }
        if (this.isMarked()) {
            return;
        }
        if (this.expected > 0L && this.count >= this.expected) {
            this.close();
        }
    }
    
    private boolean isMarked() {
        return this.markLimit >= 0 && this.count - this.markedCount <= this.markLimit;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        final int read = this.in.read();
        if (read != -1) {
            this.justRead(1L);
        }
        else {
            this.justRead(read);
        }
        return read;
    }
    
    @Override
    public synchronized int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            return -1;
        }
        final int read = this.in.read(array, n, n2);
        this.justRead(read);
        return read;
    }
    
    @Override
    public synchronized long skip(long n) throws IOException {
        if (this.closed) {
            return 0L;
        }
        if (this.in instanceof ChunkedInputStream) {
            n = this.in.skip(n);
        }
        else {
            n = this.in.skip((n > this.expected - this.count) ? (this.expected - this.count) : n);
        }
        this.justRead(n);
        return n;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.pi != null) {
            this.pi.finishTracking();
        }
        this.closed = true;
        this.in.close();
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.closed ? 0 : this.in.available();
    }
    
    @Override
    public synchronized void mark(final int markLimit) {
        if (this.closed) {
            return;
        }
        super.mark(markLimit);
        this.markedCount = this.count;
        this.markLimit = markLimit;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        if (this.closed) {
            return;
        }
        if (!this.isMarked()) {
            throw new IOException("Resetting to an invalid mark");
        }
        this.count = this.markedCount;
        super.reset();
    }
    
    @Override
    public boolean markSupported() {
        return !this.closed && super.markSupported();
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
            if (this.pi != null) {
                this.pi.close();
            }
        }
        finally {
            super.finalize();
        }
    }
}
