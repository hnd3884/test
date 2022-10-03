package org.apache.lucene.util.packed;

final class BulkOperationPacked9 extends BulkOperationPacked
{
    public BulkOperationPacked9() {
        super(9);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 55);
            values[valuesOffset++] = (int)(block0 >>> 46 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 37 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x1FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1L) << 8 | block2 >>> 56);
            values[valuesOffset++] = (int)(block2 >>> 47 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 29 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 20 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 11 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x1FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 7 | block3 >>> 57);
            values[valuesOffset++] = (int)(block3 >>> 48 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 39 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 30 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 21 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x1FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x7L) << 6 | block4 >>> 58);
            values[valuesOffset++] = (int)(block4 >>> 49 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 31 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 22 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 13 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 4 & 0x1FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFL) << 5 | block5 >>> 59);
            values[valuesOffset++] = (int)(block5 >>> 50 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 41 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 32 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 23 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 14 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 5 & 0x1FFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FL) << 4 | block6 >>> 60);
            values[valuesOffset++] = (int)(block6 >>> 51 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 42 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 33 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 24 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 15 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x1FFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 3 | block7 >>> 61);
            values[valuesOffset++] = (int)(block7 >>> 52 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 43 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 34 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 25 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 16 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 7 & 0x1FFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x7FL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (int)(block8 >>> 53 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 44 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 35 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 26 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 17 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 8 & 0x1FFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFFL) << 1 | block9 >>> 63);
            values[valuesOffset++] = (int)(block9 >>> 54 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 >>> 45 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 >>> 36 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 >>> 27 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 >>> 18 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 >>> 9 & 0x1FFL);
            values[valuesOffset++] = (int)(block9 & 0x1FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 1 | byte2 >>> 7);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x7F) << 2 | byte3 >>> 6);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3F) << 3 | byte4 >>> 5);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x1F) << 4 | byte5 >>> 4);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0xF) << 5 | byte6 >>> 3);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x7) << 6 | byte7 >>> 2);
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x3) << 7 | byte8 >>> 1);
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x1) << 8 | byte9);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 55;
            values[valuesOffset++] = (block0 >>> 46 & 0x1FFL);
            values[valuesOffset++] = (block0 >>> 37 & 0x1FFL);
            values[valuesOffset++] = (block0 >>> 28 & 0x1FFL);
            values[valuesOffset++] = (block0 >>> 19 & 0x1FFL);
            values[valuesOffset++] = (block0 >>> 10 & 0x1FFL);
            values[valuesOffset++] = (block0 >>> 1 & 0x1FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1L) << 8 | block2 >>> 56);
            values[valuesOffset++] = (block2 >>> 47 & 0x1FFL);
            values[valuesOffset++] = (block2 >>> 38 & 0x1FFL);
            values[valuesOffset++] = (block2 >>> 29 & 0x1FFL);
            values[valuesOffset++] = (block2 >>> 20 & 0x1FFL);
            values[valuesOffset++] = (block2 >>> 11 & 0x1FFL);
            values[valuesOffset++] = (block2 >>> 2 & 0x1FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 7 | block3 >>> 57);
            values[valuesOffset++] = (block3 >>> 48 & 0x1FFL);
            values[valuesOffset++] = (block3 >>> 39 & 0x1FFL);
            values[valuesOffset++] = (block3 >>> 30 & 0x1FFL);
            values[valuesOffset++] = (block3 >>> 21 & 0x1FFL);
            values[valuesOffset++] = (block3 >>> 12 & 0x1FFL);
            values[valuesOffset++] = (block3 >>> 3 & 0x1FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x7L) << 6 | block4 >>> 58);
            values[valuesOffset++] = (block4 >>> 49 & 0x1FFL);
            values[valuesOffset++] = (block4 >>> 40 & 0x1FFL);
            values[valuesOffset++] = (block4 >>> 31 & 0x1FFL);
            values[valuesOffset++] = (block4 >>> 22 & 0x1FFL);
            values[valuesOffset++] = (block4 >>> 13 & 0x1FFL);
            values[valuesOffset++] = (block4 >>> 4 & 0x1FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFL) << 5 | block5 >>> 59);
            values[valuesOffset++] = (block5 >>> 50 & 0x1FFL);
            values[valuesOffset++] = (block5 >>> 41 & 0x1FFL);
            values[valuesOffset++] = (block5 >>> 32 & 0x1FFL);
            values[valuesOffset++] = (block5 >>> 23 & 0x1FFL);
            values[valuesOffset++] = (block5 >>> 14 & 0x1FFL);
            values[valuesOffset++] = (block5 >>> 5 & 0x1FFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1FL) << 4 | block6 >>> 60);
            values[valuesOffset++] = (block6 >>> 51 & 0x1FFL);
            values[valuesOffset++] = (block6 >>> 42 & 0x1FFL);
            values[valuesOffset++] = (block6 >>> 33 & 0x1FFL);
            values[valuesOffset++] = (block6 >>> 24 & 0x1FFL);
            values[valuesOffset++] = (block6 >>> 15 & 0x1FFL);
            values[valuesOffset++] = (block6 >>> 6 & 0x1FFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FL) << 3 | block7 >>> 61);
            values[valuesOffset++] = (block7 >>> 52 & 0x1FFL);
            values[valuesOffset++] = (block7 >>> 43 & 0x1FFL);
            values[valuesOffset++] = (block7 >>> 34 & 0x1FFL);
            values[valuesOffset++] = (block7 >>> 25 & 0x1FFL);
            values[valuesOffset++] = (block7 >>> 16 & 0x1FFL);
            values[valuesOffset++] = (block7 >>> 7 & 0x1FFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x7FL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (block8 >>> 53 & 0x1FFL);
            values[valuesOffset++] = (block8 >>> 44 & 0x1FFL);
            values[valuesOffset++] = (block8 >>> 35 & 0x1FFL);
            values[valuesOffset++] = (block8 >>> 26 & 0x1FFL);
            values[valuesOffset++] = (block8 >>> 17 & 0x1FFL);
            values[valuesOffset++] = (block8 >>> 8 & 0x1FFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0xFFL) << 1 | block9 >>> 63);
            values[valuesOffset++] = (block9 >>> 54 & 0x1FFL);
            values[valuesOffset++] = (block9 >>> 45 & 0x1FFL);
            values[valuesOffset++] = (block9 >>> 36 & 0x1FFL);
            values[valuesOffset++] = (block9 >>> 27 & 0x1FFL);
            values[valuesOffset++] = (block9 >>> 18 & 0x1FFL);
            values[valuesOffset++] = (block9 >>> 9 & 0x1FFL);
            values[valuesOffset++] = (block9 & 0x1FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 1 | byte2 >>> 7);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x7FL) << 2 | byte3 >>> 6);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3FL) << 3 | byte4 >>> 5);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x1FL) << 4 | byte5 >>> 4);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0xFL) << 5 | byte6 >>> 3);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x7L) << 6 | byte7 >>> 2);
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x3L) << 7 | byte8 >>> 1);
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x1L) << 8 | byte9);
        }
    }
}
