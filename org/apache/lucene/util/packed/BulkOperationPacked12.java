package org.apache.lucene.util.packed;

final class BulkOperationPacked12 extends BulkOperationPacked
{
    public BulkOperationPacked12() {
        super(12);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 52);
            values[valuesOffset++] = (int)(block0 >>> 40 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0xFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (int)(block2 >>> 44 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 20 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0xFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 48 & 0xFFFL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0xFFFL);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0xFFFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0xFFFL);
            values[valuesOffset++] = (int)(block3 & 0xFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 4 | byte2 >>> 4);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0xF) << 8 | byte3);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 52;
            values[valuesOffset++] = (block0 >>> 40 & 0xFFFL);
            values[valuesOffset++] = (block0 >>> 28 & 0xFFFL);
            values[valuesOffset++] = (block0 >>> 16 & 0xFFFL);
            values[valuesOffset++] = (block0 >>> 4 & 0xFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (block2 >>> 44 & 0xFFFL);
            values[valuesOffset++] = (block2 >>> 32 & 0xFFFL);
            values[valuesOffset++] = (block2 >>> 20 & 0xFFFL);
            values[valuesOffset++] = (block2 >>> 8 & 0xFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0xFFL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (block3 >>> 48 & 0xFFFL);
            values[valuesOffset++] = (block3 >>> 36 & 0xFFFL);
            values[valuesOffset++] = (block3 >>> 24 & 0xFFFL);
            values[valuesOffset++] = (block3 >>> 12 & 0xFFFL);
            values[valuesOffset++] = (block3 & 0xFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 4 | byte2 >>> 4);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0xFL) << 8 | byte3);
        }
    }
}
