package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class DeltaRecord extends StandardRecord
{
    public static final short sid = 16;
    public static final double DEFAULT_VALUE = 0.001;
    private double field_1_max_change;
    
    public DeltaRecord(final double maxChange) {
        this.field_1_max_change = maxChange;
    }
    
    public DeltaRecord(final DeltaRecord other) {
        super(other);
        this.field_1_max_change = other.field_1_max_change;
    }
    
    public DeltaRecord(final RecordInputStream in) {
        this.field_1_max_change = in.readDouble();
    }
    
    public double getMaxChange() {
        return this.field_1_max_change;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DELTA]\n");
        buffer.append("    .maxchange = ").append(this.getMaxChange()).append("\n");
        buffer.append("[/DELTA]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeDouble(this.getMaxChange());
    }
    
    @Override
    protected int getDataSize() {
        return 8;
    }
    
    @Override
    public short getSid() {
        return 16;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DeltaRecord clone() {
        return this.copy();
    }
    
    @Override
    public DeltaRecord copy() {
        return this;
    }
}
