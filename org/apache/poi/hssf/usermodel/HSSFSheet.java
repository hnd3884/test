package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.Configurator;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.usermodel.Name;
import java.util.Map;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.util.SSCellRange;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import java.io.PrintWriter;
import org.apache.poi.ss.util.PaneInformation;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.usermodel.helpers.HSSFColumnShifter;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.helpers.RowShifter;
import org.apache.poi.hssf.usermodel.helpers.HSSFRowShifter;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.util.Internal;
import java.util.TreeSet;
import java.util.Collection;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.util.TreeMap;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Sheet;

public final class HSSFSheet implements Sheet
{
    private static final POILogger log;
    private static final int DEBUG = 1;
    private static final float PX_DEFAULT = 32.0f;
    private static final float PX_MODIFIED = 36.56f;
    public static final int INITIAL_CAPACITY;
    private final InternalSheet _sheet;
    private final TreeMap<Integer, HSSFRow> _rows;
    protected final InternalWorkbook _book;
    protected final HSSFWorkbook _workbook;
    private HSSFPatriarch _patriarch;
    private int _firstrow;
    private int _lastrow;
    
    protected HSSFSheet(final HSSFWorkbook workbook) {
        this._firstrow = -1;
        this._lastrow = -1;
        this._sheet = InternalSheet.createSheet();
        this._rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
    }
    
    protected HSSFSheet(final HSSFWorkbook workbook, final InternalSheet sheet) {
        this._firstrow = -1;
        this._lastrow = -1;
        this._sheet = sheet;
        this._rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
        this.setPropertiesFromSheet(sheet);
    }
    
    HSSFSheet cloneSheet(final HSSFWorkbook workbook) {
        this.getDrawingPatriarch();
        final HSSFSheet sheet = new HSSFSheet(workbook, this._sheet.cloneSheet());
        final int pos = sheet._sheet.findFirstRecordLocBySid((short)236);
        final DrawingRecord dr = (DrawingRecord)sheet._sheet.findFirstRecordBySid((short)236);
        if (null != dr) {
            sheet._sheet.getRecords().remove(dr);
        }
        if (this.getDrawingPatriarch() != null) {
            final HSSFPatriarch patr = HSSFPatriarch.createPatriarch(this.getDrawingPatriarch(), sheet);
            sheet._sheet.getRecords().add(pos, patr.getBoundAggregate());
            sheet._patriarch = patr;
        }
        return sheet;
    }
    
    protected void preSerialize() {
        if (this._patriarch != null) {
            this._patriarch.preSerialize();
        }
    }
    
    @Override
    public HSSFWorkbook getWorkbook() {
        return this._workbook;
    }
    
    private void setPropertiesFromSheet(final InternalSheet sheet) {
        for (RowRecord row = sheet.getNextRow(); row != null; row = sheet.getNextRow()) {
            this.createRowFromRecord(row);
        }
        final Iterator<CellValueRecordInterface> iter = sheet.getCellValueIterator();
        final long timestart = System.currentTimeMillis();
        if (HSSFSheet.log.check(1)) {
            HSSFSheet.log.log(1, "Time at start of cell creating in HSSF sheet = ", timestart);
        }
        HSSFRow lastrow = null;
        while (iter.hasNext()) {
            final CellValueRecordInterface cval = iter.next();
            final long cellstart = System.currentTimeMillis();
            HSSFRow hrow = lastrow;
            if (hrow == null || hrow.getRowNum() != cval.getRow()) {
                hrow = this.getRow(cval.getRow());
                if ((lastrow = hrow) == null) {
                    final RowRecord rowRec = new RowRecord(cval.getRow());
                    sheet.addRow(rowRec);
                    hrow = this.createRowFromRecord(rowRec);
                }
            }
            if (HSSFSheet.log.check(1)) {
                if (cval instanceof Record) {
                    HSSFSheet.log.log(1, "record id = " + Integer.toHexString(((Record)cval).getSid()));
                }
                else {
                    HSSFSheet.log.log(1, "record = " + cval);
                }
            }
            hrow.createCellFromRecord(cval);
            if (HSSFSheet.log.check(1)) {
                HSSFSheet.log.log(1, "record took ", System.currentTimeMillis() - cellstart);
            }
        }
        if (HSSFSheet.log.check(1)) {
            HSSFSheet.log.log(1, "total sheet cell creation took ", System.currentTimeMillis() - timestart);
        }
    }
    
    @Override
    public HSSFRow createRow(final int rownum) {
        final HSSFRow row = new HSSFRow(this._workbook, this, rownum);
        row.setHeight(this.getDefaultRowHeight());
        row.getRowRecord().setBadFontHeight(false);
        this.addRow(row, true);
        return row;
    }
    
    private HSSFRow createRowFromRecord(final RowRecord row) {
        final HSSFRow hrow = new HSSFRow(this._workbook, this, row);
        this.addRow(hrow, false);
        return hrow;
    }
    
    @Override
    public void removeRow(final Row row) {
        final HSSFRow hrow = (HSSFRow)row;
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        for (final Cell cell : row) {
            final HSSFCell xcell = (HSSFCell)cell;
            if (xcell.isPartOfArrayFormulaGroup()) {
                final String msg = "Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
                xcell.tryToDeleteArrayFormula(msg);
            }
        }
        if (this._rows.size() > 0) {
            final Integer key = row.getRowNum();
            final HSSFRow removedRow = this._rows.remove(key);
            if (removedRow != row) {
                throw new IllegalArgumentException("Specified row does not belong to this sheet");
            }
            if (hrow.getRowNum() == this.getLastRowNum()) {
                this._lastrow = this.findLastRow(this._lastrow);
            }
            if (hrow.getRowNum() == this.getFirstRowNum()) {
                this._firstrow = this.findFirstRow(this._firstrow);
            }
            this._sheet.removeRow(hrow.getRowRecord());
            if (this._rows.size() == 0) {
                this._firstrow = -1;
                this._lastrow = -1;
            }
        }
    }
    
    private int findLastRow(final int lastrow) {
        if (lastrow < 1) {
            return 0;
        }
        int rownum;
        HSSFRow r;
        for (rownum = lastrow - 1, r = this.getRow(rownum); r == null && rownum > 0; r = this.getRow(--rownum)) {}
        if (r == null) {
            return 0;
        }
        return rownum;
    }
    
