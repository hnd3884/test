package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class MMSRecord extends StandardRecord
{
    public static final short sid = 193;
    private byte field_1_addMenuCount;
    private byte field_2_delMenuCount;
    
    public MMSRecord() {
    }
    
    public MMSRecord(final MMSRecord other) {
        this.field_1_addMenuCount = other.field_1_addMenuCount;
        this.field_2_delMenuCount = other.field_2_delMenuCount;
    }
    
    public MMSRecord(final RecordInputStream in) {
        if (in.remaining() == 0) {
            return;
        }
        this.field_1_addMenuCount = in.readByte();
        this.field_2_delMenuCount = in.readByte();
    }
    
    public void setAddMenuCount(final byte am) {
        this.field_1_addMenuCount = am;
    }
    
    public void setDelMenuCount(final byte dm) {
        this.field_2_delMenuCount = dm;
    }
    
    public byte getAddMenuCount() {
        return this.field_1_addMenuCount;
    }
    
    public byte getDelMenuCount() {
        return this.field_2_delMenuCount;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[MMS]\n");
        buffer.append("    .addMenu        = ").append(Integer.toHexString(this.getAddMenuCount())).append("\n");
        buffer.append("    .delMenu        = ").append(Integer.toHexString(this.getDelMenuCount())).append("\n");
        buffer.append("[/MMS]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeByte(this.getAddMenuCount());
        out.writeByte(this.getDelMenuCount());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 193;
    }
    
    @Override
    public MMSRecord copy() {
        return new MMSRecord(this);
    }
}
