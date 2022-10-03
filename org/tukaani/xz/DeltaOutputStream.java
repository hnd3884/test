package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.delta.DeltaEncoder;

class DeltaOutputStream extends FinishableOutputStream
{
    private static final int FILTER_BUF_SIZE = 4096;
    private FinishableOutputStream out;
    private final DeltaEncoder delta;
    private final byte[] filterBuf;
    private boolean finished;
    private IOException exception;
    private final byte[] tempBuf;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    DeltaOutputStream(final FinishableOutputStream out, final DeltaOptions deltaOptions) {
        this.filterBuf = new byte[4096];
        this.finished = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        this.out = out;
        this.delta = new DeltaEncoder(deltaOptions.getDistance());
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.tempBuf[0] = (byte)n;
        this.write(this.tempBuf, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished");
        }
        try {
            while (i > 4096) {
                this.delta.encode(array, n, 4096, this.filterBuf);
                this.out.write(this.filterBuf);
                n += 4096;
                i -= 4096;
            }
            this.delta.encode(array, n, i, this.filterBuf);
            this.out.write(this.filterBuf, 0, i);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            this.out.flush();
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            if (this.exception != null) {
                throw this.exception;
            }
            try {
                this.out.finish();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
            this.finished = true;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            try {
                this.out.close();
            }
            catch (final IOException exception) {
                if (this.exception == null) {
                    this.exception = exception;
                }
            }
            this.out = null;
        }
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
