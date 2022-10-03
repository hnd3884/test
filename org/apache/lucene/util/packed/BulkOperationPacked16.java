package org.apache.lucene.util.packed;

final class BulkOperationPacked16 extends BulkOperationPacked
{
    public BulkOperationPacked16() {
        super(16);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 48; shift >= 0; shift -= 16) {
                values[valuesOffset++] = (int)(block >>> shift & 0xFFFFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = ((blocks[blocksOffset++] & 0xFF) << 8 | (blocks[blocksOffset++] & 0xFF));
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block = blocks[blocksOffset++];
            for (int shift = 48; shift >= 0; shift -= 16) {
                values[valuesOffset++] = (block >>> shift & 0xFFFFL);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = (((long)blocks[blocksOffset++] & 0xFFL) << 8 | ((long)blocks[blocksOffset++] & 0xFFL));
        }
    }
}
