package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.store.DataOutput;

public final class BlockPackedWriter extends AbstractBlockPackedWriter
{
    public BlockPackedWriter(final DataOutput out, final int blockSize) {
        super(out, blockSize);
    }
    
    @Override
    protected void flush() throws IOException {
        assert this.off > 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < this.off; ++i) {
            min = Math.min(this.values[i], min);
            max = Math.max(this.values[i], max);
        }
        final long delta = max - min;
        final int bitsRequired = (delta == 0L) ? 0 : PackedInts.unsignedBitsRequired(delta);
        if (bitsRequired == 64) {
            min = 0L;
        }
        else if (min > 0L) {
            min = Math.max(0L, max - PackedInts.maxValue(bitsRequired));
        }
        final int token = bitsRequired << 1 | ((min == 0L) ? 1 : 0);
        this.out.writeByte((byte)token);
        if (min != 0L) {
            AbstractBlockPackedWriter.writeVLong(this.out, BitUtil.zigZagEncode(min) - 1L);
        }
        if (bitsRequired > 0) {
            if (min != 0L) {
                for (int j = 0; j < this.off; ++j) {
                    final long[] values = this.values;
                    final int n = j;
                    values[n] -= min;
                }
            }
            this.writeValues(bitsRequired);
        }
        this.off = 0;
    }
}
