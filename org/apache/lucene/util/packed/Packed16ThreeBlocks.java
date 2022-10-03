package org.apache.lucene.util.packed;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

final class Packed16ThreeBlocks extends PackedInts.MutableImpl
{
    final short[] blocks;
    public static final int MAX_SIZE = 715827882;
    
    Packed16ThreeBlocks(final int valueCount) {
        super(valueCount, 48);
        if (valueCount > 715827882) {
            throw new ArrayIndexOutOfBoundsException("MAX_SIZE exceeded");
        }
        this.blocks = new short[valueCount * 3];
    }
    
    Packed16ThreeBlocks(final int packedIntsVersion, final DataInput in, final int valueCount) throws IOException {
        this(valueCount);
        for (int i = 0; i < 3 * valueCount; ++i) {
            this.blocks[i] = in.readShort();
        }
        for (int remaining = (int)(PackedInts.Format.PACKED.byteCount(packedIntsVersion, valueCount, 48) - 3L * valueCount * 2L), j = 0; j < remaining; ++j) {
            in.readByte();
        }
    }
    
    @Override
    public long get(final int index) {
        final int o = index * 3;
        return ((long)this.blocks[o] & 0xFFFFL) << 32 | ((long)this.blocks[o + 1] & 0xFFFFL) << 16 | ((long)this.blocks[o + 2] & 0xFFFFL);
    }
    
    @Override
    public int get(final int index, final long[] arr, int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int gets = Math.min(this.valueCount - index, len);
        for (int i = index * 3, end = (index + gets) * 3; i < end; i += 3) {
            arr[off++] = (((long)this.blocks[i] & 0xFFFFL) << 32 | ((long)this.blocks[i + 1] & 0xFFFFL) << 16 | ((long)this.blocks[i + 2] & 0xFFFFL));
        }
        return gets;
    }
    
    @Override
    public void set(final int index, final long value) {
        final int o = index * 3;
        this.blocks[o] = (short)(value >>> 32);
        this.blocks[o + 1] = (short)(value >>> 16);
        this.blocks[o + 2] = (short)value;
    }
    
    @Override
    public int set(final int index, final long[] arr, final int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int sets = Math.min(this.valueCount - index, len);
        int i = off;
        int o = index * 3;
        for (int end = off + sets; i < end; ++i) {
            final long value = arr[i];
            this.blocks[o++] = (short)(value >>> 32);
            this.blocks[o++] = (short)(value >>> 16);
            this.blocks[o++] = (short)value;
        }
        return sets;
    }
    
    @Override
    public void fill(final int fromIndex, final int toIndex, final long val) {
        final short block1 = (short)(val >>> 32);
        final short block2 = (short)(val >>> 16);
        final short block3 = (short)val;
        for (int i = fromIndex * 3, end = toIndex * 3; i < end; i += 3) {
            this.blocks[i] = block1;
            this.blocks[i + 1] = block2;
            this.blocks[i + 2] = block3;
        }
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.blocks, (short)0);
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(bitsPerValue=" + this.bitsPerValue + ",size=" + this.size() + ",blocks=" + this.blocks.length + ")";
    }
}
