package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class UCSReader extends Reader
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final short UCS2LE = 1;
    public static final short UCS2BE = 2;
    public static final short UCS4LE = 4;
    public static final short UCS4BE = 8;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected final short fEncoding;
    
    public UCSReader(final InputStream inputStream, final short n) {
        this(inputStream, 8192, n);
    }
    
    public UCSReader(final InputStream inputStream, final int n, final short n2) {
        this(inputStream, new byte[n], n2);
    }
    
    public UCSReader(final InputStream fInputStream, final byte[] fBuffer, final short fEncoding) {
        this.fInputStream = fInputStream;
        this.fBuffer = fBuffer;
        this.fEncoding = fEncoding;
    }
    
    public int read() throws IOException {
        final int n = this.fInputStream.read() & 0xFF;
        if (n == 255) {
            return -1;
        }
        final int n2 = this.fInputStream.read() & 0xFF;
        if (n2 == 255) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            final int n3 = this.fInputStream.read() & 0xFF;
            if (n3 == 255) {
                return -1;
            }
            final int n4 = this.fInputStream.read() & 0xFF;
            if (n4 == 255) {
                return -1;
            }
            if (this.fEncoding == 8) {
                return (n << 24) + (n2 << 16) + (n3 << 8) + n4;
            }
            return (n4 << 24) + (n3 << 16) + (n2 << 8) + n;
        }
        else {
            if (this.fEncoding == 2) {
                return (n << 8) + n2;
            }
            return (n2 << 8) + n;
        }
    }
    
    public int read(final char[] array, final int n, final int n2) throws IOException {
        int length = n2 << ((this.fEncoding >= 4) ? 2 : 1);
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        int read = this.fInputStream.read(this.fBuffer, 0, length);
        if (read == -1) {
            return -1;
        }
        if (this.fEncoding >= 4) {
            final int n3 = 4 - (read & 0x3) & 0x3;
            for (int i = 0; i < n3; ++i) {
                final int read2 = this.fInputStream.read();
                if (read2 == -1) {
                    for (int j = i; j < n3; ++j) {
                        this.fBuffer[read + j] = 0;
                    }
                    break;
                }
                this.fBuffer[read + i] = (byte)read2;
            }
            read += n3;
        }
        else if ((read & 0x1) != 0x0) {
            ++read;
            final int read3 = this.fInputStream.read();
            if (read3 == -1) {
                this.fBuffer[read] = 0;
            }
            else {
                this.fBuffer[read] = (byte)read3;
            }
        }
        final int n4 = read >> ((this.fEncoding >= 4) ? 2 : 1);
        int n5 = 0;
        for (int k = 0; k < n4; ++k) {
            final int n6 = this.fBuffer[n5++] & 0xFF;
            final int n7 = this.fBuffer[n5++] & 0xFF;
            if (this.fEncoding >= 4) {
                final int n8 = this.fBuffer[n5++] & 0xFF;
                final int n9 = this.fBuffer[n5++] & 0xFF;
                if (this.fEncoding == 8) {
                    array[n + k] = (char)((n6 << 24) + (n7 << 16) + (n8 << 8) + n9);
                }
                else {
                    array[n + k] = (char)((n9 << 24) + (n8 << 16) + (n7 << 8) + n6);
                }
            }
            else if (this.fEncoding == 2) {
                array[n + k] = (char)((n6 << 8) + n7);
            }
            else {
                array[n + k] = (char)((n7 << 8) + n6);
            }
        }
        return n4;
    }
    
    public long skip(final long n) throws IOException {
        final int n2 = (this.fEncoding >= 4) ? 2 : 1;
        final long skip = this.fInputStream.skip(n << n2);
        if ((skip & (long)(n2 | 0x1)) == 0x0L) {
            return skip >> n2;
        }
        return (skip >> n2) + 1L;
    }
    
    public boolean ready() throws IOException {
        return false;
    }
    
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }
    
    public void mark(final int n) throws IOException {
        this.fInputStream.mark(n);
    }
    
    public void reset() throws IOException {
        this.fInputStream.reset();
    }
    
    public void close() throws IOException {
        this.fInputStream.close();
    }
}
