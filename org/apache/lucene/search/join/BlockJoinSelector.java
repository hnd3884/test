package org.apache.lucene.search.join;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSelector;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.SortedSetSelector;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.Bits;

public class BlockJoinSelector
{
    private BlockJoinSelector() {
    }
    
    public static Bits wrap(final Bits docsWithValue, final BitSet parents, final BitSet children) {
        return (Bits)new Bits() {
            public boolean get(final int docID) {
                assert parents.get(docID) : "this selector may only be used on parent documents";
                if (docID == 0) {
                    return false;
                }
                final int firstChild = parents.prevSetBit(docID - 1) + 1;
                for (int child = children.nextSetBit(firstChild); child < docID; child = children.nextSetBit(child + 1)) {
                    if (docsWithValue.get(child)) {
                        return true;
                    }
                }
                return false;
            }
            
            public int length() {
                return docsWithValue.length();
            }
        };
    }
    
    public static SortedDocValues wrap(final SortedSetDocValues sortedSet, final Type selection, final BitSet parents, final BitSet children) {
        SortedDocValues values = null;
        switch (selection) {
            case MIN: {
                values = SortedSetSelector.wrap(sortedSet, SortedSetSelector.Type.MIN);
                break;
            }
            case MAX: {
                values = SortedSetSelector.wrap(sortedSet, SortedSetSelector.Type.MAX);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return wrap(values, selection, parents, children);
    }
    
    public static SortedDocValues wrap(final SortedDocValues values, final Type selection, final BitSet parents, final BitSet children) {
        return new SortedDocValues() {
            public int getOrd(final int docID) {
                assert parents.get(docID) : "this selector may only be used on parent documents";
                if (docID == 0) {
                    return -1;
                }
                final int firstChild = parents.prevSetBit(docID - 1) + 1;
                int ord = -1;
                for (int child = children.nextSetBit(firstChild); child < docID; child = children.nextSetBit(child + 1)) {
                    final int childOrd = values.getOrd(child);
                    switch (selection) {
                        case MIN: {
                            if (ord == -1) {
                                ord = childOrd;
                                break;
                            }
                            if (childOrd != -1) {
                                ord = Math.min(ord, childOrd);
                                break;
                            }
                            break;
                        }
                        case MAX: {
                            ord = Math.max(ord, childOrd);
                            break;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                }
                return ord;
            }
            
            public BytesRef lookupOrd(final int ord) {
                return values.lookupOrd(ord);
            }
            
            public int getValueCount() {
                return values.getValueCount();
            }
        };
    }
    
    public static NumericDocValues wrap(final SortedNumericDocValues sortedNumerics, final Type selection, final BitSet parents, final BitSet children) {
        NumericDocValues values = null;
        switch (selection) {
            case MIN: {
                values = SortedNumericSelector.wrap(sortedNumerics, SortedNumericSelector.Type.MIN, SortField.Type.LONG);
                break;
            }
            case MAX: {
                values = SortedNumericSelector.wrap(sortedNumerics, SortedNumericSelector.Type.MAX, SortField.Type.LONG);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return wrap(values, DocValues.docsWithValue(sortedNumerics, parents.length()), selection, parents, children);
    }
    
    public static NumericDocValues wrap(final NumericDocValues values, final Bits docsWithValue, final Type selection, final BitSet parents, final BitSet children) {
        return new NumericDocValues() {
            public long get(final int docID) {
                assert parents.get(docID) : "this selector may only be used on parent documents";
                if (docID == 0) {
                    return 0L;
                }
                final int firstChild = parents.prevSetBit(docID - 1) + 1;
                long value = 0L;
                boolean hasValue = false;
                for (int child = children.nextSetBit(firstChild); child < docID; child = children.nextSetBit(child + 1)) {
                    final long childValue = values.get(child);
                    final boolean childHasValue = value != 0L || docsWithValue.get(child);
                    if (!hasValue) {
                        value = childValue;
                        hasValue = childHasValue;
                    }
                    else if (childHasValue) {
                        switch (selection) {
                            case MIN: {
                                value = Math.min(value, childValue);
                                break;
                            }
                            case MAX: {
                                value = Math.max(value, childValue);
                                break;
                            }
                            default: {
                                throw new AssertionError();
                            }
                        }
                    }
                }
                return value;
            }
        };
    }
    
    public enum Type
    {
        MIN, 
        MAX;
    }
}
