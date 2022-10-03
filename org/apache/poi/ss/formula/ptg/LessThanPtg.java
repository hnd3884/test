package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class LessThanPtg extends ValueOperatorPtg
{
    public static final byte sid = 9;
    private static final String LESSTHAN = "<";
    public static final LessThanPtg instance;
    
    private LessThanPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 9;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("<");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public LessThanPtg copy() {
        return LessThanPtg.instance;
    }
    
    static {
        instance = new LessThanPtg();
    }
}
