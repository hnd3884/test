package org.apache.lucene.util;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;

public final class FixedBitSet extends BitSet implements MutableBits, Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private final long[] bits;
    private final int numBits;
    private final int numWords;
    
    public static FixedBitSet ensureCapacity(final FixedBitSet bits, final int numBits) {
        if (numBits < bits.numBits) {
            return bits;
        }
        final int numWords = bits2words(numBits);
        long[] arr = bits.getBits();
        if (numWords >= arr.length) {
            arr = ArrayUtil.grow(arr, numWords + 1);
        }
        return new FixedBitSet(arr, arr.length << 6);
    }
    
    public static int bits2words(final int numBits) {
        return (numBits - 1 >> 6) + 1;
    }
    
    public static long intersectionCount(final FixedBitSet a, final FixedBitSet b) {
        return BitUtil.pop_intersect(a.bits, b.bits, 0, Math.min(a.numWords, b.numWords));
    }
    
    public static long unionCount(final FixedBitSet a, final FixedBitSet b) {
        long tot = BitUtil.pop_union(a.bits, b.bits, 0, Math.min(a.numWords, b.numWords));
        if (a.numWords < b.numWords) {
            tot += BitUtil.pop_array(b.bits, a.numWords, b.numWords - a.numWords);
        }
        else if (a.numWords > b.numWords) {
            tot += BitUtil.pop_array(a.bits, b.numWords, a.numWords - b.numWords);
        }
        return tot;
    }
    
    public static long andNotCount(final FixedBitSet a, final FixedBitSet b) {
        long tot = BitUtil.pop_andnot(a.bits, b.bits, 0, Math.min(a.numWords, b.numWords));
        if (a.numWords > b.numWords) {
            tot += BitUtil.pop_array(a.bits, b.numWords, a.numWords - b.numWords);
        }
        return tot;
    }
    
    public FixedBitSet(final int numBits) {
        this.numBits = numBits;
        this.bits = new long[bits2words(numBits)];
        this.numWords = this.bits.length;
    }
    
    public FixedBitSet(final long[] storedBits, final int numBits) {
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
        if ((this.numBits & 0x3F) == 0x0) {
            return true;
        }
        final long mask = -1L << this.numBits;
        return (this.bits[this.numWords - 1] & mask) == 0x0L;
    }
    
    @Override
    public int length() {
        return this.numBits;
    }
    
    @Override
    public long ramBytesUsed() {
        return FixedBitSet.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.bits);
    }
    
    public long[] getBits() {
        return this.bits;
    }
    
    @Override
    public int cardinality() {
        return (int)BitUtil.pop_array(this.bits, 0, this.numWords);
    }
    
    @Override
    public boolean get(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int i = index >> 6;
        final long bitmask = 1L << index;
        return (this.bits[i] & bitmask) != 0x0L;
    }
    
    @Override
    public void set(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = index >> 6;
        final long bitmask = 1L << index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] |= bitmask;
    }
    
    public boolean getAndSet(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = index >> 6;
        final long bitmask = 1L << index;
        final boolean val = (this.bits[wordNum] & bitmask) != 0x0L;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] |= bitmask;
        return val;
    }
    
    @Override
    public void clear(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = index >> 6;
        final long bitmask = 1L << index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] &= ~bitmask;
    }
    
    public boolean getAndClear(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        final int wordNum = index >> 6;
        final long bitmask = 1L << index;
        final boolean val = (this.bits[wordNum] & bitmask) != 0x0L;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] &= ~bitmask;
        return val;
    }
    
    @Override
    public int nextSetBit(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + ", numBits=" + this.numBits;
        int i = index >> 6;
        long word = this.bits[i] >> index;
        if (word != 0L) {
            return index + Long.numberOfTrailingZeros(word);
        }
        while (++i < this.numWords) {
            word = this.bits[i];
            if (word != 0L) {
                return (i << 6) + Long.numberOfTrailingZeros(word);
            }
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public int prevSetBit(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + " numBits=" + this.numBits;
        int i = index >> 6;
        final int subIndex = index & 0x3F;
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
        return -1;
    }
    
    @Override
    public void or(final DocIdSetIterator iter) throws IOException {
        if (BitSetIterator.getFixedBitSetOrNull(iter) != null) {
            this.assertUnpositioned(iter);
            final FixedBitSet bits = BitSetIterator.getFixedBitSetOrNull(iter);
            this.or(bits);
        }
        else {
            super.or(iter);
        }
    }
    
    public void or(final FixedBitSet other) {
        this.or(other.bits, other.numWords);
    }
    
    private void or(final long[] otherArr, final int otherNumWords) {
        assert otherNumWords <= this.numWords : "numWords=" + this.numWords + ", otherNumWords=" + otherNumWords;
        final long[] thisArr = this.bits;
        int pos = Math.min(this.numWords, otherNumWords);
        while (--pos >= 0) {
            final long[] array = thisArr;
            final int n = pos;
            array[n] |= otherArr[pos];
        }
    }
    
    public void xor(final FixedBitSet other) {
        this.xor(other.bits, other.numWords);
    }
    
    public void xor(final DocIdSetIterator iter) throws IOException {
        this.assertUnpositioned(iter);
        if (BitSetIterator.getFixedBitSetOrNull(iter) != null) {
            final FixedBitSet bits = BitSetIterator.getFixedBitSetOrNull(iter);
            this.xor(bits);
        }
        else {
            int doc;
            while ((doc = iter.nextDoc()) < this.numBits) {
                this.flip(doc);
            }
        }
    }
    
    private void xor(final long[] otherBits, final int otherNumWords) {
        assert otherNumWords <= this.numWords : "numWords=" + this.numWords + ", other.numWords=" + otherNumWords;
        final long[] thisBits = this.bits;
        int pos = Math.min(this.numWords, otherNumWords);
        while (--pos >= 0) {
            final long[] array = thisBits;
            final int n = pos;
            array[n] ^= otherBits[pos];
        }
    }
    
    @Override
    public void and(final DocIdSetIterator iter) throws IOException {
        if (BitSetIterator.getFixedBitSetOrNull(iter) != null) {
            this.assertUnpositioned(iter);
            final FixedBitSet bits = BitSetIterator.getFixedBitSetOrNull(iter);
            this.and(bits);
        }
        else {
            super.and(iter);
        }
    }
    
    public boolean intersects(final FixedBitSet other) {
        int pos = Math.min(this.numWords, other.numWords);
        while (--pos >= 0) {
            if ((this.bits[pos] & other.bits[pos]) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    public void and(final FixedBitSet other) {
        this.and(other.bits, other.numWords);
    }
    
    private void and(final long[] otherArr, final int otherNumWords) {
        final long[] thisArr = this.bits;
        int pos = Math.min(this.numWords, otherNumWords);
        while (--pos >= 0) {
            final long[] array = thisArr;
            final int n = pos;
            array[n] &= otherArr[pos];
        }
        if (this.numWords > otherNumWords) {
            Arrays.fill(thisArr, otherNumWords, this.numWords, 0L);
        }
    }
    
    @Override
    public void andNot(final DocIdSetIterator iter) throws IOException {
        if (BitSetIterator.getFixedBitSetOrNull(iter) != null) {
            this.assertUnpositioned(iter);
            final FixedBitSet bits = BitSetIterator.getFixedBitSetOrNull(iter);
            this.andNot(bits);
        }
        else {
            super.andNot(iter);
        }
    }
    
    public void andNot(final FixedBitSet other) {
        this.andNot(other.bits, other.numWords);
    }
    
    private void andNot(final long[] otherArr, final int otherNumWords) {
        final long[] thisArr = this.bits;
        int pos = Math.min(this.numWords, otherNumWords);
        while (--pos >= 0) {
            final long[] array = thisArr;
            final int n = pos;
            array[n] &= ~otherArr[pos];
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
    
    public void flip(final int startIndex, final int endIndex) {
        assert startIndex >= 0 && startIndex < this.numBits;
        assert endIndex >= 0 && endIndex <= this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = startIndex >> 6;
        final int endWord = endIndex - 1 >> 6;
        final long startmask = -1L << startIndex;
        final long endmask = -1L >>> -endIndex;
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
    
    public void flip(final int index) {
        assert index >= 0 && index < this.numBits : "index=" + index + " numBits=" + this.numBits;
        final int wordNum = index >> 6;
        final long bitmask = 1L << index;
        final long[] bits = this.bits;
        final int n = wordNum;
        bits[n] ^= bitmask;
    }
    
    public void set(final int startIndex, final int endIndex) {
        assert startIndex >= 0 && startIndex < this.numBits : "startIndex=" + startIndex + ", numBits=" + this.numBits;
        assert endIndex >= 0 && endIndex <= this.numBits : "endIndex=" + endIndex + ", numBits=" + this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = startIndex >> 6;
        final int endWord = endIndex - 1 >> 6;
        final long startmask = -1L << startIndex;
        final long endmask = -1L >>> -endIndex;
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
    
    @Override
    public void clear(final int startIndex, final int endIndex) {
        assert startIndex >= 0 && startIndex < this.numBits : "startIndex=" + startIndex + ", numBits=" + this.numBits;
        assert endIndex >= 0 && endIndex <= this.numBits : "endIndex=" + endIndex + ", numBits=" + this.numBits;
        if (endIndex <= startIndex) {
            return;
        }
        final int startWord = startIndex >> 6;
        final int endWord = endIndex - 1 >> 6;
        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;
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
    
    public FixedBitSet clone() {
        final long[] bits = new long[this.bits.length];
        System.arraycopy(this.bits, 0, bits, 0, this.numWords);
        return new FixedBitSet(bits, this.numBits);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FixedBitSet)) {
            return false;
        }
        final FixedBitSet other = (FixedBitSet)o;
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
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(FixedBitSet.class);
    }
}
