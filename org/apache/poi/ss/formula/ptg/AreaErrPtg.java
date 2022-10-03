package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class AreaErrPtg extends OperandPtg
{
    public static final byte sid = 43;
    private final int unused1;
    private final int unused2;
    
    public AreaErrPtg() {
        this.unused1 = 0;
        this.unused2 = 0;
    }
    
    public AreaErrPtg(final LittleEndianInput in) {
        this.unused1 = in.readInt();
        this.unused2 = in.readInt();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(43 + this.getPtgClass());
        out.writeInt(this.unused1);
        out.writeInt(this.unused2);
    }
    
    @Override
    public String toFormulaString() {
        return FormulaError.REF.getString();
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }
    
    @Override
    public int getSize() {
        return 9;
    }
    
    @Override
    public AreaErrPtg copy() {
        return this;
    }
}
