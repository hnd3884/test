package org.apache.lucene.util.packed;

final class BulkOperationPacked5 extends BulkOperationPacked
{
    public BulkOperationPacked5() {
        super(5);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 59);
            values[valuesOffset++] = (int)(block0 >>> 54 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 49 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 44 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 39 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 29 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 14 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 9 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x1FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 1 | block2 >>> 63);
            values[valuesOffset++] = (int)(block2 >>> 58 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 53 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 48 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 43 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 33 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 28 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 18 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 13 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 3 & 0x1FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x7L) << 2 | block3 >>> 62);
            values[valuesOffset++] = (int)(block3 >>> 57 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 52 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 47 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 42 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 37 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 32 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 27 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 17 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 7 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 2 & 0x1FL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3L) << 3 | block4 >>> 61);
            values[valuesOffset++] = (int)(block4 >>> 56 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 51 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 46 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 41 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 36 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 31 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 26 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 21 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 16 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 11 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 6 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 1 & 0x1FL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1L) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 55 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 50 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 45 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 40 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 35 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 30 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 25 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 15 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 10 & 0x1FL);
            values[valuesOffset++] = (int)(block5 >>> 5 & 0x1FL);
            values[valuesOffset++] = (int)(block5 & 0x1FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 3;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x7) << 2 | byte2 >>> 6);
            values[valuesOffset++] = (byte2 >>> 1 & 0x1F);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1) << 4 | byte3 >>> 4);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xF) << 1 | byte4 >>> 7);
            values[valuesOffset++] = (byte4 >>> 2 & 0x1F);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3) << 3 | byte5 >>> 5);
            values[valuesOffset++] = (byte5 & 0x1F);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 59;
            values[valuesOffset++] = (block0 >>> 54 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 49 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 44 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 39 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 34 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 29 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 24 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 19 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 14 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 9 & 0x1FL);
            values[valuesOffset++] = (block0 >>> 4 & 0x1FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 1 | block2 >>> 63);
            values[valuesOffset++] = (block2 >>> 58 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 53 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 48 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 43 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 38 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 33 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 28 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 23 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 18 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 13 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 8 & 0x1FL);
            values[valuesOffset++] = (block2 >>> 3 & 0x1FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x7L) << 2 | block3 >>> 62);
            values[valuesOffset++] = (block3 >>> 57 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 52 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 47 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 42 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 37 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 32 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 27 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 22 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 17 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 12 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 7 & 0x1FL);
            values[valuesOffset++] = (block3 >>> 2 & 0x1FL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x3L) << 3 | block4 >>> 61);
            values[valuesOffset++] = (block4 >>> 56 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 51 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 46 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 41 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 36 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 31 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 26 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 21 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 16 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 11 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 6 & 0x1FL);
            values[valuesOffset++] = (block4 >>> 1 & 0x1FL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x1L) << 4 | block5 >>> 60);
            values[valuesOffset++] = (block5 >>> 55 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 50 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 45 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 40 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 35 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 30 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 25 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 20 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 15 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 10 & 0x1FL);
            values[valuesOffset++] = (block5 >>> 5 & 0x1FL);
            values[valuesOffset++] = (block5 & 0x1FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 3;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x7L) << 2 | byte2 >>> 6);
            values[valuesOffset++] = (byte2 >>> 1 & 0x1FL);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1L) << 4 | byte3 >>> 4);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0xFL) << 1 | byte4 >>> 7);
            values[valuesOffset++] = (byte4 >>> 2 & 0x1FL);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3L) << 3 | byte5 >>> 5);
            values[valuesOffset++] = (byte5 & 0x1FL);
        }
    }
}
