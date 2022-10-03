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

public final class DatRecord extends StandardRecord
{
    public static final short sid = 4195;
    private static final BitField horizontalBorder;
    private static final BitField verticalBorder;
    private static final BitField border;
    private static final BitField showSeriesKey;
    private short field_1_options;
    
    public DatRecord() {
    }
    
    public DatRecord(final DatRecord other) {
        super(other);
        this.field_1_options = other.field_1_options;
    }
    
    public DatRecord(final RecordInputStream in) {
        this.field_1_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DAT]\n");
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .horizontalBorder         = ").append(this.isHorizontalBorder()).append('\n');
        buffer.append("         .verticalBorder           = ").append(this.isVerticalBorder()).append('\n');
        buffer.append("         .border                   = ").append(this.isBorder()).append('\n');
        buffer.append("         .showSeriesKey            = ").append(this.isShowSeriesKey()).append('\n');
        buffer.append("[/DAT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_options);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 4195;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DatRecord clone() {
        return this.copy();
    }
    
    @Override
    public DatRecord copy() {
        return new DatRecord(this);
    }
    
    public short getOptions() {
        return this.field_1_options;
    }
    
    public void setOptions(final short field_1_options) {
        this.field_1_options = field_1_options;
    }
    
    public void setHorizontalBorder(final boolean value) {
        this.field_1_options = DatRecord.horizontalBorder.setShortBoolean(this.field_1_options, value);
    }
    
    public boolean isHorizontalBorder() {
        return DatRecord.horizontalBorder.isSet(this.field_1_options);
    }
    
    public void setVerticalBorder(final boolean value) {
        this.field_1_options = DatRecord.verticalBorder.setShortBoolean(this.field_1_options, value);
    }
    
    public boolean isVerticalBorder() {
        return DatRecord.verticalBorder.isSet(this.field_1_options);
    }
    
    public void setBorder(final boolean value) {
        this.field_1_options = DatRecord.border.setShortBoolean(this.field_1_options, value);
    }
    
    public boolean isBorder() {
        return DatRecord.border.isSet(this.field_1_options);
    }
    
    public void setShowSeriesKey(final boolean value) {
        this.field_1_options = DatRecord.showSeriesKey.setShortBoolean(this.field_1_options, value);
    }
    
    public boolean isShowSeriesKey() {
        return DatRecord.showSeriesKey.isSet(this.field_1_options);
    }
    
    static {
        horizontalBorder = BitFieldFactory.getInstance(1);
        verticalBorder = BitFieldFactory.getInstance(2);
        border = BitFieldFactory.getInstance(4);
        showSeriesKey = BitFieldFactory.getInstance(8);
    }
}
