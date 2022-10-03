package org.apache.lucene.util.packed;

final class BulkOperationPacked8 extends BulkOperationPacked
{
    public BulkOperationPacked8() {
        super(8);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 56; shift >= 0; shift -= 8) {
                values[valuesOffset++] = (int)(block >>> shift & 0xFFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = (blocks[blocksOffset++] & 0xFF);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 56; shift >= 0; shift -= 8) {
                values[valuesOffset++] = (block >>> shift & 0xFFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = (blocks[blocksOffset++] & 0xFF);
        }
    }
}
