package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class IntersectionPtg extends OperationPtg
{
    public static final byte sid = 15;
    public static final IntersectionPtg instance;
    
    private IntersectionPtg() {
    }
    
    @Override
    public final boolean isBaseToken() {
        return true;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(15 + this.getPtgClass());
    }
    
    @Override
    public String toFormulaString() {
        return " ";
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(operands[0]);
        buffer.append(" ");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public IntersectionPtg copy() {
        return IntersectionPtg.instance;
    }
    
    static {
        instance = new IntersectionPtg();
    }
}
