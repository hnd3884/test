package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class VCenterRecord extends StandardRecord
{
    public static final short sid = 132;
    private int field_1_vcenter;
    
    public VCenterRecord() {
    }
    
    public VCenterRecord(final VCenterRecord other) {
        super(other);
        this.field_1_vcenter = other.field_1_vcenter;
    }
    
    public VCenterRecord(final RecordInputStream in) {
        this.field_1_vcenter = in.readShort();
    }
    
    public void setVCenter(final boolean hc) {
        this.field_1_vcenter = (hc ? 1 : 0);
    }
    
    public boolean getVCenter() {
        return this.field_1_vcenter == 1;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[VCENTER]\n");
        buffer.append("    .vcenter        = ").append(this.getVCenter()).append("\n");
        buffer.append("[/VCENTER]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_vcenter);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 132;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public VCenterRecord clone() {
        return this.copy();
    }
    
    @Override
    public VCenterRecord copy() {
        return new VCenterRecord(this);
    }
}
