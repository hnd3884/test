package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class PowerPtg extends ValueOperatorPtg
{
    public static final byte sid = 7;
    public static final PowerPtg instance;
    
    private PowerPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 7;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("^");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public PowerPtg copy() {
        return PowerPtg.instance;
    }
    
    static {
        instance = new PowerPtg();
    }
}
