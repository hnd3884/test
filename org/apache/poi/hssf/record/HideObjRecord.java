package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class HideObjRecord extends StandardRecord
{
    public static final short sid = 141;
    public static final short HIDE_ALL = 2;
    public static final short SHOW_PLACEHOLDERS = 1;
    public static final short SHOW_ALL = 0;
    private short field_1_hide_obj;
    
    public HideObjRecord() {
    }
    
    public HideObjRecord(final HideObjRecord other) {
        super(other);
        this.field_1_hide_obj = other.field_1_hide_obj;
    }
    
    public HideObjRecord(final RecordInputStream in) {
        this.field_1_hide_obj = in.readShort();
    }
    
    public void setHideObj(final short hide) {
        this.field_1_hide_obj = hide;
    }
    
    public short getHideObj() {
        return this.field_1_hide_obj;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[HIDEOBJ]\n");
        buffer.append("    .hideobj         = ").append(Integer.toHexString(this.getHideObj())).append("\n");
        buffer.append("[/HIDEOBJ]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getHideObj());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 141;
    }
    
    @Override
    public HideObjRecord copy() {
        return new HideObjRecord(this);
    }
}
