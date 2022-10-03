package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class DataLabelExtensionRecord extends StandardRecord
{
    public static final short sid = 2154;
    private int rt;
    private int grbitFrt;
    private final byte[] unused;
    
    public DataLabelExtensionRecord(final DataLabelExtensionRecord other) {
        super(other);
        this.unused = new byte[8];
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        System.arraycopy(other.unused, 0, this.unused, 0, this.unused.length);
    }
    
    public DataLabelExtensionRecord(final RecordInputStream in) {
        this.unused = new byte[8];
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        in.readFully(this.unused);
    }
    
    @Override
    protected int getDataSize() {
        return 12;
    }
    
    @Override
    public short getSid() {
        return 2154;
    }
    
    @Override
    protected void serialize(final LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.write(this.unused);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DATALABEXT]\n");
        buffer.append("    .rt      =").append(HexDump.shortToHex(this.rt)).append('\n');
        buffer.append("    .grbitFrt=").append(HexDump.shortToHex(this.grbitFrt)).append('\n');
        buffer.append("    .unused  =").append(HexDump.toHex(this.unused)).append('\n');
        buffer.append("[/DATALABEXT]\n");
        return buffer.toString();
    }
    
    @Override
    public DataLabelExtensionRecord copy() {
        return new DataLabelExtensionRecord(this);
    }
}
