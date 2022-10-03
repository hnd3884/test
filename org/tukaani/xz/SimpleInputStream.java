package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;
import java.io.InputStream;

class SimpleInputStream extends InputStream
{
    private static final int FILTER_BUF_SIZE = 4096;
    private InputStream in;
    private final SimpleFilter simpleFilter;
    private final byte[] filterBuf;
    private int pos;
    private int filtered;
    private int unfiltered;
    private boolean endReached;
    private IOException exception;
    private final byte[] tempBuf;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    SimpleInputStream(final InputStream in, final SimpleFilter simpleFilter) {
        this.filterBuf = new byte[4096];
        this.pos = 0;
        this.filtered = 0;
        this.unfiltered = 0;
        this.endReached = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        if (in == null) {
            throw new NullPointerException();
        }
        assert simpleFilter != null;
        this.in = in;
        this.simpleFilter = simpleFilter;
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, int n, int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 < 0 || n + n2 > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        try {
            int n3 = 0;
            while (true) {
                final int min = Math.min(this.filtered, n2);
                System.arraycopy(this.filterBuf, this.pos, array, n, min);
                this.pos += min;
                this.filtered -= min;
                n += min;
                n2 -= min;
                n3 += min;
                if (this.pos + this.filtered + this.unfiltered == 4096) {
                    System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.filtered + this.unfiltered);
                    this.pos = 0;
                }
                if (n2 == 0 || this.endReached) {
                    return (n3 > 0) ? n3 : -1;
                }
                assert this.filtered == 0;
                final int read = this.in.read(this.filterBuf, this.pos + this.filtered + this.unfiltered, 4096 - (this.pos + this.filtered + this.unfiltered));
                if (read == -1) {
                    this.endReached = true;
                    this.filtered = this.unfiltered;
                    this.unfiltered = 0;
                }
                else {
                    this.unfiltered += read;
                    this.filtered = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);
                    assert this.filtered <= this.unfiltered;
                    this.unfiltered -= this.filtered;
                }
            }
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.filtered;
    }
    
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
}
