package org.apache.lucene.util.packed;

final class BulkOperationPacked3 extends BulkOperationPacked
{
    public BulkOperationPacked3() {
        super(3);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 61);
            values[valuesOffset++] = (int)(block0 >>> 58 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 55 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 52 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 49 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 46 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 43 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 40 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 37 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 31 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 25 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 13 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 7 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x7L);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x7L);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1L) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 59 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 56 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 53 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 50 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 47 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 44 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 41 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 35 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 29 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 26 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 20 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 17 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 14 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 11 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 5 & 0x7L);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x7L);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 1 | block3 >>> 63);
            values[valuesOffset++] = (int)(block3 >>> 60 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 57 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 54 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 51 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 48 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 45 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 42 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 39 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 33 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 30 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 27 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 21 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 18 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 15 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 9 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 6 & 0x7L);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x7L);
            values[valuesOffset++] = (int)(block3 & 0x7L);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 5;
            values[valuesOffset++] = (byte0 >>> 2 & 0x7);
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x3) << 1 | byte2 >>> 7);
            values[valuesOffset++] = (byte2 >>> 4 & 0x7);
            values[valuesOffset++] = (byte2 >>> 1 & 0x7);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1) << 2 | byte3 >>> 6);
            values[valuesOffset++] = (byte3 >>> 3 & 0x7);
            values[valuesOffset++] = (byte3 & 0x7);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 61;
            values[valuesOffset++] = (block0 >>> 58 & 0x7L);
            values[valuesOffset++] = (block0 >>> 55 & 0x7L);
            values[valuesOffset++] = (block0 >>> 52 & 0x7L);
            values[valuesOffset++] = (block0 >>> 49 & 0x7L);
            values[valuesOffset++] = (block0 >>> 46 & 0x7L);
            values[valuesOffset++] = (block0 >>> 43 & 0x7L);
            values[valuesOffset++] = (block0 >>> 40 & 0x7L);
            values[valuesOffset++] = (block0 >>> 37 & 0x7L);
            values[valuesOffset++] = (block0 >>> 34 & 0x7L);
            values[valuesOffset++] = (block0 >>> 31 & 0x7L);
            values[valuesOffset++] = (block0 >>> 28 & 0x7L);
            values[valuesOffset++] = (block0 >>> 25 & 0x7L);
            values[valuesOffset++] = (block0 >>> 22 & 0x7L);
            values[valuesOffset++] = (block0 >>> 19 & 0x7L);
            values[valuesOffset++] = (block0 >>> 16 & 0x7L);
            values[valuesOffset++] = (block0 >>> 13 & 0x7L);
            values[valuesOffset++] = (block0 >>> 10 & 0x7L);
            values[valuesOffset++] = (block0 >>> 7 & 0x7L);
            values[valuesOffset++] = (block0 >>> 4 & 0x7L);
            values[valuesOffset++] = (block0 >>> 1 & 0x7L);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1L) << 2 | block2 >>> 62);
            values[valuesOffset++] = (block2 >>> 59 & 0x7L);
            values[valuesOffset++] = (block2 >>> 56 & 0x7L);
            values[valuesOffset++] = (block2 >>> 53 & 0x7L);
            values[valuesOffset++] = (block2 >>> 50 & 0x7L);
            values[valuesOffset++] = (block2 >>> 47 & 0x7L);
            values[valuesOffset++] = (block2 >>> 44 & 0x7L);
            values[valuesOffset++] = (block2 >>> 41 & 0x7L);
            values[valuesOffset++] = (block2 >>> 38 & 0x7L);
            values[valuesOffset++] = (block2 >>> 35 & 0x7L);
            values[valuesOffset++] = (block2 >>> 32 & 0x7L);
            values[valuesOffset++] = (block2 >>> 29 & 0x7L);
            values[valuesOffset++] = (block2 >>> 26 & 0x7L);
            values[valuesOffset++] = (block2 >>> 23 & 0x7L);
            values[valuesOffset++] = (block2 >>> 20 & 0x7L);
            values[valuesOffset++] = (block2 >>> 17 & 0x7L);
            values[valuesOffset++] = (block2 >>> 14 & 0x7L);
            values[valuesOffset++] = (block2 >>> 11 & 0x7L);
            values[valuesOffset++] = (block2 >>> 8 & 0x7L);
            values[valuesOffset++] = (block2 >>> 5 & 0x7L);
            values[valuesOffset++] = (block2 >>> 2 & 0x7L);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 1 | block3 >>> 63);
            values[valuesOffset++] = (block3 >>> 60 & 0x7L);
            values[valuesOffset++] = (block3 >>> 57 & 0x7L);
            values[valuesOffset++] = (block3 >>> 54 & 0x7L);
            values[valuesOffset++] = (block3 >>> 51 & 0x7L);
            values[valuesOffset++] = (block3 >>> 48 & 0x7L);
            values[valuesOffset++] = (block3 >>> 45 & 0x7L);
            values[valuesOffset++] = (block3 >>> 42 & 0x7L);
            values[valuesOffset++] = (block3 >>> 39 & 0x7L);
            values[valuesOffset++] = (block3 >>> 36 & 0x7L);
            values[valuesOffset++] = (block3 >>> 33 & 0x7L);
            values[valuesOffset++] = (block3 >>> 30 & 0x7L);
            values[valuesOffset++] = (block3 >>> 27 & 0x7L);
            values[valuesOffset++] = (block3 >>> 24 & 0x7L);
            values[valuesOffset++] = (block3 >>> 21 & 0x7L);
            values[valuesOffset++] = (block3 >>> 18 & 0x7L);
            values[valuesOffset++] = (block3 >>> 15 & 0x7L);
            values[valuesOffset++] = (block3 >>> 12 & 0x7L);
            values[valuesOffset++] = (block3 >>> 9 & 0x7L);
            values[valuesOffset++] = (block3 >>> 6 & 0x7L);
            values[valuesOffset++] = (block3 >>> 3 & 0x7L);
            values[valuesOffset++] = (block3 & 0x7L);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 5;
            values[valuesOffset++] = (byte0 >>> 2 & 0x7L);
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x3L) << 1 | byte2 >>> 7);
            values[valuesOffset++] = (byte2 >>> 4 & 0x7L);
            values[valuesOffset++] = (byte2 >>> 1 & 0x7L);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1L) << 2 | byte3 >>> 6);
            values[valuesOffset++] = (byte3 >>> 3 & 0x7L);
            values[valuesOffset++] = (byte3 & 0x7L);
        }
    }
}
