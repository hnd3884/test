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

public final class FrameRecord extends StandardRecord
{
    public static final short sid = 4146;
    public static final short BORDER_TYPE_REGULAR = 0;
    public static final short BORDER_TYPE_SHADOW = 1;
    private static final BitField autoSize;
    private static final BitField autoPosition;
    private short field_1_borderType;
    private short field_2_options;
    
    public FrameRecord() {
    }
    
    public FrameRecord(final FrameRecord other) {
        super(other);
        this.field_1_borderType = other.field_1_borderType;
        this.field_2_options = other.field_2_options;
    }
    
    public FrameRecord(final RecordInputStream in) {
        this.field_1_borderType = in.readShort();
        this.field_2_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FRAME]\n");
        buffer.append("    .borderType           = ").append("0x").append(HexDump.toHex(this.getBorderType())).append(" (").append(this.getBorderType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoSize                 = ").append(this.isAutoSize()).append('\n');
        buffer.append("         .autoPosition             = ").append(this.isAutoPosition()).append('\n');
        buffer.append("[/FRAME]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_borderType);
        out.writeShort(this.field_2_options);
    }
    
    @Override
    protected int getDataSize() {
        return 4;
    }
    
    @Override
    public short getSid() {
        return 4146;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FrameRecord clone() {
        return this.copy();
    }
    
    @Override
    public FrameRecord copy() {
        return new FrameRecord(this);
    }
    
    public short getBorderType() {
        return this.field_1_borderType;
    }
    
    public void setBorderType(final short field_1_borderType) {
        this.field_1_borderType = field_1_borderType;
    }
    
    public short getOptions() {
        return this.field_2_options;
    }
    
    public void setOptions(final short field_2_options) {
        this.field_2_options = field_2_options;
    }
    
    public void setAutoSize(final boolean value) {
        this.field_2_options = FrameRecord.autoSize.setShortBoolean(this.field_2_options, value);
    }
    
    public boolean isAutoSize() {
        return FrameRecord.autoSize.isSet(this.field_2_options);
    }
    
    public void setAutoPosition(final boolean value) {
        this.field_2_options = FrameRecord.autoPosition.setShortBoolean(this.field_2_options, value);
    }
    
    public boolean isAutoPosition() {
        return FrameRecord.autoPosition.isSet(this.field_2_options);
    }
    
    static {
        autoSize = BitFieldFactory.getInstance(1);
        autoPosition = BitFieldFactory.getInstance(2);
    }
}
