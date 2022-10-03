package org.apache.lucene.util.packed;

final class BulkOperationPacked23 extends BulkOperationPacked
{
    public BulkOperationPacked23() {
        super(23);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 41);
            values[valuesOffset++] = (int)(block0 >>> 18 & 0x7FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x3FFFFL) << 5 | block2 >>> 59);
            values[valuesOffset++] = (int)(block2 >>> 36 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 13 & 0x7FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x1FFFL) << 10 | block3 >>> 54);
            values[valuesOffset++] = (int)(block3 >>> 31 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 8 & 0x7FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFL) << 15 | block4 >>> 49);
            values[valuesOffset++] = (int)(block4 >>> 26 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 3 & 0x7FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x7L) << 20 | block5 >>> 44);
            values[valuesOffset++] = (int)(block5 >>> 21 & 0x7FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FFFFFL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (int)(block6 >>> 39 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block6 >>> 16 & 0x7FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0xFFFFL) << 7 | block7 >>> 57);
            values[valuesOffset++] = (int)(block7 >>> 34 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block7 >>> 11 & 0x7FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x7FFL) << 12 | block8 >>> 52);
            values[valuesOffset++] = (int)(block8 >>> 29 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block8 >>> 6 & 0x7FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FL) << 17 | block9 >>> 47);
            values[valuesOffset++] = (int)(block9 >>> 24 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block9 >>> 1 & 0x7FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x1L) << 22 | block10 >>> 42);
            values[valuesOffset++] = (int)(block10 >>> 19 & 0x7FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x7FFFFL) << 4 | block11 >>> 60);
            values[valuesOffset++] = (int)(block11 >>> 37 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block11 >>> 14 & 0x7FFFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x3FFFL) << 9 | block12 >>> 55);
            values[valuesOffset++] = (int)(block12 >>> 32 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block12 >>> 9 & 0x7FFFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x1FFL) << 14 | block13 >>> 50);
            values[valuesOffset++] = (int)(block13 >>> 27 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block13 >>> 4 & 0x7FFFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0xFL) << 19 | block14 >>> 45);
            values[valuesOffset++] = (int)(block14 >>> 22 & 0x7FFFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x3FFFFFL) << 1 | block15 >>> 63);
            values[valuesOffset++] = (int)(block15 >>> 40 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block15 >>> 17 & 0x7FFFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0x1FFFFL) << 6 | block16 >>> 58);
            values[valuesOffset++] = (int)(block16 >>> 35 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block16 >>> 12 & 0x7FFFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0xFFFL) << 11 | block17 >>> 53);
            values[valuesOffset++] = (int)(block17 >>> 30 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block17 >>> 7 & 0x7FFFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block17 & 0x7FL) << 16 | block18 >>> 48);
            values[valuesOffset++] = (int)(block18 >>> 25 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block18 >>> 2 & 0x7FFFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block18 & 0x3L) << 21 | block19 >>> 43);
            values[valuesOffset++] = (int)(block19 >>> 20 & 0x7FFFFFL);
            final long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block19 & 0xFFFFFL) << 3 | block20 >>> 61);
            values[valuesOffset++] = (int)(block20 >>> 38 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block20 >>> 15 & 0x7FFFFFL);
            final long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block20 & 0x7FFFL) << 8 | block21 >>> 56);
            values[valuesOffset++] = (int)(block21 >>> 33 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block21 >>> 10 & 0x7FFFFFL);
            final long block22 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block21 & 0x3FFL) << 13 | block22 >>> 51);
            values[valuesOffset++] = (int)(block22 >>> 28 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block22 >>> 5 & 0x7FFFFFL);
            final long block23 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block22 & 0x1FL) << 18 | block23 >>> 46);
            values[valuesOffset++] = (int)(block23 >>> 23 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block23 & 0x7FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 15 | byte2 << 7 | byte3 >>> 1);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x1) << 22 | byte4 << 14 | byte5 << 6 | byte6 >>> 2);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3) << 21 | byte7 << 13 | byte8 << 5 | byte9 >>> 3);
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            final int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x7) << 20 | byte10 << 12 | byte11 << 4 | byte12 >>> 4);
            final int byte13 = blocks[blocksOffset++] & 0xFF;
            final int byte14 = blocks[blocksOffset++] & 0xFF;
            final int byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0xF) << 19 | byte13 << 11 | byte14 << 3 | byte15 >>> 5);
            final int byte16 = blocks[blocksOffset++] & 0xFF;
            final int byte17 = blocks[blocksOffset++] & 0xFF;
            final int byte18 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x1F) << 18 | byte16 << 10 | byte17 << 2 | byte18 >>> 6);
            final int byte19 = blocks[blocksOffset++] & 0xFF;
            final int byte20 = blocks[blocksOffset++] & 0xFF;
            final int byte21 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte18 & 0x3F) << 17 | byte19 << 9 | byte20 << 1 | byte21 >>> 7);
            final int byte22 = blocks[blocksOffset++] & 0xFF;
            final int byte23 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte21 & 0x7F) << 16 | byte22 << 8 | byte23);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 41;
            values[valuesOffset++] = (block0 >>> 18 & 0x7FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x3FFFFL) << 5 | block2 >>> 59);
            values[valuesOffset++] = (block2 >>> 36 & 0x7FFFFFL);
            values[valuesOffset++] = (block2 >>> 13 & 0x7FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x1FFFL) << 10 | block3 >>> 54);
            values[valuesOffset++] = (block3 >>> 31 & 0x7FFFFFL);
            values[valuesOffset++] = (block3 >>> 8 & 0x7FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0xFFL) << 15 | block4 >>> 49);
            values[valuesOffset++] = (block4 >>> 26 & 0x7FFFFFL);
            values[valuesOffset++] = (block4 >>> 3 & 0x7FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x7L) << 20 | block5 >>> 44);
            values[valuesOffset++] = (block5 >>> 21 & 0x7FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x1FFFFFL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (block6 >>> 39 & 0x7FFFFFL);
            values[valuesOffset++] = (block6 >>> 16 & 0x7FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0xFFFFL) << 7 | block7 >>> 57);
            values[valuesOffset++] = (block7 >>> 34 & 0x7FFFFFL);
            values[valuesOffset++] = (block7 >>> 11 & 0x7FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0x7FFL) << 12 | block8 >>> 52);
            values[valuesOffset++] = (block8 >>> 29 & 0x7FFFFFL);
            values[valuesOffset++] = (block8 >>> 6 & 0x7FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3FL) << 17 | block9 >>> 47);
            values[valuesOffset++] = (block9 >>> 24 & 0x7FFFFFL);
            values[valuesOffset++] = (block9 >>> 1 & 0x7FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0x1L) << 22 | block10 >>> 42);
            values[valuesOffset++] = (block10 >>> 19 & 0x7FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x7FFFFL) << 4 | block11 >>> 60);
            values[valuesOffset++] = (block11 >>> 37 & 0x7FFFFFL);
            values[valuesOffset++] = (block11 >>> 14 & 0x7FFFFFL);
            final long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block11 & 0x3FFFL) << 9 | block12 >>> 55);
            values[valuesOffset++] = (block12 >>> 32 & 0x7FFFFFL);
            values[valuesOffset++] = (block12 >>> 9 & 0x7FFFFFL);
            final long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block12 & 0x1FFL) << 14 | block13 >>> 50);
            values[valuesOffset++] = (block13 >>> 27 & 0x7FFFFFL);
            values[valuesOffset++] = (block13 >>> 4 & 0x7FFFFFL);
            final long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block13 & 0xFL) << 19 | block14 >>> 45);
            values[valuesOffset++] = (block14 >>> 22 & 0x7FFFFFL);
            final long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block14 & 0x3FFFFFL) << 1 | block15 >>> 63);
            values[valuesOffset++] = (block15 >>> 40 & 0x7FFFFFL);
            values[valuesOffset++] = (block15 >>> 17 & 0x7FFFFFL);
            final long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block15 & 0x1FFFFL) << 6 | block16 >>> 58);
            values[valuesOffset++] = (block16 >>> 35 & 0x7FFFFFL);
            values[valuesOffset++] = (block16 >>> 12 & 0x7FFFFFL);
            final long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block16 & 0xFFFL) << 11 | block17 >>> 53);
            values[valuesOffset++] = (block17 >>> 30 & 0x7FFFFFL);
            values[valuesOffset++] = (block17 >>> 7 & 0x7FFFFFL);
            final long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block17 & 0x7FL) << 16 | block18 >>> 48);
            values[valuesOffset++] = (block18 >>> 25 & 0x7FFFFFL);
            values[valuesOffset++] = (block18 >>> 2 & 0x7FFFFFL);
            final long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block18 & 0x3L) << 21 | block19 >>> 43);
            values[valuesOffset++] = (block19 >>> 20 & 0x7FFFFFL);
            final long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block19 & 0xFFFFFL) << 3 | block20 >>> 61);
            values[valuesOffset++] = (block20 >>> 38 & 0x7FFFFFL);
            values[valuesOffset++] = (block20 >>> 15 & 0x7FFFFFL);
            final long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block20 & 0x7FFFL) << 8 | block21 >>> 56);
            values[valuesOffset++] = (block21 >>> 33 & 0x7FFFFFL);
            values[valuesOffset++] = (block21 >>> 10 & 0x7FFFFFL);
            final long block22 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block21 & 0x3FFL) << 13 | block22 >>> 51);
            values[valuesOffset++] = (block22 >>> 28 & 0x7FFFFFL);
            values[valuesOffset++] = (block22 >>> 5 & 0x7FFFFFL);
            final long block23 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block22 & 0x1FL) << 18 | block23 >>> 46);
            values[valuesOffset++] = (block23 >>> 23 & 0x7FFFFFL);
            values[valuesOffset++] = (block23 & 0x7FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 15 | byte2 << 7 | byte3 >>> 1);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x1L) << 22 | byte4 << 14 | byte5 << 6 | byte6 >>> 2);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3L) << 21 | byte7 << 13 | byte8 << 5 | byte9 >>> 3);
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            final long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x7L) << 20 | byte10 << 12 | byte11 << 4 | byte12 >>> 4);
            final long byte13 = blocks[blocksOffset++] & 0xFF;
            final long byte14 = blocks[blocksOffset++] & 0xFF;
            final long byte15 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte12 & 0xFL) << 19 | byte13 << 11 | byte14 << 3 | byte15 >>> 5);
            final long byte16 = blocks[blocksOffset++] & 0xFF;
            final long byte17 = blocks[blocksOffset++] & 0xFF;
            final long byte18 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte15 & 0x1FL) << 18 | byte16 << 10 | byte17 << 2 | byte18 >>> 6);
            final long byte19 = blocks[blocksOffset++] & 0xFF;
            final long byte20 = blocks[blocksOffset++] & 0xFF;
            final long byte21 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte18 & 0x3FL) << 17 | byte19 << 9 | byte20 << 1 | byte21 >>> 7);
            final long byte22 = blocks[blocksOffset++] & 0xFF;
            final long byte23 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte21 & 0x7FL) << 16 | byte22 << 8 | byte23);
        }
    }
}
