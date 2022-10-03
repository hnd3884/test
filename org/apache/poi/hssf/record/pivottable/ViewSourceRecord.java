package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;

public final class ViewSourceRecord extends StandardRecord
{
    public static final short sid = 227;
    private int vs;
    
    public ViewSourceRecord(final ViewSourceRecord other) {
        super(other);
        this.vs = other.vs;
    }
    
    public ViewSourceRecord(final RecordInputStream in) {
        this.vs = in.readShort();
    }
    
    @Override
    protected void serialize(final LittleEndianOutput out) {
        out.writeShort(this.vs);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 227;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SXVS]\n");
        buffer.append("    .vs      =").append(HexDump.shortToHex(this.vs)).append('\n');
        buffer.append("[/SXVS]\n");
        return buffer.toString();
    }
    
    @Override
    public ViewSourceRecord copy() {
        return new ViewSourceRecord(this);
    }
}
