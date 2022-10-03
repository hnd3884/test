package org.apache.lucene.util.packed;

final class BulkOperationPacked19 extends BulkOperationPacked
{
    public BulkOperationPacked19() {
        super(19);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 45);
            values[valuesOffset++] = (int)(block0 >>> 26 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 7 & 0x7FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x7FL) << 12 | block2 >>> 52);
            values[valuesOffset++] = (int)(block2 >>> 33 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 14 & 0x7FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3FFFL) << 5 | block3 >>> 59);
            values[valuesOffset++] = (int)(block3 >>> 40 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 21 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 2 & 0x7FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3L) << 17 | block4 >>> 47);
            values[valuesOffset++] = (int)(block4 >>> 28 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 9 & 0x7FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FFL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (int)(block5 >>> 35 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 16 & 0x7FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFFFFL) << 3 | block6 >>> 61);
            values[valuesOffset++] = (int)(block6 >>> 42 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 23 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 4 & 0x7FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0xFL) << 15 | block7 >>> 49);
            values[valuesOffset++] = (int)(block7 >>> 30 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 11 & 0x7FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x7FFL) << 8 | block8 >>> 56);
            values[valuesOffset++] = (int)(block8 >>> 37 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 18 & 0x7FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FFFFL) << 1 | block9 >>> 63);
            values[valuesOffset++] = (int)(block9 >>> 44 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 25 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 6 & 0x7FFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x3FL) << 13 | block10 >>> 51);
            values[valuesOffset++] = (int)(block10 >>> 32 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 13 & 0x7FFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x1FFFL) << 6 | block11 >>> 58);
            values[valuesOffset++] = (int)(block11 >>> 39 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 20 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 1 & 0x7FFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x1L) << 18 | block12 >>> 46);
            values[valuesOffset++] = (int)(block12 >>> 27 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block12 >>> 8 & 0x7FFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0xFFL) << 11 | block13 >>> 53);
            values[valuesOffset++] = (int)(block13 >>> 34 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 15 & 0x7FFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0x7FFFL) << 4 | block14 >>> 60);
            values[valuesOffset++] = (int)(block14 >>> 41 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 22 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 3 & 0x7FFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x7L) << 16 | block15 >>> 48);
            values[valuesOffset++] = (int)(block15 >>> 29 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 10 & 0x7FFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0x3FFL) << 9 | block16 >>> 55);
            values[valuesOffset++] = (int)(block16 >>> 36 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 17 & 0x7FFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0x1FFFFL) << 2 | block17 >>> 62);
            values[valuesOffset++] = (int)(block17 >>> 43 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block17 >>> 24 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block17 >>> 5 & 0x7FFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block17 & 0x1FL) << 14 | block18 >>> 50);
            values[valuesOffset++] = (int)(block18 >>> 31 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block18 >>> 12 & 0x7FFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block18 & 0xFFFL) << 7 | block19 >>> 57);
            values[valuesOffset++] = (int)(block19 >>> 38 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block19 >>> 19 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block19 & 0x7FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 11 | byte2 << 3 | byte3 >>> 5);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x1F) << 14 | byte4 << 6 | byte5 >>> 2);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x3) << 17 | byte6 << 9 | byte7 << 1 | byte8 >>> 7);
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x7F) << 12 | byte9 << 4 | byte10 >>> 4);
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0xF) << 15 | byte11 << 7 | byte12 >>> 1);
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            final int byte14 = blocks[blocksOffset++] & 0xFF;
            final int byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x1) << 18 | byte13 << 10 | byte14 << 2 | byte15 >>> 6);
            final int byte16 = blocks[blocksOffset++] & 0xFF;
            final int byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x3F) << 13 | byte16 << 5 | byte17 >>> 3);
            final int byte18 = blocks[blocksOffset++] & 0xFF;
            final int byte19 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte17 & 0x7) << 16 | byte18 << 8 | byte19);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 45;
            values[valuesOffset++] = (block0 >>> 26 & 0x7FFFFL);
            values[valuesOffset++] = (block0 >>> 7 & 0x7FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x7FL) << 12 | block2 >>> 52);
            values[valuesOffset++] = (block2 >>> 33 & 0x7FFFFL);
            values[valuesOffset++] = (block2 >>> 14 & 0x7FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3FFFL) << 5 | block3 >>> 59);
            values[valuesOffset++] = (block3 >>> 40 & 0x7FFFFL);
            values[valuesOffset++] = (block3 >>> 21 & 0x7FFFFL);
            values[valuesOffset++] = (block3 >>> 2 & 0x7FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x3L) << 17 | block4 >>> 47);
            values[valuesOffset++] = (block4 >>> 28 & 0x7FFFFL);
            values[valuesOffset++] = (block4 >>> 9 & 0x7FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x1FFL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (block5 >>> 35 & 0x7FFFFL);
            values[valuesOffset++] = (block5 >>> 16 & 0x7FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0xFFFFL) << 3 | block6 >>> 61);
            values[valuesOffset++] = (block6 >>> 42 & 0x7FFFFL);
            values[valuesOffset++] = (block6 >>> 23 & 0x7FFFFL);
            values[valuesOffset++] = (block6 >>> 4 & 0x7FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0xFL) << 15 | block7 >>> 49);
            values[valuesOffset++] = (block7 >>> 30 & 0x7FFFFL);
            values[valuesOffset++] = (block7 >>> 11 & 0x7FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x7FFL) << 8 | block8 >>> 56);
            values[valuesOffset++] = (block8 >>> 37 & 0x7FFFFL);
            values[valuesOffset++] = (block8 >>> 18 & 0x7FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3FFFFL) << 1 | block9 >>> 63);
            values[valuesOffset++] = (block9 >>> 44 & 0x7FFFFL);
            values[valuesOffset++] = (block9 >>> 25 & 0x7FFFFL);
            values[valuesOffset++] = (block9 >>> 6 & 0x7FFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0x3FL) << 13 | block10 >>> 51);
            values[valuesOffset++] = (block10 >>> 32 & 0x7FFFFL);
            values[valuesOffset++] = (block10 >>> 13 & 0x7FFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x1FFFL) << 6 | block11 >>> 58);
            values[valuesOffset++] = (block11 >>> 39 & 0x7FFFFL);
            values[valuesOffset++] = (block11 >>> 20 & 0x7FFFFL);
            values[valuesOffset++] = (block11 >>> 1 & 0x7FFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x1L) << 18 | block12 >>> 46);
            values[valuesOffset++] = (block12 >>> 27 & 0x7FFFFL);
            values[valuesOffset++] = (block12 >>> 8 & 0x7FFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0xFFL) << 11 | block13 >>> 53);
            values[valuesOffset++] = (block13 >>> 34 & 0x7FFFFL);
            values[valuesOffset++] = (block13 >>> 15 & 0x7FFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block13 & 0x7FFFL) << 4 | block14 >>> 60);
            values[valuesOffset++] = (block14 >>> 41 & 0x7FFFFL);
            values[valuesOffset++] = (block14 >>> 22 & 0x7FFFFL);
            values[valuesOffset++] = (block14 >>> 3 & 0x7FFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block14 & 0x7L) << 16 | block15 >>> 48);
            values[valuesOffset++] = (block15 >>> 29 & 0x7FFFFL);
            values[valuesOffset++] = (block15 >>> 10 & 0x7FFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block15 & 0x3FFL) << 9 | block16 >>> 55);
            values[valuesOffset++] = (block16 >>> 36 & 0x7FFFFL);
            values[valuesOffset++] = (block16 >>> 17 & 0x7FFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block16 & 0x1FFFFL) << 2 | block17 >>> 62);
            values[valuesOffset++] = (block17 >>> 43 & 0x7FFFFL);
            values[valuesOffset++] = (block17 >>> 24 & 0x7FFFFL);
            values[valuesOffset++] = (block17 >>> 5 & 0x7FFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block17 & 0x1FL) << 14 | block18 >>> 50);
            values[valuesOffset++] = (block18 >>> 31 & 0x7FFFFL);
            values[valuesOffset++] = (block18 >>> 12 & 0x7FFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block18 & 0xFFFL) << 7 | block19 >>> 57);
            values[valuesOffset++] = (block19 >>> 38 & 0x7FFFFL);
            values[valuesOffset++] = (block19 >>> 19 & 0x7FFFFL);
            values[valuesOffset++] = (block19 & 0x7FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 11 | byte2 << 3 | byte3 >>> 5);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x1FL) << 14 | byte4 << 6 | byte5 >>> 2);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0x3L) << 17 | byte6 << 9 | byte7 << 1 | byte8 >>> 7);
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x7FL) << 12 | byte9 << 4 | byte10 >>> 4);
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte10 & 0xFL) << 15 | byte11 << 7 | byte12 >>> 1);
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            final long byte14 = blocks[blocksOffset++] & 0xFF;
            final long byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0x1L) << 18 | byte13 << 10 | byte14 << 2 | byte15 >>> 6);
            final long byte16 = blocks[blocksOffset++] & 0xFF;
            final long byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x3FL) << 13 | byte16 << 5 | byte17 >>> 3);
            final long byte18 = blocks[blocksOffset++] & 0xFF;
            final long byte19 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte17 & 0x7L) << 16 | byte18 << 8 | byte19);
        }
    }
}
