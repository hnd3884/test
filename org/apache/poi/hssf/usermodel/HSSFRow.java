package org.apache.poi.hssf.usermodel;

import java.util.NoSuchElementException;
import org.apache.poi.util.Configurator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.helpers.RowShifter;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.ss.usermodel.Row;

public final class HSSFRow implements Row, Comparable<HSSFRow>
{
    public static final int INITIAL_CAPACITY;
    private int rowNum;
    private HSSFCell[] cells;
    private final RowRecord row;
    private final HSSFWorkbook book;
    private final HSSFSheet sheet;
    
    HSSFRow(final HSSFWorkbook book, final HSSFSheet sheet, final int rowNum) {
        this(book, sheet, new RowRecord(rowNum));
    }
    
    HSSFRow(final HSSFWorkbook book, final HSSFSheet sheet, final RowRecord record) {
        this.book = book;
        this.sheet = sheet;
        this.row = record;
        this.setRowNum(record.getRowNumber());
        this.cells = new HSSFCell[record.getLastCol() + HSSFRow.INITIAL_CAPACITY];
        record.setEmpty();
    }
    
    @Override
    public HSSFCell createCell(final int column) {
        return this.createCell(column, CellType.BLANK);
    }
    
    @Override
    public HSSFCell createCell(final int columnIndex, final CellType type) {
        short shortCellNum = (short)columnIndex;
        if (columnIndex > 32767) {
            shortCellNum = (short)(65535 - columnIndex);
        }
        final HSSFCell cell = new HSSFCell(this.book, this.sheet, this.getRowNum(), shortCellNum, type);
        this.addCell(cell);
        this.sheet.getSheet().addValueRecord(this.getRowNum(), cell.getCellValueRecord());
        return cell;
    }
    
