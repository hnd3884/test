package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Arrays;

abstract class Packed64SingleBlock extends PackedInts.MutableImpl
{
    public static final int MAX_SUPPORTED_BITS_PER_VALUE = 32;
    private static final int[] SUPPORTED_BITS_PER_VALUE;
    final long[] blocks;
    
    public static boolean isSupported(final int bitsPerValue) {
        return Arrays.binarySearch(Packed64SingleBlock.SUPPORTED_BITS_PER_VALUE, bitsPerValue) >= 0;
    }
    
    private static int requiredCapacity(final int valueCount, final int valuesPerBlock) {
        return valueCount / valuesPerBlock + ((valueCount % valuesPerBlock != 0) ? 1 : 0);
    }
    
    Packed64SingleBlock(final int valueCount, final int bitsPerValue) {
        super(valueCount, bitsPerValue);
        assert isSupported(bitsPerValue);
        final int valuesPerBlock = 64 / bitsPerValue;
        this.blocks = new long[requiredCapacity(valueCount, valuesPerBlock)];
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.blocks, 0L);
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }
    
    @Override
    public int get(int index, final long[] arr, int off, int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        len = Math.min(len, this.valueCount - index);
        assert off + len <= arr.length;
        final int originalIndex = index;
        final int valuesPerBlock = 64 / this.bitsPerValue;
        final int offsetInBlock = index % valuesPerBlock;
        if (offsetInBlock != 0) {
            for (int i = offsetInBlock; i < valuesPerBlock && len > 0; --len, ++i) {
                arr[off++] = this.get(index++);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert index % valuesPerBlock == 0;
        final PackedInts.Decoder decoder = BulkOperation.of(PackedInts.Format.PACKED_SINGLE_BLOCK, this.bitsPerValue);
        assert decoder.longBlockCount() == 1;
        assert decoder.longValueCount() == valuesPerBlock;
        final int blockIndex = index / valuesPerBlock;
        final int nblocks = (index + len) / valuesPerBlock - blockIndex;
        decoder.decode(this.blocks, blockIndex, arr, off, nblocks);
        final int diff = nblocks * valuesPerBlock;
        index += diff;
        len -= diff;
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert index == originalIndex;
        return super.get(index, arr, off, len);
    }
    
    @Override
    public int set(int index, final long[] arr, int off, int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        len = Math.min(len, this.valueCount - index);
        assert off + len <= arr.length;
        final int originalIndex = index;
        final int valuesPerBlock = 64 / this.bitsPerValue;
        final int offsetInBlock = index % valuesPerBlock;
        if (offsetInBlock != 0) {
            for (int i = offsetInBlock; i < valuesPerBlock && len > 0; --len, ++i) {
                this.set(index++, arr[off++]);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert index % valuesPerBlock == 0;
        final BulkOperation op = BulkOperation.of(PackedInts.Format.PACKED_SINGLE_BLOCK, this.bitsPerValue);
        assert op.longBlockCount() == 1;
        assert op.longValueCount() == valuesPerBlock;
        final int blockIndex = index / valuesPerBlock;
        final int nblocks = (index + len) / valuesPerBlock - blockIndex;
        op.encode(arr, off, this.blocks, blockIndex, nblocks);
        final int diff = nblocks * valuesPerBlock;
        index += diff;
        len -= diff;
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert index == originalIndex;
        return super.set(index, arr, off, len);
    }
    
    @Override
    public void fill(int fromIndex, final int toIndex, final long val) {
        assert fromIndex >= 0;
        assert fromIndex <= toIndex;
        assert PackedInts.unsignedBitsRequired(val) <= this.bitsPerValue;
        final int valuesPerBlock = 64 / this.bitsPerValue;
        if (toIndex - fromIndex <= valuesPerBlock << 1) {
            super.fill(fromIndex, toIndex, val);
            return;
        }
        final int fromOffsetInBlock = fromIndex % valuesPerBlock;
        if (fromOffsetInBlock != 0) {
            for (int i = fromOffsetInBlock; i < valuesPerBlock; ++i) {
                this.set(fromIndex++, val);
            }
            assert fromIndex % valuesPerBlock == 0;
        }
        final int fromBlock = fromIndex / valuesPerBlock;
        final int toBlock = toIndex / valuesPerBlock;
        assert fromBlock * valuesPerBlock == fromIndex;
        long blockValue = 0L;
        for (int j = 0; j < valuesPerBlock; ++j) {
            blockValue |= val << j * this.bitsPerValue;
        }
        Arrays.fill(this.blocks, fromBlock, toBlock, blockValue);
        for (int j = valuesPerBlock * toBlock; j < toIndex; ++j) {
            this.set(j, val);
        }
    }
    
    protected PackedInts.Format getFormat() {
        return PackedInts.Format.PACKED_SINGLE_BLOCK;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(bitsPerValue=" + this.bitsPerValue + ",size=" + this.size() + ",blocks=" + this.blocks.length + ")";
    }
    
    public static Packed64SingleBlock create(final DataInput in, final int valueCount, final int bitsPerValue) throws IOException {
        final Packed64SingleBlock reader = create(valueCount, bitsPerValue);
        for (int i = 0; i < reader.blocks.length; ++i) {
            reader.blocks[i] = in.readLong();
        }
        return reader;
    }
    
    public static Packed64SingleBlock create(final int valueCount, final int bitsPerValue) {
        switch (bitsPerValue) {
            case 1: {
                return new Packed64SingleBlock1(valueCount);
            }
            case 2: {
                return new Packed64SingleBlock2(valueCount);
            }
            case 3: {
                return new Packed64SingleBlock3(valueCount);
            }
            case 4: {
                return new Packed64SingleBlock4(valueCount);
            }
            case 5: {
                return new Packed64SingleBlock5(valueCount);
            }
            case 6: {
                return new Packed64SingleBlock6(valueCount);
            }
            case 7: {
                return new Packed64SingleBlock7(valueCount);
            }
            case 8: {
                return new Packed64SingleBlock8(valueCount);
            }
            case 9: {
                return new Packed64SingleBlock9(valueCount);
            }
            case 10: {
                return new Packed64SingleBlock10(valueCount);
            }
            case 12: {
                return new Packed64SingleBlock12(valueCount);
            }
            case 16: {
                return new Packed64SingleBlock16(valueCount);
            }
            case 21: {
                return new Packed64SingleBlock21(valueCount);
            }
            case 32: {
                return new Packed64SingleBlock32(valueCount);
            }
            default: {
                throw new IllegalArgumentException("Unsupported number of bits per value: 32");
            }
        }
    }
    
    static {
        SUPPORTED_BITS_PER_VALUE = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 16, 21, 32 };
    }
    
    static class Packed64SingleBlock1 extends Packed64SingleBlock
    {
        Packed64SingleBlock1(final int valueCount) {
            super(valueCount, 1);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 6;
            final int b = index & 0x3F;
            final int shift = b << 0;
            return this.blocks[o] >>> shift & 0x1L;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 6;
            final int b = index & 0x3F;
            final int shift = b << 0;
            this.blocks[o] = ((this.blocks[o] & ~(1L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock2 extends Packed64SingleBlock
    {
        Packed64SingleBlock2(final int valueCount) {
            super(valueCount, 2);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 5;
            final int b = index & 0x1F;
            final int shift = b << 1;
            return this.blocks[o] >>> shift & 0x3L;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 5;
            final int b = index & 0x1F;
            final int shift = b << 1;
            this.blocks[o] = ((this.blocks[o] & ~(3L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock3 extends Packed64SingleBlock
    {
        Packed64SingleBlock3(final int valueCount) {
            super(valueCount, 3);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 21;
            final int b = index % 21;
            final int shift = b * 3;
            return this.blocks[o] >>> shift & 0x7L;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 21;
            final int b = index % 21;
            final int shift = b * 3;
            this.blocks[o] = ((this.blocks[o] & ~(7L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock4 extends Packed64SingleBlock
    {
        Packed64SingleBlock4(final int valueCount) {
            super(valueCount, 4);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 4;
            final int b = index & 0xF;
            final int shift = b << 2;
            return this.blocks[o] >>> shift & 0xFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 4;
            final int b = index & 0xF;
            final int shift = b << 2;
            this.blocks[o] = ((this.blocks[o] & ~(15L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock5 extends Packed64SingleBlock
    {
        Packed64SingleBlock5(final int valueCount) {
            super(valueCount, 5);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 12;
            final int b = index % 12;
            final int shift = b * 5;
            return this.blocks[o] >>> shift & 0x1FL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 12;
            final int b = index % 12;
            final int shift = b * 5;
            this.blocks[o] = ((this.blocks[o] & ~(31L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock6 extends Packed64SingleBlock
    {
        Packed64SingleBlock6(final int valueCount) {
            super(valueCount, 6);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 10;
            final int b = index % 10;
            final int shift = b * 6;
            return this.blocks[o] >>> shift & 0x3FL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 10;
            final int b = index % 10;
            final int shift = b * 6;
            this.blocks[o] = ((this.blocks[o] & ~(63L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock7 extends Packed64SingleBlock
    {
        Packed64SingleBlock7(final int valueCount) {
            super(valueCount, 7);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 9;
            final int b = index % 9;
            final int shift = b * 7;
            return this.blocks[o] >>> shift & 0x7FL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 9;
            final int b = index % 9;
            final int shift = b * 7;
            this.blocks[o] = ((this.blocks[o] & ~(127L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock8 extends Packed64SingleBlock
    {
        Packed64SingleBlock8(final int valueCount) {
            super(valueCount, 8);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 3;
            final int b = index & 0x7;
            final int shift = b << 3;
            return this.blocks[o] >>> shift & 0xFFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 3;
            final int b = index & 0x7;
            final int shift = b << 3;
            this.blocks[o] = ((this.blocks[o] & ~(255L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock9 extends Packed64SingleBlock
    {
        Packed64SingleBlock9(final int valueCount) {
            super(valueCount, 9);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 7;
            final int b = index % 7;
            final int shift = b * 9;
            return this.blocks[o] >>> shift & 0x1FFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 7;
            final int b = index % 7;
            final int shift = b * 9;
            this.blocks[o] = ((this.blocks[o] & ~(511L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock10 extends Packed64SingleBlock
    {
        Packed64SingleBlock10(final int valueCount) {
            super(valueCount, 10);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 6;
            final int b = index % 6;
            final int shift = b * 10;
            return this.blocks[o] >>> shift & 0x3FFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 6;
            final int b = index % 6;
            final int shift = b * 10;
            this.blocks[o] = ((this.blocks[o] & ~(1023L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock12 extends Packed64SingleBlock
    {
        Packed64SingleBlock12(final int valueCount) {
            super(valueCount, 12);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 5;
            final int b = index % 5;
            final int shift = b * 12;
            return this.blocks[o] >>> shift & 0xFFFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 5;
            final int b = index % 5;
            final int shift = b * 12;
            this.blocks[o] = ((this.blocks[o] & ~(4095L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock16 extends Packed64SingleBlock
    {
        Packed64SingleBlock16(final int valueCount) {
            super(valueCount, 16);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 2;
            final int b = index & 0x3;
            final int shift = b << 4;
            return this.blocks[o] >>> shift & 0xFFFFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 2;
            final int b = index & 0x3;
            final int shift = b << 4;
            this.blocks[o] = ((this.blocks[o] & ~(65535L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock21 extends Packed64SingleBlock
    {
        Packed64SingleBlock21(final int valueCount) {
            super(valueCount, 21);
        }
        
        @Override
        public long get(final int index) {
            final int o = index / 3;
            final int b = index % 3;
            final int shift = b * 21;
            return this.blocks[o] >>> shift & 0x1FFFFFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index / 3;
            final int b = index % 3;
            final int shift = b * 21;
            this.blocks[o] = ((this.blocks[o] & ~(2097151L << shift)) | value << shift);
        }
    }
    
    static class Packed64SingleBlock32 extends Packed64SingleBlock
    {
        Packed64SingleBlock32(final int valueCount) {
            super(valueCount, 32);
        }
        
        @Override
        public long get(final int index) {
            final int o = index >>> 1;
            final int b = index & 0x1;
            final int shift = b << 5;
            return this.blocks[o] >>> shift & 0xFFFFFFFFL;
        }
        
        @Override
        public void set(final int index, final long value) {
            final int o = index >>> 1;
            final int b = index & 0x1;
            final int shift = b << 5;
            this.blocks[o] = ((this.blocks[o] & ~(4294967295L << shift)) | value << shift);
        }
    }
}
