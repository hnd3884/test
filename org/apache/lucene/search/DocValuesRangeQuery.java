package org.apache.lucene.search;

import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ToStringUtils;
import java.util.Objects;
import org.apache.lucene.util.BytesRef;

public final class DocValuesRangeQuery extends Query
{
    private final String field;
    private final Object lowerVal;
    private final Object upperVal;
    private final boolean includeLower;
    private final boolean includeUpper;
    
    public static Query newLongRange(final String field, final Long lowerVal, final Long upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeQuery(field, lowerVal, upperVal, includeLower, includeUpper);
    }
    
    public static Query newBytesRefRange(final String field, final BytesRef lowerVal, final BytesRef upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeQuery(field, deepCopyOf(lowerVal), deepCopyOf(upperVal), includeLower, includeUpper);
    }
    
    private static BytesRef deepCopyOf(final BytesRef b) {
        if (b == null) {
            return null;
        }
        return BytesRef.deepCopyOf(b);
    }
    
    private DocValuesRangeQuery(final String field, final Object lowerVal, final Object upperVal, final boolean includeLower, final boolean includeUpper) {
        this.field = Objects.requireNonNull(field);
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DocValuesRangeQuery that = (DocValuesRangeQuery)obj;
        return this.field.equals(that.field) && Objects.equals(this.lowerVal, that.lowerVal) && Objects.equals(this.upperVal, that.upperVal) && this.includeLower == that.includeLower && this.includeUpper == that.includeUpper && super.equals(obj);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(this.field, this.lowerVal, this.upperVal, this.includeLower, this.includeUpper);
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        if (!this.field.equals(field)) {
            sb.append(this.field).append(':');
        }
        sb.append(this.includeLower ? '[' : '{');
        sb.append((this.lowerVal == null) ? "*" : this.lowerVal.toString());
        sb.append(" TO ");
        sb.append((this.upperVal == null) ? "*" : this.upperVal.toString());
        sb.append(this.includeUpper ? ']' : '}');
        sb.append(ToStringUtils.boost(this.getBoost()));
        return sb.toString();
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        if (this.lowerVal == null && this.upperVal == null) {
            return (Query)new FieldValueQuery(this.field);
        }
        return super.rewrite(reader);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        if (this.lowerVal == null && this.upperVal == null) {
            throw new IllegalStateException("Both min and max values cannot be null, call rewrite first");
        }
        return (Weight)new RandomAccessWeight(this) {
            protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
                if (DocValuesRangeQuery.this.lowerVal instanceof Long || DocValuesRangeQuery.this.upperVal instanceof Long) {
                    final SortedNumericDocValues values = DocValues.getSortedNumeric(context.reader(), DocValuesRangeQuery.this.field);
                    long min;
                    if (DocValuesRangeQuery.this.lowerVal == null) {
                        min = Long.MIN_VALUE;
                    }
                    else if (DocValuesRangeQuery.this.includeLower) {
                        min = (long)DocValuesRangeQuery.this.lowerVal;
                    }
                    else {
                        if ((long)DocValuesRangeQuery.this.lowerVal == Long.MAX_VALUE) {
                            return null;
                        }
                        min = 1L + (long)DocValuesRangeQuery.this.lowerVal;
                    }
                    long max;
                    if (DocValuesRangeQuery.this.upperVal == null) {
                        max = Long.MAX_VALUE;
                    }
                    else if (DocValuesRangeQuery.this.includeUpper) {
                        max = (long)DocValuesRangeQuery.this.upperVal;
                    }
                    else {
                        if ((long)DocValuesRangeQuery.this.upperVal == Long.MIN_VALUE) {
                            return null;
                        }
                        max = -1L + (long)DocValuesRangeQuery.this.upperVal;
                    }
                    if (min > max) {
                        return null;
                    }
                    return (Bits)new Bits() {
                        public boolean get(final int doc) {
                            values.setDocument(doc);
                            for (int count = values.count(), i = 0; i < count; ++i) {
                                final long value = values.valueAt(i);
                                if (value >= min && value <= max) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        
                        public int length() {
                            return context.reader().maxDoc();
                        }
                    };
                }
                else {
                    if (!(DocValuesRangeQuery.this.lowerVal instanceof BytesRef) && !(DocValuesRangeQuery.this.upperVal instanceof BytesRef)) {
                        throw new AssertionError();
                    }
                    final SortedSetDocValues values2 = DocValues.getSortedSet(context.reader(), DocValuesRangeQuery.this.field);
                    long minOrd;
                    if (DocValuesRangeQuery.this.lowerVal == null) {
                        minOrd = 0L;
                    }
                    else {
                        final long ord = values2.lookupTerm((BytesRef)DocValuesRangeQuery.this.lowerVal);
                        if (ord < 0L) {
                            minOrd = -1L - ord;
                        }
                        else if (DocValuesRangeQuery.this.includeLower) {
                            minOrd = ord;
                        }
                        else {
                            minOrd = ord + 1L;
                        }
                    }
                    long maxOrd;
                    if (DocValuesRangeQuery.this.upperVal == null) {
                        maxOrd = values2.getValueCount() - 1L;
                    }
                    else {
                        final long ord2 = values2.lookupTerm((BytesRef)DocValuesRangeQuery.this.upperVal);
                        if (ord2 < 0L) {
                            maxOrd = -2L - ord2;
                        }
                        else if (DocValuesRangeQuery.this.includeUpper) {
                            maxOrd = ord2;
                        }
                        else {
                            maxOrd = ord2 - 1L;
                        }
                    }
                    if (minOrd > maxOrd) {
                        return null;
                    }
                    return (Bits)new Bits() {
                        public boolean get(final int doc) {
                            values2.setDocument(doc);
                            for (long ord = values2.nextOrd(); ord != -1L; ord = values2.nextOrd()) {
                                if (ord >= minOrd && ord <= maxOrd) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        
                        public int length() {
                            return context.reader().maxDoc();
                        }
                    };
                }
            }
        };
    }
}
