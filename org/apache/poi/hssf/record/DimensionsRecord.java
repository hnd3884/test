package org.apache.poi.hssf.record;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogger;

public final class DimensionsRecord extends StandardRecord
{
    private static final POILogger logger;
    public static final short sid = 512;
    private int field_1_first_row;
    private int field_2_last_row;
    private short field_3_first_col;
    private short field_4_last_col;
    private short field_5_zero;
    
    public DimensionsRecord() {
    }
    
    public DimensionsRecord(final DimensionsRecord other) {
        super(other);
        this.field_1_first_row = other.field_1_first_row;
        this.field_2_last_row = other.field_2_last_row;
        this.field_3_first_col = other.field_3_first_col;
        this.field_4_last_col = other.field_4_last_col;
        this.field_5_zero = other.field_5_zero;
    }
    
    public DimensionsRecord(final RecordInputStream in) {
        this.field_1_first_row = in.readInt();
        this.field_2_last_row = in.readInt();
        this.field_3_first_col = in.readShort();
        this.field_4_last_col = in.readShort();
        this.field_5_zero = in.readShort();
        if (in.available() == 2) {
            DimensionsRecord.logger.log(3, "DimensionsRecord has extra 2 bytes.");
            in.readShort();
        }
    }
    
    public void setFirstRow(final int row) {
        this.field_1_first_row = row;
    }
    
    public void setLastRow(final int row) {
        this.field_2_last_row = row;
    }
    
    public void setFirstCol(final short col) {
        this.field_3_first_col = col;
    }
    
    public void setLastCol(final short col) {
        this.field_4_last_col = col;
    }
    
    public int getFirstRow() {
        return this.field_1_first_row;
    }
    
    public int getLastRow() {
        return this.field_2_last_row;
    }
    
    public short getFirstCol() {
        return this.field_3_first_col;
    }
    
    public short getLastCol() {
        return this.field_4_last_col;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DIMENSIONS]\n");
        buffer.append("    .firstrow       = ").append(Integer.toHexString(this.getFirstRow())).append("\n");
        buffer.append("    .lastrow        = ").append(Integer.toHexString(this.getLastRow())).append("\n");
        buffer.append("    .firstcol       = ").append(Integer.toHexString(this.getFirstCol())).append("\n");
        buffer.append("    .lastcol        = ").append(Integer.toHexString(this.getLastCol())).append("\n");
        buffer.append("    .zero           = ").append(Integer.toHexString(this.field_5_zero)).append("\n");
        buffer.append("[/DIMENSIONS]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.getFirstRow());
        out.writeInt(this.getLastRow());
        out.writeShort(this.getFirstCol());
        out.writeShort(this.getLastCol());
        out.writeShort(0);
    }
    
    @Override
    protected int getDataSize() {
        return 14;
    }
    
    @Override
    public short getSid() {
        return 512;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DimensionsRecord clone() {
        return this.copy();
    }
    
    @Override
    public DimensionsRecord copy() {
        return new DimensionsRecord(this);
    }
    
    static {
        logger = POILogFactory.getLogger(DimensionsRecord.class);
    }
}
