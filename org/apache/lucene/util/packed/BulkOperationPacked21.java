package org.apache.lucene.util.packed;

final class BulkOperationPacked21 extends BulkOperationPacked
{
    public BulkOperationPacked21() {
        super(21);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 43);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x1FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1L) << 20 | block2 >>> 44);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x1FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 19 | block3 >>> 45);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x1FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x7L) << 18 | block4 >>> 46);
            values[valuesOffset++] = (int)(block4 >>> 25 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 4 & 0x1FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFL) << 17 | block5 >>> 47);
            values[valuesOffset++] = (int)(block5 >>> 26 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block5 >>> 5 & 0x1FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FL) << 16 | block6 >>> 48);
            values[valuesOffset++] = (int)(block6 >>> 27 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x1FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 15 | block7 >>> 49);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block7 >>> 7 & 0x1FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x7FL) << 14 | block8 >>> 50);
            values[valuesOffset++] = (int)(block8 >>> 29 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block8 >>> 8 & 0x1FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFFL) << 13 | block9 >>> 51);
            values[valuesOffset++] = (int)(block9 >>> 30 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block9 >>> 9 & 0x1FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x1FFL) << 12 | block10 >>> 52);
            values[valuesOffset++] = (int)(block10 >>> 31 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block10 >>> 10 & 0x1FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3FFL) << 11 | block11 >>> 53);
            values[valuesOffset++] = (int)(block11 >>> 32 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block11 >>> 11 & 0x1FFFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x7FFL) << 10 | block12 >>> 54);
            values[valuesOffset++] = (int)(block12 >>> 33 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block12 >>> 12 & 0x1FFFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0xFFFL) << 9 | block13 >>> 55);
            values[valuesOffset++] = (int)(block13 >>> 34 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block13 >>> 13 & 0x1FFFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0x1FFFL) << 8 | block14 >>> 56);
            values[valuesOffset++] = (int)(block14 >>> 35 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block14 >>> 14 & 0x1FFFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x3FFFL) << 7 | block15 >>> 57);
            values[valuesOffset++] = (int)(block15 >>> 36 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block15 >>> 15 & 0x1FFFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0x7FFFL) << 6 | block16 >>> 58);
            values[valuesOffset++] = (int)(block16 >>> 37 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block16 >>> 16 & 0x1FFFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0xFFFFL) << 5 | block17 >>> 59);
            values[valuesOffset++] = (int)(block17 >>> 38 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block17 >>> 17 & 0x1FFFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block17 & 0x1FFFFL) << 4 | block18 >>> 60);
            values[valuesOffset++] = (int)(block18 >>> 39 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block18 >>> 18 & 0x1FFFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block18 & 0x3FFFFL) << 3 | block19 >>> 61);
            values[valuesOffset++] = (int)(block19 >>> 40 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block19 >>> 19 & 0x1FFFFFL);
            final long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block19 & 0x7FFFFL) << 2 | block20 >>> 62);
            values[valuesOffset++] = (int)(block20 >>> 41 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block20 >>> 20 & 0x1FFFFFL);
            final long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block20 & 0xFFFFFL) << 1 | block21 >>> 63);
            values[valuesOffset++] = (int)(block21 >>> 42 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block21 >>> 21 & 0x1FFFFFL);
            values[valuesOffset++] = (int)(block21 & 0x1FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 13 | byte2 << 5 | byte3 >>> 3);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7) << 18 | byte4 << 10 | byte5 << 2 | byte6 >>> 6);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3F) << 15 | byte7 << 7 | byte8 >>> 1);
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x1) << 20 | byte9 << 12 | byte10 << 4 | byte11 >>> 4);
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            final int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte11 & 0xF) << 17 | byte12 << 9 | byte13 << 1 | byte14 >>> 7);
            final int byte15 = blocks[blocksOffset++] & 0xFF;
            final int byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte14 & 0x7F) << 14 | byte15 << 6 | byte16 >>> 2);
            final int byte17 = blocks[blocksOffset++] & 0xFF;
            final int byte18 = blocks[blocksOffset++] & 0xFF;
            final int byte19 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte16 & 0x3) << 19 | byte17 << 11 | byte18 << 3 | byte19 >>> 5);
            final int byte20 = blocks[blocksOffset++] & 0xFF;
            final int byte21 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte19 & 0x1F) << 16 | byte20 << 8 | byte21);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 43;
            values[valuesOffset++] = (block0 >>> 22 & 0x1FFFFFL);
            values[valuesOffset++] = (block0 >>> 1 & 0x1FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x1L) << 20 | block2 >>> 44);
            values[valuesOffset++] = (block2 >>> 23 & 0x1FFFFFL);
            values[valuesOffset++] = (block2 >>> 2 & 0x1FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 19 | block3 >>> 45);
            values[valuesOffset++] = (block3 >>> 24 & 0x1FFFFFL);
            values[valuesOffset++] = (block3 >>> 3 & 0x1FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x7L) << 18 | block4 >>> 46);
            values[valuesOffset++] = (block4 >>> 25 & 0x1FFFFFL);
            values[valuesOffset++] = (block4 >>> 4 & 0x1FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFL) << 17 | block5 >>> 47);
            values[valuesOffset++] = (block5 >>> 26 & 0x1FFFFFL);
            values[valuesOffset++] = (block5 >>> 5 & 0x1FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1FL) << 16 | block6 >>> 48);
            values[valuesOffset++] = (block6 >>> 27 & 0x1FFFFFL);
            values[valuesOffset++] = (block6 >>> 6 & 0x1FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FL) << 15 | block7 >>> 49);
            values[valuesOffset++] = (block7 >>> 28 & 0x1FFFFFL);
            values[valuesOffset++] = (block7 >>> 7 & 0x1FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x7FL) << 14 | block8 >>> 50);
            values[valuesOffset++] = (block8 >>> 29 & 0x1FFFFFL);
            values[valuesOffset++] = (block8 >>> 8 & 0x1FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0xFFL) << 13 | block9 >>> 51);
            values[valuesOffset++] = (block9 >>> 30 & 0x1FFFFFL);
            values[valuesOffset++] = (block9 >>> 9 & 0x1FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0x1FFL) << 12 | block10 >>> 52);
            values[valuesOffset++] = (block10 >>> 31 & 0x1FFFFFL);
            values[valuesOffset++] = (block10 >>> 10 & 0x1FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x3FFL) << 11 | block11 >>> 53);
            values[valuesOffset++] = (block11 >>> 32 & 0x1FFFFFL);
            values[valuesOffset++] = (block11 >>> 11 & 0x1FFFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x7FFL) << 10 | block12 >>> 54);
            values[valuesOffset++] = (block12 >>> 33 & 0x1FFFFFL);
            values[valuesOffset++] = (block12 >>> 12 & 0x1FFFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0xFFFL) << 9 | block13 >>> 55);
            values[valuesOffset++] = (block13 >>> 34 & 0x1FFFFFL);
            values[valuesOffset++] = (block13 >>> 13 & 0x1FFFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block13 & 0x1FFFL) << 8 | block14 >>> 56);
            values[valuesOffset++] = (block14 >>> 35 & 0x1FFFFFL);
            values[valuesOffset++] = (block14 >>> 14 & 0x1FFFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block14 & 0x3FFFL) << 7 | block15 >>> 57);
            values[valuesOffset++] = (block15 >>> 36 & 0x1FFFFFL);
            values[valuesOffset++] = (block15 >>> 15 & 0x1FFFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block15 & 0x7FFFL) << 6 | block16 >>> 58);
            values[valuesOffset++] = (block16 >>> 37 & 0x1FFFFFL);
            values[valuesOffset++] = (block16 >>> 16 & 0x1FFFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block16 & 0xFFFFL) << 5 | block17 >>> 59);
            values[valuesOffset++] = (block17 >>> 38 & 0x1FFFFFL);
            values[valuesOffset++] = (block17 >>> 17 & 0x1FFFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block17 & 0x1FFFFL) << 4 | block18 >>> 60);
            values[valuesOffset++] = (block18 >>> 39 & 0x1FFFFFL);
            values[valuesOffset++] = (block18 >>> 18 & 0x1FFFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block18 & 0x3FFFFL) << 3 | block19 >>> 61);
            values[valuesOffset++] = (block19 >>> 40 & 0x1FFFFFL);
            values[valuesOffset++] = (block19 >>> 19 & 0x1FFFFFL);
            final long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block19 & 0x7FFFFL) << 2 | block20 >>> 62);
            values[valuesOffset++] = (block20 >>> 41 & 0x1FFFFFL);
            values[valuesOffset++] = (block20 >>> 20 & 0x1FFFFFL);
            final long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block20 & 0xFFFFFL) << 1 | block21 >>> 63);
            values[valuesOffset++] = (block21 >>> 42 & 0x1FFFFFL);
            values[valuesOffset++] = (block21 >>> 21 & 0x1FFFFFL);
            values[valuesOffset++] = (block21 & 0x1FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 13 | byte2 << 5 | byte3 >>> 3);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x7L) << 18 | byte4 << 10 | byte5 << 2 | byte6 >>> 6);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3FL) << 15 | byte7 << 7 | byte8 >>> 1);
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte8 & 0x1L) << 20 | byte9 << 12 | byte10 << 4 | byte11 >>> 4);
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            final long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte11 & 0xFL) << 17 | byte12 << 9 | byte13 << 1 | byte14 >>> 7);
            final long byte15 = blocks[blocksOffset++] & 0xFF;
            final long byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte14 & 0x7FL) << 14 | byte15 << 6 | byte16 >>> 2);
            final long byte17 = blocks[blocksOffset++] & 0xFF;
            final long byte18 = blocks[blocksOffset++] & 0xFF;
            final long byte19 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte16 & 0x3L) << 19 | byte17 << 11 | byte18 << 3 | byte19 >>> 5);
            final long byte20 = blocks[blocksOffset++] & 0xFF;
            final long byte21 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte19 & 0x1FL) << 16 | byte20 << 8 | byte21);
        }
    }
}
