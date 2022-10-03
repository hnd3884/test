package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class NotEqualPtg extends ValueOperatorPtg
{
    public static final byte sid = 14;
    public static final NotEqualPtg instance;
    
    private NotEqualPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 14;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("<>");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public NotEqualPtg copy() {
        return NotEqualPtg.instance;
    }
    
    static {
        instance = new NotEqualPtg();
    }
}
