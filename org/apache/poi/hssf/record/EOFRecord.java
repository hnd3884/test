package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class EOFRecord extends StandardRecord
{
    public static final short sid = 10;
    public static final int ENCODED_SIZE = 4;
    public static final EOFRecord instance;
    
    private EOFRecord() {
    }
    
    public EOFRecord(final RecordInputStream in) {
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[EOF]\n");
        buffer.append("[/EOF]\n");
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
        return 10;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public EOFRecord clone() {
        return this.copy();
    }
    
    @Override
    public EOFRecord copy() {
        return EOFRecord.instance;
    }
    
    static {
        instance = new EOFRecord();
    }
}
