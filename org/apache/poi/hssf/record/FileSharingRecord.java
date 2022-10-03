package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class FileSharingRecord extends StandardRecord
{
    public static final short sid = 91;
    private short field_1_readonly;
    private short field_2_password;
    private byte field_3_username_unicode_options;
    private String field_3_username_value;
    
    public FileSharingRecord() {
    }
    
    public FileSharingRecord(final FileSharingRecord other) {
        super(other);
        this.field_1_readonly = other.field_1_readonly;
        this.field_2_password = other.field_2_password;
        this.field_3_username_unicode_options = other.field_3_username_unicode_options;
        this.field_3_username_value = other.field_3_username_value;
    }
    
    public FileSharingRecord(final RecordInputStream in) {
        this.field_1_readonly = in.readShort();
        this.field_2_password = in.readShort();
        final int nameLen = in.readShort();
        if (nameLen > 0) {
            this.field_3_username_unicode_options = in.readByte();
            this.field_3_username_value = in.readCompressedUnicode(nameLen);
        }
        else {
            this.field_3_username_value = "";
        }
    }
    
    public void setReadOnly(final short readonly) {
        this.field_1_readonly = readonly;
    }
    
    public short getReadOnly() {
        return this.field_1_readonly;
    }
    
    public void setPassword(final short password) {
        this.field_2_password = password;
    }
    
    public short getPassword() {
        return this.field_2_password;
    }
    
    public String getUsername() {
        return this.field_3_username_value;
    }
    
    public void setUsername(final String username) {
        this.field_3_username_value = username;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FILESHARING]\n");
        buffer.append("    .readonly       = ").append((this.getReadOnly() == 1) ? "true" : "false").append("\n");
        buffer.append("    .password       = ").append(Integer.toHexString(this.getPassword())).append("\n");
        buffer.append("    .username       = ").append(this.getUsername()).append("\n");
        buffer.append("[/FILESHARING]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getReadOnly());
        out.writeShort(this.getPassword());
        out.writeShort(this.field_3_username_value.length());
        if (this.field_3_username_value.length() > 0) {
            out.writeByte(this.field_3_username_unicode_options);
            StringUtil.putCompressedUnicode(this.getUsername(), out);
        }
    }
    
    @Override
    protected int getDataSize() {
        final int nameLen = this.field_3_username_value.length();
        if (nameLen < 1) {
            return 6;
        }
        return 7 + nameLen;
    }
    
    @Override
    public short getSid() {
        return 91;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FileSharingRecord clone() {
        return this.copy();
    }
    
    @Override
    public FileSharingRecord copy() {
        return new FileSharingRecord(this);
    }
}
