package org.apache.lucene.util.packed;

class BulkOperationPacked extends BulkOperation
{
    private final int bitsPerValue;
    private final int longBlockCount;
    private final int longValueCount;
    private final int byteBlockCount;
    private final int byteValueCount;
    private final long mask;
    private final int intMask;
    
    public BulkOperationPacked(final int bitsPerValue) {
        this.bitsPerValue = bitsPerValue;
        assert bitsPerValue > 0 && bitsPerValue <= 64;
        int blocks;
        for (blocks = bitsPerValue; (blocks & 0x1) == 0x0; blocks >>>= 1) {}
        this.longBlockCount = blocks;
        this.longValueCount = 64 * this.longBlockCount / bitsPerValue;
        int byteBlockCount;
        int byteValueCount;
        for (byteBlockCount = 8 * this.longBlockCount, byteValueCount = this.longValueCount; (byteBlockCount & 0x1) == 0x0 && (byteValueCount & 0x1) == 0x0; byteBlockCount >>>= 1, byteValueCount >>>= 1) {}
        this.byteBlockCount = byteBlockCount;
        this.byteValueCount = byteValueCount;
        if (bitsPerValue == 64) {
            this.mask = -1L;
        }
        else {
            this.mask = (1L << bitsPerValue) - 1L;
        }
        this.intMask = (int)this.mask;
        assert this.longValueCount * bitsPerValue == 64 * this.longBlockCount;
    }
    
    @Override
    public int longBlockCount() {
        return this.longBlockCount;
    }
    
    @Override
    public int longValueCount() {
        return this.longValueCount;
    }
    
    @Override
    public int byteBlockCount() {
        return this.byteBlockCount;
    }
    
    @Override
    public int byteValueCount() {
        return this.byteValueCount;
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            bitsLeft -= this.bitsPerValue;
            if (bitsLeft < 0) {
                values[valuesOffset++] = ((blocks[blocksOffset++] & (1L << this.bitsPerValue + bitsLeft) - 1L) << -bitsLeft | blocks[blocksOffset] >>> 64 + bitsLeft);
                bitsLeft += 64;
            }
            else {
                values[valuesOffset++] = (blocks[blocksOffset] >>> bitsLeft & this.mask);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final long[] values, int valuesOffset, final int iterations) {
        long nextValue = 0L;
        int bitsLeft = this.bitsPerValue;
        for (int i = 0; i < iterations * this.byteBlockCount; ++i) {
            final long bytes = (long)blocks[blocksOffset++] & 0xFFL;
            if (bitsLeft > 8) {
                bitsLeft -= 8;
                nextValue |= bytes << bitsLeft;
            }
            else {
                int bits = 8 - bitsLeft;
                values[valuesOffset++] = (nextValue | bytes >>> bits);
                while (bits >= this.bitsPerValue) {
                    bits -= this.bitsPerValue;
                    values[valuesOffset++] = (bytes >>> bits & this.mask);
                }
                bitsLeft = this.bitsPerValue - bits;
                nextValue = (bytes & (1L << bits) - 1L) << bitsLeft;
            }
        }
        assert bitsLeft == this.bitsPerValue;
    }
    
    @Override
    public void decode(final long[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            bitsLeft -= this.bitsPerValue;
            if (bitsLeft < 0) {
                values[valuesOffset++] = (int)((blocks[blocksOffset++] & (1L << this.bitsPerValue + bitsLeft) - 1L) << -bitsLeft | blocks[blocksOffset] >>> 64 + bitsLeft);
                bitsLeft += 64;
            }
            else {
                values[valuesOffset++] = (int)(blocks[blocksOffset] >>> bitsLeft & this.mask);
            }
        }
    }
    
    @Override
    public void decode(final byte[] blocks, int blocksOffset, final int[] values, int valuesOffset, final int iterations) {
        int nextValue = 0;
        int bitsLeft = this.bitsPerValue;
        for (int i = 0; i < iterations * this.byteBlockCount; ++i) {
            final int bytes = blocks[blocksOffset++] & 0xFF;
            if (bitsLeft > 8) {
                bitsLeft -= 8;
                nextValue |= bytes << bitsLeft;
            }
            else {
                int bits = 8 - bitsLeft;
                values[valuesOffset++] = (nextValue | bytes >>> bits);
                while (bits >= this.bitsPerValue) {
                    bits -= this.bitsPerValue;
                    values[valuesOffset++] = (bytes >>> bits & this.intMask);
                }
                bitsLeft = this.bitsPerValue - bits;
                nextValue = (bytes & (1 << bits) - 1) << bitsLeft;
            }
        }
        assert bitsLeft == this.bitsPerValue;
    }
    
    @Override
    public void encode(final long[] values, int valuesOffset, final long[] blocks, int blocksOffset, final int iterations) {
        long nextBlock = 0L;
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            bitsLeft -= this.bitsPerValue;
            if (bitsLeft > 0) {
                nextBlock |= values[valuesOffset++] << bitsLeft;
            }
            else if (bitsLeft == 0) {
                nextBlock |= values[valuesOffset++];
                blocks[blocksOffset++] = nextBlock;
                nextBlock = 0L;
                bitsLeft = 64;
            }
            else {
                nextBlock |= values[valuesOffset] >>> -bitsLeft;
                blocks[blocksOffset++] = nextBlock;
                nextBlock = (values[valuesOffset++] & (1L << -bitsLeft) - 1L) << 64 + bitsLeft;
                bitsLeft += 64;
            }
        }
    }
    
