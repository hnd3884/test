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

public final class CategorySeriesAxisRecord extends StandardRecord
{
    public static final short sid = 4128;
    private static final BitField valueAxisCrossing;
    private static final BitField crossesFarRight;
    private static final BitField reversed;
    private short field_1_crossingPoint;
    private short field_2_labelFrequency;
    private short field_3_tickMarkFrequency;
    private short field_4_options;
    
    public CategorySeriesAxisRecord() {
    }
    
    public CategorySeriesAxisRecord(final CategorySeriesAxisRecord other) {
        super(other);
        this.field_1_crossingPoint = other.field_1_crossingPoint;
        this.field_2_labelFrequency = other.field_2_labelFrequency;
        this.field_3_tickMarkFrequency = other.field_3_tickMarkFrequency;
        this.field_4_options = other.field_4_options;
    }
    
    public CategorySeriesAxisRecord(final RecordInputStream in) {
        this.field_1_crossingPoint = in.readShort();
        this.field_2_labelFrequency = in.readShort();
        this.field_3_tickMarkFrequency = in.readShort();
        this.field_4_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CATSERRANGE]\n");
        buffer.append("    .crossingPoint        = ").append("0x").append(HexDump.toHex(this.getCrossingPoint())).append(" (").append(this.getCrossingPoint()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .labelFrequency       = ").append("0x").append(HexDump.toHex(this.getLabelFrequency())).append(" (").append(this.getLabelFrequency()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .tickMarkFrequency    = ").append("0x").append(HexDump.toHex(this.getTickMarkFrequency())).append(" (").append(this.getTickMarkFrequency()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .valueAxisCrossing        = ").append(this.isValueAxisCrossing()).append('\n');
        buffer.append("         .crossesFarRight          = ").append(this.isCrossesFarRight()).append('\n');
        buffer.append("         .reversed                 = ").append(this.isReversed()).append('\n');
        buffer.append("[/CATSERRANGE]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_crossingPoint);
        out.writeShort(this.field_2_labelFrequency);
        out.writeShort(this.field_3_tickMarkFrequency);
        out.writeShort(this.field_4_options);
    }
    
    @Override
    protected int getDataSize() {
        return 8;
    }
    
    @Override
    public short getSid() {
        return 4128;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public CategorySeriesAxisRecord clone() {
        return this.copy();
    }
    
    public short getCrossingPoint() {
        return this.field_1_crossingPoint;
    }
    
    public void setCrossingPoint(final short field_1_crossingPoint) {
        this.field_1_crossingPoint = field_1_crossingPoint;
    }
    
    public short getLabelFrequency() {
        return this.field_2_labelFrequency;
    }
    
    public void setLabelFrequency(final short field_2_labelFrequency) {
        this.field_2_labelFrequency = field_2_labelFrequency;
    }
    
    public short getTickMarkFrequency() {
        return this.field_3_tickMarkFrequency;
    }
    
    public void setTickMarkFrequency(final short field_3_tickMarkFrequency) {
        this.field_3_tickMarkFrequency = field_3_tickMarkFrequency;
    }
    
    public short getOptions() {
        return this.field_4_options;
    }
    
    public void setOptions(final short field_4_options) {
        this.field_4_options = field_4_options;
    }
    
    public void setValueAxisCrossing(final boolean value) {
        this.field_4_options = CategorySeriesAxisRecord.valueAxisCrossing.setShortBoolean(this.field_4_options, value);
    }
    
    public boolean isValueAxisCrossing() {
        return CategorySeriesAxisRecord.valueAxisCrossing.isSet(this.field_4_options);
    }
    
    public void setCrossesFarRight(final boolean value) {
        this.field_4_options = CategorySeriesAxisRecord.crossesFarRight.setShortBoolean(this.field_4_options, value);
    }
    
    public boolean isCrossesFarRight() {
        return CategorySeriesAxisRecord.crossesFarRight.isSet(this.field_4_options);
    }
    
    public void setReversed(final boolean value) {
        this.field_4_options = CategorySeriesAxisRecord.reversed.setShortBoolean(this.field_4_options, value);
    }
    
    public boolean isReversed() {
        return CategorySeriesAxisRecord.reversed.isSet(this.field_4_options);
    }
    
    @Override
    public CategorySeriesAxisRecord copy() {
        return new CategorySeriesAxisRecord(this);
    }
    
    static {
        valueAxisCrossing = BitFieldFactory.getInstance(1);
        crossesFarRight = BitFieldFactory.getInstance(2);
        reversed = BitFieldFactory.getInstance(4);
    }
}
