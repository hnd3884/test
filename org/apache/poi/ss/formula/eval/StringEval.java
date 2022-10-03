package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

public final class StringEval implements StringValueEval
{
    public static final StringEval EMPTY_INSTANCE;
    private final String _value;
    
    public StringEval(final Ptg ptg) {
        this(((StringPtg)ptg).getValue());
    }
    
    public StringEval(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        this._value = value;
    }
    
    @Override
    public String getStringValue() {
        return this._value;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this._value + "]";
    }
    
    static {
        EMPTY_INSTANCE = new StringEval("");
    }
}
