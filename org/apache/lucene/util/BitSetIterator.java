package org.apache.lucene.util;

import org.apache.lucene.search.DocIdSetIterator;

public class BitSetIterator extends DocIdSetIterator
{
    private final BitSet bits;
    private final int length;
    private final long cost;
    private int doc;
    
    private static <T extends BitSet> T getBitSet(final DocIdSetIterator iterator, final Class<? extends T> clazz) {
        if (iterator instanceof BitSetIterator) {
            final BitSet bits = ((BitSetIterator)iterator).bits;
            assert bits != null;
            if (clazz.isInstance(bits)) {
                return (T)clazz.cast(bits);
            }
        }
        return null;
    }
    
    public static FixedBitSet getFixedBitSetOrNull(final DocIdSetIterator iterator) {
        return getBitSet(iterator, (Class<? extends FixedBitSet>)FixedBitSet.class);
    }
    
    public static SparseFixedBitSet getSparseFixedBitSetOrNull(final DocIdSetIterator iterator) {
        return getBitSet(iterator, (Class<? extends SparseFixedBitSet>)SparseFixedBitSet.class);
    }
    
    public BitSetIterator(final BitSet bits, final long cost) {
        this.doc = -1;
        this.bits = bits;
        this.length = bits.length();
        this.cost = cost;
    }
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int nextDoc() {
        return this.advance(this.doc + 1);
    }
    
    @Override
    public int advance(final int target) {
        if (target >= this.length) {
            return this.doc = Integer.MAX_VALUE;
        }
        return this.doc = this.bits.nextSetBit(target);
    }
    
    @Override
    public long cost() {
        return this.cost;
    }
}
