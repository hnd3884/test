package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class LessEqualPtg extends ValueOperatorPtg
{
    public static final byte sid = 10;
    public static final LessEqualPtg instance;
    
    private LessEqualPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 10;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("<=");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public LessEqualPtg copy() {
        return LessEqualPtg.instance;
    }
    
    static {
        instance = new LessEqualPtg();
    }
}
