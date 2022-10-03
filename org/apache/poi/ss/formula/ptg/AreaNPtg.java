package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianInput;

public final class AreaNPtg extends Area2DPtgBase
{
    public static final short sid = 45;
    
    public AreaNPtg(final AreaNPtg other) {
        super(other);
    }
    
    public AreaNPtg(final LittleEndianInput in) {
        super(in);
    }
    
    @Override
    protected byte getSid() {
        return 45;
    }
    
    @Override
    public AreaNPtg copy() {
        return new AreaNPtg(this);
    }
}
