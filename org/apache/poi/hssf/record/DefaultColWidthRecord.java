package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class DefaultColWidthRecord extends StandardRecord
{
    public static final short sid = 85;
    public static final int DEFAULT_COLUMN_WIDTH = 8;
    private int field_1_col_width;
    
    public DefaultColWidthRecord() {
        this.field_1_col_width = 8;
    }
    
    public DefaultColWidthRecord(final DefaultColWidthRecord other) {
        super(other);
        this.field_1_col_width = other.field_1_col_width;
    }
    
    public DefaultColWidthRecord(final RecordInputStream in) {
        this.field_1_col_width = in.readUShort();
    }
    
    public void setColWidth(final int width) {
        this.field_1_col_width = width;
    }
    
    public int getColWidth() {
        return this.field_1_col_width;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DEFAULTCOLWIDTH]\n");
        buffer.append("    .colwidth      = ").append(Integer.toHexString(this.getColWidth())).append("\n");
        buffer.append("[/DEFAULTCOLWIDTH]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getColWidth());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 85;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DefaultColWidthRecord clone() {
        return this.copy();
    }
    
    @Override
    public DefaultColWidthRecord copy() {
        return new DefaultColWidthRecord(this);
    }
}
