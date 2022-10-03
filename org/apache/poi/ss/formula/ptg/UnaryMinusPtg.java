package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class UnaryMinusPtg extends ValueOperatorPtg
{
    public static final byte sid = 19;
    private static final String MINUS = "-";
    public static final UnaryMinusPtg instance;
    
    private UnaryMinusPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 19;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 1;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("-");
        buffer.append(operands[0]);
        return buffer.toString();
    }
    
    @Override
    public UnaryMinusPtg copy() {
        return UnaryMinusPtg.instance;
    }
    
    static {
        instance = new UnaryMinusPtg();
    }
}
