package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public final class DBCellRecord extends StandardRecord
{
    public static final short sid = 215;
    public static final int BLOCK_SIZE = 32;
    private final int field_1_row_offset;
    private final short[] field_2_cell_offsets;
    
    public DBCellRecord(final int rowOffset, final short[] cellOffsets) {
        this.field_1_row_offset = rowOffset;
        this.field_2_cell_offsets = cellOffsets;
    }
    
    public DBCellRecord(final RecordInputStream in) {
        this.field_1_row_offset = in.readUShort();
        final int size = in.remaining();
        this.field_2_cell_offsets = new short[size / 2];
        for (int i = 0; i < this.field_2_cell_offsets.length; ++i) {
            this.field_2_cell_offsets[i] = in.readShort();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DBCELL]\n");
        buffer.append("    .rowoffset = ").append(HexDump.intToHex(this.field_1_row_offset)).append("\n");
        for (int k = 0; k < this.field_2_cell_offsets.length; ++k) {
            buffer.append("    .cell_").append(k).append(" = ").append(HexDump.shortToHex(this.field_2_cell_offsets[k])).append("\n");
        }
        buffer.append("[/DBCELL]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.field_1_row_offset);
        for (final short field_2_cell_offset : this.field_2_cell_offsets) {
            out.writeShort(field_2_cell_offset);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 4 + this.field_2_cell_offsets.length * 2;
    }
    
    @Override
    public short getSid() {
        return 215;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DBCellRecord clone() {
        return this.copy();
    }
    
    @Override
    public DBCellRecord copy() {
        return this;
    }
}
