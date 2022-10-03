package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class FontIndexRecord extends StandardRecord
{
    public static final short sid = 4134;
    private short field_1_fontIndex;
    
    public FontIndexRecord() {
    }
    
    public FontIndexRecord(final FontIndexRecord other) {
        super(other);
        this.field_1_fontIndex = other.field_1_fontIndex;
    }
    
    public FontIndexRecord(final RecordInputStream in) {
        this.field_1_fontIndex = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FONTX]\n");
        buffer.append("    .fontIndex            = ").append("0x").append(HexDump.toHex(this.getFontIndex())).append(" (").append(this.getFontIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/FONTX]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_fontIndex);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4134;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FontIndexRecord clone() {
        return this.copy();
    }
    
    @Override
    public FontIndexRecord copy() {
        return new FontIndexRecord(this);
    }
    
    public short getFontIndex() {
        return this.field_1_fontIndex;
    }
    
    public void setFontIndex(final short field_1_fontIndex) {
        this.field_1_fontIndex = field_1_fontIndex;
    }
}
