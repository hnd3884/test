package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class DividePtg extends ValueOperatorPtg
{
    public static final byte sid = 6;
    public static final DividePtg instance;
    
    private DividePtg() {
    }
    
    @Override
    protected byte getSid() {
        return 6;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("/");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public DividePtg copy() {
        return DividePtg.instance;
    }
    
    static {
        instance = new DividePtg();
    }
}
