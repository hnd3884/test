package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class UnitsRecord extends StandardRecord
{
    public static final short sid = 4097;
    private short field_1_units;
    
    public UnitsRecord() {
    }
    
    public UnitsRecord(final UnitsRecord other) {
        super(other);
        this.field_1_units = other.field_1_units;
    }
    
    public UnitsRecord(final RecordInputStream in) {
        this.field_1_units = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[UNITS]\n");
        buffer.append("    .units                = ").append("0x").append(HexDump.toHex(this.getUnits())).append(" (").append(this.getUnits()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/UNITS]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_units);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4097;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public UnitsRecord clone() {
        return this.copy();
    }
    
    @Override
    public UnitsRecord copy() {
        return new UnitsRecord(this);
    }
    
    public short getUnits() {
        return this.field_1_units;
    }
    
    public void setUnits(final short field_1_units) {
        this.field_1_units = field_1_units;
    }
}
