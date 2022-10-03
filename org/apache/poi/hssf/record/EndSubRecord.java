package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndianInput;

public final class EndSubRecord extends SubRecord
{
    public static final short sid = 0;
    private static final int ENCODED_SIZE = 0;
    
    public EndSubRecord() {
    }
    
    public EndSubRecord(final LittleEndianInput in, final int size) {
        if ((size & 0xFF) != 0x0) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
    }
    
    @Override
    public boolean isTerminating() {
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[ftEnd]\n");
        buffer.append("[/ftEnd]\n");
        return buffer.toString();
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(0);
        out.writeShort(0);
    }
    
    @Override
    protected int getDataSize() {
        return 0;
    }
    
    public short getSid() {
        return 0;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public EndSubRecord clone() {
        return this.copy();
    }
    
    @Override
    public EndSubRecord copy() {
        return new EndSubRecord();
    }
}