    private int findFirstRow(final int firstrow) {
        int rownum = firstrow + 1;
        for (HSSFRow r = this.getRow(rownum); r == null && rownum <= this.getLastRowNum(); r = this.getRow(++rownum)) {}
        if (rownum > this.getLastRowNum()) {
            return 0;
        }
        return rownum;
    }
    
    private void addRow(final HSSFRow row, final boolean addLow) {
        this._rows.put(row.getRowNum(), row);
        if (addLow) {
            this._sheet.addRow(row.getRowRecord());
        }
        final boolean firstRow = this._rows.size() == 1;
        if (row.getRowNum() > this.getLastRowNum() || firstRow) {
            this._lastrow = row.getRowNum();
        }
        if (row.getRowNum() < this.getFirstRowNum() || firstRow) {
            this._firstrow = row.getRowNum();
        }
    }
    
    @Override
    public HSSFRow getRow(final int rowIndex) {
        return this._rows.get(rowIndex);
    }
    
    @Override
    public int getPhysicalNumberOfRows() {
        return this._rows.size();
    }
    
    @Override
    public int getFirstRowNum() {
        return this._firstrow;
    }
    
    @Override
    public int getLastRowNum() {
        return this._lastrow;
    }
    
    @Override
    public List<HSSFDataValidation> getDataValidations() {
        final DataValidityTable dvt = this._sheet.getOrCreateDataValidityTable();
        final List<HSSFDataValidation> hssfValidations = new ArrayList<HSSFDataValidation>();
        final RecordAggregate.RecordVisitor visitor = new RecordAggregate.RecordVisitor() {
            private HSSFEvaluationWorkbook book = HSSFEvaluationWorkbook.create(HSSFSheet.this.getWorkbook());
            
            @Override
            public void visitRecord(final Record r) {
                if (!(r instanceof DVRecord)) {
                    return;
                }
                final DVRecord dvRecord = (DVRecord)r;
                final CellRangeAddressList regions = dvRecord.getCellRangeAddress().copy();
                final DVConstraint constraint = DVConstraint.createDVConstraint(dvRecord, this.book);
                final HSSFDataValidation hssfDataValidation = new HSSFDataValidation(regions, constraint);
                hssfDataValidation.setErrorStyle(dvRecord.getErrorStyle());
                hssfDataValidation.setEmptyCellAllowed(dvRecord.getEmptyCellAllowed());
                hssfDataValidation.setSuppressDropDownArrow(dvRecord.getSuppressDropdownArrow());
                hssfDataValidation.createPromptBox(dvRecord.getPromptTitle(), dvRecord.getPromptText());
                hssfDataValidation.setShowPromptBox(dvRecord.getShowPromptOnCellSelected());
                hssfDataValidation.createErrorBox(dvRecord.getErrorTitle(), dvRecord.getErrorText());
                hssfDataValidation.setShowErrorBox(dvRecord.getShowErrorOnInvalidValue());
                hssfValidations.add(hssfDataValidation);
            }
        };
        dvt.visitContainedRecords(visitor);
        return hssfValidations;
    }
    
    @Override
    public void addValidationData(final DataValidation dataValidation) {
        if (dataValidation == null) {
            throw new IllegalArgumentException("objValidation must not be null");
        }
        final HSSFDataValidation hssfDataValidation = (HSSFDataValidation)dataValidation;
        final DataValidityTable dvt = this._sheet.getOrCreateDataValidityTable();
        final DVRecord dvRecord = hssfDataValidation.createDVRecord(this);
        dvt.addDataValidation(dvRecord);
    }
    
    @Override
    public void setColumnHidden(final int columnIndex, final boolean hidden) {
        this._sheet.setColumnHidden(columnIndex, hidden);
    }
    
    @Override
    public boolean isColumnHidden(final int columnIndex) {
        return this._sheet.isColumnHidden(columnIndex);
    }
    
    @Override
    public void setColumnWidth(final int columnIndex, final int width) {
        this._sheet.setColumnWidth(columnIndex, width);
    }
    
    @Override
    public int getColumnWidth(final int columnIndex) {
        return this._sheet.getColumnWidth(columnIndex);
    }
    
    @Override
    public float getColumnWidthInPixels(final int column) {
        final int cw = this.getColumnWidth(column);
        final int def = this.getDefaultColumnWidth() * 256;
        final float px = (cw == def) ? 32.0f : 36.56f;
        return cw / px;
    }
    
    @Override
    public int getDefaultColumnWidth() {
        return this._sheet.getDefaultColumnWidth();
    }
    
    @Override
    public void setDefaultColumnWidth(final int width) {
        this._sheet.setDefaultColumnWidth(width);
    }
    
    @Override
    public short getDefaultRowHeight() {
        return this._sheet.getDefaultRowHeight();
    }
    
    @Override
    public float getDefaultRowHeightInPoints() {
        return this._sheet.getDefaultRowHeight() / 20.0f;
    }
    
    @Override
    public void setDefaultRowHeight(final short height) {
        this._sheet.setDefaultRowHeight(height);
    }
    
    @Override
    public void setDefaultRowHeightInPoints(final float height) {
        this._sheet.setDefaultRowHeight((short)(height * 20.0f));
    }
    
