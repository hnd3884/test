package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndianInput;

public final class FtCfSubRecord extends SubRecord
{
    public static final short sid = 7;
    public static final short length = 2;
    public static final short METAFILE_BIT = 2;
    public static final short BITMAP_BIT = 9;
    public static final short UNSPECIFIED_BIT = -1;
    private short flags;
    
    public FtCfSubRecord() {
    }
    
    public FtCfSubRecord(final FtCfSubRecord other) {
        super(other);
        this.flags = other.flags;
    }
    
    public FtCfSubRecord(final LittleEndianInput in, final int size) {
        if (size != 2) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
        this.flags = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FtCf ]\n");
        buffer.append("  size     = ").append(2).append("\n");
        buffer.append("  flags    = ").append(HexDump.toHex(this.flags)).append("\n");
        buffer.append("[/FtCf ]\n");
        return buffer.toString();
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(7);
        out.writeShort(2);
        out.writeShort(this.flags);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    public short getSid() {
        return 7;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public FtCfSubRecord clone() {
        return this.copy();
    }
    
    @Override
    public FtCfSubRecord copy() {
        return new FtCfSubRecord(this);
    }
    
    public short getFlags() {
        return this.flags;
    }
    
    public void setFlags(final short flags) {
        this.flags = flags;
    }
}
