package org.tukaani.xz.lz;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.ArrayCache;

public final class LZDecoder
{
    private final byte[] buf;
    private final int bufSize;
    private int start;
    private int pos;
    private int full;
    private int limit;
    private int pendingLen;
    private int pendingDist;
    
    public LZDecoder(final int bufSize, final byte[] array, final ArrayCache arrayCache) {
        this.start = 0;
        this.pos = 0;
        this.full = 0;
        this.limit = 0;
        this.pendingLen = 0;
        this.pendingDist = 0;
        this.bufSize = bufSize;
        this.buf = arrayCache.getByteArray(this.bufSize, false);
        if (array != null) {
            this.pos = Math.min(array.length, bufSize);
            this.full = this.pos;
            this.start = this.pos;
            System.arraycopy(array, array.length - this.pos, this.buf, 0, this.pos);
        }
    }
    
    public void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.buf);
    }
    
    public void reset() {
        this.start = 0;
        this.pos = 0;
        this.full = 0;
        this.limit = 0;
        this.buf[this.bufSize - 1] = 0;
    }
    
    public void setLimit(final int n) {
        if (this.bufSize - this.pos <= n) {
            this.limit = this.bufSize;
        }
        else {
            this.limit = this.pos + n;
        }
    }
    
    public boolean hasSpace() {
        return this.pos < this.limit;
    }
    
    public boolean hasPending() {
        return this.pendingLen > 0;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public int getByte(final int n) {
        int n2 = this.pos - n - 1;
        if (n >= this.pos) {
            n2 += this.bufSize;
        }
        return this.buf[n2] & 0xFF;
    }
    
    public void putByte(final byte b) {
        this.buf[this.pos++] = b;
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public void repeat(final int pendingDist, final int n) throws IOException {
        if (pendingDist < 0 || pendingDist >= this.full) {
            throw new CorruptedInputException();
        }
        int i = Math.min(this.limit - this.pos, n);
        this.pendingLen = n - i;
        this.pendingDist = pendingDist;
        int n2 = this.pos - pendingDist - 1;
        if (n2 < 0) {
            assert this.full == this.bufSize;
            final int n3 = n2 + this.bufSize;
            final int min = Math.min(this.bufSize - n3, i);
            assert min <= pendingDist + 1;
            System.arraycopy(this.buf, n3, this.buf, this.pos, min);
            this.pos += min;
            n2 = 0;
            i -= min;
            if (i == 0) {
                return;
            }
        }
        assert n2 < this.pos;
        assert i > 0;
        do {
            final int min2 = Math.min(i, this.pos - n2);
            System.arraycopy(this.buf, n2, this.buf, this.pos, min2);
            this.pos += min2;
            i -= min2;
        } while (i > 0);
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public void repeatPending() throws IOException {
        if (this.pendingLen > 0) {
            this.repeat(this.pendingDist, this.pendingLen);
        }
    }
    
    public void copyUncompressed(final DataInputStream dataInputStream, final int n) throws IOException {
        final int min = Math.min(this.bufSize - this.pos, n);
        dataInputStream.readFully(this.buf, this.pos, min);
        this.pos += min;
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public int flush(final byte[] array, final int n) {
        final int n2 = this.pos - this.start;
        if (this.pos == this.bufSize) {
            this.pos = 0;
        }
        System.arraycopy(this.buf, this.start, array, n, n2);
        this.start = this.pos;
        return n2;
    }
}
