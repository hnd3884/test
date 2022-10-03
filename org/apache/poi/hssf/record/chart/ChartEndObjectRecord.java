package org.apache.poi.hssf.record.chart;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class ChartEndObjectRecord extends StandardRecord
{
    public static final short sid = 2133;
    private short rt;
    private short grbitFrt;
    private short iObjectKind;
    private byte[] reserved;
    
    public ChartEndObjectRecord(final ChartEndObjectRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.iObjectKind = other.iObjectKind;
        this.reserved = (byte[])((other.reserved == null) ? null : ((byte[])other.reserved.clone()));
    }
    
    public ChartEndObjectRecord(final RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.iObjectKind = in.readShort();
        this.reserved = new byte[6];
        if (in.available() != 0) {
            in.readFully(this.reserved);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 12;
    }
    
    @Override
    public short getSid() {
        return 2133;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.iObjectKind);
        out.write(this.reserved);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[ENDOBJECT]\n");
        buffer.append("    .rt         =").append(HexDump.shortToHex(this.rt)).append('\n');
        buffer.append("    .grbitFrt   =").append(HexDump.shortToHex(this.grbitFrt)).append('\n');
        buffer.append("    .iObjectKind=").append(HexDump.shortToHex(this.iObjectKind)).append('\n');
        buffer.append("    .reserved   =").append(HexDump.toHex(this.reserved)).append('\n');
        buffer.append("[/ENDOBJECT]\n");
        return buffer.toString();
    }
    
    @Override
    public ChartEndObjectRecord copy() {
        return new ChartEndObjectRecord(this);
    }
}
