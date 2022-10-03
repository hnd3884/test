package org.apache.lucene.search;

import java.util.LinkedList;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import java.util.Objects;
import org.apache.lucene.document.FieldType;

public final class NumericRangeQuery<T extends Number> extends MultiTermQuery
{
    final int precisionStep;
    final FieldType.NumericType dataType;
    final T min;
    final T max;
    final boolean minInclusive;
    final boolean maxInclusive;
    static final long LONG_NEGATIVE_INFINITY;
    static final long LONG_POSITIVE_INFINITY;
    static final int INT_NEGATIVE_INFINITY;
    static final int INT_POSITIVE_INFINITY;
    
    private NumericRangeQuery(final String field, final int precisionStep, final FieldType.NumericType dataType, final T min, final T max, final boolean minInclusive, final boolean maxInclusive) {
        super(field);
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        this.precisionStep = precisionStep;
        this.dataType = Objects.requireNonNull(dataType, "NumericType must not be null");
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }
    
    public static NumericRangeQuery<Long> newLongRange(final String field, final int precisionStep, final Long min, final Long max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, precisionStep, FieldType.NumericType.LONG, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Long> newLongRange(final String field, final Long min, final Long max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Long>(field, 16, FieldType.NumericType.LONG, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Integer> newIntRange(final String field, final int precisionStep, final Integer min, final Integer max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, precisionStep, FieldType.NumericType.INT, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Integer> newIntRange(final String field, final Integer min, final Integer max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Integer>(field, 8, FieldType.NumericType.INT, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Double> newDoubleRange(final String field, final int precisionStep, final Double min, final Double max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, precisionStep, FieldType.NumericType.DOUBLE, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Double> newDoubleRange(final String field, final Double min, final Double max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Double>(field, 16, FieldType.NumericType.DOUBLE, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Float> newFloatRange(final String field, final int precisionStep, final Float min, final Float max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, precisionStep, FieldType.NumericType.FLOAT, min, max, minInclusive, maxInclusive);
    }
    
    public static NumericRangeQuery<Float> newFloatRange(final String field, final Float min, final Float max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeQuery<Float>(field, 8, FieldType.NumericType.FLOAT, min, max, minInclusive, maxInclusive);
    }
    
    @Override
    protected TermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        if (this.min != null && this.max != null && ((Comparable)this.min).compareTo(this.max) > 0) {
            return TermsEnum.EMPTY;
        }
        return new NumericRangeTermsEnum(terms.iterator());
    }
    
    public boolean includesMin() {
        return this.minInclusive;
    }
    
    public boolean includesMax() {
        return this.maxInclusive;
    }
    
    public T getMin() {
        return this.min;
    }
    
    public T getMax() {
        return this.max;
    }
    
    public int getPrecisionStep() {
        return this.precisionStep;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        if (!this.getField().equals(field)) {
            sb.append(this.getField()).append(':');
        }
        return sb.append(this.minInclusive ? '[' : '{').append((this.min == null) ? "*" : this.min.toString()).append(" TO ").append((this.max == null) ? "*" : this.max.toString()).append(this.maxInclusive ? ']' : '}').append(ToStringUtils.boost(this.getBoost())).toString();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof NumericRangeQuery) {
            final NumericRangeQuery q = (NumericRangeQuery)o;
            if (q.min == null) {
                if (this.min != null) {
                    return false;
                }
            }
            else if (!q.min.equals(this.min)) {
                return false;
            }
            if (q.max == null) {
                if (this.max != null) {
                    return false;
                }
            }
            else if (!q.max.equals(this.max)) {
                return false;
            }
            if (this.minInclusive == q.minInclusive && this.maxInclusive == q.maxInclusive && this.precisionStep == q.precisionStep) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        int hash = super.hashCode();
        hash += (this.precisionStep ^ 0x64365465);
        if (this.min != null) {
            hash += (this.min.hashCode() ^ 0x14FA55FB);
        }
        if (this.max != null) {
            hash += (this.max.hashCode() ^ 0x733FA5FE);
        }
        return hash + (Boolean.valueOf(this.minInclusive).hashCode() ^ 0x14FA55FB) + (Boolean.valueOf(this.maxInclusive).hashCode() ^ 0x733FA5FE);
    }
    
    static {
        LONG_NEGATIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.NEGATIVE_INFINITY);
        LONG_POSITIVE_INFINITY = NumericUtils.doubleToSortableLong(Double.POSITIVE_INFINITY);
        INT_NEGATIVE_INFINITY = NumericUtils.floatToSortableInt(Float.NEGATIVE_INFINITY);
        INT_POSITIVE_INFINITY = NumericUtils.floatToSortableInt(Float.POSITIVE_INFINITY);
    }
    
    private final class NumericRangeTermsEnum extends FilteredTermsEnum
    {
        private BytesRef currentLowerBound;
        private BytesRef currentUpperBound;
        private final LinkedList<BytesRef> rangeBounds;
        
        NumericRangeTermsEnum(final TermsEnum tenum) {
            super(tenum);
            this.rangeBounds = new LinkedList<BytesRef>();
            switch (NumericRangeQuery.this.dataType) {
                case LONG:
                case DOUBLE: {
                    long minBound;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.LONG) {
                        minBound = ((NumericRangeQuery.this.min == null) ? Long.MIN_VALUE : NumericRangeQuery.this.min.longValue());
                    }
                    else {
                        assert NumericRangeQuery.this.dataType == FieldType.NumericType.DOUBLE;
                        minBound = ((NumericRangeQuery.this.min == null) ? NumericRangeQuery.LONG_NEGATIVE_INFINITY : NumericUtils.doubleToSortableLong(NumericRangeQuery.this.min.doubleValue()));
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound == Long.MAX_VALUE) {
                            break;
                        }
                        ++minBound;
                    }
                    long maxBound;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.LONG) {
                        maxBound = ((NumericRangeQuery.this.max == null) ? Long.MAX_VALUE : NumericRangeQuery.this.max.longValue());
                    }
                    else {
                        assert NumericRangeQuery.this.dataType == FieldType.NumericType.DOUBLE;
                        maxBound = ((NumericRangeQuery.this.max == null) ? NumericRangeQuery.LONG_POSITIVE_INFINITY : NumericUtils.doubleToSortableLong(NumericRangeQuery.this.max.doubleValue()));
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound == Long.MIN_VALUE) {
                            break;
                        }
                        --maxBound;
                    }
                    NumericUtils.splitLongRange(new NumericUtils.LongRangeBuilder() {
                        @Override
                        public final void addRange(final BytesRef minPrefixCoded, final BytesRef maxPrefixCoded) {
                            NumericRangeTermsEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermsEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound, maxBound);
                    break;
                }
                case INT:
                case FLOAT: {
                    int minBound2;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.INT) {
                        minBound2 = ((NumericRangeQuery.this.min == null) ? Integer.MIN_VALUE : NumericRangeQuery.this.min.intValue());
                    }
                    else {
                        assert NumericRangeQuery.this.dataType == FieldType.NumericType.FLOAT;
                        minBound2 = ((NumericRangeQuery.this.min == null) ? NumericRangeQuery.INT_NEGATIVE_INFINITY : NumericUtils.floatToSortableInt(NumericRangeQuery.this.min.floatValue()));
                    }
                    if (!NumericRangeQuery.this.minInclusive && NumericRangeQuery.this.min != null) {
                        if (minBound2 == Integer.MAX_VALUE) {
                            break;
                        }
                        ++minBound2;
                    }
                    int maxBound2;
                    if (NumericRangeQuery.this.dataType == FieldType.NumericType.INT) {
                        maxBound2 = ((NumericRangeQuery.this.max == null) ? Integer.MAX_VALUE : NumericRangeQuery.this.max.intValue());
                    }
                    else {
                        assert NumericRangeQuery.this.dataType == FieldType.NumericType.FLOAT;
                        maxBound2 = ((NumericRangeQuery.this.max == null) ? NumericRangeQuery.INT_POSITIVE_INFINITY : NumericUtils.floatToSortableInt(NumericRangeQuery.this.max.floatValue()));
                    }
                    if (!NumericRangeQuery.this.maxInclusive && NumericRangeQuery.this.max != null) {
                        if (maxBound2 == Integer.MIN_VALUE) {
                            break;
                        }
                        --maxBound2;
                    }
                    NumericUtils.splitIntRange(new NumericUtils.IntRangeBuilder() {
                        @Override
                        public final void addRange(final BytesRef minPrefixCoded, final BytesRef maxPrefixCoded) {
                            NumericRangeTermsEnum.this.rangeBounds.add(minPrefixCoded);
                            NumericRangeTermsEnum.this.rangeBounds.add(maxPrefixCoded);
                        }
                    }, NumericRangeQuery.this.precisionStep, minBound2, maxBound2);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid NumericType");
                }
            }
        }
        
        private void nextRange() {
            assert this.rangeBounds.size() % 2 == 0;
            this.currentLowerBound = this.rangeBounds.removeFirst();
            assert this.currentUpperBound.compareTo(this.currentLowerBound) <= 0 : "The current upper bound must be <= the new lower bound";
            this.currentUpperBound = this.rangeBounds.removeFirst();
        }
        
        @Override
        protected final BytesRef nextSeekTerm(final BytesRef term) {
            while (this.rangeBounds.size() >= 2) {
                this.nextRange();
                if (term != null && term.compareTo(this.currentUpperBound) > 0) {
                    continue;
                }
                return (term != null && term.compareTo(this.currentLowerBound) > 0) ? term : this.currentLowerBound;
            }
            assert this.rangeBounds.isEmpty();
            final BytesRef bytesRef = null;
            this.currentUpperBound = bytesRef;
            this.currentLowerBound = bytesRef;
            return null;
        }
        
        @Override
        protected final AcceptStatus accept(final BytesRef term) {
            while (this.currentUpperBound == null || term.compareTo(this.currentUpperBound) > 0) {
                if (this.rangeBounds.isEmpty()) {
                    return AcceptStatus.END;
                }
                if (term.compareTo((BytesRef)this.rangeBounds.getFirst()) < 0) {
                    return AcceptStatus.NO_AND_SEEK;
                }
                this.nextRange();
            }
            return AcceptStatus.YES;
        }
    }
}
