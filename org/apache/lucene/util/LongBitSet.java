package org.apache.lucene.util;

import java.util.Arrays;

public final class LongBitSet
{
    private final long[] bits;
    private final long numBits;
    private final int numWords;
    
    public static LongBitSet ensureCapacity(final LongBitSet bits, final long numBits) {
        if (numBits < bits.numBits) {
            return bits;
        }
        final int numWords = bits2words(numBits);
        long[] arr = bits.getBits();
        if (numWords >= arr.length) {
            arr = ArrayUtil.grow(arr, numWords + 1);
        }
        return new LongBitSet(arr, (long)arr.length << 6);
    }
    
    public static int bits2words(final long numBits) {
        return (int)(numBits - 1L >> 6) + 1;
    }
    
    public LongBitSet(final long numBits) {
        this.numBits = numBits;
        this.bits = new long[bits2words(numBits)];
        this.numWords = this.bits.length;
    }
    
    public LongBitSet(final long[] storedBits, final long numBits) {
        this.numWords = bits2words(numBits);
        if (this.numWords > storedBits.length) {
            throw new IllegalArgumentException("The given long array is too small  to hold " + numBits + " bits");
        }
        this.numBits = numBits;
        this.bits = storedBits;
        assert this.verifyGhostBitsClear();
    }
    
    private boolean verifyGhostBitsClear() {
        for (int i = this.numWords; i < this.bits.length; ++i) {
            if (this.bits[i] != 0L) {
                return false;
            }
        }
        if ((this.numBits & 0x3FL) == 0x0L) {
            return true;
        }
        final long mask = -1L << (int)this.numBits;
        return (this.bits[this.numWords - 1] & mask) == 0x0L;
    }
    
    public long length() {
        return this.numBits;
    }
    
    public long[] getBits() {
        return this.bits;
    }
    
    public long cardinality() {
        return BitUtil.pop_array(this.bits, 0, this.numWords);
    }
    
    public boolean get(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int i = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        return (this.bits[i] & bitmask) != 0x0L;
    }
    
