package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.SheetUtil;
import java.util.Set;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.poi.ss.util.PaneInformation;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.PrintSetup;
import java.util.List;
import java.util.Collection;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import java.util.Map;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.poi.util.Internal;
import java.io.IOException;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;

public class SXSSFSheet implements Sheet
{
    final XSSFSheet _sh;
    private final SXSSFWorkbook _workbook;
    private final TreeMap<Integer, SXSSFRow> _rows;
    private final SheetDataWriter _writer;
    private int _randomAccessWindowSize;
    private final AutoSizeColumnTracker _autoSizeColumnTracker;
    private int outlineLevelRow;
    private int lastFlushedRowNumber;
    private boolean allFlushed;
    
    public SXSSFSheet(final SXSSFWorkbook workbook, final XSSFSheet xSheet) throws IOException {
        this._rows = new TreeMap<Integer, SXSSFRow>();
        this._randomAccessWindowSize = 100;
        this.lastFlushedRowNumber = -1;
        this._workbook = workbook;
        this._sh = xSheet;
        this._writer = workbook.createSheetDataWriter();
        this.setRandomAccessWindowSize(this._workbook.getRandomAccessWindowSize());
        this._autoSizeColumnTracker = new AutoSizeColumnTracker((Sheet)this);
    }
    
    @Internal
    SheetDataWriter getSheetDataWriter() {
        return this._writer;
    }
    
    public InputStream getWorksheetXMLInputStream() throws IOException {
        this.flushRows(0);
        this._writer.close();
        return this._writer.getWorksheetXMLInputStream();
    }
    
    public Iterator<Row> iterator() {
        return this.rowIterator();
    }
    
    public SXSSFRow createRow(final int rownum) {
        final int maxrow = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        if (rownum < 0 || rownum > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rownum + ") outside allowable range (0.." + maxrow + ")");
        }
        if (rownum <= this._writer.getLastFlushedRow()) {
            throw new IllegalArgumentException("Attempting to write a row[" + rownum + "] in the range [0," + this._writer.getLastFlushedRow() + "] that is already written to disk.");
        }
        if (this._sh.getPhysicalNumberOfRows() > 0 && rownum <= this._sh.getLastRowNum()) {
            throw new IllegalArgumentException("Attempting to write a row[" + rownum + "] in the range [0," + this._sh.getLastRowNum() + "] that is already written to disk.");
        }
        final SXSSFRow newRow = new SXSSFRow(this);
        this._rows.put(rownum, newRow);
        this.allFlushed = false;
        if (this._randomAccessWindowSize >= 0 && this._rows.size() > this._randomAccessWindowSize) {
            try {
                this.flushRows(this._randomAccessWindowSize);
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return newRow;
    }
    
    public void removeRow(final Row row) {
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        final Iterator<Map.Entry<Integer, SXSSFRow>> iter = this._rows.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Integer, SXSSFRow> entry = iter.next();
            if (entry.getValue() == row) {
                iter.remove();
            }
        }
    }
    
    public SXSSFRow getRow(final int rownum) {
        return this._rows.get(rownum);
    }
    
    public int getPhysicalNumberOfRows() {
        return this._rows.size() + this._writer.getNumberOfFlushedRows();
    }
    
    public int getFirstRowNum() {
        if (this._writer.getNumberOfFlushedRows() > 0) {
            return this._writer.getLowestIndexOfFlushedRows();
        }
        return (this._rows.size() == 0) ? -1 : this._rows.firstKey();
    }
    
    public int getLastRowNum() {
        return (this._rows.size() == 0) ? -1 : this._rows.lastKey();
    }
    
    public void setColumnHidden(final int columnIndex, final boolean hidden) {
        this._sh.setColumnHidden(columnIndex, hidden);
    }
    
    public boolean isColumnHidden(final int columnIndex) {
        return this._sh.isColumnHidden(columnIndex);
    }
    
    public void setColumnWidth(final int columnIndex, final int width) {
        this._sh.setColumnWidth(columnIndex, width);
    }
    
    public int getColumnWidth(final int columnIndex) {
        return this._sh.getColumnWidth(columnIndex);
    }
    
    public float getColumnWidthInPixels(final int columnIndex) {
        return this._sh.getColumnWidthInPixels(columnIndex);
    }
    
