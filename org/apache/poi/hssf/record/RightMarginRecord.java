package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class RightMarginRecord extends StandardRecord implements Margin
{
    public static final short sid = 39;
    private double field_1_margin;
    
    public RightMarginRecord() {
    }
    
    public RightMarginRecord(final RightMarginRecord other) {
        super(other);
        this.field_1_margin = other.field_1_margin;
    }
    
    public RightMarginRecord(final RecordInputStream in) {
        this.field_1_margin = in.readDouble();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[RightMargin]\n");
        buffer.append("    .margin               = ").append(" (").append(this.getMargin()).append(" )\n");
        buffer.append("[/RightMargin]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeDouble(this.field_1_margin);
    }
    
    @Override
    protected int getDataSize() {
        return 8;
    }
    
    @Override
    public short getSid() {
        return 39;
    }
    
    @Override
    public double getMargin() {
        return this.field_1_margin;
    }
    
    @Override
    public void setMargin(final double field_1_margin) {
        this.field_1_margin = field_1_margin;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public RightMarginRecord clone() {
        return this.copy();
    }
    
    @Override
    public RightMarginRecord copy() {
        return new RightMarginRecord(this);
    }
}
