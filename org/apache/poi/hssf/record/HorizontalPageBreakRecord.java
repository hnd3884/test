package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;

public final class HorizontalPageBreakRecord extends PageBreakRecord
{
    public static final short sid = 27;
    
    public HorizontalPageBreakRecord() {
    }
    
    public HorizontalPageBreakRecord(final HorizontalPageBreakRecord other) {
        super(other);
    }
    
    public HorizontalPageBreakRecord(final RecordInputStream in) {
        super(in);
    }
    
    @Override
    public short getSid() {
        return 27;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PageBreakRecord clone() {
        return this.copy();
    }
    
    @Override
    public HorizontalPageBreakRecord copy() {
        return new HorizontalPageBreakRecord(this);
    }
}
