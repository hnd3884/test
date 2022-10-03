package org.apache.poi.ss.formula;

import org.apache.poi.util.Internal;

@Internal
public enum FormulaType
{
    CELL(true), 
    SHARED(true), 
    ARRAY(false), 
    CONDFORMAT(true), 
    NAMEDRANGE(false), 
    DATAVALIDATION_LIST(false);
    
    private final boolean isSingleValue;
    
    private FormulaType(final boolean singleValue) {
        this.isSingleValue = singleValue;
    }
    
    public boolean isSingleValue() {
        return this.isSingleValue;
    }
    
    public static FormulaType forInt(final int code) {
        if (code >= 0 && code < values().length) {
            return values()[code];
        }
        throw new IllegalArgumentException("Invalid FormulaType code: " + code);
    }
}
