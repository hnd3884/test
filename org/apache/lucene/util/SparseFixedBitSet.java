package org.apache.lucene.util;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import java.util.Arrays;

public class SparseFixedBitSet extends BitSet implements Bits, Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private static final long SINGLE_ELEMENT_ARRAY_BYTES_USED;
    private static final int MASK_4096 = 4095;
    final long[] indices;
    final long[][] bits;
    final int length;
    int nonZeroLongCount;
    long ramBytesUsed;
    
    private static int blockCount(final int length) {
        int blockCount = length >>> 12;
        if (blockCount << 12 < length) {
            ++blockCount;
        }
        assert blockCount << 12 >= length;
        return blockCount;
    }
    
    public SparseFixedBitSet(final int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length needs to be >= 1");
        }
        this.length = length;
        final int blockCount = blockCount(length);
        this.indices = new long[blockCount];
        this.bits = new long[blockCount][];
        this.ramBytesUsed = SparseFixedBitSet.BASE_RAM_BYTES_USED + RamUsageEstimator.shallowSizeOf(this.indices) + RamUsageEstimator.shallowSizeOf(this.bits);
    }
    
    @Override
    public int length() {
        return this.length;
    }
    
    private boolean consistent(final int index) {
        assert index >= 0 && index < this.length : "index=" + index + ",length=" + this.length;
        return true;
    }
    
    @Override
    public int cardinality() {
        int cardinality = 0;
        for (final long[] bitArray : this.bits) {
            if (bitArray != null) {
                for (final long bits : bitArray) {
                    cardinality += Long.bitCount(bits);
                }
            }
        }
        return cardinality;
    }
    
    @Override
    public int approximateCardinality() {
        final int totalLongs = this.length + 63 >>> 6;
        assert totalLongs >= this.nonZeroLongCount;
        final int zeroLongs = totalLongs - this.nonZeroLongCount;
        final long estimate = Math.round(totalLongs * Math.log(totalLongs / (double)zeroLongs));
        return (int)Math.min(this.length, estimate);
    }
    
    @Override
    public boolean get(final int i) {
        assert this.consistent(i);
        final int i2 = i >>> 12;
        final long index = this.indices[i2];
        final int i3 = i >>> 6;
        if ((index & 1L << i3) == 0x0L) {
            return false;
        }
        final long bits = this.bits[i2][Long.bitCount(index & (1L << i3) - 1L)];
        return (bits & 1L << i) != 0x0L;
    }
    
    private static int oversize(final int s) {
        int newSize = s + (s >>> 1);
        if (newSize > 50) {
            newSize = 64;
        }
        return newSize;
    }
    
    @Override
    public void set(final int i) {
        assert this.consistent(i);
        final int i2 = i >>> 12;
        final long index = this.indices[i2];
        final int i3 = i >>> 6;
        if ((index & 1L << i3) != 0x0L) {
            final long[] array = this.bits[i2];
            final int bitCount = Long.bitCount(index & (1L << i3) - 1L);
            array[bitCount] |= 1L << i;
        }
        else if (index == 0L) {
            this.insertBlock(i2, i3, i);
        }
        else {
            this.insertLong(i2, i3, i, index);
        }
    }
    
    private void insertBlock(final int i4096, final int i64, final int i) {
        this.indices[i4096] = 1L << i64;
        assert this.bits[i4096] == null;
        this.bits[i4096] = new long[] { 1L << i };
        ++this.nonZeroLongCount;
        this.ramBytesUsed += SparseFixedBitSet.SINGLE_ELEMENT_ARRAY_BYTES_USED;
    }
    
    private void insertLong(final int i4096, final int i64, final int i, final long index) {
        final long[] indices = this.indices;
        indices[i4096] |= 1L << i64;
        final int o = Long.bitCount(index & (1L << i64) - 1L);
        final long[] bitArray = this.bits[i4096];
        if (bitArray[bitArray.length - 1] == 0L) {
            System.arraycopy(bitArray, o, bitArray, o + 1, bitArray.length - o - 1);
            bitArray[o] = 1L << i;
        }
        else {
            final int newSize = oversize(bitArray.length + 1);
            final long[] newBitArray = new long[newSize];
            System.arraycopy(bitArray, 0, newBitArray, 0, o);
            newBitArray[o] = 1L << i;
            System.arraycopy(bitArray, o, newBitArray, o + 1, bitArray.length - o);
            this.bits[i4096] = newBitArray;
            this.ramBytesUsed += RamUsageEstimator.sizeOf(newBitArray) - RamUsageEstimator.sizeOf(bitArray);
        }
        ++this.nonZeroLongCount;
    }
    
    @Override
    public void clear(final int i) {
        assert this.consistent(i);
        final int i2 = i >>> 12;
        final int i3 = i >>> 6;
        this.and(i2, i3, ~(1L << i));
    }
    
    private void and(final int i4096, final int i64, final long mask) {
        final long index = this.indices[i4096];
        if ((index & 1L << i64) != 0x0L) {
            final int o = Long.bitCount(index & (1L << i64) - 1L);
            final long bits = this.bits[i4096][o] & mask;
            if (bits == 0L) {
                this.removeLong(i4096, i64, index, o);
            }
            else {
                this.bits[i4096][o] = bits;
            }
        }
    }
    
    private void removeLong(final int i4096, final int i64, long index, final int o) {
        index &= ~(1L << i64);
        this.indices[i4096] = index;
        if (index == 0L) {
            this.bits[i4096] = null;
        }
        else {
            final int length = Long.bitCount(index);
            final long[] bitArray = this.bits[i4096];
            System.arraycopy(bitArray, o + 1, bitArray, o, length - o);
            bitArray[length] = 0L;
        }
        --this.nonZeroLongCount;
    }
    
    @Override
    public void clear(final int from, final int to) {
        assert from >= 0;
        assert to <= this.length;
        if (from >= to) {
            return;
        }
        final int firstBlock = from >>> 12;
        final int lastBlock = to - 1 >>> 12;
        if (firstBlock == lastBlock) {
            this.clearWithinBlock(firstBlock, from & 0xFFF, to - 1 & 0xFFF);
        }
        else {
            this.clearWithinBlock(firstBlock, from & 0xFFF, 4095);
            for (int i = firstBlock + 1; i < lastBlock; ++i) {
                this.nonZeroLongCount -= Long.bitCount(this.indices[i]);
                this.indices[i] = 0L;
                this.bits[i] = null;
            }
            this.clearWithinBlock(lastBlock, 0, to - 1 & 0xFFF);
        }
    }
    
    private static long mask(final int from, final int to) {
        return (1L << to - from << 1) - 1L << from;
    }
    
    private void clearWithinBlock(final int i4096, final int from, final int to) {
        final int firstLong = from >>> 6;
        final int lastLong = to >>> 6;
        if (firstLong == lastLong) {
            this.and(i4096, firstLong, ~mask(from, to));
        }
        else {
            assert firstLong < lastLong;
            this.and(i4096, lastLong, ~mask(0, to));
            for (int j = lastLong - 1; j >= firstLong + 1; --j) {
                this.and(i4096, j, 0L);
            }
            this.and(i4096, firstLong, ~mask(from, 63));
        }
    }
    
    private int firstDoc(int i4096) {
        long index = 0L;
        while (i4096 < this.indices.length) {
            index = this.indices[i4096];
            if (index != 0L) {
                final int i4097 = Long.numberOfTrailingZeros(index);
                return i4096 << 12 | i4097 << 6 | Long.numberOfTrailingZeros(this.bits[i4096][0]);
            }
            ++i4096;
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public int nextSetBit(final int i) {
        assert i < this.length;
        final int i2 = i >>> 12;
        final long index = this.indices[i2];
        final long[] bitArray = this.bits[i2];
        int i3 = i >>> 6;
        int o = Long.bitCount(index & (1L << i3) - 1L);
        if ((index & 1L << i3) != 0x0L) {
            final long bits = bitArray[o] >>> i;
            if (bits != 0L) {
                return i + Long.numberOfTrailingZeros(bits);
            }
            ++o;
        }
        final long indexBits = index >>> i3 >>> 1;
        if (indexBits == 0L) {
            return this.firstDoc(i2 + 1);
        }
        i3 += 1 + Long.numberOfTrailingZeros(indexBits);
        final long bits2 = bitArray[o];
        return i3 << 6 | Long.numberOfTrailingZeros(bits2);
    }
    
    private int lastDoc(int i4096) {
        while (i4096 >= 0) {
            final long index = this.indices[i4096];
            if (index != 0L) {
                final int i4097 = 63 - Long.numberOfLeadingZeros(index);
                final long bits = this.bits[i4096][Long.bitCount(index) - 1];
                return i4096 << 12 | i4097 << 6 | 63 - Long.numberOfLeadingZeros(bits);
            }
            --i4096;
        }
        return -1;
    }
    
    @Override
    public int prevSetBit(final int i) {
        assert i >= 0;
        final int i2 = i >>> 12;
        final long index = this.indices[i2];
        final long[] bitArray = this.bits[i2];
        int i3 = i >>> 6;
        final long indexBits = index & (1L << i3) - 1L;
        final int o = Long.bitCount(indexBits);
        if ((index & 1L << i3) != 0x0L) {
            final long bits = bitArray[o] & (1L << i << 1) - 1L;
            if (bits != 0L) {
                return i3 << 6 | 63 - Long.numberOfLeadingZeros(bits);
            }
        }
        if (indexBits == 0L) {
            return this.lastDoc(i2 - 1);
        }
        i3 = 63 - Long.numberOfLeadingZeros(indexBits);
        final long bits = bitArray[o - 1];
        return i2 << 12 | i3 << 6 | 63 - Long.numberOfLeadingZeros(bits);
    }
    
    private long longBits(final long index, final long[] bits, final int i64) {
        if ((index & 1L << i64) == 0x0L) {
            return 0L;
        }
        return bits[Long.bitCount(index & (1L << i64) - 1L)];
    }
    
    private void or(final int i4096, final long index, final long[] bits, final int nonZeroLongCount) {
        assert Long.bitCount(index) == nonZeroLongCount;
        final long currentIndex = this.indices[i4096];
        if (currentIndex == 0L) {
            this.indices[i4096] = index;
            this.bits[i4096] = Arrays.copyOf(bits, nonZeroLongCount);
            this.nonZeroLongCount += nonZeroLongCount;
            return;
        }
        final long[] currentBits = this.bits[i4096];
        final long newIndex = currentIndex | index;
        final int requiredCapacity = Long.bitCount(newIndex);
        long[] newBits;
        if (currentBits.length >= requiredCapacity) {
            newBits = currentBits;
        }
        else {
            newBits = new long[oversize(requiredCapacity)];
        }
        for (int j = Long.numberOfLeadingZeros(newIndex), newO = Long.bitCount(newIndex) - 1; j < 64; j += 1 + Long.numberOfLeadingZeros(newIndex << j + 1), --newO) {
            final int bitIndex = 63 - j;
            assert newO == Long.bitCount(newIndex & (1L << bitIndex) - 1L);
            newBits[newO] = (this.longBits(currentIndex, currentBits, bitIndex) | this.longBits(index, bits, bitIndex));
        }
        this.indices[i4096] = newIndex;
        this.bits[i4096] = newBits;
        this.nonZeroLongCount += nonZeroLongCount - Long.bitCount(currentIndex & index);
    }
    
    private void or(final SparseFixedBitSet other) {
        for (int i = 0; i < other.indices.length; ++i) {
            final long index = other.indices[i];
            if (index != 0L) {
                this.or(i, index, other.bits[i], Long.bitCount(index));
            }
        }
    }
    
    private void orDense(final DocIdSetIterator it) throws IOException {
        this.assertUnpositioned(it);
        final int firstDoc = it.nextDoc();
        if (firstDoc == Integer.MAX_VALUE) {
            return;
        }
        int i4096 = firstDoc >>> 12;
        int i4097 = firstDoc >>> 6;
        long index = 1L << i4097;
        long currentLong = 1L << firstDoc;
        final long[] longs = new long[64];
        int numLongs = 0;
        for (int doc = it.nextDoc(); doc != Integer.MAX_VALUE; doc = it.nextDoc()) {
            final int doc2 = doc >>> 6;
            if (doc2 == i4097) {
                currentLong |= 1L << doc;
            }
            else {
                longs[numLongs++] = currentLong;
                final int doc3 = doc >>> 12;
                if (doc3 == i4096) {
                    index |= 1L << doc2;
                }
                else {
                    this.or(i4096, index, longs, numLongs);
                    i4096 = doc3;
                    index = 1L << doc2;
                    numLongs = 0;
                }
                i4097 = doc2;
                currentLong = 1L << doc;
            }
        }
        longs[numLongs++] = currentLong;
        this.or(i4096, index, longs, numLongs);
    }
    
    @Override
    public void or(final DocIdSetIterator it) throws IOException {
        final SparseFixedBitSet other = BitSetIterator.getSparseFixedBitSetOrNull(it);
        if (other != null) {
            this.assertUnpositioned(it);
            this.or(other);
            return;
        }
        if (it.cost() < this.indices.length) {
            super.or(it);
        }
        else {
            this.orDense(it);
        }
    }
    
    @Override
    public void and(final DocIdSetIterator it) throws IOException {
        final SparseFixedBitSet other = BitSetIterator.getSparseFixedBitSetOrNull(it);
        if (other != null) {
            for (int numCommonBlocks = Math.min(this.indices.length, other.indices.length), i = 0; i < numCommonBlocks; ++i) {
                if ((this.indices[i] & other.indices[i]) == 0x0L) {
                    this.nonZeroLongCount -= Long.bitCount(this.indices[i]);
                    this.indices[i] = 0L;
                    this.bits[i] = null;
                }
            }
        }
        super.and(it);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.ramBytesUsed;
    }
    
    @Override
    public String toString() {
        return "SparseFixedBitSet(size=" + this.length + ",cardinality=~" + this.approximateCardinality();
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(SparseFixedBitSet.class);
        SINGLE_ELEMENT_ARRAY_BYTES_USED = RamUsageEstimator.sizeOf(new long[1]);
    }
}
