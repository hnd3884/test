package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;

public abstract class ValueOperatorPtg extends OperationPtg
{
    protected ValueOperatorPtg() {
    }
    
    @Override
    public final boolean isBaseToken() {
        return true;
    }
    
    @Override
    public final byte getDefaultOperandClass() {
        return 32;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(this.getSid());
    }
    
    protected abstract byte getSid();
    
    @Override
    public final int getSize() {
        return 1;
    }
    
    @Override
    public final String toFormulaString() {
        throw new RuntimeException("toFormulaString(String[] operands) should be used for subclasses of OperationPtgs");
    }
}
