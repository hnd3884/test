package org.apache.lucene.util.packed;

final class BulkOperationPacked24 extends BulkOperationPacked
{
    public BulkOperationPacked24() {
        super(24);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 40);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0xFFFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0xFFFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0xFFFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 16 | block3 >>> 48);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0xFFFFFFL);
            values[valuesOffset++] = (int)(block3 & 0xFFFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 16 | byte2 << 8 | byte3);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 40;
            values[valuesOffset++] = (block0 >>> 16 & 0xFFFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFFFFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (block2 >>> 32 & 0xFFFFFFL);
            values[valuesOffset++] = (block2 >>> 8 & 0xFFFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0xFFL) << 16 | block3 >>> 48);
            values[valuesOffset++] = (block3 >>> 24 & 0xFFFFFFL);
            values[valuesOffset++] = (block3 & 0xFFFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 16 | byte2 << 8 | byte3);
        }
    }
}
