package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.helpers.RowShifter;
import java.util.Set;
import org.apache.poi.xssf.usermodel.helpers.XSSFRowShifter;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Collection;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.HashSet;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import java.util.Objects;
import java.util.IdentityHashMap;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import java.util.Iterator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import java.util.TreeMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.apache.poi.ss.usermodel.Row;

public class XSSFRow implements Row, Comparable<XSSFRow>
{
    private final CTRow _row;
    private final TreeMap<Integer, XSSFCell> _cells;
    private final XSSFSheet _sheet;
    
    protected XSSFRow(final CTRow row, final XSSFSheet sheet) {
        this._row = row;
        this._sheet = sheet;
        this._cells = new TreeMap<Integer, XSSFCell>();
        for (final CTCell c : row.getCArray()) {
            final XSSFCell cell = new XSSFCell(this, c);
            final Integer colI = cell.getColumnIndex();
            this._cells.put(colI, cell);
            sheet.onReadCell(cell);
        }
        if (!row.isSetR()) {
            int nextRowNum = sheet.getLastRowNum() + 2;
            if (nextRowNum == 2 && sheet.getPhysicalNumberOfRows() == 0) {
                nextRowNum = 1;
            }
            row.setR((long)nextRowNum);
        }
    }
    
    public XSSFSheet getSheet() {
        return this._sheet;
    }
    
    public Iterator<Cell> cellIterator() {
        return (Iterator<Cell>)this._cells.values().iterator();
    }
    
    public Iterator<Cell> iterator() {
        return this.cellIterator();
    }
    
