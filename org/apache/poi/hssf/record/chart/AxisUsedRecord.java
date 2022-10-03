package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class AxisUsedRecord extends StandardRecord
{
    public static final short sid = 4166;
    private short field_1_numAxis;
    
    public AxisUsedRecord() {
    }
    
    public AxisUsedRecord(final AxisUsedRecord other) {
        super(other);
        this.field_1_numAxis = other.field_1_numAxis;
    }
    
    public AxisUsedRecord(final RecordInputStream in) {
        this.field_1_numAxis = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[AXISUSED]\n");
        buffer.append("    .numAxis              = ").append("0x").append(HexDump.toHex(this.getNumAxis())).append(" (").append(this.getNumAxis()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXISUSED]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_numAxis);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4166;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public AxisUsedRecord clone() {
        return this.copy();
    }
    
    public short getNumAxis() {
        return this.field_1_numAxis;
    }
    
    public void setNumAxis(final short field_1_numAxis) {
        this.field_1_numAxis = field_1_numAxis;
    }
    
    @Override
    public AxisUsedRecord copy() {
        return new AxisUsedRecord(this);
    }
}
