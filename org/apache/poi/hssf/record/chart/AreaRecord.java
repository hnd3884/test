package org.apache.poi.hssf.record.chart;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.hssf.record.StandardRecord;

public final class AreaRecord extends StandardRecord
{
    public static final short sid = 4122;
    private static final BitField stacked;
    private static final BitField displayAsPercentage;
    private static final BitField shadow;
    private short field_1_formatFlags;
    
    public AreaRecord() {
    }
    
    public AreaRecord(final AreaRecord other) {
        super(other);
        this.field_1_formatFlags = other.field_1_formatFlags;
    }
    
    public AreaRecord(final RecordInputStream in) {
        this.field_1_formatFlags = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[AREA]\n");
        buffer.append("    .formatFlags          = ").append("0x").append(HexDump.toHex(this.getFormatFlags())).append(" (").append(this.getFormatFlags()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .stacked                  = ").append(this.isStacked()).append('\n');
        buffer.append("         .displayAsPercentage      = ").append(this.isDisplayAsPercentage()).append('\n');
        buffer.append("         .shadow                   = ").append(this.isShadow()).append('\n');
        buffer.append("[/AREA]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_formatFlags);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4122;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public AreaRecord clone() {
        return this.copy();
    }
    
    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }
    
    public void setFormatFlags(final short field_1_formatFlags) {
        this.field_1_formatFlags = field_1_formatFlags;
    }
    
    public void setStacked(final boolean value) {
        this.field_1_formatFlags = AreaRecord.stacked.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isStacked() {
        return AreaRecord.stacked.isSet(this.field_1_formatFlags);
    }
    
    public void setDisplayAsPercentage(final boolean value) {
        this.field_1_formatFlags = AreaRecord.displayAsPercentage.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isDisplayAsPercentage() {
        return AreaRecord.displayAsPercentage.isSet(this.field_1_formatFlags);
    }
    
    public void setShadow(final boolean value) {
        this.field_1_formatFlags = AreaRecord.shadow.setShortBoolean(this.field_1_formatFlags, value);
    }
    
    public boolean isShadow() {
        return AreaRecord.shadow.isSet(this.field_1_formatFlags);
    }
    
    @Override
    public AreaRecord copy() {
        return new AreaRecord(this);
    }
    
    static {
        stacked = BitFieldFactory.getInstance(1);
        displayAsPercentage = BitFieldFactory.getInstance(2);
        shadow = BitFieldFactory.getInstance(4);
    }
}
