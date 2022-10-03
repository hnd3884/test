package org.apache.poi.hssf.record.chart;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.hssf.record.StandardRecord;

public final class ChartFormatRecord extends StandardRecord
{
    public static final short sid = 4116;
    private static final BitField varyDisplayPattern;
    private int field1_x_position;
    private int field2_y_position;
    private int field3_width;
    private int field4_height;
    private int field5_grbit;
    private int field6_unknown;
    
    public ChartFormatRecord() {
    }
    
    public ChartFormatRecord(final ChartFormatRecord other) {
        super(other);
        this.field1_x_position = other.field1_x_position;
        this.field2_y_position = other.field2_y_position;
        this.field3_width = other.field3_width;
        this.field4_height = other.field4_height;
        this.field5_grbit = other.field5_grbit;
        this.field6_unknown = other.field6_unknown;
    }
    
    public ChartFormatRecord(final RecordInputStream in) {
        this.field1_x_position = in.readInt();
        this.field2_y_position = in.readInt();
        this.field3_width = in.readInt();
        this.field4_height = in.readInt();
        this.field5_grbit = in.readUShort();
        this.field6_unknown = in.readUShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CHARTFORMAT]\n");
        buffer.append("    .xPosition       = ").append(this.getXPosition()).append("\n");
        buffer.append("    .yPosition       = ").append(this.getYPosition()).append("\n");
        buffer.append("    .width           = ").append(this.getWidth()).append("\n");
        buffer.append("    .height          = ").append(this.getHeight()).append("\n");
        buffer.append("    .grBit           = ").append(HexDump.intToHex(this.field5_grbit)).append("\n");
        buffer.append("[/CHARTFORMAT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.getXPosition());
        out.writeInt(this.getYPosition());
        out.writeInt(this.getWidth());
        out.writeInt(this.getHeight());
        out.writeShort(this.field5_grbit);
        out.writeShort(this.field6_unknown);
    }
    
    @Override
    protected int getDataSize() {
        return 20;
    }
    
    @Override
    public short getSid() {
        return 4116;
    }
    
    public int getXPosition() {
        return this.field1_x_position;
    }
    
    public void setXPosition(final int xPosition) {
        this.field1_x_position = xPosition;
    }
    
    public int getYPosition() {
        return this.field2_y_position;
    }
    
    public void setYPosition(final int yPosition) {
        this.field2_y_position = yPosition;
    }
    
    public int getWidth() {
        return this.field3_width;
    }
    
    public void setWidth(final int width) {
        this.field3_width = width;
    }
    
    public int getHeight() {
        return this.field4_height;
    }
    
    public void setHeight(final int height) {
        this.field4_height = height;
    }
    
    public boolean getVaryDisplayPattern() {
        return ChartFormatRecord.varyDisplayPattern.isSet(this.field5_grbit);
    }
    
    public void setVaryDisplayPattern(final boolean value) {
        this.field5_grbit = ChartFormatRecord.varyDisplayPattern.setBoolean(this.field5_grbit, value);
    }
    
    @Override
    public ChartFormatRecord copy() {
        return new ChartFormatRecord(this);
    }
    
    static {
        varyDisplayPattern = BitFieldFactory.getInstance(1);
    }
}
