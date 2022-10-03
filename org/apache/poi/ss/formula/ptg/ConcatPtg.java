package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class ConcatPtg extends ValueOperatorPtg
{
    public static final byte sid = 8;
    private static final String CONCAT = "&";
    public static final ConcatPtg instance;
    
    private ConcatPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 8;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("&");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public ConcatPtg copy() {
        return ConcatPtg.instance;
    }
    
    static {
        instance = new ConcatPtg();
    }
}
