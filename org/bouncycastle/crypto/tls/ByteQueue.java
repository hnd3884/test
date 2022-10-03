package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteQueue
{
    private static final int DEFAULT_CAPACITY = 1024;
    private byte[] databuf;
    private int skipped;
    private int available;
    private boolean readOnlyBuf;
    
    public static int nextTwoPow(int n) {
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
    
    public ByteQueue() {
        this(1024);
    }
    
    public ByteQueue(final int n) {
        this.skipped = 0;
        this.available = 0;
        this.readOnlyBuf = false;
        this.databuf = ((n == 0) ? TlsUtils.EMPTY_BYTES : new byte[n]);
    }
    
    public ByteQueue(final byte[] databuf, final int skipped, final int available) {
        this.skipped = 0;
        this.available = 0;
        this.readOnlyBuf = false;
        this.databuf = databuf;
        this.skipped = skipped;
        this.available = available;
        this.readOnlyBuf = true;
    }
    
    public void addData(final byte[] array, final int n, final int n2) {
        if (this.readOnlyBuf) {
            throw new IllegalStateException("Cannot add data to read-only buffer");
        }
        if (this.skipped + this.available + n2 > this.databuf.length) {
            final int nextTwoPow = nextTwoPow(this.available + n2);
            if (nextTwoPow > this.databuf.length) {
                final byte[] databuf = new byte[nextTwoPow];
                System.arraycopy(this.databuf, this.skipped, databuf, 0, this.available);
                this.databuf = databuf;
            }
            else {
                System.arraycopy(this.databuf, this.skipped, this.databuf, 0, this.available);
            }
            this.skipped = 0;
        }
        System.arraycopy(array, n, this.databuf, this.skipped + this.available, n2);
        this.available += n2;
    }
    
    public int available() {
        return this.available;
    }
    
    public void copyTo(final OutputStream outputStream, final int n) throws IOException {
        if (n > this.available) {
            throw new IllegalStateException("Cannot copy " + n + " bytes, only got " + this.available);
        }
        outputStream.write(this.databuf, this.skipped, n);
    }
    
    public void read(final byte[] array, final int n, final int n2, final int n3) {
        if (array.length - n < n2) {
            throw new IllegalArgumentException("Buffer size of " + array.length + " is too small for a read of " + n2 + " bytes");
        }
        if (this.available - n3 < n2) {
            throw new IllegalStateException("Not enough data to read");
        }
        System.arraycopy(this.databuf, this.skipped + n3, array, n, n2);
    }
    
    public ByteArrayInputStream readFrom(final int n) {
        if (n > this.available) {
            throw new IllegalStateException("Cannot read " + n + " bytes, only got " + this.available);
        }
        final int skipped = this.skipped;
        this.available -= n;
        this.skipped += n;
        return new ByteArrayInputStream(this.databuf, skipped, n);
    }
    
    public void removeData(final int n) {
        if (n > this.available) {
            throw new IllegalStateException("Cannot remove " + n + " bytes, only got " + this.available);
        }
        this.available -= n;
        this.skipped += n;
    }
    
    public void removeData(final byte[] array, final int n, final int n2, final int n3) {
        this.read(array, n, n2, n3);
        this.removeData(n3 + n2);
    }
    
    public byte[] removeData(final int n, final int n2) {
        final byte[] array = new byte[n];
        this.removeData(array, 0, n, n2);
        return array;
    }
    
    public void shrink() {
        if (this.available == 0) {
            this.databuf = TlsUtils.EMPTY_BYTES;
            this.skipped = 0;
        }
        else {
            final int nextTwoPow = nextTwoPow(this.available);
            if (nextTwoPow < this.databuf.length) {
                final byte[] databuf = new byte[nextTwoPow];
                System.arraycopy(this.databuf, this.skipped, databuf, 0, this.available);
                this.databuf = databuf;
                this.skipped = 0;
            }
        }
    }
}
