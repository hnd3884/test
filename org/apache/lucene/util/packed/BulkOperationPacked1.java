package org.apache.lucene.util.packed;

final class BulkOperationPacked1 extends BulkOperationPacked
{
    public BulkOperationPacked1() {
        super(1);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 63; shift >= 0; --shift) {
                values[valuesOffset++] = (int)(block >>> shift & 0x1L);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 7 & 0x1);
            values[valuesOffset++] = (block >>> 6 & 0x1);
            values[valuesOffset++] = (block >>> 5 & 0x1);
            values[valuesOffset++] = (block >>> 4 & 0x1);
            values[valuesOffset++] = (block >>> 3 & 0x1);
            values[valuesOffset++] = (block >>> 2 & 0x1);
            values[valuesOffset++] = (block >>> 1 & 0x1);
            values[valuesOffset++] = (block & 0x1);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 63; shift >= 0; --shift) {
                values[valuesOffset++] = (block >>> shift & 0x1L);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 7 & 0x1);
            values[valuesOffset++] = (block >>> 6 & 0x1);
            values[valuesOffset++] = (block >>> 5 & 0x1);
            values[valuesOffset++] = (block >>> 4 & 0x1);
            values[valuesOffset++] = (block >>> 3 & 0x1);
            values[valuesOffset++] = (block >>> 2 & 0x1);
            values[valuesOffset++] = (block >>> 1 & 0x1);
            values[valuesOffset++] = (block & 0x1);
        }
    }
}
