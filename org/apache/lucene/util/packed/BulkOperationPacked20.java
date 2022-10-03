package org.apache.lucene.util.packed;

final class BulkOperationPacked20 extends BulkOperationPacked
{
    public BulkOperationPacked20() {
        super(20);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 44);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0xFFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 16 | block2 >>> 48);
            values[valuesOffset++] = (int)(block2 >>> 28 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0xFFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 12 | block3 >>> 52);
            values[valuesOffset++] = (int)(block3 >>> 32 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0xFFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFFL) << 8 | block4 >>> 56);
            values[valuesOffset++] = (int)(block4 >>> 36 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 16 & 0xFFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFFFFL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 40 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block5 & 0xFFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 12 | byte2 << 4 | byte3 >>> 4);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xF) << 16 | byte4 << 8 | byte5);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 44;
            values[valuesOffset++] = (block0 >>> 24 & 0xFFFFFL);
            values[valuesOffset++] = (block0 >>> 4 & 0xFFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 16 | block2 >>> 48);
            values[valuesOffset++] = (block2 >>> 28 & 0xFFFFFL);
            values[valuesOffset++] = (block2 >>> 8 & 0xFFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0xFFL) << 12 | block3 >>> 52);
            values[valuesOffset++] = (block3 >>> 32 & 0xFFFFFL);
            values[valuesOffset++] = (block3 >>> 12 & 0xFFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0xFFFL) << 8 | block4 >>> 56);
            values[valuesOffset++] = (block4 >>> 36 & 0xFFFFFL);
            values[valuesOffset++] = (block4 >>> 16 & 0xFFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFFFFL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (block5 >>> 40 & 0xFFFFFL);
            values[valuesOffset++] = (block5 >>> 20 & 0xFFFFFL);
            values[valuesOffset++] = (block5 & 0xFFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 12 | byte2 << 4 | byte3 >>> 4);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xFL) << 16 | byte4 << 8 | byte5);
        }
    }
}
