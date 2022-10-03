package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class BackupRecord extends StandardRecord
{
    public static final short sid = 64;
    private short field_1_backup;
    
    public BackupRecord() {
    }
    
    public BackupRecord(final BackupRecord other) {
        super(other);
        this.field_1_backup = other.field_1_backup;
    }
    
    public BackupRecord(final RecordInputStream in) {
        this.field_1_backup = in.readShort();
    }
    
    public void setBackup(final short backup) {
        this.field_1_backup = backup;
    }
    
    public short getBackup() {
        return this.field_1_backup;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[BACKUP]\n");
        buffer.append("    .backup          = ").append(Integer.toHexString(this.getBackup())).append("\n");
        buffer.append("[/BACKUP]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getBackup());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 64;
    }
    
    @Override
    public BackupRecord copy() {
        return new BackupRecord(this);
    }
}
