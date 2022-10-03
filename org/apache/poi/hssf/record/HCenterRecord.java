package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class HCenterRecord extends StandardRecord
{
    public static final short sid = 131;
    private short field_1_hcenter;
    
    public HCenterRecord() {
    }
    
    public HCenterRecord(final HCenterRecord other) {
        super(other);
        this.field_1_hcenter = other.field_1_hcenter;
    }
    
    public HCenterRecord(final RecordInputStream in) {
        this.field_1_hcenter = in.readShort();
    }
    
    public void setHCenter(final boolean hc) {
        this.field_1_hcenter = (short)(hc ? 1 : 0);
    }
    
    public boolean getHCenter() {
        return this.field_1_hcenter == 1;
    }
    
    @Override
    public String toString() {
        return "[HCENTER]\n    .hcenter        = " + this.getHCenter() + "\n[/HCENTER]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_hcenter);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 131;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public HCenterRecord clone() {
        return this.copy();
    }
    
    @Override
    public HCenterRecord copy() {
        return new HCenterRecord(this);
    }
}
