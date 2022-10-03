package org.apache.lucene.search;

import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedNumericDocValues;

public class SortedNumericSelector
{
    public static NumericDocValues wrap(final SortedNumericDocValues sortedNumeric, final Type selector, final SortField.Type numericType) {
        if (numericType != SortField.Type.INT && numericType != SortField.Type.LONG && numericType != SortField.Type.FLOAT && numericType != SortField.Type.DOUBLE) {
            throw new IllegalArgumentException("numericType must be a numeric type");
        }
        final NumericDocValues singleton = DocValues.unwrapSingleton(sortedNumeric);
        NumericDocValues view = null;
        if (singleton != null) {
            view = singleton;
        }
        else {
            switch (selector) {
                case MIN: {
                    view = new MinValue(sortedNumeric);
                    break;
                }
                case MAX: {
                    view = new MaxValue(sortedNumeric);
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        switch (numericType) {
            case FLOAT: {
                return new NumericDocValues() {
                    @Override
                    public long get(final int docID) {
                        return NumericUtils.sortableFloatBits((int)view.get(docID));
                    }
                };
            }
            case DOUBLE: {
                return new NumericDocValues() {
                    @Override
                    public long get(final int docID) {
                        return NumericUtils.sortableDoubleBits(view.get(docID));
                    }
                };
            }
            default: {
                return view;
            }
        }
    }
    
    public enum Type
    {
        MIN, 
        MAX;
    }
    
    static class MinValue extends NumericDocValues
    {
        final SortedNumericDocValues in;
        
        MinValue(final SortedNumericDocValues in) {
            this.in = in;
        }
        
        @Override
        public long get(final int docID) {
            this.in.setDocument(docID);
            if (this.in.count() == 0) {
                return 0L;
            }
            return this.in.valueAt(0);
        }
    }
    
    static class MaxValue extends NumericDocValues
    {
        final SortedNumericDocValues in;
        
        MaxValue(final SortedNumericDocValues in) {
            this.in = in;
        }
        
        @Override
        public long get(final int docID) {
            this.in.setDocument(docID);
            final int count = this.in.count();
            if (count == 0) {
                return 0L;
            }
            return this.in.valueAt(count - 1);
        }
    }
}
