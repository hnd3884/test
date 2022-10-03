package org.apache.lucene.util.packed;

final class BulkOperationPacked14 extends BulkOperationPacked
{
    public BulkOperationPacked14() {
        super(14);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 50);
            values[valuesOffset++] = (int)(block0 >>> 36 & 0x3FFFL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x3FFFL);
            values[valuesOffset++] = (int)(block0 >>> 8 & 0x3FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFL) << 6 | block2 >>> 58);
            values[valuesOffset++] = (int)(block2 >>> 44 & 0x3FFFL);
            values[valuesOffset++] = (int)(block2 >>> 30 & 0x3FFFL);
            values[valuesOffset++] = (int)(block2 >>> 16 & 0x3FFFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x3FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 12 | block3 >>> 52);
            values[valuesOffset++] = (int)(block3 >>> 38 & 0x3FFFL);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0x3FFFL);
            values[valuesOffset++] = (int)(block3 >>> 10 & 0x3FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3FFL) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 46 & 0x3FFFL);
            values[valuesOffset++] = (int)(block4 >>> 32 & 0x3FFFL);
            values[valuesOffset++] = (int)(block4 >>> 18 & 0x3FFFL);
            values[valuesOffset++] = (int)(block4 >>> 4 & 0x3FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (int)(block5 >>> 40 & 0x3FFFL);
            values[valuesOffset++] = (int)(block5 >>> 26 & 0x3FFFL);
            values[valuesOffset++] = (int)(block5 >>> 12 & 0x3FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFFFL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (int)(block6 >>> 48 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 >>> 34 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 >>> 20 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x3FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 8 | block7 >>> 56);
            values[valuesOffset++] = (int)(block7 >>> 42 & 0x3FFFL);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x3FFFL);
            values[valuesOffset++] = (int)(block7 >>> 14 & 0x3FFFL);
            values[valuesOffset++] = (int)(block7 & 0x3FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 6 | byte2 >>> 2);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            final int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3) << 12 | byte3 << 4 | byte4 >>> 4);
            final int byte5 = blocks[blocksOffset++] & 0xFF;
            final int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0xF) << 10 | byte5 << 2 | byte6 >>> 6);
            final int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3F) << 8 | byte7);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 50;
            values[valuesOffset++] = (block0 >>> 36 & 0x3FFFL);
            values[valuesOffset++] = (block0 >>> 22 & 0x3FFFL);
            values[valuesOffset++] = (block0 >>> 8 & 0x3FFFL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFFL) << 6 | block2 >>> 58);
            values[valuesOffset++] = (block2 >>> 44 & 0x3FFFL);
            values[valuesOffset++] = (block2 >>> 30 & 0x3FFFL);
            values[valuesOffset++] = (block2 >>> 16 & 0x3FFFL);
            values[valuesOffset++] = (block2 >>> 2 & 0x3FFFL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 12 | block3 >>> 52);
            values[valuesOffset++] = (block3 >>> 38 & 0x3FFFL);
            values[valuesOffset++] = (block3 >>> 24 & 0x3FFFL);
            values[valuesOffset++] = (block3 >>> 10 & 0x3FFFL);
            final long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block3 & 0x3FFL) << 4 | block4 >>> 60);
            values[valuesOffset++] = (block4 >>> 46 & 0x3FFFL);
            values[valuesOffset++] = (block4 >>> 32 & 0x3FFFL);
            values[valuesOffset++] = (block4 >>> 18 & 0x3FFFL);
            values[valuesOffset++] = (block4 >>> 4 & 0x3FFFL);
            final long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block4 & 0xFL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (block5 >>> 40 & 0x3FFFL);
            values[valuesOffset++] = (block5 >>> 26 & 0x3FFFL);
            values[valuesOffset++] = (block5 >>> 12 & 0x3FFFL);
            final long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block5 & 0xFFFL) << 2 | block6 >>> 62);
            values[valuesOffset++] = (block6 >>> 48 & 0x3FFFL);
            values[valuesOffset++] = (block6 >>> 34 & 0x3FFFL);
            values[valuesOffset++] = (block6 >>> 20 & 0x3FFFL);
            values[valuesOffset++] = (block6 >>> 6 & 0x3FFFL);
            final long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block6 & 0x3FL) << 8 | block7 >>> 56);
            values[valuesOffset++] = (block7 >>> 42 & 0x3FFFL);
            values[valuesOffset++] = (block7 >>> 28 & 0x3FFFL);
            values[valuesOffset++] = (block7 >>> 14 & 0x3FFFL);
            values[valuesOffset++] = (block7 & 0x3FFFL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 << 6 | byte2 >>> 2);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            final long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0x3L) << 12 | byte3 << 4 | byte4 >>> 4);
            final long byte5 = blocks[blocksOffset++] & 0xFF;
            final long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte4 & 0xFL) << 10 | byte5 << 2 | byte6 >>> 6);
            final long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte6 & 0x3FL) << 8 | byte7);
        }
    }
}
