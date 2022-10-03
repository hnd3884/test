package org.apache.lucene.util.packed;

final class BulkOperationPacked18 extends BulkOperationPacked
{
    public BulkOperationPacked18() {
        super(18);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 46);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x3FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x3FFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 20 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x3FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 16 | block3 >>> 48);
            values[valuesOffset++] = (int)(block3 >>> 30 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x3FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFFL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 22 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 4 & 0x3FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFL) << 14 | block5 >>> 50);
            values[valuesOffset++] = (int)(block5 >>> 32 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 14 & 0x3FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FFFL) << 4 | block6 >>> 60);
            values[valuesOffset++] = (int)(block6 >>> 42 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 24 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x3FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 12 | block7 >>> 52);
            values[valuesOffset++] = (int)(block7 >>> 34 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 16 & 0x3FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0xFFFFL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (int)(block8 >>> 44 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 26 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 8 & 0x3FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFFL) << 10 | block9 >>> 54);
            values[valuesOffset++] = (int)(block9 >>> 36 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 18 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block9 & 0x3FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 10 | byte2 << 2 | byte3 >>> 6);
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3F) << 12 | byte4 << 4 | byte5 >>> 4);
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0xF) << 14 | byte6 << 6 | byte7 >>> 2);
            final int byte8 = blocks[blocksOffset++] & 0xFF;
            final int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x3) << 16 | byte8 << 8 | byte9);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 46;
            values[valuesOffset++] = (block0 >>> 28 & 0x3FFFFL);
            values[valuesOffset++] = (block0 >>> 10 & 0x3FFFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0x3FFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (block2 >>> 38 & 0x3FFFFL);
            values[valuesOffset++] = (block2 >>> 20 & 0x3FFFFL);
            values[valuesOffset++] = (block2 >>> 2 & 0x3FFFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 16 | block3 >>> 48);
            values[valuesOffset++] = (block3 >>> 30 & 0x3FFFFL);
            values[valuesOffset++] = (block3 >>> 12 & 0x3FFFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0xFFFL) << 6 | block4 >>> 58);
            values[valuesOffset++] = (block4 >>> 40 & 0x3FFFFL);
            values[valuesOffset++] = (block4 >>> 22 & 0x3FFFFL);
            values[valuesOffset++] = (block4 >>> 4 & 0x3FFFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFL) << 14 | block5 >>> 50);
            values[valuesOffset++] = (block5 >>> 32 & 0x3FFFFL);
            values[valuesOffset++] = (block5 >>> 14 & 0x3FFFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0x3FFFL) << 4 | block6 >>> 60);
            values[valuesOffset++] = (block6 >>> 42 & 0x3FFFFL);
            values[valuesOffset++] = (block6 >>> 24 & 0x3FFFFL);
            values[valuesOffset++] = (block6 >>> 6 & 0x3FFFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FL) << 12 | block7 >>> 52);
            values[valuesOffset++] = (block7 >>> 34 & 0x3FFFFL);
            values[valuesOffset++] = (block7 >>> 16 & 0x3FFFFL);
            final long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block7 & 0xFFFFL) << 2 | block8 >>> 62);
            values[valuesOffset++] = (block8 >>> 44 & 0x3FFFFL);
            values[valuesOffset++] = (block8 >>> 26 & 0x3FFFFL);
            values[valuesOffset++] = (block8 >>> 8 & 0x3FFFFL);
            final long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block8 & 0xFFL) << 10 | block9 >>> 54);
            values[valuesOffset++] = (block9 >>> 36 & 0x3FFFFL);
            values[valuesOffset++] = (block9 >>> 18 & 0x3FFFFL);
            values[valuesOffset++] = (block9 & 0x3FFFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 10 | byte2 << 2 | byte3 >>> 6);
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte3 & 0x3FL) << 12 | byte4 << 4 | byte5 >>> 4);
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte5 & 0xFL) << 14 | byte6 << 6 | byte7 >>> 2);
            final long byte8 = blocks[blocksOffset++] & 0xFF;
            final long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte7 & 0x3L) << 16 | byte8 << 8 | byte9);
        }
    }
}
