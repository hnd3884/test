package org.tukaani.xz.rangecoder;

import java.io.IOException;
import org.tukaani.xz.CorruptedInputException;
import java.io.DataInputStream;
import org.tukaani.xz.ArrayCache;

public final class RangeDecoderFromBuffer extends RangeDecoder
{
    private static final int INIT_SIZE = 5;
    private final byte[] buf;
    private int pos;
    
    public RangeDecoderFromBuffer(final int n, final ArrayCache arrayCache) {
        this.buf = arrayCache.getByteArray(n - 5, false);
        this.pos = this.buf.length;
    }
    
    public void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.buf);
    }
    
    public void prepareInputBuffer(final DataInputStream dataInputStream, int n) throws IOException {
        if (n < 5) {
            throw new CorruptedInputException();
        }
        if (dataInputStream.readUnsignedByte() != 0) {
            throw new CorruptedInputException();
        }
        this.code = dataInputStream.readInt();
        this.range = -1;
        n -= 5;
        this.pos = this.buf.length - n;
        dataInputStream.readFully(this.buf, this.pos, n);
    }
    
    public boolean isFinished() {
        return this.pos == this.buf.length && this.code == 0;
    }
    
    @Override
    public void normalize() throws IOException {
        if ((this.range & 0xFF000000) == 0x0) {
            try {
                this.code = (this.code << 8 | (this.buf[this.pos++] & 0xFF));
                this.range <<= 8;
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                throw new CorruptedInputException();
            }
        }
    }
}
