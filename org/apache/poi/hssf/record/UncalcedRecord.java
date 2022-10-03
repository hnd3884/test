package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;

public final class UncalcedRecord extends StandardRecord
{
    public static final short sid = 94;
    private short _reserved;
    
    public UncalcedRecord() {
        this._reserved = 0;
    }
    
    public UncalcedRecord(final UncalcedRecord other) {
        super(other);
        this._reserved = other._reserved;
    }
    
    @Override
    public short getSid() {
        return 94;
    }
    
    public UncalcedRecord(final RecordInputStream in) {
        this._reserved = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[UNCALCED]\n");
        buffer.append("    _reserved: ").append(this._reserved).append('\n');
        buffer.append("[/UNCALCED]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._reserved);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    public static int getStaticRecordSize() {
        return 6;
    }
    
    @Override
    public UncalcedRecord copy() {
        return new UncalcedRecord(this);
    }
}