    public int compareTo(final XSSFRow other) {
        if (this.getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        final int thisRow = this.getRowNum();
        final int otherRow = other.getRowNum();
        return Integer.compare(thisRow, otherRow);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof XSSFRow)) {
            return false;
        }
        final XSSFRow other = (XSSFRow)obj;
        return this.getRowNum() == other.getRowNum() && this.getSheet() == other.getSheet();
    }
    
    @Override
    public int hashCode() {
        return this._row.hashCode();
    }
    
    public XSSFCell createCell(final int columnIndex) {
        return this.createCell(columnIndex, CellType.BLANK);
    }
    
    public XSSFCell createCell(final int columnIndex, final CellType type) {
        final Integer colI = columnIndex;
        final XSSFCell prev = this._cells.get(colI);
        CTCell ctCell;
        if (prev != null) {
            ctCell = prev.getCTCell();
            ctCell.set((XmlObject)CTCell.Factory.newInstance());
        }
        else {
            ctCell = this._row.addNewC();
        }
        final XSSFCell xcell = new XSSFCell(this, ctCell);
        try {
            xcell.setCellNum(columnIndex);
        }
        catch (final IllegalArgumentException e) {
            this._row.removeC(this._row.getCList().size() - 1);
            throw e;
        }
        if (type != CellType.BLANK && type != CellType.FORMULA) {
            setDefaultValue(xcell, type);
        }
        this._cells.put(colI, xcell);
        return xcell;
    }
    
    private static void setDefaultValue(final XSSFCell cell, final CellType type) {
        switch (type) {
            case NUMERIC: {
                cell.setCellValue(0.0);
                break;
            }
            case STRING: {
                cell.setCellValue("");
                break;
            }
            case BOOLEAN: {
                cell.setCellValue(false);
                break;
            }
            case ERROR: {
                cell.setCellErrorValue(FormulaError._NO_ERROR);
                break;
            }
            default: {
                throw new AssertionError((Object)("Unknown cell-type specified: " + type));
            }
        }
    }
    
    public XSSFCell getCell(final int cellnum) {
        return this.getCell(cellnum, this._sheet.getWorkbook().getMissingCellPolicy());
    }
    
    public XSSFCell getCell(final int cellnum, final Row.MissingCellPolicy policy) {
        if (cellnum < 0) {
            throw new IllegalArgumentException("Cell index must be >= 0");
        }
        final Integer colI = cellnum;
        final XSSFCell cell = this._cells.get(colI);
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
        return (short)((this._cells.size() == 0) ? -1 : this._cells.firstKey());
    }
    
    public short getLastCellNum() {
        return (short)((this._cells.size() == 0) ? -1 : (this._cells.lastKey() + 1));
    }
    
    public short getHeight() {
        return (short)(this.getHeightInPoints() * 20.0f);
    }
    
    public float getHeightInPoints() {
        if (this._row.isSetHt()) {
            return (float)this._row.getHt();
        }
        return this._sheet.getDefaultRowHeightInPoints();
    }
    
    public void setHeight(final short height) {
        if (height == -1) {
            if (this._row.isSetHt()) {
                this._row.unsetHt();
            }
            if (this._row.isSetCustomHeight()) {
                this._row.unsetCustomHeight();
            }
        }
        else {
            this._row.setHt(height / 20.0);
            this._row.setCustomHeight(true);
        }
    }
    
    public void setHeightInPoints(final float height) {
        this.setHeight((short)((height == -1.0f) ? -1.0f : (height * 20.0f)));
    }
    
    public int getPhysicalNumberOfCells() {
        return this._cells.size();
    }
    
    public int getRowNum() {
        return Math.toIntExact(this._row.getR() - 1L);
    }
    
    public void setRowNum(final int rowIndex) {
        final int maxrow = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex + ") outside allowable range (0.." + maxrow + ")");
        }
        this._row.setR((long)(rowIndex + 1));
    }
    
    public boolean getZeroHeight() {
        return this._row.getHidden();
    }
    
    public void setZeroHeight(final boolean height) {
        this._row.setHidden(height);
    }
    
    public boolean isFormatted() {
        return this._row.isSetS();
    }
    
    public XSSFCellStyle getRowStyle() {
        if (!this.isFormatted()) {
            return null;
        }
        final StylesTable stylesSource = this.getSheet().getWorkbook().getStylesSource();
        if (stylesSource.getNumCellStyles() > 0) {
            return stylesSource.getStyleAt(Math.toIntExact(this._row.getS()));
        }
        return null;
    }
    
    public void setRowStyle(final CellStyle style) {
        if (style == null) {
            if (this._row.isSetS()) {
                this._row.unsetS();
                this._row.unsetCustomFormat();
            }
        }
        else {
            final StylesTable styleSource = this.getSheet().getWorkbook().getStylesSource();
            final XSSFCellStyle xStyle = (XSSFCellStyle)style;
            xStyle.verifyBelongsToStylesSource(styleSource);
            final long idx = styleSource.putStyle(xStyle);
            this._row.setS(idx);
            this._row.setCustomFormat(true);
        }
    }
    
    public void removeCell(final Cell cell) {
        if (cell.getRow() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this row");
        }
        if (!this._cells.containsValue(cell)) {
            throw new IllegalArgumentException("the row does not contain this cell");
        }
        final XSSFCell xcell = (XSSFCell)cell;
        if (xcell.isPartOfArrayFormulaGroup()) {
            xcell.setCellFormula((String)null);
        }
        if (cell.getCellType() == CellType.FORMULA) {
            this._sheet.getWorkbook().onDeleteFormula(xcell);
        }
        final Integer colI = cell.getColumnIndex();
        final XSSFCell removed = this._cells.remove(colI);
        int i = 0;
        for (final CTCell ctCell : this._row.getCArray()) {
            if (ctCell == removed.getCTCell()) {
                this._row.removeC(i);
            }
            ++i;
        }
    }
    
    @Internal
    public CTRow getCTRow() {
        return this._row;
    }
    
    protected void onDocumentWrite() {
        final CTCell[] cArrayOrig = this._row.getCArray();
        if (cArrayOrig.length == this._cells.size()) {
            boolean allEqual = true;
            final Iterator<XSSFCell> it = this._cells.values().iterator();
            for (final CTCell ctCell : cArrayOrig) {
                final XSSFCell cell = it.next();
                if (ctCell != cell.getCTCell()) {
                    allEqual = false;
                    break;
                }
            }
            if (allEqual) {
                return;
            }
        }
        this.fixupCTCells(cArrayOrig);
    }
    
    private void fixupCTCells(final CTCell[] cArrayOrig) {
        final CTCell[] cArrayCopy = new CTCell[cArrayOrig.length];
        final IdentityHashMap<CTCell, Integer> map = new IdentityHashMap<CTCell, Integer>(this._cells.size());
        int i = 0;
        for (final CTCell ctCell : cArrayOrig) {
            cArrayCopy[i] = (CTCell)ctCell.copy();
            map.put(ctCell, i);
            ++i;
        }
        i = 0;
        for (final XSSFCell cell : this._cells.values()) {
            final Integer correctPosition = map.get(cell.getCTCell());
            Objects.requireNonNull(correctPosition, "Should find CTCell in _row");
            if (correctPosition != i) {
                this._row.setCArray(i, cArrayCopy[correctPosition]);
                cell.setCTCell(this._row.getCArray(i));
            }
            ++i;
        }
        while (cArrayOrig.length > this._cells.size()) {
            this._row.removeC(this._cells.size());
        }
    }
    
    @Override
    public String toString() {
        return this._row.toString();
    }
    
    protected void shift(final int n) {
        final int rownum = this.getRowNum() + n;
        final String msg = "Row[rownum=" + this.getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
        this.setRowNum(rownum);
        for (final Cell c : this) {
            ((XSSFCell)c).updateCellReferencesForShifting(msg);
        }
    }
    
    public void copyRowFrom(final Row srcRow, final CellCopyPolicy policy) {
        if (srcRow == null) {
            for (final Cell destCell : this) {
                final XSSFCell srcCell = null;
                ((XSSFCell)destCell).copyCellFrom((Cell)srcCell, policy);
            }
            if (policy.isCopyMergedRegions()) {
                final int destRowNum = this.getRowNum();
                int index = 0;
                final Set<Integer> indices = new HashSet<Integer>();
                for (final CellRangeAddress destRegion : this.getSheet().getMergedRegions()) {
                    if (destRowNum == destRegion.getFirstRow() && destRowNum == destRegion.getLastRow()) {
                        indices.add(index);
                    }
                    ++index;
                }
                this.getSheet().removeMergedRegions(indices);
            }
            if (policy.isCopyRowHeight()) {
                this.setHeight((short)(-1));
            }
        }
        else {
            for (final Cell c : srcRow) {
                final XSSFCell srcCell = (XSSFCell)c;
                final XSSFCell destCell2 = this.createCell(srcCell.getColumnIndex());
                destCell2.copyCellFrom((Cell)srcCell, policy);
            }
            final int sheetIndex = this._sheet.getWorkbook().getSheetIndex((Sheet)this._sheet);
            final String sheetName = this._sheet.getWorkbook().getSheetName(sheetIndex);
            final int srcRowNum = srcRow.getRowNum();
            final int destRowNum2 = this.getRowNum();
            final int rowDifference = destRowNum2 - srcRowNum;
            final FormulaShifter formulaShifter = FormulaShifter.createForRowCopy(sheetIndex, sheetName, srcRowNum, srcRowNum, rowDifference, SpreadsheetVersion.EXCEL2007);
            final XSSFRowShifter rowShifter = new XSSFRowShifter(this._sheet);
            rowShifter.updateRowFormulas(this, formulaShifter);
            if (policy.isCopyMergedRegions()) {
                for (final CellRangeAddress srcRegion : srcRow.getSheet().getMergedRegions()) {
                    if (srcRowNum == srcRegion.getFirstRow() && srcRowNum == srcRegion.getLastRow()) {
                        final CellRangeAddress destRegion2 = srcRegion.copy();
                        destRegion2.setFirstRow(destRowNum2);
                        destRegion2.setLastRow(destRowNum2);
                        this.getSheet().addMergedRegion(destRegion2);
                    }
                }
            }
            if (policy.isCopyRowHeight()) {
                this.setHeight(srcRow.getHeight());
            }
        }
    }
    
    public int getOutlineLevel() {
        return this._row.getOutlineLevel();
    }
    
    public void shiftCellsRight(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        RowShifter.validateShiftParameters(firstShiftColumnIndex, lastShiftColumnIndex, step);
        for (int columnIndex = lastShiftColumnIndex; columnIndex >= firstShiftColumnIndex; --columnIndex) {
            this.shiftCell(columnIndex, step);
        }
        for (int columnIndex = firstShiftColumnIndex; columnIndex <= firstShiftColumnIndex + step - 1; ++columnIndex) {
            this._cells.remove(columnIndex);
            final XSSFCell targetCell = this.getCell(columnIndex);
            if (targetCell != null) {
                targetCell.getCTCell().set((XmlObject)CTCell.Factory.newInstance());
            }
        }
    }
    
    public void shiftCellsLeft(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        RowShifter.validateShiftLeftParameters(firstShiftColumnIndex, lastShiftColumnIndex, step);
        for (int columnIndex = firstShiftColumnIndex; columnIndex <= lastShiftColumnIndex; ++columnIndex) {
            this.shiftCell(columnIndex, -step);
        }
        for (int columnIndex = lastShiftColumnIndex - step + 1; columnIndex <= lastShiftColumnIndex; ++columnIndex) {
            this._cells.remove(columnIndex);
            final XSSFCell targetCell = this.getCell(columnIndex);
            if (targetCell != null) {
                targetCell.getCTCell().set((XmlObject)CTCell.Factory.newInstance());
            }
        }
    }
    
    private void shiftCell(final int columnIndex, final int step) {
        if (columnIndex + step < 0) {
            throw new IllegalStateException("Column index less than zero : " + (Object)(columnIndex + step));
        }
        final XSSFCell currentCell = this.getCell(columnIndex);
        if (currentCell != null) {
            currentCell.setCellNum(columnIndex + step);
            this._cells.put(columnIndex + step, currentCell);
        }
        else {
            this._cells.remove(columnIndex + step);
            final XSSFCell targetCell = this.getCell(columnIndex + step);
            if (targetCell != null) {
                targetCell.getCTCell().set((XmlObject)CTCell.Factory.newInstance());
            }
        }
    }
}
