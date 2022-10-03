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

public final class LineFormatRecord extends StandardRecord
{
    public static final short sid = 4103;
    private static final BitField auto;
    private static final BitField drawTicks;
    private static final BitField unknown;
    public static final short LINE_PATTERN_SOLID = 0;
    public static final short LINE_PATTERN_DASH = 1;
    public static final short LINE_PATTERN_DOT = 2;
    public static final short LINE_PATTERN_DASH_DOT = 3;
    public static final short LINE_PATTERN_DASH_DOT_DOT = 4;
    public static final short LINE_PATTERN_NONE = 5;
    public static final short LINE_PATTERN_DARK_GRAY_PATTERN = 6;
    public static final short LINE_PATTERN_MEDIUM_GRAY_PATTERN = 7;
    public static final short LINE_PATTERN_LIGHT_GRAY_PATTERN = 8;
    public static final short WEIGHT_HAIRLINE = -1;
    public static final short WEIGHT_NARROW = 0;
    public static final short WEIGHT_MEDIUM = 1;
    public static final short WEIGHT_WIDE = 2;
    private int field_1_lineColor;
    private short field_2_linePattern;
    private short field_3_weight;
    private short field_4_format;
    private short field_5_colourPaletteIndex;
    
    public LineFormatRecord() {
    }
    
    public LineFormatRecord(final LineFormatRecord other) {
        super(other);
        this.field_1_lineColor = other.field_1_lineColor;
        this.field_2_linePattern = other.field_2_linePattern;
        this.field_3_weight = other.field_3_weight;
        this.field_4_format = other.field_4_format;
        this.field_5_colourPaletteIndex = other.field_5_colourPaletteIndex;
    }
    
    public LineFormatRecord(final RecordInputStream in) {
        this.field_1_lineColor = in.readInt();
        this.field_2_linePattern = in.readShort();
        this.field_3_weight = in.readShort();
        this.field_4_format = in.readShort();
        this.field_5_colourPaletteIndex = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[LINEFORMAT]\n");
        buffer.append("    .lineColor            = ").append("0x").append(HexDump.toHex(this.getLineColor())).append(" (").append(this.getLineColor()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .linePattern          = ").append("0x").append(HexDump.toHex(this.getLinePattern())).append(" (").append(this.getLinePattern()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .weight               = ").append("0x").append(HexDump.toHex(this.getWeight())).append(" (").append(this.getWeight()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .format               = ").append("0x").append(HexDump.toHex(this.getFormat())).append(" (").append(this.getFormat()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .auto                     = ").append(this.isAuto()).append('\n');
        buffer.append("         .drawTicks                = ").append(this.isDrawTicks()).append('\n');
        buffer.append("         .unknown                  = ").append(this.isUnknown()).append('\n');
        buffer.append("    .colourPaletteIndex   = ").append("0x").append(HexDump.toHex(this.getColourPaletteIndex())).append(" (").append(this.getColourPaletteIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/LINEFORMAT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.field_1_lineColor);
        out.writeShort(this.field_2_linePattern);
        out.writeShort(this.field_3_weight);
        out.writeShort(this.field_4_format);
        out.writeShort(this.field_5_colourPaletteIndex);
    }
    
    @Override
    protected int getDataSize() {
        return 12;
    }
    
    @Override
    public short getSid() {
        return 4103;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public LineFormatRecord clone() {
        return this.copy();
    }
    
    @Override
    public LineFormatRecord copy() {
        return new LineFormatRecord(this);
    }
    
    public int getLineColor() {
        return this.field_1_lineColor;
    }
    
    public void setLineColor(final int field_1_lineColor) {
        this.field_1_lineColor = field_1_lineColor;
    }
    
    public short getLinePattern() {
        return this.field_2_linePattern;
    }
    
    public void setLinePattern(final short field_2_linePattern) {
        this.field_2_linePattern = field_2_linePattern;
    }
    
    public short getWeight() {
        return this.field_3_weight;
    }
    
    public void setWeight(final short field_3_weight) {
        this.field_3_weight = field_3_weight;
    }
    
    public short getFormat() {
        return this.field_4_format;
    }
    
    public void setFormat(final short field_4_format) {
        this.field_4_format = field_4_format;
    }
    
    public short getColourPaletteIndex() {
        return this.field_5_colourPaletteIndex;
    }
    
    public void setColourPaletteIndex(final short field_5_colourPaletteIndex) {
        this.field_5_colourPaletteIndex = field_5_colourPaletteIndex;
    }
    
    public void setAuto(final boolean value) {
        this.field_4_format = LineFormatRecord.auto.setShortBoolean(this.field_4_format, value);
    }
    
    public boolean isAuto() {
        return LineFormatRecord.auto.isSet(this.field_4_format);
    }
    
    public void setDrawTicks(final boolean value) {
        this.field_4_format = LineFormatRecord.drawTicks.setShortBoolean(this.field_4_format, value);
    }
    
    public boolean isDrawTicks() {
        return LineFormatRecord.drawTicks.isSet(this.field_4_format);
    }
    
    public void setUnknown(final boolean value) {
        this.field_4_format = LineFormatRecord.unknown.setShortBoolean(this.field_4_format, value);
    }
    
    public boolean isUnknown() {
        return LineFormatRecord.unknown.isSet(this.field_4_format);
    }
    
    static {
        auto = BitFieldFactory.getInstance(1);
        drawTicks = BitFieldFactory.getInstance(4);
        unknown = BitFieldFactory.getInstance(4);
    }
}
