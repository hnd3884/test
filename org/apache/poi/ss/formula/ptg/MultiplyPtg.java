package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class MultiplyPtg extends ValueOperatorPtg
{
    public static final byte sid = 5;
    public static final MultiplyPtg instance;
    
    private MultiplyPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 5;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("*");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public MultiplyPtg copy() {
        return MultiplyPtg.instance;
    }
    
    static {
        instance = new MultiplyPtg();
    }
}
