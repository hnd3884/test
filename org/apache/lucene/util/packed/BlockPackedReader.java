package org.apache.lucene.util.packed;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.LongValues;

public final class BlockPackedReader extends LongValues implements Accountable
{
    private final int blockShift;
    private final int blockMask;
    private final long valueCount;
    private final long[] minValues;
    private final PackedInts.Reader[] subReaders;
    private final long sumBPV;
    
    public BlockPackedReader(final IndexInput in, final int packedIntsVersion, final int blockSize, final long valueCount, final boolean direct) throws IOException {
        this.valueCount = valueCount;
        this.blockShift = PackedInts.checkBlockSize(blockSize, 64, 134217728);
        this.blockMask = blockSize - 1;
        final int numBlocks = PackedInts.numBlocks(valueCount, blockSize);
        long[] minValues = null;
        this.subReaders = new PackedInts.Reader[numBlocks];
        long sumBPV = 0L;
        for (int i = 0; i < numBlocks; ++i) {
            final int token = in.readByte() & 0xFF;
            final int bitsPerValue = token >>> 1;
            sumBPV += bitsPerValue;
            if (bitsPerValue > 64) {
                throw new IOException("Corrupted");
            }
            if ((token & 0x1) == 0x0) {
                if (minValues == null) {
                    minValues = new long[numBlocks];
                }
                minValues[i] = BitUtil.zigZagDecode(1L + BlockPackedReaderIterator.readVLong(in));
            }
            if (bitsPerValue == 0) {
                this.subReaders[i] = new PackedInts.NullReader(blockSize);
            }
            else {
                final int size = (int)Math.min(blockSize, valueCount - i * (long)blockSize);
                if (direct) {
                    final long pointer = in.getFilePointer();
                    this.subReaders[i] = PackedInts.getDirectReaderNoHeader(in, PackedInts.Format.PACKED, packedIntsVersion, size, bitsPerValue);
                    in.seek(pointer + PackedInts.Format.PACKED.byteCount(packedIntsVersion, size, bitsPerValue));
                }
                else {
                    this.subReaders[i] = PackedInts.getReaderNoHeader(in, PackedInts.Format.PACKED, packedIntsVersion, size, bitsPerValue);
                }
            }
        }
        this.minValues = minValues;
        this.sumBPV = sumBPV;
    }
    
    @Override
    public long get(final long index) {
        assert index >= 0L && index < this.valueCount;
        final int block = (int)(index >>> this.blockShift);
        final int idx = (int)(index & (long)this.blockMask);
        return ((this.minValues == null) ? 0L : this.minValues[block]) + this.subReaders[block].get(idx);
    }
    
    @Override
    public long ramBytesUsed() {
        long size = 0L;
        for (final PackedInts.Reader reader : this.subReaders) {
            size += reader.ramBytesUsed();
        }
        return size;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    @Override
    public String toString() {
        final long avgBPV = (this.subReaders.length == 0) ? 0L : (this.sumBPV / this.subReaders.length);
        return this.getClass().getSimpleName() + "(blocksize=" + (1 << this.blockShift) + ",size=" + this.valueCount + ",avgBPV=" + avgBPV + ")";
    }
}
