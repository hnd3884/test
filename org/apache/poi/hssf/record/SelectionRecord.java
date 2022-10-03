package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.hssf.util.CellRangeAddress8Bit;

public final class SelectionRecord extends StandardRecord
{
    public static final short sid = 29;
    private byte field_1_pane;
    private int field_2_row_active_cell;
    private int field_3_col_active_cell;
    private int field_4_active_cell_ref_index;
    private CellRangeAddress8Bit[] field_6_refs;
    
    public SelectionRecord(final SelectionRecord other) {
        super(other);
        this.field_1_pane = other.field_1_pane;
        this.field_2_row_active_cell = other.field_2_row_active_cell;
        this.field_3_col_active_cell = other.field_3_col_active_cell;
        this.field_4_active_cell_ref_index = other.field_4_active_cell_ref_index;
        this.field_6_refs = (CellRangeAddress8Bit[])((other.field_6_refs == null) ? null : ((CellRangeAddress8Bit[])Stream.of(other.field_6_refs).map((Function<? super CellRangeAddress8Bit, ?>)CellRangeAddress8Bit::copy).toArray(CellRangeAddress8Bit[]::new)));
    }
    
    public SelectionRecord(final int activeCellRow, final int activeCellCol) {
        this.field_1_pane = 3;
        this.field_2_row_active_cell = activeCellRow;
        this.field_3_col_active_cell = activeCellCol;
        this.field_4_active_cell_ref_index = 0;
        this.field_6_refs = new CellRangeAddress8Bit[] { new CellRangeAddress8Bit(activeCellRow, activeCellRow, activeCellCol, activeCellCol) };
    }
    
    public SelectionRecord(final RecordInputStream in) {
        this.field_1_pane = in.readByte();
        this.field_2_row_active_cell = in.readUShort();
        this.field_3_col_active_cell = in.readShort();
        this.field_4_active_cell_ref_index = in.readShort();
        final int field_5_num_refs = in.readUShort();
        this.field_6_refs = new CellRangeAddress8Bit[field_5_num_refs];
        for (int i = 0; i < this.field_6_refs.length; ++i) {
            this.field_6_refs[i] = new CellRangeAddress8Bit(in);
        }
    }
    
    public void setPane(final byte pane) {
        this.field_1_pane = pane;
    }
    
    public void setActiveCellRow(final int row) {
        this.field_2_row_active_cell = row;
        this.resetField6();
    }
    
    public void setActiveCellCol(final short col) {
        this.field_3_col_active_cell = col;
        this.resetField6();
    }
    
    private void resetField6() {
        this.field_6_refs = new CellRangeAddress8Bit[] { new CellRangeAddress8Bit(this.field_2_row_active_cell, this.field_2_row_active_cell, this.field_3_col_active_cell, this.field_3_col_active_cell) };
    }
    
    public void setActiveCellRef(final short ref) {
        this.field_4_active_cell_ref_index = ref;
    }
    
    public byte getPane() {
        return this.field_1_pane;
    }
    
    public int getActiveCellRow() {
        return this.field_2_row_active_cell;
    }
    
    public int getActiveCellCol() {
        return this.field_3_col_active_cell;
    }
    
    public int getActiveCellRef() {
        return this.field_4_active_cell_ref_index;
    }
    
    @Override
    public String toString() {
        return "[SELECTION]\n    .pane            = " + HexDump.byteToHex(this.getPane()) + "\n    .activecellrow   = " + HexDump.shortToHex(this.getActiveCellRow()) + "\n    .activecellcol   = " + HexDump.shortToHex(this.getActiveCellCol()) + "\n    .activecellref   = " + HexDump.shortToHex(this.getActiveCellRef()) + "\n    .numrefs         = " + HexDump.shortToHex(this.field_6_refs.length) + "\n[/SELECTION]\n";
    }
    
    @Override
    protected int getDataSize() {
        return 9 + CellRangeAddress8Bit.getEncodedSize(this.field_6_refs.length);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeByte(this.getPane());
        out.writeShort(this.getActiveCellRow());
        out.writeShort(this.getActiveCellCol());
        out.writeShort(this.getActiveCellRef());
        final int nRefs = this.field_6_refs.length;
        out.writeShort(nRefs);
        for (final CellRangeAddress8Bit field_6_ref : this.field_6_refs) {
            field_6_ref.serialize(out);
        }
    }
    
    @Override
    public short getSid() {
        return 29;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public SelectionRecord clone() {
        return this.copy();
    }
    
    @Override
    public SelectionRecord copy() {
        return new SelectionRecord(this);
    }
}
