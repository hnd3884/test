package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public final class PackedDataOutput
{
    final DataOutput out;
    long current;
    int remainingBits;
    
    public PackedDataOutput(final DataOutput out) {
        this.out = out;
        this.current = 0L;
        this.remainingBits = 8;
    }
    
    public void writeLong(final long value, int bitsPerValue) throws IOException {
        assert value >= 0L && value <= PackedInts.maxValue(bitsPerValue);
        while (bitsPerValue > 0) {
            if (this.remainingBits == 0) {
                this.out.writeByte((byte)this.current);
                this.current = 0L;
                this.remainingBits = 8;
            }
            final int bits = Math.min(this.remainingBits, bitsPerValue);
            this.current |= (value >>> bitsPerValue - bits & (1L << bits) - 1L) << this.remainingBits - bits;
            bitsPerValue -= bits;
            this.remainingBits -= bits;
        }
    }
    
    public void flush() throws IOException {
        if (this.remainingBits < 8) {
            this.out.writeByte((byte)this.current);
        }
        this.remainingBits = 8;
        this.current = 0L;
    }
}
