package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class WriteProtectRecord extends StandardRecord
{
    public static final short sid = 134;
    
    public WriteProtectRecord() {
    }
    
    public WriteProtectRecord(final RecordInputStream in) {
        if (in.remaining() == 2) {
            in.readShort();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[WRITEPROTECT]\n");
        buffer.append("[/WRITEPROTECT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
    }
    
    @Override
    protected int getDataSize() {
        return 0;
    }
    
    @Override
    public short getSid() {
        return 134;
    }
    
    @Override
    public WriteProtectRecord copy() {
        return new WriteProtectRecord();
    }
}
