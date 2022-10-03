package org.apache.lucene.util.packed;

final class BulkOperationPacked4 extends BulkOperationPacked
{
    public BulkOperationPacked4() {
        super(4);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 60; shift >= 0; shift -= 4) {
                values[valuesOffset++] = (int)(block >>> shift & 0xFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 4 & 0xF);
            values[valuesOffset++] = (block & 0xF);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 60; shift >= 0; shift -= 4) {
                values[valuesOffset++] = (block >>> shift & 0xFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            final byte block = blocks[blocksOffset++];
            values[valuesOffset++] = (block >>> 4 & 0xF);
            values[valuesOffset++] = (block & 0xF);
        }
    }
}
