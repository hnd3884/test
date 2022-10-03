package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;

public final class VerticalPageBreakRecord extends PageBreakRecord
{
    public static final short sid = 26;
    
    public VerticalPageBreakRecord() {
    }
    
    public VerticalPageBreakRecord(final VerticalPageBreakRecord other) {
        super(other);
    }
    
    public VerticalPageBreakRecord(final RecordInputStream in) {
        super(in);
    }
    
    @Override
    public short getSid() {
        return 26;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public VerticalPageBreakRecord clone() {
        return this.copy();
    }
    
    @Override
    public VerticalPageBreakRecord copy() {
        return new VerticalPageBreakRecord(this);
    }
}
