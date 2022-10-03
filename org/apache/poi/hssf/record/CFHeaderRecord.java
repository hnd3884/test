package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.util.CellRangeAddress;

public final class CFHeaderRecord extends CFHeaderBase
{
    public static final short sid = 432;
    
    public CFHeaderRecord() {
        this.createEmpty();
    }
    
    public CFHeaderRecord(final CFHeaderRecord other) {
        super(other);
    }
    
    public CFHeaderRecord(final CellRangeAddress[] regions, final int nRules) {
        super(regions, nRules);
    }
    
    public CFHeaderRecord(final RecordInputStream in) {
        this.read(in);
    }
    
    @Override
    protected String getRecordName() {
        return "CFHEADER";
    }
    
    @Override
    public short getSid() {
        return 432;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public CFHeaderRecord clone() {
        return this.copy();
    }
    
    @Override
    public CFHeaderRecord copy() {
        return new CFHeaderRecord(this);
    }
}
