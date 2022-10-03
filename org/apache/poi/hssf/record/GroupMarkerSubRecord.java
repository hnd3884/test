package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInput;

public final class GroupMarkerSubRecord extends SubRecord
{
    public static final short sid = 6;
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private byte[] reserved;
    
    public GroupMarkerSubRecord() {
        this.reserved = GroupMarkerSubRecord.EMPTY_BYTE_ARRAY;
    }
    
    public GroupMarkerSubRecord(final GroupMarkerSubRecord other) {
        super(other);
        this.reserved = other.reserved.clone();
    }
    
    public GroupMarkerSubRecord(final LittleEndianInput in, final int size) {
        final byte[] buf = IOUtils.safelyAllocate(size, 100000);
        in.readFully(buf);
        this.reserved = buf;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final String nl = System.getProperty("line.separator");
        buffer.append("[ftGmo]" + nl);
        buffer.append("  reserved = ").append(HexDump.toHex(this.reserved)).append(nl);
        buffer.append("[/ftGmo]" + nl);
        return buffer.toString();
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(6);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }
    
    @Override
    protected int getDataSize() {
        return this.reserved.length;
    }
    
    public short getSid() {
        return 6;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public GroupMarkerSubRecord clone() {
        return this.copy();
    }
    
    @Override
    public GroupMarkerSubRecord copy() {
        return new GroupMarkerSubRecord(this);
    }
    
    static {
        EMPTY_BYTE_ARRAY = new byte[0];
    }
}
