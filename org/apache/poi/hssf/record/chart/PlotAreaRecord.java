package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class PlotAreaRecord extends StandardRecord
{
    public static final short sid = 4149;
    
    public PlotAreaRecord() {
    }
    
    public PlotAreaRecord(final RecordInputStream in) {
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PLOTAREA]\n");
        buffer.append("[/PLOTAREA]\n");
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
        return 4149;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PlotAreaRecord clone() {
        return this.copy();
    }
    
    @Override
    public PlotAreaRecord copy() {
        return new PlotAreaRecord();
    }
}
