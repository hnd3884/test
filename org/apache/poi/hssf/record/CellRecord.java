package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;

public abstract class CellRecord extends StandardRecord implements CellValueRecordInterface
{
    private int _rowIndex;
    private int _columnIndex;
    private int _formatIndex;
    
    protected CellRecord() {
    }
    
    protected CellRecord(final CellRecord other) {
        super(other);
        this._rowIndex = other.getRow();
        this._columnIndex = other.getColumn();
        this._formatIndex = other.getXFIndex();
    }
    
    protected CellRecord(final RecordInputStream in) {
        this._rowIndex = in.readUShort();
        this._columnIndex = in.readUShort();
        this._formatIndex = in.readUShort();
    }
    
    @Override
    public final void setRow(final int row) {
        this._rowIndex = row;
    }
    
    @Override
    public final void setColumn(final short col) {
        this._columnIndex = col;
    }
    
    @Override
    public final void setXFIndex(final short xf) {
        this._formatIndex = xf;
    }
    
    @Override
    public final int getRow() {
        return this._rowIndex;
    }
    
    @Override
    public final short getColumn() {
        return (short)this._columnIndex;
    }
    
    @Override
    public final short getXFIndex() {
        return (short)this._formatIndex;
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        final String recordName = this.getRecordName();
        sb.append("[").append(recordName).append("]\n");
        sb.append("    .row    = ").append(HexDump.shortToHex(this.getRow())).append("\n");
        sb.append("    .col    = ").append(HexDump.shortToHex(this.getColumn())).append("\n");
        sb.append("    .xfindex= ").append(HexDump.shortToHex(this.getXFIndex())).append("\n");
        this.appendValueText(sb);
        sb.append("\n");
        sb.append("[/").append(recordName).append("]\n");
        return sb.toString();
    }
    
    protected abstract void appendValueText(final StringBuilder p0);
    
    protected abstract String getRecordName();
    
    protected abstract void serializeValue(final LittleEndianOutput p0);
    
    protected abstract int getValueDataSize();
    
    public final void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getRow());
        out.writeShort(this.getColumn());
        out.writeShort(this.getXFIndex());
        this.serializeValue(out);
    }
    
    @Override
    protected final int getDataSize() {
        return 6 + this.getValueDataSize();
    }
    
    @Override
    public abstract CellRecord copy();
}
