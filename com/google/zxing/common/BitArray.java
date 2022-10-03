package com.google.zxing.common;

public final class BitArray
{
    private int[] bits;
    private int size;
    
    public BitArray() {
        this.size = 0;
        this.bits = new int[1];
    }
    
    public BitArray(final int size) {
        this.size = size;
        this.bits = makeArray(size);
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getSizeInBytes() {
        return this.size + 7 >> 3;
    }
    
    private void ensureCapacity(final int size) {
        if (size > this.bits.length << 5) {
            final int[] newBits = makeArray(size);
            System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
            this.bits = newBits;
        }
    }
    
    public boolean get(final int i) {
        return (this.bits[i >> 5] & 1 << (i & 0x1F)) != 0x0;
    }
    
    public void set(final int i) {
        final int[] bits = this.bits;
        final int n = i >> 5;
        bits[n] |= 1 << (i & 0x1F);
    }
    
    public void flip(final int i) {
        final int[] bits = this.bits;
        final int n = i >> 5;
        bits[n] ^= 1 << (i & 0x1F);
    }
    
    public int getNextSet(final int from) {
        if (from >= this.size) {
            return this.size;
        }
        int bitsOffset;
        int currentBits;
        for (bitsOffset = from >> 5, currentBits = this.bits[bitsOffset], currentBits &= ~((1 << (from & 0x1F)) - 1); currentBits == 0; currentBits = this.bits[bitsOffset]) {
            if (++bitsOffset == this.bits.length) {
                return this.size;
            }
        }
        final int result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits);
        return (result > this.size) ? this.size : result;
    }
    
    public int getNextUnset(final int from) {
        if (from >= this.size) {
            return this.size;
        }
        int bitsOffset;
        int currentBits;
        for (bitsOffset = from >> 5, currentBits = ~this.bits[bitsOffset], currentBits &= ~((1 << (from & 0x1F)) - 1); currentBits == 0; currentBits = ~this.bits[bitsOffset]) {
            if (++bitsOffset == this.bits.length) {
                return this.size;
            }
        }
        final int result = (bitsOffset << 5) + Integer.numberOfTrailingZeros(currentBits);
        return (result > this.size) ? this.size : result;
    }
    
    public void setBulk(final int i, final int newBits) {
        this.bits[i >> 5] = newBits;
    }
    
    public void setRange(final int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException();
        }
        if (end == start) {
            return;
        }
        --end;
        final int firstInt = start >> 5;
        for (int lastInt = end >> 5, i = firstInt; i <= lastInt; ++i) {
            final int firstBit = (i > firstInt) ? 0 : (start & 0x1F);
            final int lastBit = (i < lastInt) ? 31 : (end & 0x1F);
            int mask;
            if (firstBit == 0 && lastBit == 31) {
                mask = -1;
            }
            else {
                mask = 0;
                for (int j = firstBit; j <= lastBit; ++j) {
                    mask |= 1 << j;
                }
            }
            final int[] bits = this.bits;
            final int n = i;
            bits[n] |= mask;
        }
    }
    
    public void clear() {
        for (int max = this.bits.length, i = 0; i < max; ++i) {
            this.bits[i] = 0;
        }
    }
    
    public boolean isRange(final int start, int end, final boolean value) {
        if (end < start) {
            throw new IllegalArgumentException();
        }
        if (end == start) {
            return true;
        }
        --end;
        final int firstInt = start >> 5;
        for (int lastInt = end >> 5, i = firstInt; i <= lastInt; ++i) {
            final int firstBit = (i > firstInt) ? 0 : (start & 0x1F);
            final int lastBit = (i < lastInt) ? 31 : (end & 0x1F);
            int mask;
            if (firstBit == 0 && lastBit == 31) {
                mask = -1;
            }
            else {
                mask = 0;
                for (int j = firstBit; j <= lastBit; ++j) {
                    mask |= 1 << j;
                }
            }
            if ((this.bits[i] & mask) != (value ? mask : 0)) {
                return false;
            }
        }
        return true;
    }
    
    public void appendBit(final boolean bit) {
        this.ensureCapacity(this.size + 1);
        if (bit) {
            final int[] bits = this.bits;
            final int n = this.size >> 5;
            bits[n] |= 1 << (this.size & 0x1F);
        }
        ++this.size;
    }
    
    public void appendBits(final int value, final int numBits) {
        if (numBits < 0 || numBits > 32) {
            throw new IllegalArgumentException("Num bits must be between 0 and 32");
        }
        this.ensureCapacity(this.size + numBits);
        for (int numBitsLeft = numBits; numBitsLeft > 0; --numBitsLeft) {
            this.appendBit((value >> numBitsLeft - 1 & 0x1) == 0x1);
        }
    }
    
    public void appendBitArray(final BitArray other) {
        final int otherSize = other.size;
        this.ensureCapacity(this.size + otherSize);
        for (int i = 0; i < otherSize; ++i) {
            this.appendBit(other.get(i));
        }
    }
    
    public void xor(final BitArray other) {
        if (this.bits.length != other.bits.length) {
            throw new IllegalArgumentException("Sizes don't match");
        }
        for (int i = 0; i < this.bits.length; ++i) {
            final int[] bits = this.bits;
            final int n = i;
            bits[n] ^= other.bits[i];
        }
    }
    
    public void toBytes(int bitOffset, final byte[] array, final int offset, final int numBytes) {
        for (int i = 0; i < numBytes; ++i) {
            int theByte = 0;
            for (int j = 0; j < 8; ++j) {
                if (this.get(bitOffset)) {
                    theByte |= 1 << 7 - j;
                }
                ++bitOffset;
            }
            array[offset + i] = (byte)theByte;
        }
    }
    
    public int[] getBitArray() {
        return this.bits;
    }
    
    public void reverse() {
        final int[] newBits = new int[this.bits.length];
        for (int size = this.size, i = 0; i < size; ++i) {
            if (this.get(size - i - 1)) {
                final int[] array = newBits;
                final int n = i >> 5;
                array[n] |= 1 << (i & 0x1F);
            }
        }
        this.bits = newBits;
    }
    
    private static int[] makeArray(final int size) {
        return new int[size + 31 >> 5];
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(this.size);
        for (int i = 0; i < this.size; ++i) {
            if ((i & 0x7) == 0x0) {
                result.append(' ');
            }
            result.append(this.get(i) ? 'X' : '.');
        }
        return result.toString();
    }
}
