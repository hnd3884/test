package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class BeginRecord extends StandardRecord
{
    public static final short sid = 4147;
    
    public BeginRecord() {
    }
    
    public BeginRecord(final BeginRecord other) {
        super(other);
    }
    
    public BeginRecord(final RecordInputStream in) {
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[BEGIN]\n");
        buffer.append("[/BEGIN]\n");
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
        return 4147;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public BeginRecord clone() {
        return this.copy();
    }
    
    @Override
    public BeginRecord copy() {
        return new BeginRecord(this);
    }
}
