package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class ScenarioProtectRecord extends StandardRecord
{
    public static final short sid = 221;
    private short field_1_protect;
    
    public ScenarioProtectRecord() {
    }
    
    public ScenarioProtectRecord(final ScenarioProtectRecord other) {
        super(other);
        this.field_1_protect = other.field_1_protect;
    }
    
    public ScenarioProtectRecord(final RecordInputStream in) {
        this.field_1_protect = in.readShort();
    }
    
    public void setProtect(final boolean protect) {
        if (protect) {
            this.field_1_protect = 1;
        }
        else {
            this.field_1_protect = 0;
        }
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
        return 221;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ScenarioProtectRecord clone() {
        return this.copy();
    }
    
    @Override
    public ScenarioProtectRecord copy() {
        return new ScenarioProtectRecord(this);
    }
}
