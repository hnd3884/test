package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class SeriesIndexRecord extends StandardRecord
{
    public static final short sid = 4197;
    private short field_1_index;
    
    public SeriesIndexRecord() {
    }
    
    public SeriesIndexRecord(final SeriesIndexRecord other) {
        super(other);
        this.field_1_index = other.field_1_index;
    }
    
    public SeriesIndexRecord(final RecordInputStream in) {
        this.field_1_index = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SINDEX]\n");
        buffer.append("    .index                = ").append("0x").append(HexDump.toHex(this.getIndex())).append(" (").append(this.getIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/SINDEX]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_index);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4197;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SeriesIndexRecord clone() {
        return this.copy();
    }
    
    @Override
    public SeriesIndexRecord copy() {
        return new SeriesIndexRecord(this);
    }
    
    public short getIndex() {
        return this.field_1_index;
    }
    
    public void setIndex(final short field_1_index) {
        this.field_1_index = field_1_index;
    }
}