    @Override
    public void removeCell(final Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell must not be null");
        }
        this.removeCell((HSSFCell)cell, true);
    }
    
    private void removeCell(final HSSFCell cell, final boolean alsoRemoveRecords) {
        final int column = cell.getColumnIndex();
        if (column < 0) {
            throw new RuntimeException("Negative cell indexes not allowed");
        }
        if (column >= this.cells.length || cell != this.cells[column]) {
            throw new RuntimeException("Specified cell is not from this row");
        }
        if (cell.isPartOfArrayFormulaGroup()) {
            cell.tryToDeleteArrayFormula(null);
        }
        this.cells[column] = null;
        if (alsoRemoveRecords) {
            final CellValueRecordInterface cval = cell.getCellValueRecord();
            this.sheet.getSheet().removeValueRecord(this.getRowNum(), cval);
        }
        if (cell.getColumnIndex() + 1 == this.row.getLastCol()) {
            this.row.setLastCol(this.calculateNewLastCellPlusOne(this.row.getLastCol()));
        }
        if (cell.getColumnIndex() == this.row.getFirstCol()) {
            this.row.setFirstCol(this.calculateNewFirstCell(this.row.getFirstCol()));
        }
    }
    
    protected void removeAllCells() {
        for (final HSSFCell cell : this.cells) {
            if (cell != null) {
                this.removeCell(cell, true);
            }
        }
        this.cells = new HSSFCell[HSSFRow.INITIAL_CAPACITY];
    }
    
    HSSFCell createCellFromRecord(final CellValueRecordInterface cell) {
        final HSSFCell hcell = new HSSFCell(this.book, this.sheet, cell);
        this.addCell(hcell);
        final int colIx = cell.getColumn();
        if (this.row.isEmpty()) {
            this.row.setFirstCol(colIx);
            this.row.setLastCol(colIx + 1);
        }
        else if (colIx < this.row.getFirstCol()) {
            this.row.setFirstCol(colIx);
        }
        else if (colIx > this.row.getLastCol()) {
            this.row.setLastCol(colIx + 1);
        }
        return hcell;
    }
    
    @Override
    public void setRowNum(final int rowIndex) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex + ") outside allowable range (0.." + maxrow + ")");
        }
        this.rowNum = rowIndex;
        if (this.row != null) {
            this.row.setRowNumber(rowIndex);
        }
    }
    
    @Override
    public int getRowNum() {
        return this.rowNum;
    }
    
    @Override
    public HSSFSheet getSheet() {
        return this.sheet;
    }
    
    @Override
    public int getOutlineLevel() {
        return this.row.getOutlineLevel();
    }
    
    public void moveCell(final HSSFCell cell, final short newColumn) {
        if (this.cells.length > newColumn && this.cells[newColumn] != null) {
            throw new IllegalArgumentException("Asked to move cell to column " + newColumn + " but there's already a cell there");
        }
        if (!this.cells[cell.getColumnIndex()].equals(cell)) {
            throw new IllegalArgumentException("Asked to move a cell, but it didn't belong to our row");
        }
        this.removeCell(cell, false);
        cell.updateCellNum(newColumn);
        this.addCell(cell);
    }
    
    private void addCell(final HSSFCell cell) {
        final int column = cell.getColumnIndex();
        if (column >= this.cells.length) {
            final HSSFCell[] oldCells = this.cells;
            int newSize = oldCells.length * 3 / 2 + 1;
            if (newSize < column + 1) {
                newSize = column + HSSFRow.INITIAL_CAPACITY;
            }
            System.arraycopy(oldCells, 0, this.cells = new HSSFCell[newSize], 0, oldCells.length);
        }
        this.cells[column] = cell;
        if (this.row.isEmpty() || column < this.row.getFirstCol()) {
            this.row.setFirstCol((short)column);
        }
        if (this.row.isEmpty() || column >= this.row.getLastCol()) {
            this.row.setLastCol((short)(column + 1));
        }
    }
    
    private HSSFCell retrieveCell(final int cellIndex) {
        if (cellIndex < 0 || cellIndex >= this.cells.length) {
            return null;
        }
        return this.cells[cellIndex];
    }
    
    @Override
    public HSSFCell getCell(final int cellnum) {
        return this.getCell(cellnum, this.book.getMissingCellPolicy());
    }
    
    @Override
    public HSSFCell getCell(final int cellnum, final MissingCellPolicy policy) {
        final HSSFCell cell = this.retrieveCell(cellnum);
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
    
    @Override
    public short getFirstCellNum() {
        if (this.row.isEmpty()) {
            return -1;
        }
        return (short)this.row.getFirstCol();
    }
    
    @Override
    public short getLastCellNum() {
        if (this.row.isEmpty()) {
            return -1;
        }
        return (short)this.row.getLastCol();
    }
    
    @Override
    public int getPhysicalNumberOfCells() {
        int count = 0;
        for (final HSSFCell cell : this.cells) {
            if (cell != null) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public void setHeight(final short height) {
        if (height == -1) {
            this.row.setHeight((short)(-32513));
            this.row.setBadFontHeight(false);
        }
        else {
            this.row.setBadFontHeight(true);
            this.row.setHeight(height);
        }
    }
    
    @Override
    public void setZeroHeight(final boolean zHeight) {
        this.row.setZeroHeight(zHeight);
    }
    
    @Override
    public boolean getZeroHeight() {
        return this.row.getZeroHeight();
    }
    
    @Override
    public void setHeightInPoints(final float height) {
        if (height == -1.0f) {
            this.row.setHeight((short)(-32513));
            this.row.setBadFontHeight(false);
        }
        else {
            this.row.setBadFontHeight(true);
            this.row.setHeight((short)(height * 20.0f));
        }
    }
    
    @Override
    public short getHeight() {
        short height = this.row.getHeight();
        if ((height & 0x8000) != 0x0) {
            height = this.sheet.getSheet().getDefaultRowHeight();
        }
        else {
            height &= 0x7FFF;
        }
        return height;
    }
    
    @Override
    public float getHeightInPoints() {
        return this.getHeight() / 20.0f;
    }
    
    protected RowRecord getRowRecord() {
        return this.row;
    }
    
    private int calculateNewLastCellPlusOne(final int lastcell) {
        int cellIx = lastcell - 1;
        for (HSSFCell r = this.retrieveCell(cellIx); r == null; r = this.retrieveCell(--cellIx)) {
            if (cellIx < 0) {
                return 0;
            }
        }
        return cellIx + 1;
    }
    
    private int calculateNewFirstCell(final int firstcell) {
        int cellIx = firstcell + 1;
        for (HSSFCell r = this.retrieveCell(cellIx); r == null; r = this.retrieveCell(++cellIx)) {
            if (cellIx <= this.cells.length) {
                return 0;
            }
        }
        return cellIx;
    }
    
    @Override
    public boolean isFormatted() {
        return this.row.getFormatted();
    }
    
    @Override
    public HSSFCellStyle getRowStyle() {
        if (!this.isFormatted()) {
            return null;
        }
        final short styleIndex = this.row.getXFIndex();
        final ExtendedFormatRecord xf = this.book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this.book);
    }
    
    public void setRowStyle(final HSSFCellStyle style) {
        this.row.setFormatted(true);
        this.row.setXFIndex(style.getIndex());
    }
    
    @Override
    public void setRowStyle(final CellStyle style) {
        this.setRowStyle((HSSFCellStyle)style);
    }
    
    @Override
    public Iterator<Cell> cellIterator() {
        return new CellIterator();
    }
    
    @Override
    public Iterator<Cell> iterator() {
        return this.cellIterator();
    }
    
    @Override
    public int compareTo(final HSSFRow other) {
        if (this.getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        final int thisRow = this.getRowNum();
        final int otherRow = other.getRowNum();
        return Integer.compare(thisRow, otherRow);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof HSSFRow)) {
            return false;
        }
        final HSSFRow other = (HSSFRow)obj;
        return this.getRowNum() == other.getRowNum() && this.getSheet() == other.getSheet();
    }
    
    @Override
    public int hashCode() {
        return this.row.hashCode();
    }
    
    @Override
    public void shiftCellsRight(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        RowShifter.validateShiftParameters(firstShiftColumnIndex, lastShiftColumnIndex, step);
        if (lastShiftColumnIndex + step + 1 > this.cells.length) {
            this.extend(lastShiftColumnIndex + step + 1);
        }
        for (int columnIndex = lastShiftColumnIndex; columnIndex >= firstShiftColumnIndex; --columnIndex) {
            final HSSFCell cell = this.getCell(columnIndex);
            this.cells[columnIndex + step] = null;
            if (cell != null) {
                this.moveCell(cell, (short)(columnIndex + step));
            }
        }
        for (int columnIndex = firstShiftColumnIndex; columnIndex <= firstShiftColumnIndex + step - 1; ++columnIndex) {
            this.cells[columnIndex] = null;
        }
    }
    
    private void extend(final int newLength) {
        final HSSFCell[] temp = this.cells.clone();
        System.arraycopy(temp, 0, this.cells = new HSSFCell[newLength], 0, temp.length);
    }
    
    @Override
    public void shiftCellsLeft(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        RowShifter.validateShiftLeftParameters(firstShiftColumnIndex, lastShiftColumnIndex, step);
        for (int columnIndex = firstShiftColumnIndex; columnIndex <= lastShiftColumnIndex; ++columnIndex) {
            final HSSFCell cell = this.getCell(columnIndex);
            if (cell != null) {
                this.cells[columnIndex - step] = null;
                this.moveCell(cell, (short)(columnIndex - step));
            }
            else {
                this.cells[columnIndex - step] = null;
            }
        }
        for (int columnIndex = lastShiftColumnIndex - step + 1; columnIndex <= lastShiftColumnIndex; ++columnIndex) {
            this.cells[columnIndex] = null;
        }
    }
    
    static {
        INITIAL_CAPACITY = Configurator.getIntValue("HSSFRow.ColInitialCapacity", 5);
    }
    
    private class CellIterator implements Iterator<Cell>
    {
        int thisId;
        int nextId;
        
        public CellIterator() {
            this.thisId = -1;
            this.nextId = -1;
            this.findNext();
        }
        
        @Override
        public boolean hasNext() {
            return this.nextId < HSSFRow.this.cells.length;
        }
        
        @Override
        public Cell next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("At last element");
            }
            final HSSFCell cell = HSSFRow.this.cells[this.nextId];
            this.thisId = this.nextId;
            this.findNext();
            return cell;
        }
        
        @Override
        public void remove() {
            if (this.thisId == -1) {
                throw new IllegalStateException("remove() called before next()");
            }
            HSSFRow.this.cells[this.thisId] = null;
        }
        
        private void findNext() {
            int i;
            for (i = this.nextId + 1; i < HSSFRow.this.cells.length && HSSFRow.this.cells[i] == null; ++i) {}
            this.nextId = i;
        }
    }
}
