package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public abstract class TypeValidator
{
    public static final short LESS_THAN = -1;
    public static final short EQUAL = 0;
    public static final short GREATER_THAN = 1;
    public static final short INDETERMINATE = 2;
    
    public abstract short getAllowedFacets();
    
    public abstract Object getActualValue(final String p0, final ValidationContext p1) throws InvalidDatatypeValueException;
    
    public void checkExtraRules(final Object value, final ValidationContext context) throws InvalidDatatypeValueException {
    }
    
    public boolean isIdentical(final Object value1, final Object value2) {
        return value1.equals(value2);
    }
    
    public int compare(final Object value1, final Object value2) {
        return -1;
    }
    
    public int getDataLength(final Object value) {
        return (value instanceof String) ? ((String)value).length() : -1;
    }
    
    public int getTotalDigits(final Object value) {
        return -1;
    }
    
    public int getFractionDigits(final Object value) {
        return -1;
    }
    
    public static final boolean isDigit(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    public static final int getDigit(final char ch) {
        return isDigit(ch) ? (ch - '0') : -1;
    }
}
