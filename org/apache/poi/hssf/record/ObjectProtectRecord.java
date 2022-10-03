package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class ObjectProtectRecord extends StandardRecord
{
    public static final short sid = 99;
    private short field_1_protect;
    
    public ObjectProtectRecord() {
    }
    
    public ObjectProtectRecord(final ObjectProtectRecord other) {
        super(other);
        this.field_1_protect = other.field_1_protect;
    }
    
    public ObjectProtectRecord(final RecordInputStream in) {
        this.field_1_protect = in.readShort();
    }
    
    public void setProtect(final boolean protect) {
        this.field_1_protect = (short)(protect ? 1 : 0);
    }
    
    public boolean getProtect() {
        return this.field_1_protect == 1;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SCENARIOPROTECT]\n");
        buffer.append("    .protect         = ").append(this.getProtect()).append("\n");
        buffer.append("[/SCENARIOPROTECT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_protect);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 99;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ObjectProtectRecord clone() {
        return this.copy();
    }
    
    @Override
    public ObjectProtectRecord copy() {
        return new ObjectProtectRecord(this);
    }
}
