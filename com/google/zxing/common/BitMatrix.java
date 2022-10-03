package com.google.zxing.common;

public final class BitMatrix
{
    private final int width;
    private final int height;
    private final int rowSize;
    private final int[] bits;
    
    public BitMatrix(final int dimension) {
        this(dimension, dimension);
    }
    
    public BitMatrix(final int width, final int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Both dimensions must be greater than 0");
        }
        this.width = width;
        this.height = height;
        this.rowSize = width + 31 >> 5;
        this.bits = new int[this.rowSize * height];
    }
    
    public boolean get(final int x, final int y) {
        final int offset = y * this.rowSize + (x >> 5);
        return (this.bits[offset] >>> (x & 0x1F) & 0x1) != 0x0;
    }
    
    public void set(final int x, final int y) {
        final int offset = y * this.rowSize + (x >> 5);
        final int[] bits = this.bits;
        final int n = offset;
        bits[n] |= 1 << (x & 0x1F);
    }
    
    public void flip(final int x, final int y) {
        final int offset = y * this.rowSize + (x >> 5);
        final int[] bits = this.bits;
        final int n = offset;
        bits[n] ^= 1 << (x & 0x1F);
    }
    
    public void clear() {
        for (int max = this.bits.length, i = 0; i < max; ++i) {
            this.bits[i] = 0;
        }
    }
    
    public void setRegion(final int left, final int top, final int width, final int height) {
        if (top < 0 || left < 0) {
            throw new IllegalArgumentException("Left and top must be nonnegative");
        }
        if (height < 1 || width < 1) {
            throw new IllegalArgumentException("Height and width must be at least 1");
        }
        final int right = left + width;
        final int bottom = top + height;
        if (bottom > this.height || right > this.width) {
            throw new IllegalArgumentException("The region must fit inside the matrix");
        }
        for (int y = top; y < bottom; ++y) {
            final int offset = y * this.rowSize;
            for (int x = left; x < right; ++x) {
                final int[] bits = this.bits;
                final int n = offset + (x >> 5);
                bits[n] |= 1 << (x & 0x1F);
            }
        }
    }
    
    public BitArray getRow(final int y, BitArray row) {
        if (row == null || row.getSize() < this.width) {
            row = new BitArray(this.width);
        }
        final int offset = y * this.rowSize;
        for (int x = 0; x < this.rowSize; ++x) {
            row.setBulk(x << 5, this.bits[offset + x]);
        }
        return row;
    }
    
    public void setRow(final int y, final BitArray row) {
        System.arraycopy(row.getBitArray(), 0, this.bits, y * this.rowSize, this.rowSize);
    }
    
    public int[] getEnclosingRectangle() {
        int left = this.width;
        int top = this.height;
        int right = -1;
        int bottom = -1;
        for (int y = 0; y < this.height; ++y) {
            for (int x32 = 0; x32 < this.rowSize; ++x32) {
                final int theBits = this.bits[y * this.rowSize + x32];
                if (theBits != 0) {
                    if (y < top) {
                        top = y;
                    }
                    if (y > bottom) {
                        bottom = y;
                    }
                    if (x32 * 32 < left) {
                        int bit;
                        for (bit = 0; theBits << 31 - bit == 0; ++bit) {}
                        if (x32 * 32 + bit < left) {
                            left = x32 * 32 + bit;
                        }
                    }
                    if (x32 * 32 + 31 > right) {
                        int bit;
                        for (bit = 31; theBits >>> bit == 0; --bit) {}
                        if (x32 * 32 + bit > right) {
                            right = x32 * 32 + bit;
                        }
                    }
                }
            }
        }
        final int width = right - left;
        final int height = bottom - top;
        if (width < 0 || height < 0) {
            return null;
        }
        return new int[] { left, top, width, height };
    }
    
    public int[] getTopLeftOnBit() {
        int bitsOffset;
        for (bitsOffset = 0; bitsOffset < this.bits.length && this.bits[bitsOffset] == 0; ++bitsOffset) {}
        if (bitsOffset == this.bits.length) {
            return null;
        }
        final int y = bitsOffset / this.rowSize;
        int x = bitsOffset % this.rowSize << 5;
        int theBits;
        int bit;
        for (theBits = this.bits[bitsOffset], bit = 0; theBits << 31 - bit == 0; ++bit) {}
        x += bit;
        return new int[] { x, y };
    }
    
    public int[] getBottomRightOnBit() {
        int bitsOffset;
        for (bitsOffset = this.bits.length - 1; bitsOffset >= 0 && this.bits[bitsOffset] == 0; --bitsOffset) {}
        if (bitsOffset < 0) {
            return null;
        }
        final int y = bitsOffset / this.rowSize;
        int x = bitsOffset % this.rowSize << 5;
        int theBits;
        int bit;
        for (theBits = this.bits[bitsOffset], bit = 31; theBits >>> bit == 0; --bit) {}
        x += bit;
        return new int[] { x, y };
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitMatrix)) {
            return false;
        }
        final BitMatrix other = (BitMatrix)o;
        if (this.width != other.width || this.height != other.height || this.rowSize != other.rowSize || this.bits.length != other.bits.length) {
            return false;
        }
        for (int i = 0; i < this.bits.length; ++i) {
            if (this.bits[i] != other.bits[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = this.width;
        hash = 31 * hash + this.width;
        hash = 31 * hash + this.height;
        hash = 31 * hash + this.rowSize;
        for (final int bit : this.bits) {
            hash = 31 * hash + bit;
        }
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(this.height * (this.width + 1));
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                result.append(this.get(x, y) ? "X " : "  ");
            }
            result.append('\n');
        }
        return result.toString();
    }
}
