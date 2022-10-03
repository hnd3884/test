package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class MulBlankRecord extends StandardRecord
{
    public static final short sid = 190;
    private final int _row;
    private final int _firstCol;
    private final short[] _xfs;
    private final int _lastCol;
    
    public MulBlankRecord(final int row, final int firstCol, final short[] xfs) {
        this._row = row;
        this._firstCol = firstCol;
        this._xfs = xfs;
        this._lastCol = firstCol + xfs.length - 1;
    }
    
    public int getRow() {
        return this._row;
    }
    
    public int getFirstColumn() {
        return this._firstCol;
    }
    
    public int getLastColumn() {
        return this._lastCol;
    }
    
    public int getNumColumns() {
        return this._lastCol - this._firstCol + 1;
    }
    
    public short getXFAt(final int coffset) {
        return this._xfs[coffset];
    }
    
    public MulBlankRecord(final RecordInputStream in) {
        this._row = in.readUShort();
        this._firstCol = in.readShort();
        this._xfs = parseXFs(in);
        this._lastCol = in.readShort();
    }
    
    private static short[] parseXFs(final RecordInputStream in) {
        final short[] retval = new short[(in.remaining() - 2) / 2];
        for (int idx = 0; idx < retval.length; ++idx) {
            retval[idx] = in.readShort();
        }
        return retval;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[MULBLANK]\n");
        buffer.append("row  = ").append(Integer.toHexString(this.getRow())).append("\n");
        buffer.append("firstcol  = ").append(Integer.toHexString(this.getFirstColumn())).append("\n");
        buffer.append(" lastcol  = ").append(Integer.toHexString(this._lastCol)).append("\n");
        for (int k = 0; k < this.getNumColumns(); ++k) {
            buffer.append("xf").append(k).append("\t\t= ").append(Integer.toHexString(this.getXFAt(k))).append("\n");
        }
        buffer.append("[/MULBLANK]\n");
        return buffer.toString();
    }
    
    @Override
    public short getSid() {
        return 190;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._row);
        out.writeShort(this._firstCol);
        final int nItems = this._xfs.length;
        for (final short xf : this._xfs) {
            out.writeShort(xf);
        }
        out.writeShort(this._lastCol);
    }
    
    @Override
    protected int getDataSize() {
        return 6 + this._xfs.length * 2;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public MulBlankRecord clone() {
        return this.copy();
    }
    
    @Override
    public MulBlankRecord copy() {
        return this;
    }
}
