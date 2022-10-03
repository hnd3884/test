package org.apache.lucene.util.packed;

final class BulkOperationPacked6 extends BulkOperationPacked
{
    public BulkOperationPacked6() {
        super(6);
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 58);
            values[valuesOffset++] = (int)(block0 >>> 52 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 46 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 40 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x3FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 56 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 50 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 44 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 26 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 20 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 14 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x3FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3L) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 54 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 48 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 42 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 30 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 24 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 18 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 12 & 0x3FL);
            values[valuesOffset++] = (int)(block3 >>> 6 & 0x3FL);
            values[valuesOffset++] = (int)(block3 & 0x3FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 2;
            final int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x3) << 4 | byte2 >>> 4);
            final int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0xF) << 2 | byte3 >>> 6);
            values[valuesOffset++] = (byte3 & 0x3F);
        }
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 58;
            values[valuesOffset++] = (block0 >>> 52 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 46 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 40 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 34 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 28 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 22 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 16 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 10 & 0x3FL);
            values[valuesOffset++] = (block0 >>> 4 & 0x3FL);
            final long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block0 & 0xFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (block2 >>> 56 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 50 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 44 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 38 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 32 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 26 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 20 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 14 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 8 & 0x3FL);
            values[valuesOffset++] = (block2 >>> 2 & 0x3FL);
            final long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = ((block2 & 0x3L) << 4 | block3 >>> 60);
            values[valuesOffset++] = (block3 >>> 54 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 48 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 42 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 36 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 30 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 24 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 18 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 12 & 0x3FL);
            values[valuesOffset++] = (block3 >>> 6 & 0x3FL);
            values[valuesOffset++] = (block3 & 0x3FL);
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 2;
            final long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte0 & 0x3L) << 4 | byte2 >>> 4);
            final long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = ((byte2 & 0xFL) << 2 | byte3 >>> 6);
            values[valuesOffset++] = (byte3 & 0x3FL);
        }
    }
}
