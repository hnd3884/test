package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class UnaryPlusPtg extends ValueOperatorPtg
{
    public static final byte sid = 18;
    private static final String ADD = "+";
    public static final UnaryPlusPtg instance;
    
    private UnaryPlusPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 18;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 1;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("+");
        buffer.append(operands[0]);
        return buffer.toString();
    }
    
    @Override
    public UnaryPlusPtg copy() {
        return UnaryPlusPtg.instance;
    }
    
    static {
        instance = new UnaryPlusPtg();
    }
}
