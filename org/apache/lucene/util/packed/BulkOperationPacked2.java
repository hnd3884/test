package org.apache.lucene.util.packed;

final class BulkOperationPacked2 extends BulkOperationPacked
{
    public BulkOperationPacked2() {
        super(2);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 62; shift >= 0; shift -= 2) {
                values[valuesOffset++] = (int)(block >>> shift & 0x3L);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 6 & 0x3);
            values[valuesOffset++] = (block >>> 4 & 0x3);
            values[valuesOffset++] = (block >>> 2 & 0x3);
            values[valuesOffset++] = (block & 0x3);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 62; shift >= 0; shift -= 2) {
                values[valuesOffset++] = (block >>> shift & 0x3L);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 6 & 0x3);
            values[valuesOffset++] = (block >>> 4 & 0x3);
            values[valuesOffset++] = (block >>> 2 & 0x3);
            values[valuesOffset++] = (block & 0x3);
        }
    }
}
