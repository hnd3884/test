package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class PercentPtg extends ValueOperatorPtg
{
    public static final int SIZE = 1;
    public static final byte sid = 20;
    private static final String PERCENT = "%";
    public static final PercentPtg instance;
    
    private PercentPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 20;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 1;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("%");
        return buffer.toString();
    }
    
    @Override
    public PercentPtg copy() {
        return PercentPtg.instance;
    }
    
    static {
        instance = new PercentPtg();
    }
}
