package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.delta.DeltaDecoder;
import java.io.InputStream;

public class DeltaInputStream extends InputStream
{
    public static final int DISTANCE_MIN = 1;
    public static final int DISTANCE_MAX = 256;
    private InputStream in;
    private final DeltaDecoder delta;
    private IOException exception;
    private final byte[] tempBuf;
    
    public DeltaInputStream(final InputStream in, final int n) {
        this.exception = null;
        this.tempBuf = new byte[1];
        if (in == null) {
            throw new NullPointerException();
        }
        this.in = in;
        this.delta = new DeltaDecoder(n);
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        int read;
        try {
            read = this.in.read(array, n, n2);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
        if (read == -1) {
            return -1;
        }
        this.delta.decode(array, n, read);
        return read;
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.in.available();
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
