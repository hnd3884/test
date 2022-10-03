package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public final class MonotonicBlockPackedWriter extends AbstractBlockPackedWriter
{
    public MonotonicBlockPackedWriter(final DataOutput out, final int blockSize) {
        super(out, blockSize);
    }
    
    @Override
    public void add(final long l) throws IOException {
        assert l >= 0L;
        super.add(l);
    }
    
    @Override
    protected void flush() throws IOException {
        assert this.off > 0;
        final float avg = (this.off == 1) ? 0.0f : ((this.values[this.off - 1] - this.values[0]) / (float)(this.off - 1));
        long min = this.values[0];
        for (int i = 1; i < this.off; ++i) {
            final long actual = this.values[i];
            final long expected = MonotonicBlockPackedReader.expected(min, avg, i);
            if (expected > actual) {
                min -= expected - actual;
            }
        }
        long maxDelta = 0L;
        for (int j = 0; j < this.off; ++j) {
            this.values[j] -= MonotonicBlockPackedReader.expected(min, avg, j);
            maxDelta = Math.max(maxDelta, this.values[j]);
        }
        this.out.writeZLong(min);
        this.out.writeInt(Float.floatToIntBits(avg));
        if (maxDelta == 0L) {
            this.out.writeVInt(0);
        }
        else {
            final int bitsRequired = PackedInts.bitsRequired(maxDelta);
            this.out.writeVInt(bitsRequired);
            this.writeValues(bitsRequired);
        }
        this.off = 0;
    }
}
