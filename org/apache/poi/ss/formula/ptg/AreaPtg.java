package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.LittleEndianInput;

public final class AreaPtg extends Area2DPtgBase
{
    public static final short sid = 37;
    
    public AreaPtg(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean firstRowRelative, final boolean lastRowRelative, final boolean firstColRelative, final boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }
    
    public AreaPtg(final AreaPtg other) {
        super(other);
    }
    
    public AreaPtg(final LittleEndianInput in) {
        super(in);
    }
    
    public AreaPtg(final AreaReference arearef) {
        super(arearef);
    }
    
    @Override
    protected byte getSid() {
        return 37;
    }
    
    @Override
    public AreaPtg copy() {
        return new AreaPtg(this);
    }
}
