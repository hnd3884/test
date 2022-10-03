package org.apache.poi.ss.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.common.Duplicatable;

public abstract class CellRangeAddressBase implements Iterable<CellAddress>, Duplicatable
{
    private int _firstRow;
    private int _firstCol;
    private int _lastRow;
    private int _lastCol;
    
    protected CellRangeAddressBase(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        this._firstRow = firstRow;
        this._lastRow = lastRow;
        this._firstCol = firstCol;
        this._lastCol = lastCol;
    }
    
    public void validate(final SpreadsheetVersion ssVersion) {
        validateRow(this._firstRow, ssVersion);
        validateRow(this._lastRow, ssVersion);
        validateColumn(this._firstCol, ssVersion);
        validateColumn(this._lastCol, ssVersion);
    }
    
    private static void validateRow(final int row, final SpreadsheetVersion ssVersion) {
        final int maxrow = ssVersion.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }
    
    private static void validateColumn(final int column, final SpreadsheetVersion ssVersion) {
        final int maxcol = ssVersion.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        }
        if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }
    
    public final boolean isFullColumnRange() {
        return (this._firstRow == 0 && this._lastRow == SpreadsheetVersion.EXCEL97.getLastRowIndex()) || (this._firstRow == -1 && this._lastRow == -1);
    }
    
    public final boolean isFullRowRange() {
        return (this._firstCol == 0 && this._lastCol == SpreadsheetVersion.EXCEL97.getLastColumnIndex()) || (this._firstCol == -1 && this._lastCol == -1);
    }
    
    public final int getFirstColumn() {
        return this._firstCol;
    }
    
    public final int getFirstRow() {
        return this._firstRow;
    }
    
    public final int getLastColumn() {
        return this._lastCol;
    }
    
    public final int getLastRow() {
        return this._lastRow;
    }
    
    public boolean isInRange(final int rowInd, final int colInd) {
        return this._firstRow <= rowInd && rowInd <= this._lastRow && this._firstCol <= colInd && colInd <= this._lastCol;
    }
    
    public boolean isInRange(final CellReference ref) {
        return this.isInRange(ref.getRow(), ref.getCol());
    }
    
    public boolean isInRange(final CellAddress ref) {
        return this.isInRange(ref.getRow(), ref.getColumn());
    }
    
    public boolean isInRange(final Cell cell) {
        return this.isInRange(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    public boolean containsRow(final int rowInd) {
        return this._firstRow <= rowInd && rowInd <= this._lastRow;
    }
    
    public boolean containsColumn(final int colInd) {
        return this._firstCol <= colInd && colInd <= this._lastCol;
    }
    
    public boolean intersects(final CellRangeAddressBase other) {
        return this._firstRow <= other._lastRow && this._firstCol <= other._lastCol && other._firstRow <= this._lastRow && other._firstCol <= this._lastCol;
    }
    
    public Set<CellPosition> getPosition(final int rowInd, final int colInd) {
        final Set<CellPosition> positions = EnumSet.noneOf(CellPosition.class);
        if (rowInd > this.getFirstRow() && rowInd < this.getLastRow() && colInd > this.getFirstColumn() && colInd < this.getLastColumn()) {
            positions.add(CellPosition.INSIDE);
            return positions;
        }
        if (rowInd == this.getFirstRow()) {
            positions.add(CellPosition.TOP);
        }
        if (rowInd == this.getLastRow()) {
            positions.add(CellPosition.BOTTOM);
        }
        if (colInd == this.getFirstColumn()) {
            positions.add(CellPosition.LEFT);
        }
        if (colInd == this.getLastColumn()) {
            positions.add(CellPosition.RIGHT);
        }
        return positions;
    }
    
    public final void setFirstColumn(final int firstCol) {
        this._firstCol = firstCol;
    }
    
    public final void setFirstRow(final int firstRow) {
        this._firstRow = firstRow;
    }
    
    public final void setLastColumn(final int lastCol) {
        this._lastCol = lastCol;
    }
    
    public final void setLastRow(final int lastRow) {
        this._lastRow = lastRow;
    }
    
    public int getNumberOfCells() {
        return (this._lastRow - this._firstRow + 1) * (this._lastCol - this._firstCol + 1);
    }
    
    @Override
    public Iterator<CellAddress> iterator() {
        return new RowMajorCellAddressIterator(this);
    }
    
    @Override
    public final String toString() {
        final CellAddress crA = new CellAddress(this._firstRow, this._firstCol);
        final CellAddress crB = new CellAddress(this._lastRow, this._lastCol);
        return this.getClass().getName() + " [" + crA.formatAsString() + ":" + crB.formatAsString() + "]";
    }
    
    protected int getMinRow() {
        return Math.min(this._firstRow, this._lastRow);
    }
    
    protected int getMaxRow() {
        return Math.max(this._firstRow, this._lastRow);
    }
    
    protected int getMinColumn() {
        return Math.min(this._firstCol, this._lastCol);
    }
    
    protected int getMaxColumn() {
        return Math.max(this._firstCol, this._lastCol);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof CellRangeAddressBase) {
            final CellRangeAddressBase o = (CellRangeAddressBase)other;
            return this.getMinRow() == o.getMinRow() && this.getMaxRow() == o.getMaxRow() && this.getMinColumn() == o.getMinColumn() && this.getMaxColumn() == o.getMaxColumn();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getMinColumn() + (this.getMaxColumn() << 8) + (this.getMinRow() << 16) + (this.getMaxRow() << 24);
    }
    
    public enum CellPosition
    {
        TOP, 
        BOTTOM, 
        LEFT, 
        RIGHT, 
        INSIDE;
    }
    
    private static class RowMajorCellAddressIterator implements Iterator<CellAddress>
    {
        private final int firstRow;
        private final int firstCol;
        private final int lastRow;
        private final int lastCol;
        private int r;
        private int c;
        
        public RowMajorCellAddressIterator(final CellRangeAddressBase ref) {
            final int firstRow = ref.getFirstRow();
            this.firstRow = firstRow;
            this.r = firstRow;
            final int firstColumn = ref.getFirstColumn();
            this.firstCol = firstColumn;
            this.c = firstColumn;
            this.lastRow = ref.getLastRow();
            this.lastCol = ref.getLastColumn();
            if (this.firstRow < 0) {
                throw new IllegalStateException("First row cannot be negative.");
            }
            if (this.firstCol < 0) {
                throw new IllegalStateException("First column cannot be negative.");
            }
            if (this.firstRow > this.lastRow) {
                throw new IllegalStateException("First row cannot be greater than last row.");
            }
            if (this.firstCol > this.lastCol) {
                throw new IllegalStateException("First column cannot be greater than last column.");
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.r <= this.lastRow && this.c <= this.lastCol;
        }
        
        @Override
        public CellAddress next() {
            if (this.hasNext()) {
                final CellAddress addr = new CellAddress(this.r, this.c);
                if (this.c < this.lastCol) {
                    ++this.c;
                }
                else {
                    this.c = this.firstCol;
                    ++this.r;
                }
                return addr;
            }
            throw new NoSuchElementException();
        }
    }
}
