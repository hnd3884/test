package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataInput;

public final class PackedDataInput
{
    final DataInput in;
    long current;
    int remainingBits;
    
    public PackedDataInput(final DataInput in) {
        this.in = in;
        this.skipToNextByte();
    }
    
    public long readLong(int bitsPerValue) throws IOException {
        assert bitsPerValue > 0 && bitsPerValue <= 64 : bitsPerValue;
        long r = 0L;
        while (bitsPerValue > 0) {
            if (this.remainingBits == 0) {
                this.current = (this.in.readByte() & 0xFF);
                this.remainingBits = 8;
            }
            final int bits = Math.min(bitsPerValue, this.remainingBits);
            r = (r << bits | (this.current >>> this.remainingBits - bits & (1L << bits) - 1L));
            bitsPerValue -= bits;
            this.remainingBits -= bits;
        }
        return r;
    }
    
    public void skipToNextByte() {
        this.remainingBits = 0;
    }
}
