package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class GridsetRecord extends StandardRecord
{
    public static final short sid = 130;
    public short field_1_gridset_flag;
    
    public GridsetRecord() {
    }
    
    public GridsetRecord(final GridsetRecord other) {
        super(other);
        this.field_1_gridset_flag = other.field_1_gridset_flag;
    }
    
    public GridsetRecord(final RecordInputStream in) {
        this.field_1_gridset_flag = in.readShort();
    }
    
    public void setGridset(final boolean gridset) {
        if (gridset) {
            this.field_1_gridset_flag = 1;
        }
        else {
            this.field_1_gridset_flag = 0;
        }
    }
    
    public boolean getGridset() {
        return this.field_1_gridset_flag == 1;
    }
    
    @Override
    public String toString() {
        return "[GRIDSET]\n    .gridset        = " + this.getGridset() + "\n[/GRIDSET]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_gridset_flag);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 130;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public GridsetRecord clone() {
        return this.copy();
    }
    
    @Override
    public GridsetRecord copy() {
        return new GridsetRecord(this);
    }
}
