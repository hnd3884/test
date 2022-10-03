package org.apache.lucene.util;

import java.util.Collections;
import java.util.Collection;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;

public abstract class BitSet implements MutableBits, Accountable
{
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public static BitSet of(final DocIdSetIterator it, final int maxDoc) throws IOException {
        final long cost = it.cost();
        final int threshold = maxDoc >>> 7;
        BitSet set;
        if (cost < threshold) {
            set = new SparseFixedBitSet(maxDoc);
        }
        else {
            set = new FixedBitSet(maxDoc);
        }
        set.or(it);
        return set;
    }
    
    public abstract void set(final int p0);
    
    public abstract void clear(final int p0, final int p1);
    
    public abstract int cardinality();
    
    public int approximateCardinality() {
        return this.cardinality();
    }
    
    public abstract int prevSetBit(final int p0);
    
    public abstract int nextSetBit(final int p0);
    
    protected final void assertUnpositioned(final DocIdSetIterator iter) {
        if (iter.docID() != -1) {
            throw new IllegalStateException("This operation only works with an unpositioned iterator, got current position = " + iter.docID());
        }
    }
    
    public void or(final DocIdSetIterator iter) throws IOException {
        this.assertUnpositioned(iter);
        for (int doc = iter.nextDoc(); doc != Integer.MAX_VALUE; doc = iter.nextDoc()) {
            this.set(doc);
        }
    }
    
    private void leapFrog(final DocIdSetIterator iter, final LeapFrogCallBack callback) throws IOException {
        final int length = this.length();
        int bitSetDoc = -1;
        int disiDoc = iter.nextDoc();
        while (BitSet.$assertionsDisabled || bitSetDoc <= disiDoc) {
            if (disiDoc >= length) {
                callback.finish();
                return;
            }
            if (bitSetDoc < disiDoc) {
                bitSetDoc = this.nextSetBit(disiDoc);
            }
            if (bitSetDoc == disiDoc) {
                callback.onMatch(bitSetDoc);
                disiDoc = iter.nextDoc();
            }
            else {
                disiDoc = iter.advance(bitSetDoc);
            }
        }
        throw new AssertionError();
    }
    
    @Deprecated
    public void and(final DocIdSetIterator iter) throws IOException {
        this.assertUnpositioned(iter);
        this.leapFrog(iter, new LeapFrogCallBack() {
            int previous = -1;
            
            public void onMatch(final int doc) {
                BitSet.this.clear(this.previous + 1, doc);
                this.previous = doc;
            }
            
            public void finish() {
                if (this.previous + 1 < BitSet.this.length()) {
                    BitSet.this.clear(this.previous + 1, BitSet.this.length());
                }
            }
        });
    }
    
    @Deprecated
    public void andNot(final DocIdSetIterator iter) throws IOException {
        this.assertUnpositioned(iter);
        this.leapFrog(iter, new LeapFrogCallBack() {
            public void onMatch(final int doc) {
                BitSet.this.clear(doc);
            }
        });
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    private abstract static class LeapFrogCallBack
    {
        abstract void onMatch(final int p0);
        
        void finish() {
        }
    }
}
