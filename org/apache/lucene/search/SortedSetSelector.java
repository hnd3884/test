package org.apache.lucene.search;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.RandomAccessOrds;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;

public class SortedSetSelector
{
    public static SortedDocValues wrap(final SortedSetDocValues sortedSet, final Type selector) {
        if (sortedSet.getValueCount() >= 2147483647L) {
            throw new UnsupportedOperationException("fields containing more than 2147483646 unique terms are unsupported");
        }
        final SortedDocValues singleton = DocValues.unwrapSingleton(sortedSet);
        if (singleton != null) {
            return singleton;
        }
        if (selector == Type.MIN) {
            return new MinValue(sortedSet);
        }
        if (!(sortedSet instanceof RandomAccessOrds)) {
            throw new UnsupportedOperationException("codec does not support random access ordinals, cannot use selector: " + selector + " docValsImpl: " + sortedSet.toString());
        }
        final RandomAccessOrds randomOrds = (RandomAccessOrds)sortedSet;
        switch (selector) {
            case MAX: {
                return new MaxValue(randomOrds);
            }
            case MIDDLE_MIN: {
                return new MiddleMinValue(randomOrds);
            }
            case MIDDLE_MAX: {
                return new MiddleMaxValue(randomOrds);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public enum Type
    {
        MIN, 
        MAX, 
        MIDDLE_MIN, 
        MIDDLE_MAX;
    }
    
    static class MinValue extends SortedDocValues
    {
        final SortedSetDocValues in;
        
        MinValue(final SortedSetDocValues in) {
            this.in = in;
        }
        
        @Override
        public int getOrd(final int docID) {
            this.in.setDocument(docID);
            return (int)this.in.nextOrd();
        }
        
        @Override
        public BytesRef lookupOrd(final int ord) {
            return this.in.lookupOrd(ord);
        }
        
        @Override
        public int getValueCount() {
            return (int)this.in.getValueCount();
        }
        
        @Override
        public int lookupTerm(final BytesRef key) {
            return (int)this.in.lookupTerm(key);
        }
    }
    
    static class MaxValue extends SortedDocValues
    {
        final RandomAccessOrds in;
        
        MaxValue(final RandomAccessOrds in) {
            this.in = in;
        }
        
        @Override
        public int getOrd(final int docID) {
            this.in.setDocument(docID);
            final int count = this.in.cardinality();
            if (count == 0) {
                return -1;
            }
            return (int)this.in.ordAt(count - 1);
        }
        
        @Override
        public BytesRef lookupOrd(final int ord) {
            return this.in.lookupOrd(ord);
        }
        
        @Override
        public int getValueCount() {
            return (int)this.in.getValueCount();
        }
        
        @Override
        public int lookupTerm(final BytesRef key) {
            return (int)this.in.lookupTerm(key);
        }
    }
    
    static class MiddleMinValue extends SortedDocValues
    {
        final RandomAccessOrds in;
        
        MiddleMinValue(final RandomAccessOrds in) {
            this.in = in;
        }
        
        @Override
        public int getOrd(final int docID) {
            this.in.setDocument(docID);
            final int count = this.in.cardinality();
            if (count == 0) {
                return -1;
            }
            return (int)this.in.ordAt(count - 1 >>> 1);
        }
        
        @Override
        public BytesRef lookupOrd(final int ord) {
            return this.in.lookupOrd(ord);
        }
        
        @Override
        public int getValueCount() {
            return (int)this.in.getValueCount();
        }
        
        @Override
        public int lookupTerm(final BytesRef key) {
            return (int)this.in.lookupTerm(key);
        }
    }
    
    static class MiddleMaxValue extends SortedDocValues
    {
        final RandomAccessOrds in;
        
        MiddleMaxValue(final RandomAccessOrds in) {
            this.in = in;
        }
        
        @Override
        public int getOrd(final int docID) {
            this.in.setDocument(docID);
            final int count = this.in.cardinality();
            if (count == 0) {
                return -1;
            }
            return (int)this.in.ordAt(count >>> 1);
        }
        
        @Override
        public BytesRef lookupOrd(final int ord) {
            return this.in.lookupOrd(ord);
        }
        
        @Override
        public int getValueCount() {
            return (int)this.in.getValueCount();
        }
        
        @Override
        public int lookupTerm(final BytesRef key) {
            return (int)this.in.lookupTerm(key);
        }
    }
}
