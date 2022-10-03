package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class FnGroupCountRecord extends StandardRecord
{
    public static final short sid = 156;
    public static final short COUNT = 14;
    private short field_1_count;
    
    public FnGroupCountRecord() {
    }
    
    public FnGroupCountRecord(final FnGroupCountRecord other) {
        super(other);
        this.field_1_count = other.field_1_count;
    }
    
    public FnGroupCountRecord(final RecordInputStream in) {
        this.field_1_count = in.readShort();
    }
    
    public void setCount(final short count) {
        this.field_1_count = count;
    }
    
    public short getCount() {
        return this.field_1_count;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FNGROUPCOUNT]\n");
        buffer.append("    .count            = ").append(this.getCount()).append("\n");
        buffer.append("[/FNGROUPCOUNT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getCount());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 156;
    }
    
    @Override
    public FnGroupCountRecord copy() {
        return new FnGroupCountRecord(this);
    }
}
