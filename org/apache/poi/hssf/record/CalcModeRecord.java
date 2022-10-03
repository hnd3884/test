package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class CalcModeRecord extends StandardRecord
{
    public static final short sid = 13;
    public static final short MANUAL = 0;
    public static final short AUTOMATIC = 1;
    public static final short AUTOMATIC_EXCEPT_TABLES = -1;
    private short field_1_calcmode;
    
    public CalcModeRecord() {
    }
    
    public CalcModeRecord(final CalcModeRecord other) {
        super(other);
        this.field_1_calcmode = other.field_1_calcmode;
    }
    
    public CalcModeRecord(final RecordInputStream in) {
        this.field_1_calcmode = in.readShort();
    }
    
    public void setCalcMode(final short calcmode) {
        this.field_1_calcmode = calcmode;
    }
    
    public short getCalcMode() {
        return this.field_1_calcmode;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CALCMODE]\n");
        buffer.append("    .calcmode       = ").append(Integer.toHexString(this.getCalcMode())).append("\n");
        buffer.append("[/CALCMODE]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getCalcMode());
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 13;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public CalcModeRecord clone() {
        return this.copy();
    }
    
    @Override
    public CalcModeRecord copy() {
        return new CalcModeRecord(this);
    }
}
