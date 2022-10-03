package org.apache.lucene.util.packed;

final class BulkOperationPacked11 extends BulkOperationPacked
{
    public BulkOperationPacked11() {
        super(11);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 53);
            values[valuesOffset++] = (int)(block0 >>> 42 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 31 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 20 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 9 & 0x7FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1FFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 51 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 40 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 29 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 18 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 7 & 0x7FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x7FL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 49 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 38 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 27 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 5 & 0x7FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x1FL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (int)(block4 >>> 47 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 36 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 25 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 14 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 3 & 0x7FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x7L) << 8 | block5 >>> 56);
            values[valuesOffset++] = (int)(block5 >>> 45 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 34 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 23 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 12 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 1 & 0x7FFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1L) << 10 | block6 >>> 54);
            values[valuesOffset++] = (int)(block6 >>> 43 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 32 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 21 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 10 & 0x7FFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FFL) << 1 | block7 >>> 63);
            values[valuesOffset++] = (int)(block7 >>> 52 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 41 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 30 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 19 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 8 & 0x7FFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0xFFL) << 3 | block8 >>> 61);
            values[valuesOffset++] = (int)(block8 >>> 50 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 39 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 28 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 17 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 6 & 0x7FFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FL) << 5 | block9 >>> 59);
            values[valuesOffset++] = (int)(block9 >>> 48 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 37 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 26 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 15 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 4 & 0x7FFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0xFL) << 7 | block10 >>> 57);
            values[valuesOffset++] = (int)(block10 >>> 46 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 35 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 24 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 13 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 2 & 0x7FFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3L) << 9 | block11 >>> 55);
            values[valuesOffset++] = (int)(block11 >>> 44 & 0x7FFL);
            values[valuesOffset++] = (int)(block11 >>> 33 & 0x7FFL);
            values[valuesOffset++] = (int)(block11 >>> 22 & 0x7FFL);
            values[valuesOffset++] = (int)(block11 >>> 11 & 0x7FFL);
            values[valuesOffset++] = (int)(block11 & 0x7FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 3 | byte2 >>> 5);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1F) << 6 | byte3 >>> 2);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3) << 9 | byte4 << 1 | byte5 >>> 7);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x7F) << 4 | byte6 >>> 4);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0xF) << 7 | byte7 >>> 1);
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x1) << 10 | byte8 << 2 | byte9 >>> 6);
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x3F) << 5 | byte10 >>> 3);
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x7) << 8 | byte11);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 53;
            values[valuesOffset++] = (block0 >>> 42 & 0x7FFL);
            values[valuesOffset++] = (block0 >>> 31 & 0x7FFL);
            values[valuesOffset++] = (block0 >>> 20 & 0x7FFL);
            values[valuesOffset++] = (block0 >>> 9 & 0x7FFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1FFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (block2 >>> 51 & 0x7FFL);
            values[valuesOffset++] = (block2 >>> 40 & 0x7FFL);
            values[valuesOffset++] = (block2 >>> 29 & 0x7FFL);
            values[valuesOffset++] = (block2 >>> 18 & 0x7FFL);
            values[valuesOffset++] = (block2 >>> 7 & 0x7FFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x7FL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (block3 >>> 49 & 0x7FFL);
            values[valuesOffset++] = (block3 >>> 38 & 0x7FFL);
            values[valuesOffset++] = (block3 >>> 27 & 0x7FFL);
            values[valuesOffset++] = (block3 >>> 16 & 0x7FFL);
            values[valuesOffset++] = (block3 >>> 5 & 0x7FFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x1FL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (block4 >>> 47 & 0x7FFL);
            values[valuesOffset++] = (block4 >>> 36 & 0x7FFL);
            values[valuesOffset++] = (block4 >>> 25 & 0x7FFL);
            values[valuesOffset++] = (block4 >>> 14 & 0x7FFL);
            values[valuesOffset++] = (block4 >>> 3 & 0x7FFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x7L) << 8 | block5 >>> 56);
            values[valuesOffset++] = (block5 >>> 45 & 0x7FFL);
            values[valuesOffset++] = (block5 >>> 34 & 0x7FFL);
            values[valuesOffset++] = (block5 >>> 23 & 0x7FFL);
            values[valuesOffset++] = (block5 >>> 12 & 0x7FFL);
            values[valuesOffset++] = (block5 >>> 1 & 0x7FFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1L) << 10 | block6 >>> 54);
            values[valuesOffset++] = (block6 >>> 43 & 0x7FFL);
            values[valuesOffset++] = (block6 >>> 32 & 0x7FFL);
            values[valuesOffset++] = (block6 >>> 21 & 0x7FFL);
            values[valuesOffset++] = (block6 >>> 10 & 0x7FFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FFL) << 1 | block7 >>> 63);
            values[valuesOffset++] = (block7 >>> 52 & 0x7FFL);
            values[valuesOffset++] = (block7 >>> 41 & 0x7FFL);
            values[valuesOffset++] = (block7 >>> 30 & 0x7FFL);
            values[valuesOffset++] = (block7 >>> 19 & 0x7FFL);
            values[valuesOffset++] = (block7 >>> 8 & 0x7FFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0xFFL) << 3 | block8 >>> 61);
            values[valuesOffset++] = (block8 >>> 50 & 0x7FFL);
            values[valuesOffset++] = (block8 >>> 39 & 0x7FFL);
            values[valuesOffset++] = (block8 >>> 28 & 0x7FFL);
            values[valuesOffset++] = (block8 >>> 17 & 0x7FFL);
            values[valuesOffset++] = (block8 >>> 6 & 0x7FFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3FL) << 5 | block9 >>> 59);
            values[valuesOffset++] = (block9 >>> 48 & 0x7FFL);
            values[valuesOffset++] = (block9 >>> 37 & 0x7FFL);
            values[valuesOffset++] = (block9 >>> 26 & 0x7FFL);
            values[valuesOffset++] = (block9 >>> 15 & 0x7FFL);
            values[valuesOffset++] = (block9 >>> 4 & 0x7FFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0xFL) << 7 | block10 >>> 57);
            values[valuesOffset++] = (block10 >>> 46 & 0x7FFL);
            values[valuesOffset++] = (block10 >>> 35 & 0x7FFL);
            values[valuesOffset++] = (block10 >>> 24 & 0x7FFL);
            values[valuesOffset++] = (block10 >>> 13 & 0x7FFL);
            values[valuesOffset++] = (block10 >>> 2 & 0x7FFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x3L) << 9 | block11 >>> 55);
            values[valuesOffset++] = (block11 >>> 44 & 0x7FFL);
            values[valuesOffset++] = (block11 >>> 33 & 0x7FFL);
            values[valuesOffset++] = (block11 >>> 22 & 0x7FFL);
            values[valuesOffset++] = (block11 >>> 11 & 0x7FFL);
            values[valuesOffset++] = (block11 & 0x7FFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 3 | byte2 >>> 5);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1FL) << 6 | byte3 >>> 2);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3L) << 9 | byte4 << 1 | byte5 >>> 7);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x7FL) << 4 | byte6 >>> 4);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0xFL) << 7 | byte7 >>> 1);
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x1L) << 10 | byte8 << 2 | byte9 >>> 6);
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x3FL) << 5 | byte10 >>> 3);
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x7L) << 8 | byte11);
        }
    }
}
