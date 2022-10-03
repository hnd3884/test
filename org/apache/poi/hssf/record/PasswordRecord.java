package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public final class PasswordRecord extends StandardRecord
{
    public static final short sid = 19;
    private int field_1_password;
    
    public PasswordRecord(final int password) {
        this.field_1_password = password;
    }
    
    public PasswordRecord(final PasswordRecord other) {
        super(other);
        this.field_1_password = other.field_1_password;
    }
    
    public PasswordRecord(final RecordInputStream in) {
        this.field_1_password = in.readShort();
    }
    
    public void setPassword(final int password) {
        this.field_1_password = password;
    }
    
    public int getPassword() {
        return this.field_1_password;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PASSWORD]\n");
        buffer.append("    .password = ").append(HexDump.shortToHex(this.field_1_password)).append("\n");
        buffer.append("[/PASSWORD]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_password);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 19;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public PasswordRecord clone() {
        return this.copy();
    }
    
    @Override
    public PasswordRecord copy() {
        return new PasswordRecord(this);
    }
}
