package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;

public abstract class OperandPtg extends Ptg
{
    protected OperandPtg() {
    }
    
    protected OperandPtg(final OperandPtg other) {
        super(other);
    }
    
    @Override
    public final boolean isBaseToken() {
        return false;
    }
    
    @Override
    public abstract OperandPtg copy();
}
