package org.apache.lucene.util.packed;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.LongValues;

public class MonotonicBlockPackedReader extends LongValues implements Accountable
{
    final int blockShift;
    final int blockMask;
    final long valueCount;
    final long[] minValues;
    final float[] averages;
    final PackedInts.Reader[] subReaders;
    final long sumBPV;
    
    static long expected(final long origin, final float average, final int index) {
        return origin + (long)(average * index);
    }
    
    public static MonotonicBlockPackedReader of(final IndexInput in, final int packedIntsVersion, final int blockSize, final long valueCount, final boolean direct) throws IOException {
        if (packedIntsVersion < 2) {
            return new MonotonicBlockPackedReader(in, packedIntsVersion, blockSize, valueCount, direct) {
                @Override
                protected long decodeDelta(final long delta) {
                    return BitUtil.zigZagDecode(delta);
                }
            };
        }
        return new MonotonicBlockPackedReader(in, packedIntsVersion, blockSize, valueCount, direct);
    }
    
    private MonotonicBlockPackedReader(final IndexInput in, final int packedIntsVersion, final int blockSize, final long valueCount, final boolean direct) throws IOException {
        this.valueCount = valueCount;
        this.blockShift = PackedInts.checkBlockSize(blockSize, 64, 134217728);
        this.blockMask = blockSize - 1;
        final int numBlocks = PackedInts.numBlocks(valueCount, blockSize);
        this.minValues = new long[numBlocks];
        this.averages = new float[numBlocks];
        this.subReaders = new PackedInts.Reader[numBlocks];
        long sumBPV = 0L;
        for (int i = 0; i < numBlocks; ++i) {
            if (packedIntsVersion < 2) {
                this.minValues[i] = in.readVLong();
            }
            else {
                this.minValues[i] = in.readZLong();
            }
            this.averages[i] = Float.intBitsToFloat(in.readInt());
            final int bitsPerValue = in.readVInt();
            sumBPV += bitsPerValue;
            if (bitsPerValue > 64) {
                throw new IOException("Corrupted");
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
        this.sumBPV = sumBPV;
    }
    
    @Override
    public long get(final long index) {
        assert index >= 0L && index < this.valueCount;
        final int block = (int)(index >>> this.blockShift);
        final int idx = (int)(index & (long)this.blockMask);
        return expected(this.minValues[block], this.averages[block], idx) + this.decodeDelta(this.subReaders[block].get(idx));
    }
    
    protected long decodeDelta(final long delta) {
        return delta;
    }
    
    public long size() {
        return this.valueCount;
    }
    
    @Override
    public long ramBytesUsed() {
        long sizeInBytes = 0L;
        sizeInBytes += RamUsageEstimator.sizeOf(this.minValues);
        sizeInBytes += RamUsageEstimator.sizeOf(this.averages);
        for (final PackedInts.Reader reader : this.subReaders) {
            sizeInBytes += reader.ramBytesUsed();
        }
        return sizeInBytes;
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
