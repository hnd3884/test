package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class EndRecord extends StandardRecord
{
    public static final short sid = 4148;
    
    public EndRecord() {
    }
    
    public EndRecord(final RecordInputStream in) {
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[END]\n");
        buffer.append("[/END]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
    }
    
    @Override
    protected int getDataSize() {
        return 0;
    }
    
    @Override
    public short getSid() {
        return 4148;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public EndRecord clone() {
        return this.copy();
    }
    
    @Override
    public EndRecord copy() {
        return new EndRecord();
    }
}