    @Override
    public HSSFCellStyle getColumnStyle(final int column) {
        final short styleIndex = this._sheet.getXFIndexForColAt((short)column);
        if (styleIndex == 15) {
            return null;
        }
        final ExtendedFormatRecord xf = this._book.getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this._book);
    }
    
    public boolean isGridsPrinted() {
        return this._sheet.isGridsPrinted();
    }
    
    public void setGridsPrinted(final boolean value) {
        this._sheet.setGridsPrinted(value);
    }
    
    @Override
    public int addMergedRegion(final CellRangeAddress region) {
        return this.addMergedRegion(region, true);
    }
    
    @Override
    public int addMergedRegionUnsafe(final CellRangeAddress region) {
        return this.addMergedRegion(region, false);
    }
    
    @Override
    public void validateMergedRegions() {
        this.checkForMergedRegionsIntersectingArrayFormulas();
        this.checkForIntersectingMergedRegions();
    }
    
    private int addMergedRegion(final CellRangeAddress region, final boolean validate) {
        if (region.getNumberOfCells() < 2) {
            throw new IllegalArgumentException("Merged region " + region.formatAsString() + " must contain 2 or more cells");
        }
        region.validate(SpreadsheetVersion.EXCEL97);
        if (validate) {
            this.validateArrayFormulas(region);
            this.validateMergedRegions(region);
        }
        return this._sheet.addMergedRegion(region.getFirstRow(), region.getFirstColumn(), region.getLastRow(), region.getLastColumn());
    }
    
    private void validateArrayFormulas(final CellRangeAddress region) {
        final int firstRow = region.getFirstRow();
        final int firstColumn = region.getFirstColumn();
        final int lastRow = region.getLastRow();
        final int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            final HSSFRow row = this.getRow(rowIn);
            if (row != null) {
                for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                    final HSSFCell cell = row.getCell(colIn);
                    if (cell != null) {
                        if (cell.isPartOfArrayFormulaGroup()) {
                            final CellRangeAddress arrayRange = cell.getArrayFormulaRange();
                            if (arrayRange.getNumberOfCells() > 1 && region.intersects(arrayRange)) {
                                final String msg = "The range " + region.formatAsString() + " intersects with a multi-cell array formula. You cannot merge cells of an array.";
                                throw new IllegalStateException(msg);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkForMergedRegionsIntersectingArrayFormulas() {
        for (final CellRangeAddress region : this.getMergedRegions()) {
            this.validateArrayFormulas(region);
        }
    }
    
    private void validateMergedRegions(final CellRangeAddress candidateRegion) {
        for (final CellRangeAddress existingRegion : this.getMergedRegions()) {
            if (existingRegion.intersects(candidateRegion)) {
                throw new IllegalStateException("Cannot add merged region " + candidateRegion.formatAsString() + " to sheet because it overlaps with an existing merged region (" + existingRegion.formatAsString() + ").");
            }
        }
    }
    
    private void checkForIntersectingMergedRegions() {
        final List<CellRangeAddress> regions = this.getMergedRegions();
        for (int size = regions.size(), i = 0; i < size; ++i) {
            final CellRangeAddress region = regions.get(i);
            for (final CellRangeAddress other : regions.subList(i + 1, regions.size())) {
                if (region.intersects(other)) {
                    final String msg = "The range " + region.formatAsString() + " intersects with another merged region " + other.formatAsString() + " in this sheet";
                    throw new IllegalStateException(msg);
                }
            }
        }
    }
    
    @Override
    public void setForceFormulaRecalculation(final boolean value) {
        this._sheet.setUncalced(value);
    }
    
    @Override
    public boolean getForceFormulaRecalculation() {
        return this._sheet.getUncalced();
    }
    
    @Override
    public void setVerticallyCenter(final boolean value) {
        this._sheet.getPageSettings().getVCenter().setVCenter(value);
    }
    
    @Override
    public boolean getVerticallyCenter() {
        return this._sheet.getPageSettings().getVCenter().getVCenter();
    }
    
    @Override
    public void setHorizontallyCenter(final boolean value) {
        this._sheet.getPageSettings().getHCenter().setHCenter(value);
    }
    
    @Override
    public boolean getHorizontallyCenter() {
        return this._sheet.getPageSettings().getHCenter().getHCenter();
    }
    
    @Override
    public void setRightToLeft(final boolean value) {
        this._sheet.getWindowTwo().setArabic(value);
    }
    
    @Override
    public boolean isRightToLeft() {
        return this._sheet.getWindowTwo().getArabic();
    }
    
    @Override
    public void removeMergedRegion(final int index) {
        this._sheet.removeMergedRegion(index);
    }
    
    @Override
    public void removeMergedRegions(final Collection<Integer> indices) {
        for (final int i : new TreeSet(indices).descendingSet()) {
            this._sheet.removeMergedRegion(i);
        }
    }
    
    @Override
    public int getNumMergedRegions() {
        return this._sheet.getNumMergedRegions();
    }
    
    @Override
    public CellRangeAddress getMergedRegion(final int index) {
        return this._sheet.getMergedRegionAt(index);
    }
    
    @Override
    public List<CellRangeAddress> getMergedRegions() {
        final List<CellRangeAddress> addresses = new ArrayList<CellRangeAddress>();
        for (int count = this._sheet.getNumMergedRegions(), i = 0; i < count; ++i) {
            addresses.add(this._sheet.getMergedRegionAt(i));
        }
        return addresses;
    }
    
    @Override
    public Iterator<Row> rowIterator() {
        final Iterator<Row> result = (Iterator<Row>)this._rows.values().iterator();
        return result;
    }
    
    @Override
    public Iterator<Row> iterator() {
        return this.rowIterator();
    }
    
    @Internal
    public InternalSheet getSheet() {
        return this._sheet;
    }
    
    public void setAlternativeExpression(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAlternateExpression(b);
    }
    
    public void setAlternativeFormula(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAlternateFormula(b);
    }
    
    @Override
    public void setAutobreaks(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAutobreaks(b);
    }
    
    public void setDialog(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setDialog(b);
    }
    
    @Override
    public void setDisplayGuts(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setDisplayGuts(b);
    }
    
    @Override
    public void setFitToPage(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setFitToPage(b);
    }
    
    @Override
    public void setRowSumsBelow(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setRowSumsBelow(b);
        record.setAlternateExpression(b);
    }
    
    @Override
    public void setRowSumsRight(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setRowSumsRight(b);
    }
    
    public boolean getAlternateExpression() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAlternateExpression();
    }
    
    public boolean getAlternateFormula() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAlternateFormula();
    }
    
    @Override
    public boolean getAutobreaks() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAutobreaks();
    }
    
    public boolean getDialog() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getDialog();
    }
    
    @Override
    public boolean getDisplayGuts() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getDisplayGuts();
    }
    
    @Override
    public boolean isDisplayZeros() {
        return this._sheet.getWindowTwo().getDisplayZeros();
    }
    
    @Override
    public void setDisplayZeros(final boolean value) {
        this._sheet.getWindowTwo().setDisplayZeros(value);
    }
    
    @Override
    public boolean getFitToPage() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getFitToPage();
    }
    
    @Override
    public boolean getRowSumsBelow() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getRowSumsBelow();
    }
    
    @Override
    public boolean getRowSumsRight() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getRowSumsRight();
    }
    
    @Override
    public boolean isPrintGridlines() {
        return this.getSheet().getPrintGridlines().getPrintGridlines();
    }
    
    @Override
    public void setPrintGridlines(final boolean show) {
        this.getSheet().getPrintGridlines().setPrintGridlines(show);
    }
    
    @Override
    public boolean isPrintRowAndColumnHeadings() {
        return this.getSheet().getPrintHeaders().getPrintHeaders();
    }
    
    @Override
    public void setPrintRowAndColumnHeadings(final boolean show) {
        this.getSheet().getPrintHeaders().setPrintHeaders(show);
    }
    
    @Override
    public HSSFPrintSetup getPrintSetup() {
        return new HSSFPrintSetup(this._sheet.getPageSettings().getPrintSetup());
    }
    
    @Override
    public HSSFHeader getHeader() {
        return new HSSFHeader(this._sheet.getPageSettings());
    }
    
    @Override
    public HSSFFooter getFooter() {
        return new HSSFFooter(this._sheet.getPageSettings());
    }
    
    @Override
    public boolean isSelected() {
        return this.getSheet().getWindowTwo().getSelected();
    }
    
    @Override
    public void setSelected(final boolean sel) {
        this.getSheet().getWindowTwo().setSelected(sel);
    }
    
    public boolean isActive() {
        return this.getSheet().getWindowTwo().isActive();
    }
    
    public void setActive(final boolean sel) {
        this.getSheet().getWindowTwo().setActive(sel);
    }
    
    @Override
    public double getMargin(final short margin) {
        switch (margin) {
            case 5: {
                return this._sheet.getPageSettings().getPrintSetup().getFooterMargin();
            }
            case 4: {
                return this._sheet.getPageSettings().getPrintSetup().getHeaderMargin();
            }
            default: {
                return this._sheet.getPageSettings().getMargin(margin);
            }
        }
    }
    
    @Override
    public void setMargin(final short margin, final double size) {
        switch (margin) {
            case 5: {
                this._sheet.getPageSettings().getPrintSetup().setFooterMargin(size);
                break;
            }
            case 4: {
                this._sheet.getPageSettings().getPrintSetup().setHeaderMargin(size);
                break;
            }
            default: {
                this._sheet.getPageSettings().setMargin(margin, size);
                break;
            }
        }
    }
    
    private WorksheetProtectionBlock getProtectionBlock() {
        return this._sheet.getProtectionBlock();
    }
    
    @Override
    public boolean getProtect() {
        return this.getProtectionBlock().isSheetProtected();
    }
    
    public short getPassword() {
        return (short)this.getProtectionBlock().getPasswordHash();
    }
    
    public boolean getObjectProtect() {
        return this.getProtectionBlock().isObjectProtected();
    }
    
    @Override
    public boolean getScenarioProtect() {
        return this.getProtectionBlock().isScenarioProtected();
    }
    
    @Override
    public void protectSheet(final String password) {
        this.getProtectionBlock().protectSheet(password, true, true);
    }
    
    public void setZoom(final int numerator, final int denominator) {
        if (numerator < 1 || numerator > 65535) {
            throw new IllegalArgumentException("Numerator must be greater than 0 and less than 65536");
        }
        if (denominator < 1 || denominator > 65535) {
            throw new IllegalArgumentException("Denominator must be greater than 0 and less than 65536");
        }
        final SCLRecord sclRecord = new SCLRecord();
        sclRecord.setNumerator((short)numerator);
        sclRecord.setDenominator((short)denominator);
        this.getSheet().setSCLRecord(sclRecord);
    }
    
    @Override
    public void setZoom(final int scale) {
        this.setZoom(scale, 100);
    }
    
    @Override
    public short getTopRow() {
        return this._sheet.getTopRow();
    }
    
    @Override
    public short getLeftCol() {
        return this._sheet.getLeftCol();
    }
    
    @Override
    public void showInPane(final int topRow, final int leftCol) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (topRow > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        this.showInPane((short)topRow, (short)leftCol);
    }
    
    private void showInPane(final short toprow, final short leftcol) {
        this._sheet.setTopRow(toprow);
        this._sheet.setLeftCol(leftcol);
    }
    
    @Deprecated
    protected void shiftMerged(final int startRow, final int endRow, final int n, final boolean isRow) {
        final RowShifter rowShifter = new HSSFRowShifter(this);
        rowShifter.shiftMergedRegions(startRow, endRow, n);
    }
    
    @Override
    public void shiftRows(final int startRow, final int endRow, final int n) {
        this.shiftRows(startRow, endRow, n, false, false);
    }
    
    @Override
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight) {
        this.shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight, true);
    }
    
    private static int clip(final int row) {
        return Math.min(Math.max(0, row), SpreadsheetVersion.EXCEL97.getLastRowIndex());
    }
    
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight, final boolean moveComments) {
        if (endRow < startRow) {
            throw new IllegalArgumentException("startRow must be less than or equal to endRow. To shift rows up, use n<0.");
        }
        int s;
        int inc;
        if (n < 0) {
            s = startRow;
            inc = 1;
        }
        else {
            if (n <= 0) {
                return;
            }
            s = endRow;
            inc = -1;
        }
        final RowShifter rowShifter = new HSSFRowShifter(this);
        if (moveComments) {
            this.moveCommentsForRowShift(startRow, endRow, n);
        }
        rowShifter.shiftMergedRegions(startRow, endRow, n);
        this._sheet.getPageSettings().shiftRowBreaks(startRow, endRow, n);
        this.deleteOverwrittenHyperlinksForRowShift(startRow, endRow, n);
        for (int rowNum = s; rowNum >= startRow && rowNum <= endRow && rowNum >= 0 && rowNum < 65536; rowNum += inc) {
            final HSSFRow row = this.getRow(rowNum);
            if (row != null) {
                this.notifyRowShifting(row);
            }
            HSSFRow row2Replace = this.getRow(rowNum + n);
            if (row2Replace == null) {
                row2Replace = this.createRow(rowNum + n);
            }
            row2Replace.removeAllCells();
            if (row != null) {
                if (copyRowHeight) {
                    row2Replace.setHeight(row.getHeight());
                }
                if (resetOriginalRowHeight) {
                    row.setHeight((short)255);
                }
                final Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    final HSSFCell cell = cells.next();
                    final HSSFHyperlink link = cell.getHyperlink();
                    row.removeCell(cell);
                    final CellValueRecordInterface cellRecord = cell.getCellValueRecord();
                    cellRecord.setRow(rowNum + n);
                    row2Replace.createCellFromRecord(cellRecord);
                    this._sheet.addValueRecord(rowNum + n, cellRecord);
                    if (link != null) {
                        link.setFirstRow(link.getFirstRow() + n);
                        link.setLastRow(link.getLastRow() + n);
                    }
                }
                row.removeAllCells();
            }
        }
        this.recomputeFirstAndLastRowsForRowShift(startRow, endRow, n);
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final short externSheetIndex = this._book.checkExternSheet(sheetIndex);
        final String sheetName = this._workbook.getSheetName(sheetIndex);
        final FormulaShifter formulaShifter = FormulaShifter.createForRowShift(externSheetIndex, sheetName, startRow, endRow, n, SpreadsheetVersion.EXCEL97);
        this.updateFormulasForShift(formulaShifter);
    }
    
    private void updateFormulasForShift(final FormulaShifter formulaShifter) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final short externSheetIndex = this._book.checkExternSheet(sheetIndex);
        this._sheet.updateFormulasAfterCellShift(formulaShifter, externSheetIndex);
        for (int nSheets = this._workbook.getNumberOfSheets(), i = 0; i < nSheets; ++i) {
            final InternalSheet otherSheet = this._workbook.getSheetAt(i).getSheet();
            if (otherSheet != this._sheet) {
                final short otherExtSheetIx = this._book.checkExternSheet(i);
                otherSheet.updateFormulasAfterCellShift(formulaShifter, otherExtSheetIx);
            }
        }
        this._workbook.getWorkbook().updateNamesAfterCellShift(formulaShifter);
    }
    
    private void recomputeFirstAndLastRowsForRowShift(final int startRow, final int endRow, final int n) {
        if (n > 0) {
            if (startRow == this._firstrow) {
                this._firstrow = Math.max(startRow + n, 0);
                for (int i = startRow + 1; i < startRow + n; ++i) {
                    if (this.getRow(i) != null) {
                        this._firstrow = i;
                        break;
                    }
                }
            }
            if (endRow + n > this._lastrow) {
                this._lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
            }
        }
        else {
            if (startRow + n < this._firstrow) {
                this._firstrow = Math.max(startRow + n, 0);
            }
            if (endRow == this._lastrow) {
                this._lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
                for (int i = endRow - 1; i > endRow + n; --i) {
                    if (this.getRow(i) != null) {
                        this._lastrow = i;
                        break;
                    }
                }
            }
        }
    }
    
    private void deleteOverwrittenHyperlinksForRowShift(final int startRow, final int endRow, final int n) {
        final int firstOverwrittenRow = startRow + n;
        final int lastOverwrittenRow = endRow + n;
        for (final HSSFHyperlink link : this.getHyperlinkList()) {
            final int firstRow = link.getFirstRow();
            final int lastRow = link.getLastRow();
            if (firstOverwrittenRow <= firstRow && firstRow <= lastOverwrittenRow && lastOverwrittenRow <= lastRow && lastRow <= lastOverwrittenRow) {
                this.removeHyperlink(link);
            }
        }
    }
    
    private void moveCommentsForRowShift(final int startRow, final int endRow, final int n) {
        final HSSFPatriarch patriarch = this.createDrawingPatriarch();
        for (final HSSFShape shape : patriarch.getChildren()) {
            if (!(shape instanceof HSSFComment)) {
                continue;
            }
            final HSSFComment comment = (HSSFComment)shape;
            final int r = comment.getRow();
            if (startRow > r || r > endRow) {
                continue;
            }
            comment.setRow(clip(r + n));
        }
    }
    
    @Override
    public void shiftColumns(final int startColumn, final int endColumn, final int n) {
        final HSSFColumnShifter columnShifter = new HSSFColumnShifter(this);
        columnShifter.shiftColumns(startColumn, endColumn, n);
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final short externSheetIndex = this._book.checkExternSheet(sheetIndex);
        final String sheetName = this._workbook.getSheetName(sheetIndex);
        final FormulaShifter formulaShifter = FormulaShifter.createForColumnShift(externSheetIndex, sheetName, startColumn, endColumn, n, SpreadsheetVersion.EXCEL97);
        this.updateFormulasForShift(formulaShifter);
    }
    
    protected void insertChartRecords(final List<Record> records) {
        final int window2Loc = this._sheet.findFirstRecordLocBySid((short)574);
        this._sheet.getRecords().addAll(window2Loc, records);
    }
    
    private void notifyRowShifting(final HSSFRow row) {
        final String msg = "Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
        for (final Cell cell : row) {
            final HSSFCell hcell = (HSSFCell)cell;
            if (hcell.isPartOfArrayFormulaGroup()) {
                hcell.tryToDeleteArrayFormula(msg);
            }
        }
    }
    
    @Override
    public void createFreezePane(final int colSplit, final int rowSplit, final int leftmostColumn, final int topRow) {
        this.validateColumn(colSplit);
        this.validateRow(rowSplit);
        if (leftmostColumn < colSplit) {
            throw new IllegalArgumentException("leftmostColumn parameter must not be less than colSplit parameter");
        }
        if (topRow < rowSplit) {
            throw new IllegalArgumentException("topRow parameter must not be less than leftmostColumn parameter");
        }
        this.getSheet().createFreezePane(colSplit, rowSplit, topRow, leftmostColumn);
    }
    
    @Override
    public void createFreezePane(final int colSplit, final int rowSplit) {
        this.createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }
    
    @Override
    public void createSplitPane(final int xSplitPos, final int ySplitPos, final int leftmostColumn, final int topRow, final int activePane) {
        this.getSheet().createSplitPane(xSplitPos, ySplitPos, topRow, leftmostColumn, activePane);
    }
    
    @Override
    public PaneInformation getPaneInformation() {
        return this.getSheet().getPaneInformation();
    }
    
    @Override
    public void setDisplayGridlines(final boolean show) {
        this._sheet.setDisplayGridlines(show);
    }
    
    @Override
    public boolean isDisplayGridlines() {
        return this._sheet.isDisplayGridlines();
    }
    
    @Override
    public void setDisplayFormulas(final boolean show) {
        this._sheet.setDisplayFormulas(show);
    }
    
    @Override
    public boolean isDisplayFormulas() {
        return this._sheet.isDisplayFormulas();
    }
    
    @Override
    public void setDisplayRowColHeadings(final boolean show) {
        this._sheet.setDisplayRowColHeadings(show);
    }
    
    @Override
    public boolean isDisplayRowColHeadings() {
        return this._sheet.isDisplayRowColHeadings();
    }
    
    @Override
    public void setRowBreak(final int row) {
        this.validateRow(row);
        this._sheet.getPageSettings().setRowBreak(row, (short)0, (short)255);
    }
    
    @Override
    public boolean isRowBroken(final int row) {
        return this._sheet.getPageSettings().isRowBroken(row);
    }
    
    @Override
    public void removeRowBreak(final int row) {
        this._sheet.getPageSettings().removeRowBreak(row);
    }
    
    @Override
    public int[] getRowBreaks() {
        return this._sheet.getPageSettings().getRowBreaks();
    }
    
    @Override
    public int[] getColumnBreaks() {
        return this._sheet.getPageSettings().getColumnBreaks();
    }
    
    @Override
    public void setColumnBreak(final int column) {
        this.validateColumn((short)column);
        this._sheet.getPageSettings().setColumnBreak((short)column, (short)0, (short)SpreadsheetVersion.EXCEL97.getLastRowIndex());
    }
    
    @Override
    public boolean isColumnBroken(final int column) {
        return this._sheet.getPageSettings().isColumnBroken(column);
    }
    
    @Override
    public void removeColumnBreak(final int column) {
        this._sheet.getPageSettings().removeColumnBreak(column);
    }
    
    protected void validateRow(final int row) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }
    
    protected void validateColumn(final int column) {
        final int maxcol = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        }
        if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }
    
    public void dumpDrawingRecords(final boolean fat, final PrintWriter pw) {
        this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false);
        final EscherAggregate r = (EscherAggregate)this.getSheet().findFirstRecordBySid((short)9876);
        final List<EscherRecord> escherRecords = r.getEscherRecords();
        for (final EscherRecord escherRecord : escherRecords) {
            if (fat) {
                pw.println(escherRecord);
            }
            else {
                escherRecord.display(pw, 0);
            }
        }
        pw.flush();
    }
    
    public EscherAggregate getDrawingEscherAggregate() {
        this._book.findDrawingGroup();
        if (this._book.getDrawingManager() == null) {
            return null;
        }
        final int found = this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false);
        if (found == -1) {
            return null;
        }
        return (EscherAggregate)this._sheet.findFirstRecordBySid((short)9876);
    }
    
    @Override
    public HSSFPatriarch getDrawingPatriarch() {
        return this._patriarch = this.getPatriarch(false);
    }
    
    @Override
    public HSSFPatriarch createDrawingPatriarch() {
        return this._patriarch = this.getPatriarch(true);
    }
    
    private HSSFPatriarch getPatriarch(final boolean createIfMissing) {
        if (this._patriarch != null) {
            return this._patriarch;
        }
        DrawingManager2 dm = this._book.findDrawingGroup();
        if (null == dm) {
            if (!createIfMissing) {
                return null;
            }
            this._book.createDrawingGroup();
            dm = this._book.getDrawingManager();
        }
        EscherAggregate agg = (EscherAggregate)this._sheet.findFirstRecordBySid((short)9876);
        if (null == agg) {
            int pos = this._sheet.aggregateDrawingRecords(dm, false);
            if (-1 == pos) {
                if (createIfMissing) {
                    pos = this._sheet.aggregateDrawingRecords(dm, true);
                    agg = this._sheet.getRecords().get(pos);
                    final HSSFPatriarch patriarch = new HSSFPatriarch(this, agg);
                    patriarch.afterCreate();
                    return patriarch;
                }
                return null;
            }
            else {
                agg = this._sheet.getRecords().get(pos);
            }
        }
        return new HSSFPatriarch(this, agg);
    }
    
    @Override
    public void setColumnGroupCollapsed(final int columnNumber, final boolean collapsed) {
        this._sheet.setColumnGroupCollapsed(columnNumber, collapsed);
    }
    
    @Override
    public void groupColumn(final int fromColumn, final int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, true);
    }
    
    @Override
    public void ungroupColumn(final int fromColumn, final int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, false);
    }
    
    @Override
    public void groupRow(final int fromRow, final int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, true);
    }
    
    @Override
    public void ungroupRow(final int fromRow, final int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, false);
    }
    
    @Override
    public void setRowGroupCollapsed(final int rowIndex, final boolean collapse) {
        if (collapse) {
            this._sheet.getRowsAggregate().collapseRow(rowIndex);
        }
        else {
            this._sheet.getRowsAggregate().expandRow(rowIndex);
        }
    }
    
    @Override
    public void setDefaultColumnStyle(final int column, final CellStyle style) {
        this._sheet.setDefaultColumnStyle(column, style.getIndex());
    }
    
    @Override
    public void autoSizeColumn(final int column) {
        this.autoSizeColumn(column, false);
    }
    
    @Override
    public void autoSizeColumn(final int column, final boolean useMergedCells) {
        double width = SheetUtil.getColumnWidth(this, column, useMergedCells);
        if (width != -1.0) {
            width *= 256.0;
            final int maxColumnWidth = 65280;
            if (width > maxColumnWidth) {
                width = maxColumnWidth;
            }
            this.setColumnWidth(column, (int)width);
        }
    }
    
    @Override
    public HSSFComment getCellComment(final CellAddress ref) {
        return this.findCellComment(ref.getRow(), ref.getColumn());
    }
    
    @Override
    public HSSFHyperlink getHyperlink(final int row, final int column) {
        for (final RecordBase rec : this._sheet.getRecords()) {
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord link = (HyperlinkRecord)rec;
                if (link.getFirstColumn() == column && link.getFirstRow() == row) {
                    return new HSSFHyperlink(link);
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public HSSFHyperlink getHyperlink(final CellAddress addr) {
        return this.getHyperlink(addr.getRow(), addr.getColumn());
    }
    
    @Override
    public List<HSSFHyperlink> getHyperlinkList() {
        final List<HSSFHyperlink> hyperlinkList = new ArrayList<HSSFHyperlink>();
        for (final RecordBase rec : this._sheet.getRecords()) {
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord link = (HyperlinkRecord)rec;
                hyperlinkList.add(new HSSFHyperlink(link));
            }
        }
        return hyperlinkList;
    }
    
    protected void removeHyperlink(final HSSFHyperlink link) {
        this.removeHyperlink(link.record);
    }
    
    protected void removeHyperlink(final HyperlinkRecord link) {
        final Iterator<RecordBase> it = this._sheet.getRecords().iterator();
        while (it.hasNext()) {
            final RecordBase rec = it.next();
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord recLink = (HyperlinkRecord)rec;
                if (link == recLink) {
                    it.remove();
                    return;
                }
                continue;
            }
        }
    }
    
    @Override
    public HSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new HSSFSheetConditionalFormatting(this);
    }
    
    @Override
    public String getSheetName() {
        final HSSFWorkbook wb = this.getWorkbook();
        final int idx = wb.getSheetIndex(this);
        return wb.getSheetName(idx);
    }
    
    private CellRange<HSSFCell> getCellRange(final CellRangeAddress range) {
        final int firstRow = range.getFirstRow();
        final int firstColumn = range.getFirstColumn();
        final int lastRow = range.getLastRow();
        final int lastColumn = range.getLastColumn();
        final int height = lastRow - firstRow + 1;
        final int width = lastColumn - firstColumn + 1;
        final List<HSSFCell> temp = new ArrayList<HSSFCell>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                HSSFRow row = this.getRow(rowIn);
                if (row == null) {
                    row = this.createRow(rowIn);
                }
                HSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return SSCellRange.create(firstRow, firstColumn, height, width, temp, HSSFCell.class);
    }
    
    @Override
    public CellRange<HSSFCell> setArrayFormula(final String formula, final CellRangeAddress range) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._workbook, FormulaType.ARRAY, sheetIndex);
        final CellRange<HSSFCell> cells = this.getCellRange(range);
        for (final HSSFCell c : cells) {
            c.setCellArrayFormula(range);
        }
        final HSSFCell mainArrayFormulaCell = cells.getTopLeftCell();
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)mainArrayFormulaCell.getCellValueRecord();
        agg.setArrayFormula(range, ptgs);
        return cells;
    }
    
    @Override
    public CellRange<HSSFCell> removeArrayFormula(final Cell cell) {
        if (cell.getSheet() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
        }
        final CellValueRecordInterface rec = ((HSSFCell)cell).getCellValueRecord();
        if (!(rec instanceof FormulaRecordAggregate)) {
            final String ref = new CellReference(cell).formatAsString();
            throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
        }
        final FormulaRecordAggregate fra = (FormulaRecordAggregate)rec;
        final CellRangeAddress range = fra.removeArrayFormula(cell.getRowIndex(), cell.getColumnIndex());
        final CellRange<HSSFCell> result = this.getCellRange(range);
        for (final Cell c : result) {
            c.setBlank();
        }
        return result;
    }
    
    @Override
    public DataValidationHelper getDataValidationHelper() {
        return new HSSFDataValidationHelper(this);
    }
    
    @Override
    public HSSFAutoFilter setAutoFilter(final CellRangeAddress range) {
        final InternalWorkbook workbook = this._workbook.getWorkbook();
        final int sheetIndex = this._workbook.getSheetIndex(this);
        NameRecord name = workbook.getSpecificBuiltinRecord((byte)13, sheetIndex + 1);
        if (name == null) {
            name = workbook.createBuiltInName((byte)13, sheetIndex + 1);
        }
        int firstRow = range.getFirstRow();
        if (firstRow == -1) {
            firstRow = 0;
        }
        final Area3DPtg ptg = new Area3DPtg(firstRow, range.getLastRow(), range.getFirstColumn(), range.getLastColumn(), false, false, false, false, sheetIndex);
        name.setNameDefinition(new Ptg[] { ptg });
        final AutoFilterInfoRecord r = new AutoFilterInfoRecord();
        final int numcols = 1 + range.getLastColumn() - range.getFirstColumn();
        r.setNumEntries((short)numcols);
        final int idx = this._sheet.findFirstRecordLocBySid((short)512);
        this._sheet.getRecords().add(idx, r);
        final HSSFPatriarch p = this.createDrawingPatriarch();
        final int firstColumn = range.getFirstColumn();
        for (int lastColumn = range.getLastColumn(), col = firstColumn; col <= lastColumn; ++col) {
            p.createComboBox(new HSSFClientAnchor(0, 0, 0, 0, (short)col, firstRow, (short)(col + 1), firstRow + 1));
        }
        return new HSSFAutoFilter(this);
    }
    
    protected HSSFComment findCellComment(final int row, final int column) {
        HSSFPatriarch patriarch = this.getDrawingPatriarch();
        if (null == patriarch) {
            patriarch = this.createDrawingPatriarch();
        }
        return this.lookForComment(patriarch, row, column);
    }
    
    private HSSFComment lookForComment(final HSSFShapeContainer container, final int row, final int column) {
        for (final Object object : container.getChildren()) {
            final HSSFShape shape = (HSSFShape)object;
            if (shape instanceof HSSFShapeGroup) {
                final HSSFComment res = this.lookForComment((HSSFShapeContainer)shape, row, column);
                if (null != res) {
                    return res;
                }
                continue;
            }
            else {
                if (!(shape instanceof HSSFComment)) {
                    continue;
                }
                final HSSFComment comment = (HSSFComment)shape;
                if (comment.hasPosition() && comment.getColumn() == column && comment.getRow() == row) {
                    return comment;
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public Map<CellAddress, HSSFComment> getCellComments() {
        HSSFPatriarch patriarch = this.getDrawingPatriarch();
        if (null == patriarch) {
            patriarch = this.createDrawingPatriarch();
        }
        final Map<CellAddress, HSSFComment> locations = new TreeMap<CellAddress, HSSFComment>();
        this.findCellCommentLocations(patriarch, locations);
        return locations;
    }
    
    private void findCellCommentLocations(final HSSFShapeContainer container, final Map<CellAddress, HSSFComment> locations) {
        for (final Object object : container.getChildren()) {
            final HSSFShape shape = (HSSFShape)object;
            if (shape instanceof HSSFShapeGroup) {
                this.findCellCommentLocations((HSSFShapeContainer)shape, locations);
            }
            else {
                if (!(shape instanceof HSSFComment)) {
                    continue;
                }
                final HSSFComment comment = (HSSFComment)shape;
                if (!comment.hasPosition()) {
                    continue;
                }
                locations.put(new CellAddress(comment.getRow(), comment.getColumn()), comment);
            }
        }
    }
    
    @Override
    public CellRangeAddress getRepeatingRows() {
        return this.getRepeatingRowsOrColums(true);
    }
    
    @Override
    public CellRangeAddress getRepeatingColumns() {
        return this.getRepeatingRowsOrColums(false);
    }
    
    @Override
    public void setRepeatingRows(final CellRangeAddress rowRangeRef) {
        final CellRangeAddress columnRangeRef = this.getRepeatingColumns();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    @Override
    public void setRepeatingColumns(final CellRangeAddress columnRangeRef) {
        final CellRangeAddress rowRangeRef = this.getRepeatingRows();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    private void setRepeatingRowsAndColumns(final CellRangeAddress rowDef, final CellRangeAddress colDef) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final int maxRowIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        final int maxColIndex = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        int col1 = -1;
        int col2 = -1;
        int row1 = -1;
        int row2 = -1;
        if (rowDef != null) {
            row1 = rowDef.getFirstRow();
            row2 = rowDef.getLastRow();
            if ((row1 == -1 && row2 != -1) || row1 > row2 || row1 < 0 || row1 > maxRowIndex || row2 < 0 || row2 > maxRowIndex) {
                throw new IllegalArgumentException("Invalid row range specification");
            }
        }
        if (colDef != null) {
            col1 = colDef.getFirstColumn();
            col2 = colDef.getLastColumn();
            if ((col1 == -1 && col2 != -1) || col1 > col2 || col1 < 0 || col1 > maxColIndex || col2 < 0 || col2 > maxColIndex) {
                throw new IllegalArgumentException("Invalid column range specification");
            }
        }
        final short externSheetIndex = this._workbook.getWorkbook().checkExternSheet(sheetIndex);
        final boolean setBoth = rowDef != null && colDef != null;
        final boolean removeAll = rowDef == null && colDef == null;
        HSSFName name = this._workbook.getBuiltInName((byte)7, sheetIndex);
        if (removeAll) {
            if (name != null) {
                this._workbook.removeName(name);
            }
            return;
        }
        if (name == null) {
            name = this._workbook.createBuiltInName((byte)7, sheetIndex);
        }
        final List<Ptg> ptgList = new ArrayList<Ptg>();
        if (setBoth) {
            final int exprsSize = 23;
            ptgList.add(new MemFuncPtg(23));
        }
        if (colDef != null) {
            final Area3DPtg colArea = new Area3DPtg(0, maxRowIndex, col1, col2, false, false, false, false, externSheetIndex);
            ptgList.add(colArea);
        }
        if (rowDef != null) {
            final Area3DPtg rowArea = new Area3DPtg(row1, row2, 0, maxColIndex, false, false, false, false, externSheetIndex);
            ptgList.add(rowArea);
        }
        if (setBoth) {
            ptgList.add(UnionPtg.instance);
        }
        final Ptg[] ptgs = new Ptg[ptgList.size()];
        ptgList.toArray(ptgs);
        name.setNameDefinition(ptgs);
        final HSSFPrintSetup printSetup = this.getPrintSetup();
        printSetup.setValidSettings(false);
        this.setActive(true);
    }
    
    private CellRangeAddress getRepeatingRowsOrColums(final boolean rows) {
        final NameRecord rec = this.getBuiltinNameRecord((byte)7);
        if (rec == null) {
            return null;
        }
        final Ptg[] nameDefinition = rec.getNameDefinition();
        if (nameDefinition == null) {
            return null;
        }
        final int maxRowIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        final int maxColIndex = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        for (final Ptg ptg : nameDefinition) {
            if (ptg instanceof Area3DPtg) {
                final Area3DPtg areaPtg = (Area3DPtg)ptg;
                if (areaPtg.getFirstColumn() == 0 && areaPtg.getLastColumn() == maxColIndex) {
                    if (rows) {
                        return new CellRangeAddress(areaPtg.getFirstRow(), areaPtg.getLastRow(), -1, -1);
                    }
                }
                else if (areaPtg.getFirstRow() == 0 && areaPtg.getLastRow() == maxRowIndex && !rows) {
                    return new CellRangeAddress(-1, -1, areaPtg.getFirstColumn(), areaPtg.getLastColumn());
                }
            }
        }
        return null;
    }
    
    private NameRecord getBuiltinNameRecord(final byte builtinCode) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final int recIndex = this._workbook.findExistingBuiltinNameRecordIdx(sheetIndex, builtinCode);
        if (recIndex == -1) {
            return null;
        }
        return this._workbook.getNameRecord(recIndex);
    }
    
    @Override
    public int getColumnOutlineLevel(final int columnIndex) {
        return this._sheet.getColumnOutlineLevel(columnIndex);
    }
    
    @Override
    public CellAddress getActiveCell() {
        final int row = this._sheet.getActiveCellRow();
        final int col = this._sheet.getActiveCellCol();
        return new CellAddress(row, col);
    }
    
    @Override
    public void setActiveCell(final CellAddress address) {
        final int row = address.getRow();
        final short col = (short)address.getColumn();
        this._sheet.setActiveCellRow(row);
        this._sheet.setActiveCellCol(col);
    }
    
    static {
        log = POILogFactory.getLogger(HSSFSheet.class);
        INITIAL_CAPACITY = Configurator.getIntValue("HSSFSheet.RowInitialCapacity", 20);
    }
}