    public void set(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + " numBits=" + this.numBits;
        final int wordNum = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] |= bitmask;
    }
    
    public boolean getAndSet(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        final boolean val = (this.bits[wordNum] & bitmask) != 0x0L;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] |= bitmask;
        return val;
    }
    
    public void clear(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] &= ~bitmask;
    }
    
    public boolean getAndClear(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        final boolean val = (this.bits[wordNum] & bitmask) != 0x0L;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] &= ~bitmask;
        return val;
    }
    
    public long nextSetBit(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        int i = (int)(index >> 6);
        long word = this.bits[i] >> (int)index;
        if (word != 0L) {
            return index + Long.numberOfTrailingZeros(word);
        }
        while (++i < this.numWords) {
            word = this.bits[i];
            if (word != 0L) {
                return (i << 6) + Long.numberOfTrailingZeros(word);
            }
        }
        return -1L;
    }
    
    public long prevSetBit(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + " numBits=" + this.numBits;
        int i = (int)(index >> 6);
        final int subIndex = (int)(index & 0x3FL);
        long word = this.bits[i] << 63 - subIndex;
        if (word != 0L) {
            return (i << 6) + subIndex - Long.numberOfLeadingZeros(word);
        }
        while (--i >= 0) {
            word = this.bits[i];
            if (word != 0L) {
                return (i << 6) + 63 - Long.numberOfLeadingZeros(word);
            }
        }
        return -1L;
    }
    
    public void or(final LongBitSet other) {
        assert other.numWords <= this.numWords : "numWords=" + this.numWords + ", other.numWords=" + other.numWords;
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            final long[] bits = this.bits;
            final int n = pos;
            bits[n] |= other.bits[pos];
        }
    }
    
    public void xor(final LongBitSet other) {
        assert other.numWords <= this.numWords : "numWords=" + this.numWords + ", other.numWords=" + other.numWords;
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            final long[] bits = this.bits;
            final int n = pos;
            bits[n] ^= other.bits[pos];
        }
    }
    
    public boolean intersects(final LongBitSet other) {
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            if ((this.bits[pos] & other.bits[pos]) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    public void and(final LongBitSet other) {
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            final long[] bits = this.bits;
            final int n = pos;
            bits[n] &= other.bits[pos];
        }
        if (this.numWords > other.numWords) {
            Arrays.fill(this.bits, other.numWords, this.numWords, 0L);
        }
    }
    
    public void andNot(final LongBitSet other) {
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            final long[] bits = this.bits;
            final int n = pos;
            bits[n] &= ~other.bits[pos];
        }
    }
    
    public boolean scanIsEmpty() {
        for (int count = this.numWords, i = 0; i < count; ++i) {
            if (this.bits[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    public void flip(final long startIndex, final long endIndex) {
        assert startIndex >= 0L && startIndex < this.numBits;
        assert endIndex >= 0L && endIndex <= this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = (int)(startIndex >> 6);
        final int endWord = (int)(endIndex - 1L >> 6);
        final long startmask = -1L << (int)startIndex;
        final long endmask = -1L >>> (int)(-endIndex);
        if (startWord == endWord) {
            final long[] bits = this.bits;
            final int n = startWord;
            bits[n] ^= (startmask & endmask);
            return;
        }
        final long[] bits2 = this.bits;
        final int n2 = startWord;
        bits2[n2] ^= startmask;
        for (int i = startWord + 1; i < endWord; ++i) {
            this.bits[i] ^= -1L;
        }
        final long[] bits3 = this.bits;
        final int n3 = endWord;
        bits3[n3] ^= endmask;
    }
    
    public void flip(final long index) {
        assert index >= 0L && index < this.numBits : "index=" + index + " numBits=" + this.numBits;
        final int wordNum = (int)(index >> 6);
        final long bitmask = 1L << (int)index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] ^= bitmask;
    }
    
    public void set(final long startIndex, final long endIndex) {
        assert startIndex >= 0L && startIndex < this.numBits : "startIndex=" + startIndex + ", numBits=" + this.numBits;
        assert endIndex >= 0L && endIndex <= this.numBits : "endIndex=" + endIndex + ", numBits=" + this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = (int)(startIndex >> 6);
        final int endWord = (int)(endIndex - 1L >> 6);
        final long startmask = -1L << (int)startIndex;
        final long endmask = -1L >>> (int)(-endIndex);
        if (startWord == endWord) {
            final long[] bits = this.bits;
            final int n = startWord;
            bits[n] |= (startmask & endmask);
            return;
        }
        final long[] bits2 = this.bits;
        final int n2 = startWord;
        bits2[n2] |= startmask;
        Arrays.fill(this.bits, startWord + 1, endWord, -1L);
        final long[] bits3 = this.bits;
        final int n3 = endWord;
        bits3[n3] |= endmask;
    }
    
    public void clear(final long startIndex, final long endIndex) {
        assert startIndex >= 0L && startIndex < this.numBits : "startIndex=" + startIndex + ", numBits=" + this.numBits;
        assert endIndex >= 0L && endIndex <= this.numBits : "endIndex=" + endIndex + ", numBits=" + this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = (int)(startIndex >> 6);
        final int endWord = (int)(endIndex - 1L >> 6);
        long startmask = -1L << (int)startIndex;
        long endmask = -1L >>> (int)(-endIndex);
        startmask ^= -1L;
        endmask ^= -1L;
        if (startWord == endWord) {
            final long[] bits = this.bits;
            final int n = startWord;
            bits[n] &= (startmask | endmask);
            return;
        }
        final long[] bits2 = this.bits;
        final int n2 = startWord;
        bits2[n2] &= startmask;
        Arrays.fill(this.bits, startWord + 1, endWord, 0L);
        final long[] bits3 = this.bits;
        final int n3 = endWord;
        bits3[n3] &= endmask;
    }
    
    public LongBitSet clone() {
        final long[] bits = new long[this.bits.length];
        System.arraycopy(this.bits, 0, bits, 0, this.numWords);
        return new LongBitSet(bits, this.numBits);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LongBitSet)) {
            return false;
        }
        final LongBitSet other = (LongBitSet)o;
        return this.numBits == other.numBits && Arrays.equals(this.bits, other.bits);
    }
    
    @Override
    public int hashCode() {
        long h = 0L;
        int i = this.numWords;
        while (--i >= 0) {
            h ^= this.bits[i];
            h = (h << 1 | h >>> 63);
        }
        return (int)(h >> 32 ^ h) - 1737092556;
    }
}
