package javax.swing;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Component;
import java.io.FilterInputStream;

public class ProgressMonitorInputStream extends FilterInputStream
{
    private ProgressMonitor monitor;
    private int nread;
    private int size;
    
    public ProgressMonitorInputStream(final Component component, final Object o, final InputStream inputStream) {
        super(inputStream);
        this.nread = 0;
        this.size = 0;
        try {
            this.size = inputStream.available();
        }
        catch (final IOException ex) {
            this.size = 0;
        }
        this.monitor = new ProgressMonitor(component, o, null, 0, this.size);
    }
    
    public ProgressMonitor getProgressMonitor() {
        return this.monitor;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read >= 0) {
            this.monitor.setProgress(++this.nread);
        }
        if (this.monitor.isCanceled()) {
            final InterruptedIOException ex = new InterruptedIOException("progress");
            ex.bytesTransferred = this.nread;
            throw ex;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        final int read = this.in.read(array);
        if (read > 0) {
            this.monitor.setProgress(this.nread += read);
        }
        if (this.monitor.isCanceled()) {
            final InterruptedIOException ex = new InterruptedIOException("progress");
            ex.bytesTransferred = this.nread;
            throw ex;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.in.read(array, n, n2);
        if (read > 0) {
            this.monitor.setProgress(this.nread += read);
        }
        if (this.monitor.isCanceled()) {
            final InterruptedIOException ex = new InterruptedIOException("progress");
            ex.bytesTransferred = this.nread;
            throw ex;
        }
        return read;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long skip = this.in.skip(n);
        if (skip > 0L) {
            this.monitor.setProgress(this.nread += (int)skip);
        }
        return skip;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
        this.monitor.close();
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
        this.nread = this.size - this.in.available();
        this.monitor.setProgress(this.nread);
    }
}
