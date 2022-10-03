package org.apache.lucene.util;

import org.apache.lucene.search.DocIdSet;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;

public final class DocIdSetBuilder
{
    private final int maxDoc;
    private final int threshold;
    private int[] buffer;
    private int bufferSize;
    private BitSet bitSet;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public DocIdSetBuilder(final int maxDoc) {
        this.maxDoc = maxDoc;
        this.threshold = maxDoc >>> 7;
        this.buffer = new int[0];
        this.bufferSize = 0;
        this.bitSet = null;
    }
    
    private void upgradeToBitSet() {
        assert this.bitSet == null;
        this.bitSet = new FixedBitSet(this.maxDoc);
        for (int i = 0; i < this.bufferSize; ++i) {
            this.bitSet.set(this.buffer[i]);
        }
        this.buffer = null;
        this.bufferSize = 0;
    }
    
    private void growBuffer(final int minSize) {
        assert minSize < this.threshold;
        if (this.buffer.length < minSize) {
            final int nextSize = Math.min(this.threshold, ArrayUtil.oversize(minSize, 4));
            final int[] newBuffer = new int[nextSize];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
            this.buffer = newBuffer;
        }
    }
    
    public void add(final DocIdSetIterator iter) throws IOException {
        this.grow((int)Math.min(2147483647L, iter.cost()));
        if (this.bitSet == null) {
            while (DocIdSetBuilder.$assertionsDisabled || this.buffer.length <= this.threshold) {
                final int end = this.buffer.length;
                for (int i = this.bufferSize; i < end; ++i) {
                    final int doc = iter.nextDoc();
                    if (doc == Integer.MAX_VALUE) {
                        this.bufferSize = i;
                        return;
                    }
                    this.buffer[this.bufferSize++] = doc;
                }
                this.bufferSize = end;
                if (this.bufferSize + 1 >= this.threshold) {
                    this.upgradeToBitSet();
                    for (int doc2 = iter.nextDoc(); doc2 != Integer.MAX_VALUE; doc2 = iter.nextDoc()) {
                        this.bitSet.set(doc2);
                    }
                    return;
                }
                this.growBuffer(this.bufferSize + 1);
            }
            throw new AssertionError();
        }
        this.bitSet.or(iter);
    }
    
    public void grow(final int numDocs) {
        if (this.bitSet == null) {
            final long newLength = this.bufferSize + numDocs;
            if (newLength < this.threshold) {
                this.growBuffer((int)newLength);
            }
            else {
                this.upgradeToBitSet();
            }
        }
    }
    
    public void add(final int doc) {
        if (this.bitSet != null) {
            this.bitSet.set(doc);
        }
        else {
            if (this.bufferSize + 1 > this.buffer.length) {
                if (this.bufferSize + 1 >= this.threshold) {
                    this.upgradeToBitSet();
                    this.bitSet.set(doc);
                    return;
                }
                this.growBuffer(this.bufferSize + 1);
            }
            this.buffer[this.bufferSize++] = doc;
        }
    }
    
    private static int dedup(final int[] arr, final int length) {
        if (length == 0) {
            return 0;
        }
        int l = 1;
        int previous = arr[0];
        for (int i = 1; i < length; ++i) {
            final int value = arr[i];
            assert value >= previous;
            if (value != previous) {
                arr[l++] = value;
                previous = value;
            }
        }
        return l;
    }
    
    public DocIdSet build() {
        return this.build(-1L);
    }
    
    public DocIdSet build(final long costHint) {
        try {
            if (this.bitSet != null) {
                if (costHint == -1L) {
                    return new BitDocIdSet(this.bitSet);
                }
                return new BitDocIdSet(this.bitSet, costHint);
            }
            else {
                final LSBRadixSorter sorter = new LSBRadixSorter();
                sorter.sort(this.buffer, 0, this.bufferSize);
                final int l = dedup(this.buffer, this.bufferSize);
                assert l <= this.bufferSize;
                (this.buffer = ArrayUtil.grow(this.buffer, l + 1))[l] = Integer.MAX_VALUE;
                return new IntArrayDocIdSet(this.buffer, l);
            }
        }
        finally {
            this.buffer = null;
            this.bufferSize = 0;
            this.bitSet = null;
        }
    }
}
