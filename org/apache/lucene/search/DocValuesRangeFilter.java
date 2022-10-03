package org.apache.lucene.search;

import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.DocValues;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

@Deprecated
public abstract class DocValuesRangeFilter<T> extends Filter
{
    final String field;
    final T lowerVal;
    final T upperVal;
    final boolean includeLower;
    final boolean includeUpper;
    
    private DocValuesRangeFilter(final String field, final T lowerVal, final T upperVal, final boolean includeLower, final boolean includeUpper) {
        this.field = field;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }
    
    public abstract DocIdSet getDocIdSet(final LeafReaderContext p0, final Bits p1) throws IOException;
    
    public static DocValuesRangeFilter<String> newStringRange(final String field, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<String>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                final SortedDocValues fcsi = DocValues.getSorted(context.reader(), this.field);
                final int lowerPoint = (this.lowerVal == null) ? -1 : fcsi.lookupTerm(new BytesRef((CharSequence)this.lowerVal));
                final int upperPoint = (this.upperVal == null) ? -1 : fcsi.lookupTerm(new BytesRef((CharSequence)this.upperVal));
                int inclusiveLowerPoint;
                if (lowerPoint == -1 && this.lowerVal == null) {
                    inclusiveLowerPoint = 0;
                }
                else if (this.includeLower && lowerPoint >= 0) {
                    inclusiveLowerPoint = lowerPoint;
                }
                else if (lowerPoint >= 0) {
                    inclusiveLowerPoint = lowerPoint + 1;
                }
                else {
                    inclusiveLowerPoint = Math.max(0, -lowerPoint - 1);
                }
                int inclusiveUpperPoint;
                if (upperPoint == -1 && this.upperVal == null) {
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                }
                else if (this.includeUpper && upperPoint >= 0) {
                    inclusiveUpperPoint = upperPoint;
                }
                else if (upperPoint >= 0) {
                    inclusiveUpperPoint = upperPoint - 1;
                }
                else {
                    inclusiveUpperPoint = -upperPoint - 2;
                }
                if (inclusiveUpperPoint < 0 || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert inclusiveLowerPoint >= 0 && inclusiveUpperPoint >= 0;
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected final boolean matchDoc(final int doc) {
                        final int docOrd = fcsi.getOrd(doc);
                        return docOrd >= inclusiveLowerPoint && docOrd <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public static DocValuesRangeFilter<BytesRef> newBytesRefRange(final String field, final BytesRef lowerVal, final BytesRef upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<BytesRef>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                final SortedDocValues fcsi = DocValues.getSorted(context.reader(), this.field);
                final int lowerPoint = (this.lowerVal == null) ? -1 : fcsi.lookupTerm((BytesRef)this.lowerVal);
                final int upperPoint = (this.upperVal == null) ? -1 : fcsi.lookupTerm((BytesRef)this.upperVal);
                int inclusiveLowerPoint;
                if (lowerPoint == -1 && this.lowerVal == null) {
                    inclusiveLowerPoint = 0;
                }
                else if (this.includeLower && lowerPoint >= 0) {
                    inclusiveLowerPoint = lowerPoint;
                }
                else if (lowerPoint >= 0) {
                    inclusiveLowerPoint = lowerPoint + 1;
                }
                else {
                    inclusiveLowerPoint = Math.max(0, -lowerPoint - 1);
                }
                int inclusiveUpperPoint;
                if (upperPoint == -1 && this.upperVal == null) {
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                }
                else if (this.includeUpper && upperPoint >= 0) {
                    inclusiveUpperPoint = upperPoint;
                }
                else if (upperPoint >= 0) {
                    inclusiveUpperPoint = upperPoint - 1;
                }
                else {
                    inclusiveUpperPoint = -upperPoint - 2;
                }
                if (inclusiveUpperPoint < 0 || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert inclusiveLowerPoint >= 0 && inclusiveUpperPoint >= 0;
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected final boolean matchDoc(final int doc) {
                        final int docOrd = fcsi.getOrd(doc);
                        return docOrd >= inclusiveLowerPoint && docOrd <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public static DocValuesRangeFilter<Integer> newIntRange(final String field, final Integer lowerVal, final Integer upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<Integer>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                int inclusiveLowerPoint;
                if (this.lowerVal != null) {
                    final int i = (int)this.lowerVal;
                    if (!this.includeLower && i == Integer.MAX_VALUE) {
                        return null;
                    }
                    inclusiveLowerPoint = (this.includeLower ? i : (i + 1));
                }
                else {
                    inclusiveLowerPoint = Integer.MIN_VALUE;
                }
                int inclusiveUpperPoint;
                if (this.upperVal != null) {
                    final int i = (int)this.upperVal;
                    if (!this.includeUpper && i == Integer.MIN_VALUE) {
                        return null;
                    }
                    inclusiveUpperPoint = (this.includeUpper ? i : (i - 1));
                }
                else {
                    inclusiveUpperPoint = Integer.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final NumericDocValues values = DocValues.getNumeric(context.reader(), this.field);
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected boolean matchDoc(final int doc) {
                        final int value = (int)values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public static DocValuesRangeFilter<Long> newLongRange(final String field, final Long lowerVal, final Long upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<Long>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                long inclusiveLowerPoint;
                if (this.lowerVal != null) {
                    final long i = (long)this.lowerVal;
                    if (!this.includeLower && i == Long.MAX_VALUE) {
                        return null;
                    }
                    inclusiveLowerPoint = (this.includeLower ? i : (i + 1L));
                }
                else {
                    inclusiveLowerPoint = Long.MIN_VALUE;
                }
                long inclusiveUpperPoint;
                if (this.upperVal != null) {
                    final long i = (long)this.upperVal;
                    if (!this.includeUpper && i == Long.MIN_VALUE) {
                        return null;
                    }
                    inclusiveUpperPoint = (this.includeUpper ? i : (i - 1L));
                }
                else {
                    inclusiveUpperPoint = Long.MAX_VALUE;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final NumericDocValues values = DocValues.getNumeric(context.reader(), this.field);
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected boolean matchDoc(final int doc) {
                        final long value = values.get(doc);
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public static DocValuesRangeFilter<Float> newFloatRange(final String field, final Float lowerVal, final Float upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<Float>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                float inclusiveLowerPoint;
                if (this.lowerVal != null) {
                    final float f = (float)this.lowerVal;
                    if (!this.includeUpper && f > 0.0f && Float.isInfinite(f)) {
                        return null;
                    }
                    final int i = NumericUtils.floatToSortableInt(f);
                    inclusiveLowerPoint = NumericUtils.sortableIntToFloat(this.includeLower ? i : (i + 1));
                }
                else {
                    inclusiveLowerPoint = Float.NEGATIVE_INFINITY;
                }
                float inclusiveUpperPoint;
                if (this.upperVal != null) {
                    final float f = (float)this.upperVal;
                    if (!this.includeUpper && f < 0.0f && Float.isInfinite(f)) {
                        return null;
                    }
                    final int i = NumericUtils.floatToSortableInt(f);
                    inclusiveUpperPoint = NumericUtils.sortableIntToFloat(this.includeUpper ? i : (i - 1));
                }
                else {
                    inclusiveUpperPoint = Float.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final NumericDocValues values = DocValues.getNumeric(context.reader(), this.field);
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected boolean matchDoc(final int doc) {
                        final float value = Float.intBitsToFloat((int)values.get(doc));
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public static DocValuesRangeFilter<Double> newDoubleRange(final String field, final Double lowerVal, final Double upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocValuesRangeFilter<Double>(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                double inclusiveLowerPoint;
                if (this.lowerVal != null) {
                    final double f = (double)this.lowerVal;
                    if (!this.includeUpper && f > 0.0 && Double.isInfinite(f)) {
                        return null;
                    }
                    final long i = NumericUtils.doubleToSortableLong(f);
                    inclusiveLowerPoint = NumericUtils.sortableLongToDouble(this.includeLower ? i : (i + 1L));
                }
                else {
                    inclusiveLowerPoint = Double.NEGATIVE_INFINITY;
                }
                double inclusiveUpperPoint;
                if (this.upperVal != null) {
                    final double f = (double)this.upperVal;
                    if (!this.includeUpper && f < 0.0 && Double.isInfinite(f)) {
                        return null;
                    }
                    final long i = NumericUtils.doubleToSortableLong(f);
                    inclusiveUpperPoint = NumericUtils.sortableLongToDouble(this.includeUpper ? i : (i - 1L));
                }
                else {
                    inclusiveUpperPoint = Double.POSITIVE_INFINITY;
                }
                if (inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                final NumericDocValues values = DocValues.getNumeric(context.reader(), this.field);
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected boolean matchDoc(final int doc) {
                        final double value = Double.longBitsToDouble(values.get(doc));
                        return value >= inclusiveLowerPoint && value <= inclusiveUpperPoint;
                    }
                };
            }
        };
    }
    
    public final String toString(final String defaultField) {
        final StringBuilder sb = new StringBuilder(this.field).append(":");
        return sb.append(this.includeLower ? '[' : '{').append((this.lowerVal == null) ? "*" : this.lowerVal.toString()).append(" TO ").append((this.upperVal == null) ? "*" : this.upperVal.toString()).append(this.includeUpper ? ']' : '}').toString();
    }
    
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        final DocValuesRangeFilter other = (DocValuesRangeFilter)o;
        if (!this.field.equals(other.field) || this.includeLower != other.includeLower || this.includeUpper != other.includeUpper) {
            return false;
        }
        Label_0093: {
            if (this.lowerVal != null) {
                if (this.lowerVal.equals(other.lowerVal)) {
                    break Label_0093;
                }
            }
            else if (other.lowerVal == null) {
                break Label_0093;
            }
            return false;
        }
        if (this.upperVal != null) {
            if (this.upperVal.equals(other.upperVal)) {
                return true;
            }
        }
        else if (other.upperVal == null) {
            return true;
        }
        return false;
    }
    
    public final int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.field.hashCode();
        h ^= ((this.lowerVal != null) ? this.lowerVal.hashCode() : 550356204);
        h = (h << 1 | h >>> 31);
        h ^= ((this.upperVal != null) ? this.upperVal.hashCode() : -1674416163);
        h ^= ((this.includeLower ? 1549299360 : -365038026) ^ (this.includeUpper ? 1721088258 : 1948649653));
        return h;
    }
    
    public String getField() {
        return this.field;
    }
    
    public boolean includesLower() {
        return this.includeLower;
    }
    
    public boolean includesUpper() {
        return this.includeUpper;
    }
    
    public T getLowerVal() {
        return this.lowerVal;
    }
    
    public T getUpperVal() {
        return this.upperVal;
    }
}
