package org.apache.lucene.util.packed;

final class BulkOperationPacked7 extends BulkOperationPacked
{
    public BulkOperationPacked7() {
        super(7);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 57);
            values[valuesOffset++] = (int)(block0 >>> 50 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 43 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 36 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 29 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 15 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 8 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x7FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1L) << 6 | block2 >>> 58);
            values[valuesOffset++] = (int)(block2 >>> 51 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 44 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 37 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 30 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 16 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 9 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x7FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 5 | block3 >>> 59);
            values[valuesOffset++] = (int)(block3 >>> 52 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 45 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 38 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 31 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 17 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 10 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x7FL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x7L) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 53 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 46 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 39 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 32 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 25 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 18 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 11 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 4 & 0x7FL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFL) << 3 | block5 >>> 61);
            values[valuesOffset++] = (int)(block5 >>> 54 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 47 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 40 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 33 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 26 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 19 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 12 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 5 & 0x7FL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (int)(block6 >>> 55 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 48 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 41 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 34 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 27 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 20 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 13 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x7FL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 1 | block7 >>> 63);
            values[valuesOffset++] = (int)(block7 >>> 56 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 49 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 42 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 35 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 21 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 14 & 0x7FL);
            values[valuesOffset++] = (int)(block7 >>> 7 & 0x7FL);
            values[valuesOffset++] = (int)(block7 & 0x7FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 1;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x1) << 6 | byte2 >>> 2);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3) << 5 | byte3 >>> 3);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7) << 4 | byte4 >>> 4);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0xF) << 3 | byte5 >>> 5);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x1F) << 2 | byte6 >>> 6);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3F) << 1 | byte7 >>> 7);
            values[valuesOffset++] = (byte7 & 0x7F);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 57;
            values[valuesOffset++] = (block0 >>> 50 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 43 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 36 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 29 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 22 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 15 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 8 & 0x7FL);
            values[valuesOffset++] = (block0 >>> 1 & 0x7FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1L) << 6 | block2 >>> 58);
            values[valuesOffset++] = (block2 >>> 51 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 44 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 37 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 30 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 23 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 16 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 9 & 0x7FL);
            values[valuesOffset++] = (block2 >>> 2 & 0x7FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 5 | block3 >>> 59);
            values[valuesOffset++] = (block3 >>> 52 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 45 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 38 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 31 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 24 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 17 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 10 & 0x7FL);
            values[valuesOffset++] = (block3 >>> 3 & 0x7FL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x7L) << 4 | block4 >>> 60);
            values[valuesOffset++] = (block4 >>> 53 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 46 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 39 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 32 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 25 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 18 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 11 & 0x7FL);
            values[valuesOffset++] = (block4 >>> 4 & 0x7FL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFL) << 3 | block5 >>> 61);
            values[valuesOffset++] = (block5 >>> 54 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 47 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 40 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 33 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 26 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 19 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 12 & 0x7FL);
            values[valuesOffset++] = (block5 >>> 5 & 0x7FL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1FL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (block6 >>> 55 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 48 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 41 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 34 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 27 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 20 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 13 & 0x7FL);
            values[valuesOffset++] = (block6 >>> 6 & 0x7FL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FL) << 1 | block7 >>> 63);
            values[valuesOffset++] = (block7 >>> 56 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 49 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 42 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 35 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 28 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 21 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 14 & 0x7FL);
            values[valuesOffset++] = (block7 >>> 7 & 0x7FL);
            values[valuesOffset++] = (block7 & 0x7FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 1;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x1L) << 6 | byte2 >>> 2);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3L) << 5 | byte3 >>> 3);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7L) << 4 | byte4 >>> 4);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0xFL) << 3 | byte5 >>> 5);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x1FL) << 2 | byte6 >>> 6);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3FL) << 1 | byte7 >>> 7);
            values[valuesOffset++] = (byte7 & 0x7FL);
        }
    }
}
