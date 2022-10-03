package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public final class BlankRecord extends StandardRecord implements CellValueRecordInterface
{
    public static final short sid = 513;
    private int field_1_row;
    private short field_2_col;
    private short field_3_xf;
    
    public BlankRecord() {
    }
    
    public BlankRecord(final BlankRecord other) {
        super(other);
        this.field_1_row = other.field_1_row;
        this.field_2_col = other.field_2_col;
        this.field_3_xf = other.field_3_xf;
    }
    
    public BlankRecord(final RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_xf = in.readShort();
    }
    
    @Override
    public void setRow(final int row) {
        this.field_1_row = row;
    }
    
    @Override
    public int getRow() {
        return this.field_1_row;
    }
    
    @Override
    public short getColumn() {
        return this.field_2_col;
    }
    
    @Override
    public void setXFIndex(final short xf) {
        this.field_3_xf = xf;
    }
    
    @Override
    public short getXFIndex() {
        return this.field_3_xf;
    }
    
    @Override
    public void setColumn(final short col) {
        this.field_2_col = col;
    }
    
    @Override
    public short getSid() {
        return 513;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[BLANK]\n");
        sb.append("    row= ").append(HexDump.shortToHex(this.getRow())).append("\n");
        sb.append("    col= ").append(HexDump.shortToHex(this.getColumn())).append("\n");
        sb.append("    xf = ").append(HexDump.shortToHex(this.getXFIndex())).append("\n");
        sb.append("[/BLANK]\n");
        return sb.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getRow());
        out.writeShort(this.getColumn());
        out.writeShort(this.getXFIndex());
    }
    
    @Override
    protected int getDataSize() {
        return 6;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public BlankRecord clone() {
        return this.copy();
    }
    
    @Override
    public BlankRecord copy() {
        return new BlankRecord(this);
    }
}
