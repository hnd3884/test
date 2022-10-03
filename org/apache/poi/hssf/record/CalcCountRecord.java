package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class CalcCountRecord extends StandardRecord
{
    public static final short sid = 12;
    private short field_1_iterations;
    
    public CalcCountRecord() {
    }
    
    public CalcCountRecord(final CalcCountRecord other) {
        super(other);
        this.field_1_iterations = other.field_1_iterations;
    }
    
    public CalcCountRecord(final RecordInputStream in) {
        this.field_1_iterations = in.readShort();
    }
    
    public void setIterations(final short iterations) {
        this.field_1_iterations = iterations;
    }
    
    public short getIterations() {
        return this.field_1_iterations;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CALCCOUNT]\n");
        buffer.append("    .iterations     = ").append(Integer.toHexString(this.getIterations())).append("\n");
        buffer.append("[/CALCCOUNT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getIterations());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 12;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public CalcCountRecord clone() {
        return this.copy();
    }
    
    @Override
    public CalcCountRecord copy() {
        return new CalcCountRecord(this);
    }
}
