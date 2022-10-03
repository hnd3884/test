package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;

final class DirectPacked64SingleBlockReader extends PackedInts.ReaderImpl
{
    private final IndexInput in;
    private final int bitsPerValue;
    private final long startPointer;
    private final int valuesPerBlock;
    private final long mask;
    
    DirectPacked64SingleBlockReader(final int bitsPerValue, final int valueCount, final IndexInput in) {
        super(valueCount);
        this.in = in;
        this.bitsPerValue = bitsPerValue;
        this.startPointer = in.getFilePointer();
        this.valuesPerBlock = 64 / bitsPerValue;
        this.mask = ~(-1L << bitsPerValue);
    }
    
    @Override
    public long get(final int index) {
        final int blockOffset = index / this.valuesPerBlock;
        final long skip = (long)blockOffset << 3;
        try {
            this.in.seek(this.startPointer + skip);
            final long block = this.in.readLong();
            final int offsetInBlock = index % this.valuesPerBlock;
            return block >>> offsetInBlock * this.bitsPerValue & this.mask;
        }
        catch (final IOException e) {
            throw new IllegalStateException("failed", e);
        }
    }
    
    @Override
    public long ramBytesUsed() {
        return 0L;
    }
}
