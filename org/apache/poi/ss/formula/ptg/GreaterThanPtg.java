package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class GreaterThanPtg extends ValueOperatorPtg
{
    public static final byte sid = 13;
    private static final String GREATERTHAN = ">";
    public static final GreaterThanPtg instance;
    
    private GreaterThanPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 13;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append(">");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public GreaterThanPtg copy() {
        return GreaterThanPtg.instance;
    }
    
    static {
        instance = new GreaterThanPtg();
    }
}
