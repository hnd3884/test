package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianInput;

public final class RefNPtg extends Ref2DPtgBase
{
    public static final byte sid = 44;
    
    public RefNPtg(final LittleEndianInput in) {
        super(in);
    }
    
    public RefNPtg(final RefNPtg other) {
        super(other);
    }
    
    @Override
    protected byte getSid() {
        return 44;
    }
    
    @Override
    protected final String formatReferenceAsString() {
        final StringBuilder builder = new StringBuilder();
        if (this.isRowRelative()) {
            builder.append("RowOffset: ").append(this.getRow()).append(" ");
        }
        else {
            builder.append(this.getRow() + 1);
        }
        if (this.isColRelative()) {
            builder.append(" ColOffset: ").append(this.getColumn());
        }
        else {
            builder.append(CellReference.convertNumToColString(this.getColumn()));
        }
        return builder.toString();
    }
    
    @Override
    public RefNPtg copy() {
        return new RefNPtg(this);
    }
}
