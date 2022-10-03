package com.adventnet.db.persistence.metadata;

import java.util.List;
import com.adventnet.ds.query.Column;

public interface DataTypeMetaInfo
{
    void validate(final Object p0);
    
    void validate(final Object p0, final int p1, final int p2);
    
    Object convert(final String p0);
    
    void validateAllowedValues(final AllowedValues p0);
    
    void validateValueForAllowedValues(final AllowedValues p0, final Object p1);
    
    void validateCriteriaInput(final Column p0, final Object p1, final int p2, final boolean p3);
    
    @Deprecated
    default boolean matches(final int comparator, final Object lhsValue, final Object rhsValue) {
        throw new UnsupportedOperationException("Matches function should be implemented datatype specific.");
    }
    
    default boolean matches(final int comparator, final Object lhsValue, final Object rhsValue, final boolean caseSensitive) {
        return this.matches(comparator, lhsValue, rhsValue);
    }
    
    boolean isEncrypted();
    
    boolean processInput();
    
    default boolean processCheckConstraint() {
        return false;
    }
    
    default int getMaxLength(final int maxLength) {
        return maxLength;
    }
    
    default int getPrecision(final int precision) {
        return precision;
    }
    
    default Object getValueHolder() {
        return new ValueHolder();
    }
    
    default boolean isReferenceable() {
        return true;
    }
    
    default List<String> referenceableTypes() {
        return null;
    }
    
    default boolean isPartialIndexSupported() {
        return false;
    }
    
    public static class ValueHolder
    {
    }
}
