package org.apache.lucene.util.packed;

final class BulkOperationPacked13 extends BulkOperationPacked
{
    public BulkOperationPacked13() {
        super(13);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 51);
            values[valuesOffset++] = (int)(block0 >>> 38 & 0x1FFFL);
            values[valuesOffset++] = (int)(block0 >>> 25 & 0x1FFFL);
            values[valuesOffset++] = (int)(block0 >>> 12 & 0x1FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFL) << 1 | block2 >>> 63);
            values[valuesOffset++] = (int)(block2 >>> 50 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 37 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 11 & 0x1FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x7FFL) << 2 | block3 >>> 62);
            values[valuesOffset++] = (int)(block3 >>> 49 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 23 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 10 & 0x1FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3FFL) << 3 | block4 >>> 61);
            values[valuesOffset++] = (int)(block4 >>> 48 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 35 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 22 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 9 & 0x1FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FFL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 47 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 34 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 21 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 8 & 0x1FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFFL) << 5 | block6 >>> 59);
            values[valuesOffset++] = (int)(block6 >>> 46 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 33 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 20 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 7 & 0x1FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x7FL) << 6 | block7 >>> 58);
            values[valuesOffset++] = (int)(block7 >>> 45 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 32 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 19 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 6 & 0x1FFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FL) << 7 | block8 >>> 57);
            values[valuesOffset++] = (int)(block8 >>> 44 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 31 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 18 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 5 & 0x1FFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x1FL) << 8 | block9 >>> 56);
            values[valuesOffset++] = (int)(block9 >>> 43 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 30 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 17 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 4 & 0x1FFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0xFL) << 9 | block10 >>> 55);
            values[valuesOffset++] = (int)(block10 >>> 42 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 29 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 16 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 3 & 0x1FFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x7L) << 10 | block11 >>> 54);
            values[valuesOffset++] = (int)(block11 >>> 41 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 28 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 15 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 2 & 0x1FFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x3L) << 11 | block12 >>> 53);
            values[valuesOffset++] = (int)(block12 >>> 40 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 >>> 27 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 >>> 14 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 >>> 1 & 0x1FFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x1L) << 12 | block13 >>> 52);
            values[valuesOffset++] = (int)(block13 >>> 39 & 0x1FFFL);
            values[valuesOffset++] = (int)(block13 >>> 26 & 0x1FFFL);
            values[valuesOffset++] = (int)(block13 >>> 13 & 0x1FFFL);
            values[valuesOffset++] = (int)(block13 & 0x1FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 5 | byte2 >>> 3);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x7) << 10 | byte3 << 2 | byte4 >>> 6);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3F) << 7 | byte5 >>> 1);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x1) << 12 | byte6 << 4 | byte7 >>> 4);
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0xF) << 9 | byte8 << 1 | byte9 >>> 7);
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x7F) << 6 | byte10 >>> 2);
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x3) << 11 | byte11 << 3 | byte12 >>> 5);
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x1F) << 8 | byte13);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 51;
            values[valuesOffset++] = (block0 >>> 38 & 0x1FFFL);
            values[valuesOffset++] = (block0 >>> 25 & 0x1FFFL);
            values[valuesOffset++] = (block0 >>> 12 & 0x1FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFFFL) << 1 | block2 >>> 63);
            values[valuesOffset++] = (block2 >>> 50 & 0x1FFFL);
            values[valuesOffset++] = (block2 >>> 37 & 0x1FFFL);
            values[valuesOffset++] = (block2 >>> 24 & 0x1FFFL);
            values[valuesOffset++] = (block2 >>> 11 & 0x1FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x7FFL) << 2 | block3 >>> 62);
            values[valuesOffset++] = (block3 >>> 49 & 0x1FFFL);
            values[valuesOffset++] = (block3 >>> 36 & 0x1FFFL);
            values[valuesOffset++] = (block3 >>> 23 & 0x1FFFL);
            values[valuesOffset++] = (block3 >>> 10 & 0x1FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x3FFL) << 3 | block4 >>> 61);
            values[valuesOffset++] = (block4 >>> 48 & 0x1FFFL);
            values[valuesOffset++] = (block4 >>> 35 & 0x1FFFL);
            values[valuesOffset++] = (block4 >>> 22 & 0x1FFFL);
            values[valuesOffset++] = (block4 >>> 9 & 0x1FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x1FFL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (block5 >>> 47 & 0x1FFFL);
            values[valuesOffset++] = (block5 >>> 34 & 0x1FFFL);
            values[valuesOffset++] = (block5 >>> 21 & 0x1FFFL);
            values[valuesOffset++] = (block5 >>> 8 & 0x1FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0xFFL) << 5 | block6 >>> 59);
            values[valuesOffset++] = (block6 >>> 46 & 0x1FFFL);
            values[valuesOffset++] = (block6 >>> 33 & 0x1FFFL);
            values[valuesOffset++] = (block6 >>> 20 & 0x1FFFL);
            values[valuesOffset++] = (block6 >>> 7 & 0x1FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x7FL) << 6 | block7 >>> 58);
            values[valuesOffset++] = (block7 >>> 45 & 0x1FFFL);
            values[valuesOffset++] = (block7 >>> 32 & 0x1FFFL);
            values[valuesOffset++] = (block7 >>> 19 & 0x1FFFL);
            values[valuesOffset++] = (block7 >>> 6 & 0x1FFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x3FL) << 7 | block8 >>> 57);
            values[valuesOffset++] = (block8 >>> 44 & 0x1FFFL);
            values[valuesOffset++] = (block8 >>> 31 & 0x1FFFL);
            values[valuesOffset++] = (block8 >>> 18 & 0x1FFFL);
            values[valuesOffset++] = (block8 >>> 5 & 0x1FFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x1FL) << 8 | block9 >>> 56);
            values[valuesOffset++] = (block9 >>> 43 & 0x1FFFL);
            values[valuesOffset++] = (block9 >>> 30 & 0x1FFFL);
            values[valuesOffset++] = (block9 >>> 17 & 0x1FFFL);
            values[valuesOffset++] = (block9 >>> 4 & 0x1FFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0xFL) << 9 | block10 >>> 55);
            values[valuesOffset++] = (block10 >>> 42 & 0x1FFFL);
            values[valuesOffset++] = (block10 >>> 29 & 0x1FFFL);
            values[valuesOffset++] = (block10 >>> 16 & 0x1FFFL);
            values[valuesOffset++] = (block10 >>> 3 & 0x1FFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x7L) << 10 | block11 >>> 54);
            values[valuesOffset++] = (block11 >>> 41 & 0x1FFFL);
            values[valuesOffset++] = (block11 >>> 28 & 0x1FFFL);
            values[valuesOffset++] = (block11 >>> 15 & 0x1FFFL);
            values[valuesOffset++] = (block11 >>> 2 & 0x1FFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x3L) << 11 | block12 >>> 53);
            values[valuesOffset++] = (block12 >>> 40 & 0x1FFFL);
            values[valuesOffset++] = (block12 >>> 27 & 0x1FFFL);
            values[valuesOffset++] = (block12 >>> 14 & 0x1FFFL);
            values[valuesOffset++] = (block12 >>> 1 & 0x1FFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0x1L) << 12 | block13 >>> 52);
            values[valuesOffset++] = (block13 >>> 39 & 0x1FFFL);
            values[valuesOffset++] = (block13 >>> 26 & 0x1FFFL);
            values[valuesOffset++] = (block13 >>> 13 & 0x1FFFL);
            values[valuesOffset++] = (block13 & 0x1FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 5 | byte2 >>> 3);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x7L) << 10 | byte3 << 2 | byte4 >>> 6);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3FL) << 7 | byte5 >>> 1);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x1L) << 12 | byte6 << 4 | byte7 >>> 4);
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0xFL) << 9 | byte8 << 1 | byte9 >>> 7);
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x7FL) << 6 | byte10 >>> 2);
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x3L) << 11 | byte11 << 3 | byte12 >>> 5);
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x1FL) << 8 | byte13);
        }
    }
}
