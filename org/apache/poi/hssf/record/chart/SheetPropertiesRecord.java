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

public final class SheetPropertiesRecord extends StandardRecord
{
    public static final short sid = 4164;
    public static final byte EMPTY_NOT_PLOTTED = 0;
    public static final byte EMPTY_ZERO = 1;
    public static final byte EMPTY_INTERPOLATED = 2;
    private static final BitField chartTypeManuallyFormatted;
    private static final BitField plotVisibleOnly;
    private static final BitField doNotSizeWithWindow;
    private static final BitField defaultPlotDimensions;
    private static final BitField autoPlotArea;
    private int field_1_flags;
    private int field_2_empty;
    
    public SheetPropertiesRecord() {
    }
    
    public SheetPropertiesRecord(final SheetPropertiesRecord other) {
        super(other);
        this.field_1_flags = other.field_1_flags;
        this.field_2_empty = other.field_2_empty;
    }
    
    public SheetPropertiesRecord(final RecordInputStream in) {
        this.field_1_flags = in.readUShort();
        this.field_2_empty = in.readUShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SHTPROPS]\n");
        buffer.append("    .flags                = ").append(HexDump.shortToHex(this.field_1_flags)).append('\n');
        buffer.append("         .chartTypeManuallyFormatted= ").append(this.isChartTypeManuallyFormatted()).append('\n');
        buffer.append("         .plotVisibleOnly           = ").append(this.isPlotVisibleOnly()).append('\n');
        buffer.append("         .doNotSizeWithWindow       = ").append(this.isDoNotSizeWithWindow()).append('\n');
        buffer.append("         .defaultPlotDimensions     = ").append(this.isDefaultPlotDimensions()).append('\n');
        buffer.append("         .autoPlotArea              = ").append(this.isAutoPlotArea()).append('\n');
        buffer.append("    .empty                = ").append(HexDump.shortToHex(this.field_2_empty)).append('\n');
        buffer.append("[/SHTPROPS]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_flags);
        out.writeShort(this.field_2_empty);
    }
    
    @Override
    protected int getDataSize() {
        return 4;
    }
    
    @Override
    public short getSid() {
        return 4164;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SheetPropertiesRecord clone() {
        return this.copy();
    }
    
    @Override
    public SheetPropertiesRecord copy() {
        return new SheetPropertiesRecord(this);
    }
    
    public int getFlags() {
        return this.field_1_flags;
    }
    
    public int getEmpty() {
        return this.field_2_empty;
    }
    
    public void setEmpty(final byte empty) {
        this.field_2_empty = empty;
    }
    
    public void setChartTypeManuallyFormatted(final boolean value) {
        this.field_1_flags = SheetPropertiesRecord.chartTypeManuallyFormatted.setBoolean(this.field_1_flags, value);
    }
    
    public boolean isChartTypeManuallyFormatted() {
        return SheetPropertiesRecord.chartTypeManuallyFormatted.isSet(this.field_1_flags);
    }
    
    public void setPlotVisibleOnly(final boolean value) {
        this.field_1_flags = SheetPropertiesRecord.plotVisibleOnly.setBoolean(this.field_1_flags, value);
    }
    
    public boolean isPlotVisibleOnly() {
        return SheetPropertiesRecord.plotVisibleOnly.isSet(this.field_1_flags);
    }
    
    public void setDoNotSizeWithWindow(final boolean value) {
        this.field_1_flags = SheetPropertiesRecord.doNotSizeWithWindow.setBoolean(this.field_1_flags, value);
    }
    
    public boolean isDoNotSizeWithWindow() {
        return SheetPropertiesRecord.doNotSizeWithWindow.isSet(this.field_1_flags);
    }
    
    public void setDefaultPlotDimensions(final boolean value) {
        this.field_1_flags = SheetPropertiesRecord.defaultPlotDimensions.setBoolean(this.field_1_flags, value);
    }
    
    public boolean isDefaultPlotDimensions() {
        return SheetPropertiesRecord.defaultPlotDimensions.isSet(this.field_1_flags);
    }
    
    public void setAutoPlotArea(final boolean value) {
        this.field_1_flags = SheetPropertiesRecord.autoPlotArea.setBoolean(this.field_1_flags, value);
    }
    
    public boolean isAutoPlotArea() {
        return SheetPropertiesRecord.autoPlotArea.isSet(this.field_1_flags);
    }
    
    static {
        chartTypeManuallyFormatted = BitFieldFactory.getInstance(1);
        plotVisibleOnly = BitFieldFactory.getInstance(2);
        doNotSizeWithWindow = BitFieldFactory.getInstance(4);
        defaultPlotDimensions = BitFieldFactory.getInstance(8);
        autoPlotArea = BitFieldFactory.getInstance(16);
    }
}
