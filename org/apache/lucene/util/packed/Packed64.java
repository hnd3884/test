package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

class Packed64 extends PackedInts.MutableImpl
{
    static final int BLOCK_SIZE = 64;
    static final int BLOCK_BITS = 6;
    static final int MOD_MASK = 63;
    private final long[] blocks;
    private final long maskRight;
    private final int bpvMinusBlockSize;
    
    public Packed64(final int valueCount, final int bitsPerValue) {
        super(valueCount, bitsPerValue);
        final PackedInts.Format format = PackedInts.Format.PACKED;
        final int longCount = format.longCount(2, valueCount, bitsPerValue);
        this.blocks = new long[longCount];
        this.maskRight = -1L << 64 - bitsPerValue >>> 64 - bitsPerValue;
        this.bpvMinusBlockSize = bitsPerValue - 64;
    }
    
    public Packed64(final int packedIntsVersion, final DataInput in, final int valueCount, final int bitsPerValue) throws IOException {
        super(valueCount, bitsPerValue);
        final PackedInts.Format format = PackedInts.Format.PACKED;
        final long byteCount = format.byteCount(packedIntsVersion, valueCount, bitsPerValue);
        final int longCount = format.longCount(2, valueCount, bitsPerValue);
        this.blocks = new long[longCount];
        for (int i = 0; i < byteCount / 8L; ++i) {
            this.blocks[i] = in.readLong();
        }
        final int remaining = (int)(byteCount % 8L);
        if (remaining != 0) {
            long lastLong = 0L;
            for (int j = 0; j < remaining; ++j) {
                lastLong |= ((long)in.readByte() & 0xFFL) << 56 - j * 8;
            }
            this.blocks[this.blocks.length - 1] = lastLong;
        }
        this.maskRight = -1L << 64 - bitsPerValue >>> 64 - bitsPerValue;
        this.bpvMinusBlockSize = bitsPerValue - 64;
    }
    
    @Override
    public long get(final int index) {
        final long majorBitPos = index * (long)this.bitsPerValue;
        final int elementPos = (int)(majorBitPos >>> 6);
        final long endBits = (majorBitPos & 0x3FL) + this.bpvMinusBlockSize;
        if (endBits <= 0L) {
            return this.blocks[elementPos] >>> (int)(-endBits) & this.maskRight;
        }
        return (this.blocks[elementPos] << (int)endBits | this.blocks[elementPos + 1] >>> (int)(64L - endBits)) & this.maskRight;
    }
    
    @Override
    public int get(int index, final long[] arr, int off, int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        len = Math.min(len, this.valueCount - index);
        assert off + len <= arr.length;
        final int originalIndex = index;
        final PackedInts.Decoder decoder = BulkOperation.of(PackedInts.Format.PACKED, this.bitsPerValue);
        final int offsetInBlocks = index % decoder.longValueCount();
        if (offsetInBlocks != 0) {
            for (int i = offsetInBlocks; i < decoder.longValueCount() && len > 0; --len, ++i) {
                arr[off++] = this.get(index++);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert index % decoder.longValueCount() == 0;
        final int blockIndex = (int)(index * (long)this.bitsPerValue >>> 6);
        assert (index * (long)this.bitsPerValue & 0x3FL) == 0x0L;
        final int iterations = len / decoder.longValueCount();
        decoder.decode(this.blocks, blockIndex, arr, off, iterations);
        final int gotValues = iterations * decoder.longValueCount();
        index += gotValues;
        len -= gotValues;
        assert len >= 0;
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert index == originalIndex;
        return super.get(index, arr, off, len);
    }
    
    @Override
    public void set(final int index, final long value) {
        final long majorBitPos = index * (long)this.bitsPerValue;
        final int elementPos = (int)(majorBitPos >>> 6);
        final long endBits = (majorBitPos & 0x3FL) + this.bpvMinusBlockSize;
        if (endBits <= 0L) {
            this.blocks[elementPos] = ((this.blocks[elementPos] & ~(this.maskRight << (int)(-endBits))) | value << (int)(-endBits));
            return;
        }
        this.blocks[elementPos] = ((this.blocks[elementPos] & ~(this.maskRight >>> (int)endBits)) | value >>> (int)endBits);
        this.blocks[elementPos + 1] = ((this.blocks[elementPos + 1] & -1L >>> (int)endBits) | value << (int)(64L - endBits));
    }
    
    @Override
    public int set(int index, final long[] arr, int off, int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        len = Math.min(len, this.valueCount - index);
        assert off + len <= arr.length;
        final int originalIndex = index;
        final PackedInts.Encoder encoder = BulkOperation.of(PackedInts.Format.PACKED, this.bitsPerValue);
        final int offsetInBlocks = index % encoder.longValueCount();
        if (offsetInBlocks != 0) {
            for (int i = offsetInBlocks; i < encoder.longValueCount() && len > 0; --len, ++i) {
                this.set(index++, arr[off++]);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert index % encoder.longValueCount() == 0;
        final int blockIndex = (int)(index * (long)this.bitsPerValue >>> 6);
        assert (index * (long)this.bitsPerValue & 0x3FL) == 0x0L;
        final int iterations = len / encoder.longValueCount();
        encoder.encode(arr, off, this.blocks, blockIndex, iterations);
        final int setValues = iterations * encoder.longValueCount();
        index += setValues;
        len -= setValues;
        assert len >= 0;
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert index == originalIndex;
        return super.set(index, arr, off, len);
    }
    
    @Override
    public String toString() {
        return "Packed64(bitsPerValue=" + this.bitsPerValue + ",size=" + this.size() + ",blocks=" + this.blocks.length + ")";
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 12 + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }
    
    @Override
    public void fill(int fromIndex, final int toIndex, final long val) {
        assert PackedInts.unsignedBitsRequired(val) <= this.getBitsPerValue();
        assert fromIndex <= toIndex;
        final int nAlignedValues = 64 / gcd(64, this.bitsPerValue);
        final int span = toIndex - fromIndex;
        if (span <= 3 * nAlignedValues) {
            super.fill(fromIndex, toIndex, val);
            return;
        }
        final int fromIndexModNAlignedValues = fromIndex % nAlignedValues;
        if (fromIndexModNAlignedValues != 0) {
            for (int i = fromIndexModNAlignedValues; i < nAlignedValues; ++i) {
                this.set(fromIndex++, val);
            }
        }
        assert fromIndex % nAlignedValues == 0;
        final int nAlignedBlocks = nAlignedValues * this.bitsPerValue >> 6;
        final Packed64 values = new Packed64(nAlignedValues, this.bitsPerValue);
        for (int j = 0; j < nAlignedValues; ++j) {
            values.set(j, val);
        }
        final long[] nAlignedValuesBlocks = values.blocks;
        assert nAlignedBlocks <= nAlignedValuesBlocks.length;
        final int startBlock = (int)(fromIndex * (long)this.bitsPerValue >>> 6);
        final int endBlock = (int)(toIndex * (long)this.bitsPerValue >>> 6);
        for (int block = startBlock; block < endBlock; ++block) {
            final long blockValue = nAlignedValuesBlocks[block % nAlignedBlocks];
            this.blocks[block] = blockValue;
        }
        for (int k = (int)(((long)endBlock << 6) / this.bitsPerValue); k < toIndex; ++k) {
            this.set(k, val);
        }
    }
    
    private static int gcd(final int a, final int b) {
        if (a < b) {
            return gcd(b, a);
        }
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.blocks, 0L);
    }
}
