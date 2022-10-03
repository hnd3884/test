package org.apache.poi.ss.formula.eval;

public final class BoolEval implements NumericValueEval, StringValueEval
{
    private boolean _value;
    public static final BoolEval FALSE;
    public static final BoolEval TRUE;
    
    public static BoolEval valueOf(final boolean b) {
        return b ? BoolEval.TRUE : BoolEval.FALSE;
    }
    
    private BoolEval(final boolean value) {
        this._value = value;
    }
    
    public boolean getBooleanValue() {
        return this._value;
    }
    
    @Override
    public double getNumberValue() {
        return this._value ? 1.0 : 0.0;
    }
    
    @Override
    public String getStringValue() {
        return this._value ? "TRUE" : "FALSE";
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this.getStringValue() + "]";
    }
    
    static {
        FALSE = new BoolEval(false);
        TRUE = new BoolEval(true);
    }
}
