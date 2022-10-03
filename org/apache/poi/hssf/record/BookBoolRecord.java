package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class BookBoolRecord extends StandardRecord
{
    public static final short sid = 218;
    private short field_1_save_link_values;
    
    public BookBoolRecord() {
    }
    
    public BookBoolRecord(final BookBoolRecord other) {
        super(other);
        this.field_1_save_link_values = other.field_1_save_link_values;
    }
    
    public BookBoolRecord(final RecordInputStream in) {
        this.field_1_save_link_values = in.readShort();
    }
    
    public void setSaveLinkValues(final short flag) {
        this.field_1_save_link_values = flag;
    }
    
    public short getSaveLinkValues() {
        return this.field_1_save_link_values;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[BOOKBOOL]\n");
        buffer.append("    .savelinkvalues  = ").append(Integer.toHexString(this.getSaveLinkValues())).append("\n");
        buffer.append("[/BOOKBOOL]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_save_link_values);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 218;
    }
    
    @Override
    public BookBoolRecord copy() {
        return new BookBoolRecord(this);
    }
}
