package org.apache.lucene.util.packed;

final class BulkOperationPackedSingleBlock extends BulkOperation
{
    private static final int BLOCK_COUNT = 1;
    private final int bitsPerValue;
    private final int valueCount;
    private final long mask;
    
    public BulkOperationPackedSingleBlock(final int bitsPerValue) {
        this.bitsPerValue = bitsPerValue;
        this.valueCount = 64 / bitsPerValue;
        this.mask = (1L << bitsPerValue) - 1L;
    }
    
    @Override
    public final int longBlockCount() {
        return 1;
    }
    
    @Override
    public final int byteBlockCount() {
        return 8;
    }
    
    @Override
    public int longValueCount() {
        return this.valueCount;
    }
    
    @Override
    public final int byteValueCount() {
        return this.valueCount;
    }
    
    private static long readLong(final byte[] blocks, int blocksOffset) {
        return ((long)blocks[blocksOffset++] & 0xFFL) << 56 | ((long)blocks[blocksOffset++] & 0xFFL) << 48 | ((long)blocks[blocksOffset++] & 0xFFL) << 40 | ((long)blocks[blocksOffset++] & 0xFFL) << 32 | ((long)blocks[blocksOffset++] & 0xFFL) << 24 | ((long)blocks[blocksOffset++] & 0xFFL) << 16 | ((long)blocks[blocksOffset++] & 0xFFL) << 8 | ((long)blocks[blocksOffset++] & 0xFFL);
    }
    
    private int decode(long block, final long[] values, int valuesOffset) {
        values[valuesOffset++] = (block & this.mask);
        for (int j = 1; j < this.valueCount; ++j) {
            block >>>= this.bitsPerValue;
            values[valuesOffset++] = (block & this.mask);
        }
        return valuesOffset;
    }
    
    private int decode(long block, final int[] values, int valuesOffset) {
        values[valuesOffset++] = (int)(block & this.mask);
        for (int j = 1; j < this.valueCount; ++j) {
            block >>>= this.bitsPerValue;
            values[valuesOffset++] = (int)(block & this.mask);
        }
        return valuesOffset;
    }
    
    private long encode(final long[] values, int valuesOffset) {
        long block = values[valuesOffset++];
        for (int j = 1; j < this.valueCount; ++j) {
            block |= values[valuesOffset++] << j * this.bitsPerValue;
        }
        return block;
    }
    
    private long encode(final int[] values, int valuesOffset) {
        long block = (long)values[valuesOffset++] & 0xFFFFFFFFL;
        for (int j = 1; j < this.valueCount; ++j) {
            block |= ((long)values[valuesOffset++] & 0xFFFFFFFFL) << j * this.bitsPerValue;
        }
        return block;
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = readLong(blocks, blocksOffset);
            blocksOffset += 8;
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        for (int i = 0; i < iterations; ++i) {
            final long block = readLong(blocks, blocksOffset);
            blocksOffset += 8;
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }
    
    @Override
    public void encode(final long[] values, int valuesOffset, final long[] blocks, int blocksOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            blocks[blocksOffset++] = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
        }
    }
    
    @Override
    public void encode(final int[] values, int valuesOffset, final long[] blocks, int blocksOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            blocks[blocksOffset++] = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
        }
    }
    
    @Override
    public void encode(final long[] values, int valuesOffset, final byte[] blocks, int blocksOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
            blocksOffset = this.writeLong(block, blocks, blocksOffset);
        }
    }
    
    @Override
    public void encode(final int[] values, int valuesOffset, final byte[] blocks, int blocksOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
            blocksOffset = this.writeLong(block, blocks, blocksOffset);
        }
    }
}
