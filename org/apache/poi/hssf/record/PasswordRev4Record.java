package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public final class PasswordRev4Record extends StandardRecord
{
    public static final short sid = 444;
    private int field_1_password;
    
    public PasswordRev4Record(final int pw) {
        this.field_1_password = pw;
    }
    
    public PasswordRev4Record(final PasswordRev4Record other) {
        super(other);
        this.field_1_password = other.field_1_password;
    }
    
    public PasswordRev4Record(final RecordInputStream in) {
        this.field_1_password = in.readShort();
    }
    
    public void setPassword(final short pw) {
        this.field_1_password = pw;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PROT4REVPASSWORD]\n");
        buffer.append("    .password = ").append(HexDump.shortToHex(this.field_1_password)).append("\n");
        buffer.append("[/PROT4REVPASSWORD]\n");
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
        return 444;
    }
    
    @Override
    public PasswordRev4Record copy() {
        return new PasswordRev4Record(this);
    }
}
