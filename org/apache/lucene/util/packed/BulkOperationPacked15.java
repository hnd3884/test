package org.apache.lucene.util.packed;

final class BulkOperationPacked15 extends BulkOperationPacked
{
    public BulkOperationPacked15() {
        super(15);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 49);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x7FFFL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x7FFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x7FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 11 | block2 >>> 53);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x7FFFL);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x7FFFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x7FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 7 | block3 >>> 57);
            values[valuesOffset++] = (int)(block3 >>> 42 & 0x7FFFL);
            values[valuesOffset++] = (int)(block3 >>> 27 & 0x7FFFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x7FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFFL) << 3 | block4 >>> 61);
            values[valuesOffset++] = (int)(block4 >>> 46 & 0x7FFFL);
            values[valuesOffset++] = (int)(block4 >>> 31 & 0x7FFFL);
            values[valuesOffset++] = (int)(block4 >>> 16 & 0x7FFFL);
            values[valuesOffset++] = (int)(block4 >>> 1 & 0x7FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1L) << 14 | block5 >>> 50);
            values[valuesOffset++] = (int)(block5 >>> 35 & 0x7FFFL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x7FFFL);
            values[valuesOffset++] = (int)(block5 >>> 5 & 0x7FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FL) << 10 | block6 >>> 54);
            values[valuesOffset++] = (int)(block6 >>> 39 & 0x7FFFL);
            values[valuesOffset++] = (int)(block6 >>> 24 & 0x7FFFL);
            values[valuesOffset++] = (int)(block6 >>> 9 & 0x7FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x1FFL) << 6 | block7 >>> 58);
            values[valuesOffset++] = (int)(block7 >>> 43 & 0x7FFFL);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x7FFFL);
            values[valuesOffset++] = (int)(block7 >>> 13 & 0x7FFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x1FFFL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (int)(block8 >>> 47 & 0x7FFFL);
            values[valuesOffset++] = (int)(block8 >>> 32 & 0x7FFFL);
            values[valuesOffset++] = (int)(block8 >>> 17 & 0x7FFFL);
            values[valuesOffset++] = (int)(block8 >>> 2 & 0x7FFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3L) << 13 | block9 >>> 51);
            values[valuesOffset++] = (int)(block9 >>> 36 & 0x7FFFL);
            values[valuesOffset++] = (int)(block9 >>> 21 & 0x7FFFL);
            values[valuesOffset++] = (int)(block9 >>> 6 & 0x7FFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x3FL) << 9 | block10 >>> 55);
            values[valuesOffset++] = (int)(block10 >>> 40 & 0x7FFFL);
            values[valuesOffset++] = (int)(block10 >>> 25 & 0x7FFFL);
            values[valuesOffset++] = (int)(block10 >>> 10 & 0x7FFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3FFL) << 5 | block11 >>> 59);
            values[valuesOffset++] = (int)(block11 >>> 44 & 0x7FFFL);
            values[valuesOffset++] = (int)(block11 >>> 29 & 0x7FFFL);
            values[valuesOffset++] = (int)(block11 >>> 14 & 0x7FFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x3FFFL) << 1 | block12 >>> 63);
            values[valuesOffset++] = (int)(block12 >>> 48 & 0x7FFFL);
            values[valuesOffset++] = (int)(block12 >>> 33 & 0x7FFFL);
            values[valuesOffset++] = (int)(block12 >>> 18 & 0x7FFFL);
            values[valuesOffset++] = (int)(block12 >>> 3 & 0x7FFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x7L) << 12 | block13 >>> 52);
            values[valuesOffset++] = (int)(block13 >>> 37 & 0x7FFFL);
            values[valuesOffset++] = (int)(block13 >>> 22 & 0x7FFFL);
            values[valuesOffset++] = (int)(block13 >>> 7 & 0x7FFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0x7FL) << 8 | block14 >>> 56);
            values[valuesOffset++] = (int)(block14 >>> 41 & 0x7FFFL);
            values[valuesOffset++] = (int)(block14 >>> 26 & 0x7FFFL);
            values[valuesOffset++] = (int)(block14 >>> 11 & 0x7FFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x7FFL) << 4 | block15 >>> 60);
            values[valuesOffset++] = (int)(block15 >>> 45 & 0x7FFFL);
            values[valuesOffset++] = (int)(block15 >>> 30 & 0x7FFFL);
            values[valuesOffset++] = (int)(block15 >>> 15 & 0x7FFFL);
            values[valuesOffset++] = (int)(block15 & 0x7FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 7 | byte2 >>> 1);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1) << 14 | byte3 << 6 | byte4 >>> 2);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3) << 13 | byte5 << 5 | byte6 >>> 3);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x7) << 12 | byte7 << 4 | byte8 >>> 4);
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0xF) << 11 | byte9 << 3 | byte10 >>> 5);
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x1F) << 10 | byte11 << 2 | byte12 >>> 6);
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            final int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x3F) << 9 | byte13 << 1 | byte14 >>> 7);
            final int byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte14 & 0x7F) << 8 | byte15);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 49;
            values[valuesOffset++] = (block0 >>> 34 & 0x7FFFL);
            values[valuesOffset++] = (block0 >>> 19 & 0x7FFFL);
            values[valuesOffset++] = (block0 >>> 4 & 0x7FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 11 | block2 >>> 53);
            values[valuesOffset++] = (block2 >>> 38 & 0x7FFFL);
            values[valuesOffset++] = (block2 >>> 23 & 0x7FFFL);
            values[valuesOffset++] = (block2 >>> 8 & 0x7FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0xFFL) << 7 | block3 >>> 57);
            values[valuesOffset++] = (block3 >>> 42 & 0x7FFFL);
            values[valuesOffset++] = (block3 >>> 27 & 0x7FFFL);
            values[valuesOffset++] = (block3 >>> 12 & 0x7FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0xFFFL) << 3 | block4 >>> 61);
            values[valuesOffset++] = (block4 >>> 46 & 0x7FFFL);
            values[valuesOffset++] = (block4 >>> 31 & 0x7FFFL);
            values[valuesOffset++] = (block4 >>> 16 & 0x7FFFL);
            values[valuesOffset++] = (block4 >>> 1 & 0x7FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x1L) << 14 | block5 >>> 50);
            values[valuesOffset++] = (block5 >>> 35 & 0x7FFFL);
            values[valuesOffset++] = (block5 >>> 20 & 0x7FFFL);
            values[valuesOffset++] = (block5 >>> 5 & 0x7FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1FL) << 10 | block6 >>> 54);
            values[valuesOffset++] = (block6 >>> 39 & 0x7FFFL);
            values[valuesOffset++] = (block6 >>> 24 & 0x7FFFL);
            values[valuesOffset++] = (block6 >>> 9 & 0x7FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x1FFL) << 6 | block7 >>> 58);
            values[valuesOffset++] = (block7 >>> 43 & 0x7FFFL);
            values[valuesOffset++] = (block7 >>> 28 & 0x7FFFL);
            values[valuesOffset++] = (block7 >>> 13 & 0x7FFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x1FFFL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (block8 >>> 47 & 0x7FFFL);
            values[valuesOffset++] = (block8 >>> 32 & 0x7FFFL);
            values[valuesOffset++] = (block8 >>> 17 & 0x7FFFL);
            values[valuesOffset++] = (block8 >>> 2 & 0x7FFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3L) << 13 | block9 >>> 51);
            values[valuesOffset++] = (block9 >>> 36 & 0x7FFFL);
            values[valuesOffset++] = (block9 >>> 21 & 0x7FFFL);
            values[valuesOffset++] = (block9 >>> 6 & 0x7FFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0x3FL) << 9 | block10 >>> 55);
            values[valuesOffset++] = (block10 >>> 40 & 0x7FFFL);
            values[valuesOffset++] = (block10 >>> 25 & 0x7FFFL);
            values[valuesOffset++] = (block10 >>> 10 & 0x7FFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x3FFL) << 5 | block11 >>> 59);
            values[valuesOffset++] = (block11 >>> 44 & 0x7FFFL);
            values[valuesOffset++] = (block11 >>> 29 & 0x7FFFL);
            values[valuesOffset++] = (block11 >>> 14 & 0x7FFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x3FFFL) << 1 | block12 >>> 63);
            values[valuesOffset++] = (block12 >>> 48 & 0x7FFFL);
            values[valuesOffset++] = (block12 >>> 33 & 0x7FFFL);
            values[valuesOffset++] = (block12 >>> 18 & 0x7FFFL);
            values[valuesOffset++] = (block12 >>> 3 & 0x7FFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0x7L) << 12 | block13 >>> 52);
            values[valuesOffset++] = (block13 >>> 37 & 0x7FFFL);
            values[valuesOffset++] = (block13 >>> 22 & 0x7FFFL);
            values[valuesOffset++] = (block13 >>> 7 & 0x7FFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block13 & 0x7FL) << 8 | block14 >>> 56);
            values[valuesOffset++] = (block14 >>> 41 & 0x7FFFL);
            values[valuesOffset++] = (block14 >>> 26 & 0x7FFFL);
            values[valuesOffset++] = (block14 >>> 11 & 0x7FFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block14 & 0x7FFL) << 4 | block15 >>> 60);
            values[valuesOffset++] = (block15 >>> 45 & 0x7FFFL);
            values[valuesOffset++] = (block15 >>> 30 & 0x7FFFL);
            values[valuesOffset++] = (block15 >>> 15 & 0x7FFFL);
            values[valuesOffset++] = (block15 & 0x7FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 7 | byte2 >>> 1);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x1L) << 14 | byte3 << 6 | byte4 >>> 2);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0x3L) << 13 | byte5 << 5 | byte6 >>> 3);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x7L) << 12 | byte7 << 4 | byte8 >>> 4);
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0xFL) << 11 | byte9 << 3 | byte10 >>> 5);
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0x1FL) << 10 | byte11 << 2 | byte12 >>> 6);
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            final long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x3FL) << 9 | byte13 << 1 | byte14 >>> 7);
            final long byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte14 & 0x7FL) << 8 | byte15);
        }
    }
}