    public void setDefaultColumnWidth(final int width) {
        this._sh.setDefaultColumnWidth(width);
    }
    
    public int getDefaultColumnWidth() {
        return this._sh.getDefaultColumnWidth();
    }
    
    public short getDefaultRowHeight() {
        return this._sh.getDefaultRowHeight();
    }
    
    public float getDefaultRowHeightInPoints() {
        return this._sh.getDefaultRowHeightInPoints();
    }
    
    public void setDefaultRowHeight(final short height) {
        this._sh.setDefaultRowHeight(height);
    }
    
    public void setDefaultRowHeightInPoints(final float height) {
        this._sh.setDefaultRowHeightInPoints(height);
    }
    
    public CellStyle getColumnStyle(final int column) {
        return this._sh.getColumnStyle(column);
    }
    
    public int addMergedRegion(final CellRangeAddress region) {
        return this._sh.addMergedRegion(region);
    }
    
    public int addMergedRegionUnsafe(final CellRangeAddress region) {
        return this._sh.addMergedRegionUnsafe(region);
    }
    
    public void validateMergedRegions() {
        this._sh.validateMergedRegions();
    }
    
    public void setVerticallyCenter(final boolean value) {
        this._sh.setVerticallyCenter(value);
    }
    
    public void setHorizontallyCenter(final boolean value) {
        this._sh.setHorizontallyCenter(value);
    }
    
    public boolean getHorizontallyCenter() {
        return this._sh.getHorizontallyCenter();
    }
    
    public boolean getVerticallyCenter() {
        return this._sh.getVerticallyCenter();
    }
    
    public void removeMergedRegion(final int index) {
        this._sh.removeMergedRegion(index);
    }
    
    public void removeMergedRegions(final Collection<Integer> indices) {
        this._sh.removeMergedRegions(indices);
    }
    
    public int getNumMergedRegions() {
        return this._sh.getNumMergedRegions();
    }
    
    public CellRangeAddress getMergedRegion(final int index) {
        return this._sh.getMergedRegion(index);
    }
    
    public List<CellRangeAddress> getMergedRegions() {
        return this._sh.getMergedRegions();
    }
    
    public Iterator<Row> rowIterator() {
        final Iterator<Row> result = (Iterator<Row>)this._rows.values().iterator();
        return result;
    }
    
    public void setAutobreaks(final boolean value) {
        this._sh.setAutobreaks(value);
    }
    
    public void setDisplayGuts(final boolean value) {
        this._sh.setDisplayGuts(value);
    }
    
    public void setDisplayZeros(final boolean value) {
        this._sh.setDisplayZeros(value);
    }
    
    public boolean isDisplayZeros() {
        return this._sh.isDisplayZeros();
    }
    
    public void setRightToLeft(final boolean value) {
        this._sh.setRightToLeft(value);
    }
    
    public boolean isRightToLeft() {
        return this._sh.isRightToLeft();
    }
    
    public void setFitToPage(final boolean value) {
        this._sh.setFitToPage(value);
    }
    
    public void setRowSumsBelow(final boolean value) {
        this._sh.setRowSumsBelow(value);
    }
    
    public void setRowSumsRight(final boolean value) {
        this._sh.setRowSumsRight(value);
    }
    
    public boolean getAutobreaks() {
        return this._sh.getAutobreaks();
    }
    
    public boolean getDisplayGuts() {
        return this._sh.getDisplayGuts();
    }
    
    public boolean getFitToPage() {
        return this._sh.getFitToPage();
    }
    
    public boolean getRowSumsBelow() {
        return this._sh.getRowSumsBelow();
    }
    
    public boolean getRowSumsRight() {
        return this._sh.getRowSumsRight();
    }
    
    public boolean isPrintGridlines() {
        return this._sh.isPrintGridlines();
    }
    
    public void setPrintGridlines(final boolean show) {
        this._sh.setPrintGridlines(show);
    }
    
    public boolean isPrintRowAndColumnHeadings() {
        return this._sh.isPrintRowAndColumnHeadings();
    }
    
    public void setPrintRowAndColumnHeadings(final boolean show) {
        this._sh.setPrintRowAndColumnHeadings(show);
    }
    
    public PrintSetup getPrintSetup() {
        return (PrintSetup)this._sh.getPrintSetup();
    }
    
    public Header getHeader() {
        return this._sh.getHeader();
    }
    
