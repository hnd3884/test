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

public final class LegendRecord extends StandardRecord
{
    public static final short sid = 4117;
    private static final BitField autoPosition;
    private static final BitField autoSeries;
    private static final BitField autoXPositioning;
    private static final BitField autoYPositioning;
    private static final BitField vertical;
    private static final BitField dataTable;
    public static final byte TYPE_BOTTOM = 0;
    public static final byte TYPE_CORNER = 1;
    public static final byte TYPE_TOP = 2;
    public static final byte TYPE_RIGHT = 3;
    public static final byte TYPE_LEFT = 4;
    public static final byte TYPE_UNDOCKED = 7;
    public static final byte SPACING_CLOSE = 0;
    public static final byte SPACING_MEDIUM = 1;
    public static final byte SPACING_OPEN = 2;
    private int field_1_xAxisUpperLeft;
    private int field_2_yAxisUpperLeft;
    private int field_3_xSize;
    private int field_4_ySize;
    private byte field_5_type;
    private byte field_6_spacing;
    private short field_7_options;
    
    public LegendRecord() {
    }
    
    public LegendRecord(final LegendRecord other) {
        super(other);
        this.field_1_xAxisUpperLeft = other.field_1_xAxisUpperLeft;
        this.field_2_yAxisUpperLeft = other.field_2_yAxisUpperLeft;
        this.field_3_xSize = other.field_3_xSize;
        this.field_4_ySize = other.field_4_ySize;
        this.field_5_type = other.field_5_type;
        this.field_6_spacing = other.field_6_spacing;
        this.field_7_options = other.field_7_options;
    }
    
    public LegendRecord(final RecordInputStream in) {
        this.field_1_xAxisUpperLeft = in.readInt();
        this.field_2_yAxisUpperLeft = in.readInt();
        this.field_3_xSize = in.readInt();
        this.field_4_ySize = in.readInt();
        this.field_5_type = in.readByte();
        this.field_6_spacing = in.readByte();
        this.field_7_options = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[LEGEND]\n");
        buffer.append("    .xAxisUpperLeft       = ").append("0x").append(HexDump.toHex(this.getXAxisUpperLeft())).append(" (").append(this.getXAxisUpperLeft()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .yAxisUpperLeft       = ").append("0x").append(HexDump.toHex(this.getYAxisUpperLeft())).append(" (").append(this.getYAxisUpperLeft()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .xSize                = ").append("0x").append(HexDump.toHex(this.getXSize())).append(" (").append(this.getXSize()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .ySize                = ").append("0x").append(HexDump.toHex(this.getYSize())).append(" (").append(this.getYSize()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .type                 = ").append("0x").append(HexDump.toHex(this.getType())).append(" (").append(this.getType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .spacing              = ").append("0x").append(HexDump.toHex(this.getSpacing())).append(" (").append(this.getSpacing()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ").append("0x").append(HexDump.toHex(this.getOptions())).append(" (").append(this.getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoPosition             = ").append(this.isAutoPosition()).append('\n');
        buffer.append("         .autoSeries               = ").append(this.isAutoSeries()).append('\n');
        buffer.append("         .autoXPositioning         = ").append(this.isAutoXPositioning()).append('\n');
        buffer.append("         .autoYPositioning         = ").append(this.isAutoYPositioning()).append('\n');
        buffer.append("         .vertical                 = ").append(this.isVertical()).append('\n');
        buffer.append("         .dataTable                = ").append(this.isDataTable()).append('\n');
        buffer.append("[/LEGEND]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.field_1_xAxisUpperLeft);
        out.writeInt(this.field_2_yAxisUpperLeft);
        out.writeInt(this.field_3_xSize);
        out.writeInt(this.field_4_ySize);
        out.writeByte(this.field_5_type);
        out.writeByte(this.field_6_spacing);
        out.writeShort(this.field_7_options);
    }
    
    @Override
    protected int getDataSize() {
        return 20;
    }
    
    @Override
    public short getSid() {
        return 4117;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public LegendRecord clone() {
        return this.copy();
    }
    
    @Override
    public LegendRecord copy() {
        return new LegendRecord(this);
    }
    
    public int getXAxisUpperLeft() {
        return this.field_1_xAxisUpperLeft;
    }
    
    public void setXAxisUpperLeft(final int field_1_xAxisUpperLeft) {
        this.field_1_xAxisUpperLeft = field_1_xAxisUpperLeft;
    }
    
    public int getYAxisUpperLeft() {
        return this.field_2_yAxisUpperLeft;
    }
    
    public void setYAxisUpperLeft(final int field_2_yAxisUpperLeft) {
        this.field_2_yAxisUpperLeft = field_2_yAxisUpperLeft;
    }
    
    public int getXSize() {
        return this.field_3_xSize;
    }
    
    public void setXSize(final int field_3_xSize) {
        this.field_3_xSize = field_3_xSize;
    }
    
    public int getYSize() {
        return this.field_4_ySize;
    }
    
    public void setYSize(final int field_4_ySize) {
        this.field_4_ySize = field_4_ySize;
    }
    
    public byte getType() {
        return this.field_5_type;
    }
    
    public void setType(final byte field_5_type) {
        this.field_5_type = field_5_type;
    }
    
    public byte getSpacing() {
        return this.field_6_spacing;
    }
    
    public void setSpacing(final byte field_6_spacing) {
        this.field_6_spacing = field_6_spacing;
    }
    
    public short getOptions() {
        return this.field_7_options;
    }
    
    public void setOptions(final short field_7_options) {
        this.field_7_options = field_7_options;
    }
    
    public void setAutoPosition(final boolean value) {
        this.field_7_options = LegendRecord.autoPosition.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isAutoPosition() {
        return LegendRecord.autoPosition.isSet(this.field_7_options);
    }
    
    public void setAutoSeries(final boolean value) {
        this.field_7_options = LegendRecord.autoSeries.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isAutoSeries() {
        return LegendRecord.autoSeries.isSet(this.field_7_options);
    }
    
    public void setAutoXPositioning(final boolean value) {
        this.field_7_options = LegendRecord.autoXPositioning.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isAutoXPositioning() {
        return LegendRecord.autoXPositioning.isSet(this.field_7_options);
    }
    
    public void setAutoYPositioning(final boolean value) {
        this.field_7_options = LegendRecord.autoYPositioning.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isAutoYPositioning() {
        return LegendRecord.autoYPositioning.isSet(this.field_7_options);
    }
    
    public void setVertical(final boolean value) {
        this.field_7_options = LegendRecord.vertical.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isVertical() {
        return LegendRecord.vertical.isSet(this.field_7_options);
    }
    
    public void setDataTable(final boolean value) {
        this.field_7_options = LegendRecord.dataTable.setShortBoolean(this.field_7_options, value);
    }
    
    public boolean isDataTable() {
        return LegendRecord.dataTable.isSet(this.field_7_options);
    }
    
    static {
        autoPosition = BitFieldFactory.getInstance(1);
        autoSeries = BitFieldFactory.getInstance(2);
        autoXPositioning = BitFieldFactory.getInstance(4);
        autoYPositioning = BitFieldFactory.getInstance(8);
        vertical = BitFieldFactory.getInstance(16);
        dataTable = BitFieldFactory.getInstance(32);
    }
}
