package org.apache.lucene.util.packed;

final class BulkOperationPacked17 extends BulkOperationPacked
{
    public BulkOperationPacked17() {
        super(17);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 47);
            values[valuesOffset++] = (int)(block0 >>> 30 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 13 & 0x1FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1FFFL) << 4 | block2 >>> 60);
            values[valuesOffset++] = (int)(block2 >>> 43 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 26 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 9 & 0x1FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x1FFL) << 8 | block3 >>> 56);
            values[valuesOffset++] = (int)(block3 >>> 39 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 5 & 0x1FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x1FL) << 12 | block4 >>> 52);
            values[valuesOffset++] = (int)(block4 >>> 35 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 18 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 1 & 0x1FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1L) << 16 | block5 >>> 48);
            values[valuesOffset++] = (int)(block5 >>> 31 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 14 & 0x1FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FFFL) << 3 | block6 >>> 61);
            values[valuesOffset++] = (int)(block6 >>> 44 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 27 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 10 & 0x1FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FFL) << 7 | block7 >>> 57);
            values[valuesOffset++] = (int)(block7 >>> 40 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 23 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 6 & 0x1FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FL) << 11 | block8 >>> 53);
            values[valuesOffset++] = (int)(block8 >>> 36 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 19 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 2 & 0x1FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3L) << 15 | block9 >>> 49);
            values[valuesOffset++] = (int)(block9 >>> 32 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 15 & 0x1FFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x7FFFL) << 2 | block10 >>> 62);
            values[valuesOffset++] = (int)(block10 >>> 45 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 28 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 11 & 0x1FFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x7FFL) << 6 | block11 >>> 58);
            values[valuesOffset++] = (int)(block11 >>> 41 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 24 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 7 & 0x1FFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x7FL) << 10 | block12 >>> 54);
            values[valuesOffset++] = (int)(block12 >>> 37 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block12 >>> 20 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block12 >>> 3 & 0x1FFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x7L) << 14 | block13 >>> 50);
            values[valuesOffset++] = (int)(block13 >>> 33 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 16 & 0x1FFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0xFFFFL) << 1 | block14 >>> 63);
            values[valuesOffset++] = (int)(block14 >>> 46 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 29 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 12 & 0x1FFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0xFFFL) << 5 | block15 >>> 59);
            values[valuesOffset++] = (int)(block15 >>> 42 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 25 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 8 & 0x1FFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0xFFL) << 9 | block16 >>> 55);
            values[valuesOffset++] = (int)(block16 >>> 38 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 21 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 4 & 0x1FFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0xFL) << 13 | block17 >>> 51);
            values[valuesOffset++] = (int)(block17 >>> 34 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block17 >>> 17 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block17 & 0x1FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 9 | byte2 << 1 | byte3 >>> 7);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7F) << 10 | byte4 << 2 | byte5 >>> 6);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x3F) << 11 | byte6 << 3 | byte7 >>> 5);
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x1F) << 12 | byte8 << 4 | byte9 >>> 4);
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0xF) << 13 | byte10 << 5 | byte11 >>> 3);
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte11 & 0x7) << 14 | byte12 << 6 | byte13 >>> 2);
            final int byte14 = blocks[blocksOffset++] & 0xFF;
            final int byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte13 & 0x3) << 15 | byte14 << 7 | byte15 >>> 1);
            final int byte16 = blocks[blocksOffset++] & 0xFF;
            final int byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x1) << 16 | byte16 << 8 | byte17);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 47;
            values[valuesOffset++] = (block0 >>> 30 & 0x1FFFFL);
            values[valuesOffset++] = (block0 >>> 13 & 0x1FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1FFFL) << 4 | block2 >>> 60);
            values[valuesOffset++] = (block2 >>> 43 & 0x1FFFFL);
            values[valuesOffset++] = (block2 >>> 26 & 0x1FFFFL);
            values[valuesOffset++] = (block2 >>> 9 & 0x1FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x1FFL) << 8 | block3 >>> 56);
            values[valuesOffset++] = (block3 >>> 39 & 0x1FFFFL);
            values[valuesOffset++] = (block3 >>> 22 & 0x1FFFFL);
            values[valuesOffset++] = (block3 >>> 5 & 0x1FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x1FL) << 12 | block4 >>> 52);
            values[valuesOffset++] = (block4 >>> 35 & 0x1FFFFL);
            values[valuesOffset++] = (block4 >>> 18 & 0x1FFFFL);
            values[valuesOffset++] = (block4 >>> 1 & 0x1FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x1L) << 16 | block5 >>> 48);
            values[valuesOffset++] = (block5 >>> 31 & 0x1FFFFL);
            values[valuesOffset++] = (block5 >>> 14 & 0x1FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x3FFFL) << 3 | block6 >>> 61);
            values[valuesOffset++] = (block6 >>> 44 & 0x1FFFFL);
            values[valuesOffset++] = (block6 >>> 27 & 0x1FFFFL);
            values[valuesOffset++] = (block6 >>> 10 & 0x1FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FFL) << 7 | block7 >>> 57);
            values[valuesOffset++] = (block7 >>> 40 & 0x1FFFFL);
            values[valuesOffset++] = (block7 >>> 23 & 0x1FFFFL);
            values[valuesOffset++] = (block7 >>> 6 & 0x1FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x3FL) << 11 | block8 >>> 53);
            values[valuesOffset++] = (block8 >>> 36 & 0x1FFFFL);
            values[valuesOffset++] = (block8 >>> 19 & 0x1FFFFL);
            values[valuesOffset++] = (block8 >>> 2 & 0x1FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3L) << 15 | block9 >>> 49);
            values[valuesOffset++] = (block9 >>> 32 & 0x1FFFFL);
            values[valuesOffset++] = (block9 >>> 15 & 0x1FFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0x7FFFL) << 2 | block10 >>> 62);
            values[valuesOffset++] = (block10 >>> 45 & 0x1FFFFL);
            values[valuesOffset++] = (block10 >>> 28 & 0x1FFFFL);
            values[valuesOffset++] = (block10 >>> 11 & 0x1FFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x7FFL) << 6 | block11 >>> 58);
            values[valuesOffset++] = (block11 >>> 41 & 0x1FFFFL);
            values[valuesOffset++] = (block11 >>> 24 & 0x1FFFFL);
            values[valuesOffset++] = (block11 >>> 7 & 0x1FFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x7FL) << 10 | block12 >>> 54);
            values[valuesOffset++] = (block12 >>> 37 & 0x1FFFFL);
            values[valuesOffset++] = (block12 >>> 20 & 0x1FFFFL);
            values[valuesOffset++] = (block12 >>> 3 & 0x1FFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0x7L) << 14 | block13 >>> 50);
            values[valuesOffset++] = (block13 >>> 33 & 0x1FFFFL);
            values[valuesOffset++] = (block13 >>> 16 & 0x1FFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block13 & 0xFFFFL) << 1 | block14 >>> 63);
            values[valuesOffset++] = (block14 >>> 46 & 0x1FFFFL);
            values[valuesOffset++] = (block14 >>> 29 & 0x1FFFFL);
            values[valuesOffset++] = (block14 >>> 12 & 0x1FFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block14 & 0xFFFL) << 5 | block15 >>> 59);
            values[valuesOffset++] = (block15 >>> 42 & 0x1FFFFL);
            values[valuesOffset++] = (block15 >>> 25 & 0x1FFFFL);
            values[valuesOffset++] = (block15 >>> 8 & 0x1FFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block15 & 0xFFL) << 9 | block16 >>> 55);
            values[valuesOffset++] = (block16 >>> 38 & 0x1FFFFL);
            values[valuesOffset++] = (block16 >>> 21 & 0x1FFFFL);
            values[valuesOffset++] = (block16 >>> 4 & 0x1FFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block16 & 0xFL) << 13 | block17 >>> 51);
            values[valuesOffset++] = (block17 >>> 34 & 0x1FFFFL);
            values[valuesOffset++] = (block17 >>> 17 & 0x1FFFFL);
            values[valuesOffset++] = (block17 & 0x1FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 9 | byte2 << 1 | byte3 >>> 7);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7FL) << 10 | byte4 << 2 | byte5 >>> 6);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x3FL) << 11 | byte6 << 3 | byte7 >>> 5);
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x1FL) << 12 | byte8 << 4 | byte9 >>> 4);
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0xFL) << 13 | byte10 << 5 | byte11 >>> 3);
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte11 & 0x7L) << 14 | byte12 << 6 | byte13 >>> 2);
            final long byte14 = blocks[blocksOffset++] & 0xFF;
            final long byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte13 & 0x3L) << 15 | byte14 << 7 | byte15 >>> 1);
            final long byte16 = blocks[blocksOffset++] & 0xFF;
            final long byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x1L) << 16 | byte16 << 8 | byte17);
        }
    }
}
