package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class ChartRecord extends StandardRecord
{
    public static final short sid = 4098;
    private int field_1_x;
    private int field_2_y;
    private int field_3_width;
    private int field_4_height;
    
    public ChartRecord() {
    }
    
    public ChartRecord(final ChartRecord other) {
        super(other);
        this.field_1_x = other.field_1_x;
        this.field_2_y = other.field_2_y;
        this.field_3_width = other.field_3_width;
        this.field_4_height = other.field_4_height;
    }
    
    public ChartRecord(final RecordInputStream in) {
        this.field_1_x = in.readInt();
        this.field_2_y = in.readInt();
        this.field_3_width = in.readInt();
        this.field_4_height = in.readInt();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[CHART]\n");
        sb.append("    .x     = ").append(this.getX()).append('\n');
        sb.append("    .y     = ").append(this.getY()).append('\n');
        sb.append("    .width = ").append(this.getWidth()).append('\n');
        sb.append("    .height= ").append(this.getHeight()).append('\n');
        sb.append("[/CHART]\n");
        return sb.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.field_1_x);
        out.writeInt(this.field_2_y);
        out.writeInt(this.field_3_width);
        out.writeInt(this.field_4_height);
    }
    
    @Override
    protected int getDataSize() {
        return 16;
    }
    
    @Override
    public short getSid() {
        return 4098;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ChartRecord clone() {
        return this.copy();
    }
    
    @Override
    public ChartRecord copy() {
        return new ChartRecord(this);
    }
    
    public int getX() {
        return this.field_1_x;
    }
    
    public void setX(final int x) {
        this.field_1_x = x;
    }
    
    public int getY() {
        return this.field_2_y;
    }
    
    public void setY(final int y) {
        this.field_2_y = y;
    }
    
    public int getWidth() {
        return this.field_3_width;
    }
    
    public void setWidth(final int width) {
        this.field_3_width = width;
    }
    
    public int getHeight() {
        return this.field_4_height;
    }
    
    public void setHeight(final int height) {
        this.field_4_height = height;
    }
}
