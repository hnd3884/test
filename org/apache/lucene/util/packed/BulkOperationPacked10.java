package org.apache.lucene.util.packed;

final class BulkOperationPacked10 extends BulkOperationPacked
{
    public BulkOperationPacked10() {
        super(10);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 54);
            values[valuesOffset++] = (int)(block0 >>> 44 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 14 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x3FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 6 | block2 >>> 58);
            values[valuesOffset++] = (int)(block2 >>> 48 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 28 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 18 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x3FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 2 | block3 >>> 62);
            values[valuesOffset++] = (int)(block3 >>> 52 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 42 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 32 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 2 & 0x3FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3L) << 8 | block4 >>> 56);
            values[valuesOffset++] = (int)(block4 >>> 46 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 36 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 26 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 16 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 6 & 0x3FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x3FL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 50 & 0x3FFL);
            values[valuesOffset++] = (int)(block5 >>> 40 & 0x3FFL);
            values[valuesOffset++] = (int)(block5 >>> 30 & 0x3FFL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x3FFL);
            values[valuesOffset++] = (int)(block5 >>> 10 & 0x3FFL);
            values[valuesOffset++] = (int)(block5 & 0x3FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 2 | byte2 >>> 6);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3F) << 4 | byte3 >>> 4);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xF) << 6 | byte4 >>> 2);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3) << 8 | byte5);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 54;
            values[valuesOffset++] = (block0 >>> 44 & 0x3FFL);
            values[valuesOffset++] = (block0 >>> 34 & 0x3FFL);
            values[valuesOffset++] = (block0 >>> 24 & 0x3FFL);
            values[valuesOffset++] = (block0 >>> 14 & 0x3FFL);
            values[valuesOffset++] = (block0 >>> 4 & 0x3FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 6 | block2 >>> 58);
            values[valuesOffset++] = (block2 >>> 48 & 0x3FFL);
            values[valuesOffset++] = (block2 >>> 38 & 0x3FFL);
            values[valuesOffset++] = (block2 >>> 28 & 0x3FFL);
            values[valuesOffset++] = (block2 >>> 18 & 0x3FFL);
            values[valuesOffset++] = (block2 >>> 8 & 0x3FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0xFFL) << 2 | block3 >>> 62);
            values[valuesOffset++] = (block3 >>> 52 & 0x3FFL);
            values[valuesOffset++] = (block3 >>> 42 & 0x3FFL);
            values[valuesOffset++] = (block3 >>> 32 & 0x3FFL);
            values[valuesOffset++] = (block3 >>> 22 & 0x3FFL);
            values[valuesOffset++] = (block3 >>> 12 & 0x3FFL);
            values[valuesOffset++] = (block3 >>> 2 & 0x3FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x3L) << 8 | block4 >>> 56);
            values[valuesOffset++] = (block4 >>> 46 & 0x3FFL);
            values[valuesOffset++] = (block4 >>> 36 & 0x3FFL);
            values[valuesOffset++] = (block4 >>> 26 & 0x3FFL);
            values[valuesOffset++] = (block4 >>> 16 & 0x3FFL);
            values[valuesOffset++] = (block4 >>> 6 & 0x3FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x3FL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (block5 >>> 50 & 0x3FFL);
            values[valuesOffset++] = (block5 >>> 40 & 0x3FFL);
            values[valuesOffset++] = (block5 >>> 30 & 0x3FFL);
            values[valuesOffset++] = (block5 >>> 20 & 0x3FFL);
            values[valuesOffset++] = (block5 >>> 10 & 0x3FFL);
            values[valuesOffset++] = (block5 & 0x3FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 2 | byte2 >>> 6);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3FL) << 4 | byte3 >>> 4);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xFL) << 6 | byte4 >>> 2);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3L) << 8 | byte5);
        }
    }
}
