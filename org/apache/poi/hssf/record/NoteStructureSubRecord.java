package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndianInput;

public final class NoteStructureSubRecord extends SubRecord
{
    public static final short sid = 13;
    private static final int ENCODED_SIZE = 22;
    private final byte[] reserved;
    
    public NoteStructureSubRecord() {
        this.reserved = new byte[22];
    }
    
    public NoteStructureSubRecord(final NoteStructureSubRecord other) {
        super(other);
        this.reserved = other.reserved.clone();
    }
    
    public NoteStructureSubRecord(final LittleEndianInput in, final int size) {
        if (size != 22) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
        final byte[] buf = IOUtils.safelyAllocate(size, 22);
        in.readFully(buf);
        this.reserved = buf;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[ftNts ]").append("\n");
        buffer.append("  size     = ").append(this.getDataSize()).append("\n");
        buffer.append("  reserved = ").append(HexDump.toHex(this.reserved)).append("\n");
        buffer.append("[/ftNts ]").append("\n");
        return buffer.toString();
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(13);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }
    
    @Override
    protected int getDataSize() {
        return this.reserved.length;
    }
    
    public short getSid() {
        return 13;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public NoteStructureSubRecord clone() {
        return this.copy();
    }
    
    @Override
    public NoteStructureSubRecord copy() {
        return new NoteStructureSubRecord(this);
    }
}
