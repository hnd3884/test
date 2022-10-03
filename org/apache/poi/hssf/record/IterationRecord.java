package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class IterationRecord extends StandardRecord
{
    public static final short sid = 17;
    private static final BitField iterationOn;
    private int _flags;
    
    public IterationRecord(final IterationRecord other) {
        super(other);
        this._flags = other._flags;
    }
    
    public IterationRecord(final boolean iterateOn) {
        this._flags = IterationRecord.iterationOn.setBoolean(0, iterateOn);
    }
    
    public IterationRecord(final RecordInputStream in) {
        this._flags = in.readShort();
    }
    
    public void setIteration(final boolean iterate) {
        this._flags = IterationRecord.iterationOn.setBoolean(this._flags, iterate);
    }
    
    public boolean getIteration() {
        return IterationRecord.iterationOn.isSet(this._flags);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[ITERATION]\n");
        buffer.append("    .flags      = ").append(HexDump.shortToHex(this._flags)).append("\n");
        buffer.append("[/ITERATION]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._flags);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 17;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public IterationRecord clone() {
        return this.copy();
    }
    
    @Override
    public IterationRecord copy() {
        return new IterationRecord(this);
    }
    
    static {
        iterationOn = BitFieldFactory.getInstance(1);
    }
}
