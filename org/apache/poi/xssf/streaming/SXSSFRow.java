package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.CellStyle;
import java.util.NoSuchElementException;
import java.util.Map;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.SortedMap;
import org.apache.poi.ss.usermodel.Row;

public class SXSSFRow implements Row, Comparable<SXSSFRow>
{
    private static final Boolean UNDEFINED;
    private final SXSSFSheet _sheet;
    private final SortedMap<Integer, SXSSFCell> _cells;
    private short _style;
    private short _height;
    private boolean _zHeight;
    private int _outlineLevel;
    private Boolean _hidden;
    private Boolean _collapsed;
    
    public SXSSFRow(final SXSSFSheet sheet) {
        this._cells = new TreeMap<Integer, SXSSFCell>();
        this._style = -1;
        this._height = -1;
        this._hidden = SXSSFRow.UNDEFINED;
        this._collapsed = SXSSFRow.UNDEFINED;
        this._sheet = sheet;
    }
    
    public Iterator<Cell> allCellsIterator() {
        return new CellIterator();
    }
    
    public boolean hasCustomHeight() {
        return this._height != -1;
    }
    
    public int getOutlineLevel() {
        return this._outlineLevel;
    }
    
    void setOutlineLevel(final int level) {
        this._outlineLevel = level;
    }
    
    public Boolean getHidden() {
        return this._hidden;
    }
    
    public void setHidden(final Boolean hidden) {
        this._hidden = hidden;
    }
    
    public Boolean getCollapsed() {
        return this._collapsed;
    }
    
    public void setCollapsed(final Boolean collapsed) {
        this._collapsed = collapsed;
    }
    
    public Iterator<Cell> iterator() {
        return new FilledCellIterator();
    }
    
    public SXSSFCell createCell(final int column) {
        return this.createCell(column, CellType.BLANK);
    }
    
    public SXSSFCell createCell(final int column, final CellType type) {
        checkBounds(column);
        final SXSSFCell cell = new SXSSFCell(this, type);
        this._cells.put(column, cell);
        return cell;
    }
    
    private static void checkBounds(final int cellIndex) {
        final SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        final int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }
    
    public void removeCell(final Cell cell) {
        final int index = this.getCellIndex((SXSSFCell)cell);
        this._cells.remove(index);
    }
    
    int getCellIndex(final SXSSFCell cell) {
        for (final Map.Entry<Integer, SXSSFCell> entry : this._cells.entrySet()) {
            if (entry.getValue() == cell) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    public void setRowNum(final int rowNum) {
        this._sheet.changeRowNum(this, rowNum);
    }
    
    public int getRowNum() {
        return this._sheet.getRowNum(this);
    }
    
    public SXSSFCell getCell(final int cellnum) {
        final Row.MissingCellPolicy policy = this._sheet.getWorkbook().getMissingCellPolicy();
        return this.getCell(cellnum, policy);
    }
    
    public SXSSFCell getCell(final int cellnum, final Row.MissingCellPolicy policy) {
        checkBounds(cellnum);
        final SXSSFCell cell = this._cells.get(cellnum);
        switch (policy) {
            case RETURN_NULL_AND_BLANK: {
                return cell;
            }
            case RETURN_BLANK_AS_NULL: {
                final boolean isBlank = cell != null && cell.getCellType() == CellType.BLANK;
                return isBlank ? null : cell;
            }
            case CREATE_NULL_AS_BLANK: {
                return (cell == null) ? this.createCell(cellnum, CellType.BLANK) : cell;
            }
            default: {
                throw new IllegalArgumentException("Illegal policy " + policy);
            }
        }
    }
    
    public short getFirstCellNum() {
        try {
            return this._cells.firstKey().shortValue();
        }
        catch (final NoSuchElementException e) {
            return -1;
        }
    }
    
    public short getLastCellNum() {
        return (short)(this._cells.isEmpty() ? -1 : ((short)(this._cells.lastKey() + 1)));
    }
    
    public int getPhysicalNumberOfCells() {
        return this._cells.size();
    }
    
    public void setHeight(final short height) {
        this._height = height;
    }
    
    public void setZeroHeight(final boolean zHeight) {
        this._zHeight = zHeight;
    }
    
    public boolean getZeroHeight() {
        return this._zHeight;
    }
    
    public void setHeightInPoints(final float height) {
        if (height == -1.0f) {
            this._height = -1;
        }
        else {
            this._height = (short)(height * 20.0f);
        }
    }
    
    public short getHeight() {
        return (short)((this._height == -1) ? (this.getSheet().getDefaultRowHeightInPoints() * 20.0f) : this._height);
    }
    
    public float getHeightInPoints() {
        return (float)((this._height == -1) ? this.getSheet().getDefaultRowHeightInPoints() : (this._height / 20.0));
    }
    
    public boolean isFormatted() {
        return this._style > -1;
    }
    
    public CellStyle getRowStyle() {
        if (!this.isFormatted()) {
            return null;
        }
        return this.getSheet().getWorkbook().getCellStyleAt(this._style);
    }
    
    @Internal
    int getRowStyleIndex() {
        return this._style;
    }
    
    public void setRowStyle(final CellStyle style) {
        if (style == null) {
            this._style = -1;
        }
        else {
            this._style = style.getIndex();
        }
    }
    
    public Iterator<Cell> cellIterator() {
        return this.iterator();
    }
    
    public SXSSFSheet getSheet() {
        return this._sheet;
    }
    
    public int compareTo(final SXSSFRow other) {
        if (this.getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        final int thisRow = this.getRowNum();
        final int otherRow = other.getRowNum();
        return Integer.compare(thisRow, otherRow);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SXSSFRow)) {
            return false;
        }
        final SXSSFRow other = (SXSSFRow)obj;
        return this.getRowNum() == other.getRowNum() && this.getSheet() == other.getSheet();
    }
    
    @Override
    public int hashCode() {
        return this._cells.hashCode();
    }
    
    @NotImplemented
    public void shiftCellsRight(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        throw new NotImplementedException("shiftCellsRight");
    }
    
    @NotImplemented
    public void shiftCellsLeft(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        throw new NotImplementedException("shiftCellsLeft");
    }
    
    static {
        UNDEFINED = null;
    }
    
    public class FilledCellIterator implements Iterator<Cell>
    {
        private final Iterator<SXSSFCell> iter;
        
        public FilledCellIterator() {
            this.iter = SXSSFRow.this._cells.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public Cell next() throws NoSuchElementException {
            return (Cell)this.iter.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public class CellIterator implements Iterator<Cell>
    {
        final int maxColumn;
        int pos;
        
        public CellIterator() {
            this.maxColumn = SXSSFRow.this.getLastCellNum();
        }
        
        @Override
        public boolean hasNext() {
            return this.pos < this.maxColumn;
        }
        
        @Override
        public Cell next() throws NoSuchElementException {
            if (this.hasNext()) {
                return (Cell)SXSSFRow.this._cells.get(this.pos++);
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
