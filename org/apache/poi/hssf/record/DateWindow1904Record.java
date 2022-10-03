package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class DateWindow1904Record extends StandardRecord
{
    public static final short sid = 34;
    private short field_1_window;
    
    public DateWindow1904Record() {
    }
    
    public DateWindow1904Record(final DateWindow1904Record other) {
        super(other);
        this.field_1_window = other.field_1_window;
    }
    
    public DateWindow1904Record(final RecordInputStream in) {
        this.field_1_window = in.readShort();
    }
    
    public void setWindowing(final short window) {
        this.field_1_window = window;
    }
    
    public short getWindowing() {
        return this.field_1_window;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[1904]\n");
        buffer.append("    .is1904          = ").append(Integer.toHexString(this.getWindowing())).append("\n");
        buffer.append("[/1904]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getWindowing());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 34;
    }
    
    @Override
    public DateWindow1904Record copy() {
        return new DateWindow1904Record(this);
    }
}
