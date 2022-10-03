package org.apache.poi.ss.util;

import org.apache.poi.ss.usermodel.Cell;
import java.util.Locale;

public class CellAddress implements Comparable<CellAddress>
{
    public static final CellAddress A1;
    private final int _row;
    private final int _col;
    
    public CellAddress(final int row, final int column) {
        this._row = row;
        this._col = column;
    }
    
    public CellAddress(final String address) {
        int length;
        int loc;
        for (length = address.length(), loc = 0; loc < length; ++loc) {
            final char ch = address.charAt(loc);
            if (Character.isDigit(ch)) {
                break;
            }
        }
        final String sCol = address.substring(0, loc).toUpperCase(Locale.ROOT);
        final String sRow = address.substring(loc);
        this._row = Integer.parseInt(sRow) - 1;
        this._col = CellReference.convertColStringToIndex(sCol);
    }
    
    public CellAddress(final CellReference reference) {
        this(reference.getRow(), reference.getCol());
    }
    
    public CellAddress(final CellAddress address) {
        this(address.getRow(), address.getColumn());
    }
    
    public CellAddress(final Cell cell) {
        this(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    public int getRow() {
        return this._row;
    }
    
    public int getColumn() {
        return this._col;
    }
    
    @Override
    public int compareTo(final CellAddress other) {
        int r = this._row - other._row;
        if (r != 0) {
            return r;
        }
        r = this._col - other._col;
        if (r != 0) {
            return r;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CellAddress)) {
            return false;
        }
        final CellAddress other = (CellAddress)o;
        return this._row == other._row && this._col == other._col;
    }
    
    @Override
    public int hashCode() {
        return this._row + this._col << 16;
    }
    
    @Override
    public String toString() {
        return this.formatAsString();
    }
    
    public String formatAsString() {
        return CellReference.convertNumToColString(this._col) + (this._row + 1);
    }
    
    static {
        A1 = new CellAddress(0, 0);
    }
}
