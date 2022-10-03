package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class AutoFilterInfoRecord extends StandardRecord
{
    public static final short sid = 157;
    private short _cEntries;
    
    public AutoFilterInfoRecord() {
    }
    
    public AutoFilterInfoRecord(final AutoFilterInfoRecord other) {
        super(other);
        this._cEntries = other._cEntries;
    }
    
    public AutoFilterInfoRecord(final RecordInputStream in) {
        this._cEntries = in.readShort();
    }
    
    public void setNumEntries(final short num) {
        this._cEntries = num;
    }
    
    public short getNumEntries() {
        return this._cEntries;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[AUTOFILTERINFO]\n");
        buffer.append("    .numEntries          = ").append(this._cEntries).append("\n");
        buffer.append("[/AUTOFILTERINFO]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._cEntries);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 157;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public AutoFilterInfoRecord clone() {
        return this.copy();
    }
    
    @Override
    public AutoFilterInfoRecord copy() {
        return new AutoFilterInfoRecord(this);
    }
}
