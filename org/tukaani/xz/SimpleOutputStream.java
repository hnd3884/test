package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleOutputStream extends FinishableOutputStream
{
    private static final int FILTER_BUF_SIZE = 4096;
    private FinishableOutputStream out;
    private final SimpleFilter simpleFilter;
    private final byte[] filterBuf;
    private int pos;
    private int unfiltered;
    private IOException exception;
    private boolean finished;
    private final byte[] tempBuf;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    SimpleOutputStream(final FinishableOutputStream out, final SimpleFilter simpleFilter) {
        this.filterBuf = new byte[4096];
        this.pos = 0;
        this.unfiltered = 0;
        this.exception = null;
        this.finished = false;
        this.tempBuf = new byte[1];
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
        this.simpleFilter = simpleFilter;
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
            throw new XZIOException("Stream finished or closed");
        }
        while (i > 0) {
            final int min = Math.min(i, 4096 - (this.pos + this.unfiltered));
            System.arraycopy(array, n, this.filterBuf, this.pos + this.unfiltered, min);
            n += min;
            i -= min;
            this.unfiltered += min;
            final int code = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);
            assert code <= this.unfiltered;
            this.unfiltered -= code;
            try {
                this.out.write(this.filterBuf, this.pos, code);
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
            this.pos += code;
            if (this.pos + this.unfiltered != 4096) {
                continue;
            }
            System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.unfiltered);
            this.pos = 0;
        }
    }
    
    private void writePending() throws IOException {
        assert !this.finished;
        if (this.exception != null) {
            throw this.exception;
        }
        try {
            this.out.write(this.filterBuf, this.pos, this.unfiltered);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
        this.finished = true;
    }
    
    @Override
    public void flush() throws IOException {
        throw new UnsupportedOptionsException("Flushing is not supported");
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            this.writePending();
            try {
                this.out.finish();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            if (!this.finished) {
                try {
                    this.writePending();
                }
                catch (final IOException ex) {}
            }
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
