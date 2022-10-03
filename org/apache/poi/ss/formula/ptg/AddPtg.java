package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public final class AddPtg extends ValueOperatorPtg
{
    public static final byte sid = 3;
    private static final String ADD = "+";
    public static final AddPtg instance;
    
    private AddPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 3;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append("+");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public AddPtg copy() {
        return AddPtg.instance;
    }
    
    static {
        instance = new AddPtg();
    }
}