    public Footer getFooter() {
        return this._sh.getFooter();
    }
    
    public void setSelected(final boolean value) {
        this._sh.setSelected(value);
    }
    
    public double getMargin(final short margin) {
        return this._sh.getMargin(margin);
    }
    
    public void setMargin(final short margin, final double size) {
        this._sh.setMargin(margin, size);
    }
    
    public boolean getProtect() {
        return this._sh.getProtect();
    }
    
    public void protectSheet(final String password) {
        this._sh.protectSheet(password);
    }
    
    public boolean getScenarioProtect() {
        return this._sh.getScenarioProtect();
    }
    
    public void setZoom(final int scale) {
        this._sh.setZoom(scale);
    }
    
    public short getTopRow() {
        return this._sh.getTopRow();
    }
    
    public short getLeftCol() {
        return this._sh.getLeftCol();
    }
    
    public void showInPane(final int topRow, final int leftCol) {
        this._sh.showInPane(topRow, leftCol);
    }
    
    public void setForceFormulaRecalculation(final boolean value) {
        this._sh.setForceFormulaRecalculation(value);
    }
    
    public boolean getForceFormulaRecalculation() {
        return this._sh.getForceFormulaRecalculation();
    }
    
    @NotImplemented
    public void shiftRows(final int startRow, final int endRow, final int n) {
        throw new RuntimeException("Not Implemented");
    }
    
