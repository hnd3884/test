package org.apache.lucene.util.packed;

final class BulkOperationPacked22 extends BulkOperationPacked
{
    public BulkOperationPacked22() {
        super(22);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 42);
            values[valuesOffset++] = (int)(block0 >>> 20 & 0x3FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFFFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 40 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 18 & 0x3FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3FFFFL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 38 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0x3FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFFFL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (int)(block4 >>> 36 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 14 & 0x3FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x3FFFL) << 8 | block5 >>> 56);
            values[valuesOffset++] = (int)(block5 >>> 34 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block5 >>> 12 & 0x3FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFFFL) << 10 | block6 >>> 54);
            values[valuesOffset++] = (int)(block6 >>> 32 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block6 >>> 10 & 0x3FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FFL) << 12 | block7 >>> 52);
            values[valuesOffset++] = (int)(block7 >>> 30 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block7 >>> 8 & 0x3FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0xFFL) << 14 | block8 >>> 50);
            values[valuesOffset++] = (int)(block8 >>> 28 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block8 >>> 6 & 0x3FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FL) << 16 | block9 >>> 48);
            values[valuesOffset++] = (int)(block9 >>> 26 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block9 >>> 4 & 0x3FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0xFL) << 18 | block10 >>> 46);
            values[valuesOffset++] = (int)(block10 >>> 24 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block10 >>> 2 & 0x3FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3L) << 20 | block11 >>> 44);
            values[valuesOffset++] = (int)(block11 >>> 22 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block11 & 0x3FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 14 | byte2 << 6 | byte3 >>> 2);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3) << 20 | byte4 << 12 | byte5 << 4 | byte6 >>> 4);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0xF) << 18 | byte7 << 10 | byte8 << 2 | byte9 >>> 6);
            final int byte10 = blocks[blocksOffset++] & 0xFF;
            final int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x3F) << 16 | byte10 << 8 | byte11);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 42;
            values[valuesOffset++] = (block0 >>> 20 & 0x3FFFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFFFFFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (block2 >>> 40 & 0x3FFFFFL);
            values[valuesOffset++] = (block2 >>> 18 & 0x3FFFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3FFFFL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (block3 >>> 38 & 0x3FFFFFL);
            values[valuesOffset++] = (block3 >>> 16 & 0x3FFFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0xFFFFL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (block4 >>> 36 & 0x3FFFFFL);
            values[valuesOffset++] = (block4 >>> 14 & 0x3FFFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0x3FFFL) << 8 | block5 >>> 56);
            values[valuesOffset++] = (block5 >>> 34 & 0x3FFFFFL);
            values[valuesOffset++] = (block5 >>> 12 & 0x3FFFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0xFFFL) << 10 | block6 >>> 54);
            values[valuesOffset++] = (block6 >>> 32 & 0x3FFFFFL);
            values[valuesOffset++] = (block6 >>> 10 & 0x3FFFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FFL) << 12 | block7 >>> 52);
            values[valuesOffset++] = (block7 >>> 30 & 0x3FFFFFL);
            values[valuesOffset++] = (block7 >>> 8 & 0x3FFFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0xFFL) << 14 | block8 >>> 50);
            values[valuesOffset++] = (block8 >>> 28 & 0x3FFFFFL);
            values[valuesOffset++] = (block8 >>> 6 & 0x3FFFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0x3FL) << 16 | block9 >>> 48);
            values[valuesOffset++] = (block9 >>> 26 & 0x3FFFFFL);
            values[valuesOffset++] = (block9 >>> 4 & 0x3FFFFFL);
            final long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block9 & 0xFL) << 18 | block10 >>> 46);
            values[valuesOffset++] = (block10 >>> 24 & 0x3FFFFFL);
            values[valuesOffset++] = (block10 >>> 2 & 0x3FFFFFL);
            final long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block10 & 0x3L) << 20 | block11 >>> 44);
            values[valuesOffset++] = (block11 >>> 22 & 0x3FFFFFL);
            values[valuesOffset++] = (block11 & 0x3FFFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 14 | byte2 << 6 | byte3 >>> 2);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3L) << 20 | byte4 << 12 | byte5 << 4 | byte6 >>> 4);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0xFL) << 18 | byte7 << 10 | byte8 << 2 | byte9 >>> 6);
            final long byte10 = blocks[blocksOffset++] & 0xFF;
            final long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte9 & 0x3FL) << 16 | byte10 << 8 | byte11);
        }
    }
}
