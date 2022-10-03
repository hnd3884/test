package com.zoho.security.api;

import com.zoho.security.util.RangeUtil;

public final class Range<T extends Comparable<T>>
{
    private final T lowerLimit;
    private final T upperLimit;
    private final String relationalOperator;
    private String rangeNotation;
    
    private Range(final T lowerLimit, final T upperLimit, final String relationalOperator) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.relationalOperator = relationalOperator;
    }
    
    public static <T extends Comparable<T>> Range<?> createRange(final T lowerLimit, final T upperLimit, final String relationalOperator) {
        return new Range<Object>(lowerLimit, upperLimit, relationalOperator);
    }
    
    public <T extends Comparable<T>> boolean contains(T value) {
        if (value == null) {
            return false;
        }
        final String dataType = (this.lowerLimit != null) ? this.lowerLimit.getClass().getSimpleName() : this.upperLimit.getClass().getSimpleName();
        if (!dataType.equals(value.getClass().getSimpleName())) {
            value = RangeUtil.typeCastAsNumericType(value.toString(), dataType);
        }
        if (this.relationalOperator != null) {
            if (this.lowerLimit == null) {
                return "<".equals(this.relationalOperator) ? (value.compareTo((T)this.upperLimit) < 0) : (value.compareTo((T)this.upperLimit) <= 0);
            }
            if (this.upperLimit == null) {
                return ">".equals(this.relationalOperator) ? (value.compareTo((T)this.lowerLimit) > 0) : (value.compareTo((T)this.lowerLimit) >= 0);
            }
        }
        return value.compareTo((T)this.lowerLimit) >= 0 && value.compareTo((T)this.upperLimit) <= 0;
    }
    
    public T getLowerLimit() {
        return this.lowerLimit;
    }
    
    public T getUpperLimit() {
        return this.upperLimit;
    }
    
    public String getRelationalOperator() {
        return this.relationalOperator;
    }
    
    public void setRangeNotation(final String rangeNotation) {
        this.rangeNotation = rangeNotation;
    }
    
    public String getRangeNotation() {
        return this.rangeNotation;
    }
    
    @Override
    public String toString() {
        return "Range :: lowerLimit: \"" + this.lowerLimit + "\", upperLimit: \"" + this.upperLimit + "\", relationalOperator: \"" + this.relationalOperator + "\".";
    }
}
