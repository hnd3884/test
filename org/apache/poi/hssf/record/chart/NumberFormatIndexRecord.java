package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class NumberFormatIndexRecord extends StandardRecord
{
    public static final short sid = 4174;
    private short field_1_formatIndex;
    
    public NumberFormatIndexRecord() {
    }
    
    public NumberFormatIndexRecord(final NumberFormatIndexRecord other) {
        super(other);
        this.field_1_formatIndex = other.field_1_formatIndex;
    }
    
    public NumberFormatIndexRecord(final RecordInputStream in) {
        this.field_1_formatIndex = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[IFMT]\n");
        buffer.append("    .formatIndex          = ").append("0x").append(HexDump.toHex(this.getFormatIndex())).append(" (").append(this.getFormatIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/IFMT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_formatIndex);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4174;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public NumberFormatIndexRecord clone() {
        return this.copy();
    }
    
    @Override
    public NumberFormatIndexRecord copy() {
        return new NumberFormatIndexRecord(this);
    }
    
    public short getFormatIndex() {
        return this.field_1_formatIndex;
    }
    
    public void setFormatIndex(final short field_1_formatIndex) {
        this.field_1_formatIndex = field_1_formatIndex;
    }
}