    @Override
    public void encode(final int[] values, int valuesOffset, final long[] blocks, int blocksOffset, final int iterations) {
        long nextBlock = 0L;
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            bitsLeft -= this.bitsPerValue;
            if (bitsLeft > 0) {
                nextBlock |= ((long)values[valuesOffset++] & 0xFFFFFFFFL) << bitsLeft;
            }
            else if (bitsLeft == 0) {
                nextBlock |= ((long)values[valuesOffset++] & 0xFFFFFFFFL);
                blocks[blocksOffset++] = nextBlock;
                nextBlock = 0L;
                bitsLeft = 64;
            }
            else {
                nextBlock |= ((long)values[valuesOffset] & 0xFFFFFFFFL) >>> -bitsLeft;
                blocks[blocksOffset++] = nextBlock;
                nextBlock = ((long)values[valuesOffset++] & (1L << -bitsLeft) - 1L) << 64 + bitsLeft;
                bitsLeft += 64;
            }
        }
    }
    
    @Override
    public void encode(final long[] values, int valuesOffset, final byte[] blocks, int blocksOffset, final int iterations) {
        int nextBlock = 0;
        int bitsLeft = 8;
        for (int i = 0; i < this.byteValueCount * iterations; ++i) {
            final long v = values[valuesOffset++];
            assert PackedInts.unsignedBitsRequired(v) <= this.bitsPerValue;
            if (this.bitsPerValue < bitsLeft) {
                nextBlock = (int)((long)nextBlock | v << bitsLeft - this.bitsPerValue);
                bitsLeft -= this.bitsPerValue;
            }
            else {
                int bits = this.bitsPerValue - bitsLeft;
                blocks[blocksOffset++] = (byte)((long)nextBlock | v >>> bits);
                while (bits >= 8) {
                    bits -= 8;
                    blocks[blocksOffset++] = (byte)(v >>> bits);
                }
                bitsLeft = 8 - bits;
                nextBlock = (int)((v & (1L << bits) - 1L) << bitsLeft);
            }
        }
        assert bitsLeft == 8;
    }
    
    @Override
    public void encode(final int[] values, int valuesOffset, final byte[] blocks, int blocksOffset, final int iterations) {
        int nextBlock = 0;
        int bitsLeft = 8;
        for (int i = 0; i < this.byteValueCount * iterations; ++i) {
            final int v = values[valuesOffset++];
            assert PackedInts.bitsRequired((long)v & 0xFFFFFFFFL) <= this.bitsPerValue;
            if (this.bitsPerValue < bitsLeft) {
                nextBlock |= v << bitsLeft - this.bitsPerValue;
                bitsLeft -= this.bitsPerValue;
            }
            else {
                int bits = this.bitsPerValue - bitsLeft;
                blocks[blocksOffset++] = (byte)(nextBlock | v >>> bits);
                while (bits >= 8) {
                    bits -= 8;
                    blocks[blocksOffset++] = (byte)(v >>> bits);
                }
                bitsLeft = 8 - bits;
                nextBlock = (v & (1 << bits) - 1) << bitsLeft;
            }
        }
        assert bitsLeft == 8;
    }
}
