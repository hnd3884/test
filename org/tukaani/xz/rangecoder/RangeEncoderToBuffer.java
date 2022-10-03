package org.tukaani.xz.rangecoder;

import java.io.OutputStream;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;

public final class RangeEncoderToBuffer extends RangeEncoder
{
    private final byte[] buf;
    private int bufPos;
    
    public RangeEncoderToBuffer(final int n, final ArrayCache arrayCache) {
        this.buf = arrayCache.getByteArray(n, false);
        this.reset();
    }
    
    public void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.buf);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.bufPos = 0;
    }
    
    @Override
    public int getPendingSize() {
        return this.bufPos + (int)this.cacheSize + 5 - 1;
    }
    
    @Override
    public int finish() {
        try {
            super.finish();
        }
        catch (final IOException ex) {
            throw new Error();
        }
        return this.bufPos;
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.bufPos);
    }
    
    @Override
    void writeByte(final int n) {
        this.buf[this.bufPos++] = (byte)n;
    }
}
