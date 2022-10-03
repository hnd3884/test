package org.apache.lucene.search;

@Deprecated
public final class NumericRangeFilter<T extends Number> extends MultiTermQueryWrapperFilter<NumericRangeQuery<T>>
{
    private NumericRangeFilter(final NumericRangeQuery<T> query) {
        super(query);
    }
    
    public static NumericRangeFilter<Long> newLongRange(final String field, final int precisionStep, final Long min, final Long max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Long>(NumericRangeQuery.newLongRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Long> newLongRange(final String field, final Long min, final Long max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Long>(NumericRangeQuery.newLongRange(field, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Integer> newIntRange(final String field, final int precisionStep, final Integer min, final Integer max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Integer>(NumericRangeQuery.newIntRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Integer> newIntRange(final String field, final Integer min, final Integer max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Integer>(NumericRangeQuery.newIntRange(field, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Double> newDoubleRange(final String field, final int precisionStep, final Double min, final Double max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Double>(NumericRangeQuery.newDoubleRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Double> newDoubleRange(final String field, final Double min, final Double max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Double>(NumericRangeQuery.newDoubleRange(field, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Float> newFloatRange(final String field, final int precisionStep, final Float min, final Float max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Float>(NumericRangeQuery.newFloatRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }
    
    public static NumericRangeFilter<Float> newFloatRange(final String field, final Float min, final Float max, final boolean minInclusive, final boolean maxInclusive) {
        return new NumericRangeFilter<Float>(NumericRangeQuery.newFloatRange(field, min, max, minInclusive, maxInclusive));
    }
    
    public boolean includesMin() {
        return ((NumericRangeQuery)this.query).includesMin();
    }
    
    public boolean includesMax() {
        return ((NumericRangeQuery)this.query).includesMax();
    }
    
    public T getMin() {
        return ((NumericRangeQuery)this.query).getMin();
    }
    
    public T getMax() {
        return ((NumericRangeQuery)this.query).getMax();
    }
    
    public int getPrecisionStep() {
        return ((NumericRangeQuery)this.query).getPrecisionStep();
    }
}