    @NotImplemented
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight) {
        throw new RuntimeException("Not Implemented");
    }
    
    public void createFreezePane(final int colSplit, final int rowSplit, final int leftmostColumn, final int topRow) {
        this._sh.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
    }
    
    public void createFreezePane(final int colSplit, final int rowSplit) {
        this._sh.createFreezePane(colSplit, rowSplit);
    }
    
    public void createSplitPane(final int xSplitPos, final int ySplitPos, final int leftmostColumn, final int topRow, final int activePane) {
        this._sh.createSplitPane(xSplitPos, ySplitPos, leftmostColumn, topRow, activePane);
    }
    
    public PaneInformation getPaneInformation() {
        return this._sh.getPaneInformation();
    }
    
    public void setDisplayGridlines(final boolean show) {
        this._sh.setDisplayGridlines(show);
    }
    
    public boolean isDisplayGridlines() {
        return this._sh.isDisplayGridlines();
    }
    
    public void setDisplayFormulas(final boolean show) {
        this._sh.setDisplayFormulas(show);
    }
    
    public boolean isDisplayFormulas() {
        return this._sh.isDisplayFormulas();
    }
    
    public void setDisplayRowColHeadings(final boolean show) {
        this._sh.setDisplayRowColHeadings(show);
    }
    
    public boolean isDisplayRowColHeadings() {
        return this._sh.isDisplayRowColHeadings();
    }
    
    public void setRowBreak(final int row) {
        this._sh.setRowBreak(row);
    }
    
    public boolean isRowBroken(final int row) {
        return this._sh.isRowBroken(row);
    }
    
    public void removeRowBreak(final int row) {
        this._sh.removeRowBreak(row);
    }
    
    public int[] getRowBreaks() {
        return this._sh.getRowBreaks();
    }
    
    public int[] getColumnBreaks() {
        return this._sh.getColumnBreaks();
    }
    
    public void setColumnBreak(final int column) {
        this._sh.setColumnBreak(column);
    }
    
    public boolean isColumnBroken(final int column) {
        return this._sh.isColumnBroken(column);
    }
    
    public void removeColumnBreak(final int column) {
        this._sh.removeColumnBreak(column);
    }
    
    public void setColumnGroupCollapsed(final int columnNumber, final boolean collapsed) {
        this._sh.setColumnGroupCollapsed(columnNumber, collapsed);
    }
    
    public void groupColumn(final int fromColumn, final int toColumn) {
        this._sh.groupColumn(fromColumn, toColumn);
    }
    
    public void ungroupColumn(final int fromColumn, final int toColumn) {
        this._sh.ungroupColumn(fromColumn, toColumn);
    }
    
    public void groupRow(final int fromRow, final int toRow) {
        for (final SXSSFRow row : this._rows.subMap(fromRow, toRow + 1).values()) {
            final int level = row.getOutlineLevel() + 1;
            row.setOutlineLevel(level);
            if (level > this.outlineLevelRow) {
                this.outlineLevelRow = level;
            }
        }
        this.setWorksheetOutlineLevelRow();
    }
    
    public void setRowOutlineLevel(final int rownum, final int level) {
        final SXSSFRow row = this._rows.get(rownum);
        row.setOutlineLevel(level);
        if (level > 0 && level > this.outlineLevelRow) {
            this.outlineLevelRow = level;
            this.setWorksheetOutlineLevelRow();
        }
    }
    
    private void setWorksheetOutlineLevelRow() {
        final CTWorksheet ct = this._sh.getCTWorksheet();
        final CTSheetFormatPr pr = ct.isSetSheetFormatPr() ? ct.getSheetFormatPr() : ct.addNewSheetFormatPr();
        if (this.outlineLevelRow > 0) {
            pr.setOutlineLevelRow((short)this.outlineLevelRow);
        }
    }
    
    public void ungroupRow(final int fromRow, final int toRow) {
        this._sh.ungroupRow(fromRow, toRow);
    }
    
    public void setRowGroupCollapsed(final int row, final boolean collapse) {
        if (collapse) {
            this.collapseRow(row);
            return;
        }
        throw new RuntimeException("Unable to expand row: Not Implemented");
    }
    
    private void collapseRow(final int rowIndex) {
        final SXSSFRow row = this.getRow(rowIndex);
        if (row == null) {
            throw new IllegalArgumentException("Invalid row number(" + rowIndex + "). Row does not exist.");
        }
        final int startRow = this.findStartOfRowOutlineGroup(rowIndex);
        final int lastRow = this.writeHidden(row, startRow);
        final SXSSFRow lastRowObj = this.getRow(lastRow);
        if (lastRowObj != null) {
            lastRowObj.setCollapsed(true);
        }
        else {
            final SXSSFRow newRow = this.createRow(lastRow);
            newRow.setCollapsed(true);
        }
    }
    
    private int findStartOfRowOutlineGroup(final int rowIndex) {
        final Row row = (Row)this.getRow(rowIndex);
        final int level = row.getOutlineLevel();
        if (level == 0) {
            throw new IllegalArgumentException("Outline level is zero for the row (" + rowIndex + ").");
        }
        int currentRow;
        for (currentRow = rowIndex; this.getRow(currentRow) != null; --currentRow) {
            if (this.getRow(currentRow).getOutlineLevel() < level) {
                return currentRow + 1;
            }
        }
        return currentRow + 1;
    }
    
    private int writeHidden(final SXSSFRow xRow, int rowIndex) {
        final int level = xRow.getOutlineLevel();
        for (SXSSFRow currRow = this.getRow(rowIndex); currRow != null && currRow.getOutlineLevel() >= level; currRow = this.getRow(rowIndex)) {
            currRow.setHidden(true);
            ++rowIndex;
        }
        return rowIndex;
    }
    
    public void setDefaultColumnStyle(final int column, final CellStyle style) {
        this._sh.setDefaultColumnStyle(column, style);
    }
    
    public void trackColumnForAutoSizing(final int column) {
        this._autoSizeColumnTracker.trackColumn(column);
    }
    
    public void trackColumnsForAutoSizing(final Collection<Integer> columns) {
        this._autoSizeColumnTracker.trackColumns(columns);
    }
    
    public void trackAllColumnsForAutoSizing() {
        this._autoSizeColumnTracker.trackAllColumns();
    }
    
    public boolean untrackColumnForAutoSizing(final int column) {
        return this._autoSizeColumnTracker.untrackColumn(column);
    }
    
    public boolean untrackColumnsForAutoSizing(final Collection<Integer> columns) {
        return this._autoSizeColumnTracker.untrackColumns(columns);
    }
    
    public void untrackAllColumnsForAutoSizing() {
        this._autoSizeColumnTracker.untrackAllColumns();
    }
    
    public boolean isColumnTrackedForAutoSizing(final int column) {
        return this._autoSizeColumnTracker.isColumnTracked(column);
    }
    
    public Set<Integer> getTrackedColumnsForAutoSizing() {
        return this._autoSizeColumnTracker.getTrackedColumns();
    }
    
    public void autoSizeColumn(final int column) {
        this.autoSizeColumn(column, false);
    }
    
    public void autoSizeColumn(final int column, final boolean useMergedCells) {
        int flushedWidth;
        try {
            flushedWidth = this._autoSizeColumnTracker.getBestFitColumnWidth(column, useMergedCells);
        }
        catch (final IllegalStateException e) {
            throw new IllegalStateException("Could not auto-size column. Make sure the column was tracked prior to auto-sizing the column.", e);
        }
        final int activeWidth = (int)(256.0 * SheetUtil.getColumnWidth((Sheet)this, column, useMergedCells));
        final int bestFitWidth = Math.max(flushedWidth, activeWidth);
        if (bestFitWidth > 0) {
            final int maxColumnWidth = 65280;
            final int width = Math.min(bestFitWidth, 65280);
            this.setColumnWidth(column, width);
        }
    }
    
    public XSSFComment getCellComment(final CellAddress ref) {
        return this._sh.getCellComment(ref);
    }
    
    public Map<CellAddress, XSSFComment> getCellComments() {
        return this._sh.getCellComments();
    }
    
    public XSSFHyperlink getHyperlink(final int row, final int column) {
        return this._sh.getHyperlink(row, column);
    }
    
    public XSSFHyperlink getHyperlink(final CellAddress addr) {
        return this._sh.getHyperlink(addr);
    }
    
    public List<XSSFHyperlink> getHyperlinkList() {
        return this._sh.getHyperlinkList();
    }
    
    public XSSFDrawing getDrawingPatriarch() {
        return this._sh.getDrawingPatriarch();
    }
    
    public SXSSFDrawing createDrawingPatriarch() {
        return new SXSSFDrawing(this.getWorkbook(), this._sh.createDrawingPatriarch());
    }
    
    public SXSSFWorkbook getWorkbook() {
        return this._workbook;
    }
    
    public String getSheetName() {
        return this._sh.getSheetName();
    }
    
    public boolean isSelected() {
        return this._sh.isSelected();
    }
    
    public CellRange<? extends Cell> setArrayFormula(final String formula, final CellRangeAddress range) {
        throw new RuntimeException("Not Implemented");
    }
    
    public CellRange<? extends Cell> removeArrayFormula(final Cell cell) {
        throw new RuntimeException("Not Implemented");
    }
    
    public DataValidationHelper getDataValidationHelper() {
        return this._sh.getDataValidationHelper();
    }
    
    public List<XSSFDataValidation> getDataValidations() {
        return this._sh.getDataValidations();
    }
    
    public void addValidationData(final DataValidation dataValidation) {
        this._sh.addValidationData(dataValidation);
    }
    
    public AutoFilter setAutoFilter(final CellRangeAddress range) {
        return (AutoFilter)this._sh.setAutoFilter(range);
    }
    
    public SheetConditionalFormatting getSheetConditionalFormatting() {
        return (SheetConditionalFormatting)this._sh.getSheetConditionalFormatting();
    }
    
    public CellRangeAddress getRepeatingRows() {
        return this._sh.getRepeatingRows();
    }
    
    public CellRangeAddress getRepeatingColumns() {
        return this._sh.getRepeatingColumns();
    }
    
    public void setRepeatingRows(final CellRangeAddress rowRangeRef) {
        this._sh.setRepeatingRows(rowRangeRef);
    }
    
    public void setRepeatingColumns(final CellRangeAddress columnRangeRef) {
        this._sh.setRepeatingColumns(columnRangeRef);
    }
    
    public void setRandomAccessWindowSize(final int value) {
        if (value == 0 || value < -1) {
            throw new IllegalArgumentException("RandomAccessWindowSize must be either -1 or a positive integer");
        }
        this._randomAccessWindowSize = value;
    }
    
    public boolean areAllRowsFlushed() {
        return this.allFlushed;
    }
    
    public int getLastFlushedRowNum() {
        return this.lastFlushedRowNumber;
    }
    
    public void flushRows(final int remaining) throws IOException {
        while (this._rows.size() > remaining) {
            this.flushOneRow();
        }
        if (remaining == 0) {
            this.allFlushed = true;
        }
    }
    
    public void flushRows() throws IOException {
        this.flushRows(0);
    }
    
    private void flushOneRow() throws IOException {
        final Integer firstRowNum = this._rows.firstKey();
        if (firstRowNum != null) {
            final int rowIndex = firstRowNum;
            final SXSSFRow row = this._rows.get(firstRowNum);
            this._autoSizeColumnTracker.updateColumnWidths((Row)row);
            this._writer.writeRow(rowIndex, row);
            this._rows.remove(firstRowNum);
            this.lastFlushedRowNumber = rowIndex;
        }
    }
    
    public void changeRowNum(final SXSSFRow row, final int newRowNum) {
        this.removeRow((Row)row);
        this._rows.put(newRowNum, row);
    }
    
    public int getRowNum(final SXSSFRow row) {
        for (final Map.Entry<Integer, SXSSFRow> entry : this._rows.entrySet()) {
            if (entry.getValue() == row) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    boolean dispose() throws IOException {
        if (!this.allFlushed) {
            this.flushRows();
        }
        return this._writer.dispose();
    }
    
    public int getColumnOutlineLevel(final int columnIndex) {
        return this._sh.getColumnOutlineLevel(columnIndex);
    }
    
    public CellAddress getActiveCell() {
        return this._sh.getActiveCell();
    }
    
    public void setActiveCell(final CellAddress address) {
        this._sh.setActiveCell(address);
    }
    
    public XSSFColor getTabColor() {
        return this._sh.getTabColor();
    }
    
    public void setTabColor(final XSSFColor color) {
        this._sh.setTabColor(color);
    }
    
    public void enableLocking() {
        this.safeGetProtectionField().setSheet(true);
    }
    
    public void disableLocking() {
        this.safeGetProtectionField().setSheet(false);
    }
    
    public void lockAutoFilter(final boolean enabled) {
        this.safeGetProtectionField().setAutoFilter(enabled);
    }
    
    public void lockDeleteColumns(final boolean enabled) {
        this.safeGetProtectionField().setDeleteColumns(enabled);
    }
    
    public void lockDeleteRows(final boolean enabled) {
        this.safeGetProtectionField().setDeleteRows(enabled);
    }
    
    public void lockFormatCells(final boolean enabled) {
        this.safeGetProtectionField().setFormatCells(enabled);
    }
    
    public void lockFormatColumns(final boolean enabled) {
        this.safeGetProtectionField().setFormatColumns(enabled);
    }
    
    public void lockFormatRows(final boolean enabled) {
        this.safeGetProtectionField().setFormatRows(enabled);
    }
    
    public void lockInsertColumns(final boolean enabled) {
        this.safeGetProtectionField().setInsertColumns(enabled);
    }
    
    public void lockInsertHyperlinks(final boolean enabled) {
        this.safeGetProtectionField().setInsertHyperlinks(enabled);
    }
    
    public void lockInsertRows(final boolean enabled) {
        this.safeGetProtectionField().setInsertRows(enabled);
    }
    
    public void lockPivotTables(final boolean enabled) {
        this.safeGetProtectionField().setPivotTables(enabled);
    }
    
    public void lockSort(final boolean enabled) {
        this.safeGetProtectionField().setSort(enabled);
    }
    
    public void lockObjects(final boolean enabled) {
        this.safeGetProtectionField().setObjects(enabled);
    }
    
    public void lockScenarios(final boolean enabled) {
        this.safeGetProtectionField().setScenarios(enabled);
    }
    
    public void lockSelectLockedCells(final boolean enabled) {
        this.safeGetProtectionField().setSelectLockedCells(enabled);
    }
    
    public void lockSelectUnlockedCells(final boolean enabled) {
        this.safeGetProtectionField().setSelectUnlockedCells(enabled);
    }
    
    private CTSheetProtection safeGetProtectionField() {
        final CTWorksheet ct = this._sh.getCTWorksheet();
        if (!this.isSheetProtectionEnabled()) {
            return ct.addNewSheetProtection();
        }
        return ct.getSheetProtection();
    }
    
    boolean isSheetProtectionEnabled() {
        final CTWorksheet ct = this._sh.getCTWorksheet();
        return ct.isSetSheetProtection();
    }
    
    public void setTabColor(final int colorIndex) {
        final CTWorksheet ct = this._sh.getCTWorksheet();
        CTSheetPr pr = ct.getSheetPr();
        if (pr == null) {
            pr = ct.addNewSheetPr();
        }
        final CTColor color = CTColor.Factory.newInstance();
        color.setIndexed((long)colorIndex);
        pr.setTabColor(color);
    }
    
    @NotImplemented
    public void shiftColumns(final int startColumn, final int endColumn, final int n) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
