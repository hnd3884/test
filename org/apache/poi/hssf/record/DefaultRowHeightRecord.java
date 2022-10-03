package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class DefaultRowHeightRecord extends StandardRecord
{
    public static final short sid = 549;
    public static final short DEFAULT_ROW_HEIGHT = 255;
    private short field_1_option_flags;
    private short field_2_row_height;
    
    public DefaultRowHeightRecord() {
        this.field_1_option_flags = 0;
        this.field_2_row_height = 255;
    }
    
    public DefaultRowHeightRecord(final DefaultRowHeightRecord other) {
        super(other);
        this.field_1_option_flags = other.field_1_option_flags;
        this.field_2_row_height = other.field_2_row_height;
    }
    
    public DefaultRowHeightRecord(final RecordInputStream in) {
        this.field_1_option_flags = in.readShort();
        this.field_2_row_height = in.readShort();
    }
    
    public void setOptionFlags(final short flags) {
        this.field_1_option_flags = flags;
    }
    
    public void setRowHeight(final short height) {
        this.field_2_row_height = height;
    }
    
    public short getOptionFlags() {
        return this.field_1_option_flags;
    }
    
    public short getRowHeight() {
        return this.field_2_row_height;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DEFAULTROWHEIGHT]\n");
        buffer.append("    .optionflags    = ").append(Integer.toHexString(this.getOptionFlags())).append("\n");
        buffer.append("    .rowheight      = ").append(Integer.toHexString(this.getRowHeight())).append("\n");
        buffer.append("[/DEFAULTROWHEIGHT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getOptionFlags());
        out.writeShort(this.getRowHeight());
    }
    
    @Override
    protected int getDataSize() {
        return 4;
    }
    
    @Override
    public short getSid() {
        return 549;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DefaultRowHeightRecord clone() {
        return this.copy();
    }
    
    @Override
    public DefaultRowHeightRecord copy() {
        return new DefaultRowHeightRecord(this);
    }
}
