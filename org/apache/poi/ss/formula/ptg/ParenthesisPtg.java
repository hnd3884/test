package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class ParenthesisPtg extends ControlPtg
{
    private static final int SIZE = 1;
    public static final byte sid = 21;
    public static final ParenthesisPtg instance;
    
    private ParenthesisPtg() {
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(21 + this.getPtgClass());
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public String toFormulaString() {
        return "()";
    }
    
    public String toFormulaString(final String[] operands) {
        return "(" + operands[0] + ")";
    }
    
    @Override
    public ParenthesisPtg copy() {
        return ParenthesisPtg.instance;
    }
    
    static {
        instance = new ParenthesisPtg();
    }
}
