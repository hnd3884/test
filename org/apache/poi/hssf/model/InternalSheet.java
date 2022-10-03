package org.apache.poi.hssf.model;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.ss.util.PaneInformation;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.UncalcedRecord;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.aggregates.ChartSubstreamRecordAggregate;
import org.apache.poi.hssf.record.aggregates.CustomViewSettingsRecordAggregate;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.util.RecordFormatException;
import java.util.ArrayList;
import org.apache.poi.hssf.record.RowRecord;
import java.util.Iterator;
import org.apache.poi.hssf.record.aggregates.ConditionalFormattingTable;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.MergedCellsTable;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.RecordBase;
import java.util.List;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public final class InternalSheet
{
    public static final short LeftMargin = 0;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    public static final short BottomMargin = 3;
    private static POILogger log;
    private List<RecordBase> _records;
    protected PrintGridlinesRecord printGridlines;
    protected PrintHeadersRecord printHeaders;
    protected GridsetRecord gridset;
    private GutsRecord _gutsRecord;
    protected DefaultColWidthRecord defaultcolwidth;
    protected DefaultRowHeightRecord defaultrowheight;
    private PageSettingsBlock _psBlock;
    private final WorksheetProtectionBlock _protectionBlock;
    protected WindowTwoRecord windowTwo;
    protected SelectionRecord _selection;
    private final MergedCellsTable _mergedCellsTable;
    ColumnInfoRecordsAggregate _columnInfos;
    private DimensionsRecord _dimensions;
    protected final RowRecordsAggregate _rowsAggregate;
    private DataValidityTable _dataValidityTable;
    private ConditionalFormattingTable condFormatting;
    private Iterator<RowRecord> rowRecIterator;
    protected boolean _isUncalced;
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_UPPER_LEFT = 3;
    
    public static InternalSheet createSheet(final RecordStream rs) {
        return new InternalSheet(rs);
    }
    
    private InternalSheet(final RecordStream rs) {
        this.defaultcolwidth = new DefaultColWidthRecord();
        this.defaultrowheight = new DefaultRowHeightRecord();
        this._protectionBlock = new WorksheetProtectionBlock();
        this._mergedCellsTable = new MergedCellsTable();
        RowRecordsAggregate rra = null;
        final List<RecordBase> records = new ArrayList<RecordBase>(128);
        this._records = records;
        int dimsloc = -1;
        if (rs.peekNextSid() != 2057) {
            throw new RecordFormatException("BOF record expected");
        }
        final BOFRecord bof = (BOFRecord)rs.getNext();
        if (bof.getType() != 16) {
            if (bof.getType() != 32) {
                if (bof.getType() != 64) {
                    while (rs.hasNext()) {
                        final Record rec = rs.getNext();
                        if (rec instanceof EOFRecord) {
                            break;
                        }
                    }
                    throw new UnsupportedBOFType(bof.getType());
                }
            }
        }
        records.add(bof);
        while (rs.hasNext()) {
            final int recSid = rs.peekNextSid();
            if (recSid == 432 || recSid == 2169) {
                records.add(this.condFormatting = new ConditionalFormattingTable(rs));
            }
            else if (recSid == 125) {
                records.add(this._columnInfos = new ColumnInfoRecordsAggregate(rs));
            }
            else if (recSid == 434) {
                records.add(this._dataValidityTable = new DataValidityTable(rs));
            }
            else if (RecordOrderer.isRowBlockRecord(recSid)) {
                if (rra != null) {
                    throw new RecordFormatException("row/cell records found in the wrong place");
                }
                final RowBlocksReader rbr = new RowBlocksReader(rs);
                this._mergedCellsTable.addRecords(rbr.getLooseMergedCells());
                rra = new RowRecordsAggregate(rbr.getPlainRecordStream(), rbr.getSharedFormulaManager());
                records.add(rra);
            }
            else if (CustomViewSettingsRecordAggregate.isBeginRecord(recSid)) {
                records.add(new CustomViewSettingsRecordAggregate(rs));
            }
            else if (PageSettingsBlock.isComponentRecord(recSid)) {
                if (this._psBlock == null) {
                    records.add(this._psBlock = new PageSettingsBlock(rs));
                }
                else {
                    this._psBlock.addLateRecords(rs);
                }
                this._psBlock.positionRecords(records);
            }
            else if (WorksheetProtectionBlock.isComponentRecord(recSid)) {
                this._protectionBlock.addRecords(rs);
            }
            else if (recSid == 229) {
                this._mergedCellsTable.read(rs);
            }
            else if (recSid == 2057) {
                final ChartSubstreamRecordAggregate chartAgg = new ChartSubstreamRecordAggregate(rs);
                spillAggregate(chartAgg, records);
            }
            else {
                final Record rec2 = rs.getNext();
                if (recSid == 523) {
                    continue;
                }
                if (recSid == 94) {
                    this._isUncalced = true;
                }
                else if (recSid == 2152 || recSid == 2151) {
                    records.add(rec2);
                }
                else {
                    if (recSid == 10) {
                        records.add(rec2);
                        break;
                    }
                    if (recSid == 512) {
                        if (this._columnInfos == null) {
                            records.add(this._columnInfos = new ColumnInfoRecordsAggregate());
                        }
                        this._dimensions = (DimensionsRecord)rec2;
                        dimsloc = records.size();
                    }
                    else if (recSid == 85) {
                        this.defaultcolwidth = (DefaultColWidthRecord)rec2;
                    }
                    else if (recSid == 549) {
                        this.defaultrowheight = (DefaultRowHeightRecord)rec2;
                    }
                    else if (recSid == 43) {
                        this.printGridlines = (PrintGridlinesRecord)rec2;
                    }
                    else if (recSid == 42) {
                        this.printHeaders = (PrintHeadersRecord)rec2;
                    }
                    else if (recSid == 130) {
                        this.gridset = (GridsetRecord)rec2;
                    }
                    else if (recSid == 29) {
                        this._selection = (SelectionRecord)rec2;
                    }
                    else if (recSid == 574) {
                        this.windowTwo = (WindowTwoRecord)rec2;
                    }
                    else if (recSid == 128) {
                        this._gutsRecord = (GutsRecord)rec2;
                    }
                    records.add(rec2);
                }
            }
        }
        if (this.windowTwo == null) {
            throw new RecordFormatException("WINDOW2 was not found");
        }
        if (this._dimensions == null) {
            if (rra == null) {
                rra = new RowRecordsAggregate();
            }
            else if (InternalSheet.log.check(5)) {
                InternalSheet.log.log(5, "DIMENSION record not found even though row/cells present");
            }
            dimsloc = this.findFirstRecordLocBySid((short)574);
            records.add(dimsloc, this._dimensions = rra.createDimensions());
        }
        if (rra == null) {
            rra = new RowRecordsAggregate();
            records.add(dimsloc + 1, rra);
        }
        this._rowsAggregate = rra;
        RecordOrderer.addNewSheetRecord(records, this._mergedCellsTable);
        RecordOrderer.addNewSheetRecord(records, this._protectionBlock);
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "sheet createSheet (existing file) exited");
        }
    }
    
    private static void spillAggregate(final RecordAggregate ra, final List<RecordBase> recs) {
        ra.visitContainedRecords(r -> recs.add(r));
    }
    
    public InternalSheet cloneSheet() {
        final List<Record> clonedRecords = new ArrayList<Record>(this._records.size());
        for (int i = 0; i < this._records.size(); ++i) {
            RecordBase rb = this._records.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate)rb).visitContainedRecords(new RecordCloner(clonedRecords));
            }
            else {
                if (rb instanceof EscherAggregate) {
                    rb = new DrawingRecord();
                }
                final Record rec = ((Record)rb).copy();
                clonedRecords.add(rec);
            }
        }
        return createSheet(new RecordStream(clonedRecords, 0));
    }
    
    public static InternalSheet createSheet() {
        return new InternalSheet();
    }
    
    private InternalSheet() {
        this.defaultcolwidth = new DefaultColWidthRecord();
        this.defaultrowheight = new DefaultRowHeightRecord();
        this._protectionBlock = new WorksheetProtectionBlock();
        this._mergedCellsTable = new MergedCellsTable();
        final List<RecordBase> records = new ArrayList<RecordBase>(32);
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "Sheet createsheet from scratch called");
        }
        records.add(createBOF());
        records.add(createCalcMode());
        records.add(createCalcCount());
        records.add(createRefMode());
        records.add(createIteration());
        records.add(createDelta());
        records.add(createSaveRecalc());
        records.add(this.printHeaders = createPrintHeaders());
        records.add(this.printGridlines = createPrintGridlines());
        records.add(this.gridset = createGridset());
        records.add(this._gutsRecord = createGuts());
        records.add(this.defaultrowheight = createDefaultRowHeight());
        records.add(createWSBool());
        records.add(this._psBlock = new PageSettingsBlock());
        records.add(this._protectionBlock);
        records.add(this.defaultcolwidth = createDefaultColWidth());
        final ColumnInfoRecordsAggregate columns = new ColumnInfoRecordsAggregate();
        records.add(columns);
        this._columnInfos = columns;
        records.add(this._dimensions = createDimensions());
        records.add(this._rowsAggregate = new RowRecordsAggregate());
        records.add(this.windowTwo = createWindowTwo());
        records.add(this._selection = createSelection());
        records.add(this._mergedCellsTable);
        records.add(EOFRecord.instance);
        this._records = records;
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "Sheet createsheet from scratch exit");
        }
    }
    
    public RowRecordsAggregate getRowsAggregate() {
        return this._rowsAggregate;
    }
    
    private MergedCellsTable getMergedRecords() {
        return this._mergedCellsTable;
    }
    
    public void updateFormulasAfterCellShift(final FormulaShifter shifter, final int externSheetIndex) {
        this.getRowsAggregate().updateFormulasAfterRowShift(shifter, externSheetIndex);
        if (this.condFormatting != null) {
            this.getConditionalFormattingTable().updateFormulasAfterCellShift(shifter, externSheetIndex);
        }
    }
    
    public int addMergedRegion(final int rowFrom, final int colFrom, final int rowTo, final int colTo) {
        if (rowTo < rowFrom) {
            throw new IllegalArgumentException("The 'to' row (" + rowTo + ") must not be less than the 'from' row (" + rowFrom + ")");
        }
        if (colTo < colFrom) {
            throw new IllegalArgumentException("The 'to' col (" + colTo + ") must not be less than the 'from' col (" + colFrom + ")");
        }
        final MergedCellsTable mrt = this.getMergedRecords();
        mrt.addArea(rowFrom, colFrom, rowTo, colTo);
        return mrt.getNumberOfMergedRegions() - 1;
    }
    
    public void removeMergedRegion(final int index) {
        final MergedCellsTable mrt = this.getMergedRecords();
        if (index >= mrt.getNumberOfMergedRegions()) {
            return;
        }
        mrt.remove(index);
    }
    
    public CellRangeAddress getMergedRegionAt(final int index) {
        final MergedCellsTable mrt = this.getMergedRecords();
        if (index >= mrt.getNumberOfMergedRegions()) {
            return null;
        }
        return mrt.get(index);
    }
    
    public int getNumMergedRegions() {
        return this.getMergedRecords().getNumberOfMergedRegions();
    }
    
    public ConditionalFormattingTable getConditionalFormattingTable() {
        if (this.condFormatting == null) {
            this.condFormatting = new ConditionalFormattingTable();
            RecordOrderer.addNewSheetRecord(this._records, this.condFormatting);
        }
        return this.condFormatting;
    }
    
    public void setDimensions(final int firstrow, final short firstcol, final int lastrow, final short lastcol) {
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "Sheet.setDimensions");
            InternalSheet.log.log(1, "firstrow" + firstrow + "firstcol" + firstcol + "lastrow" + lastrow + "lastcol" + lastcol);
        }
        this._dimensions.setFirstCol(firstcol);
        this._dimensions.setFirstRow(firstrow);
        this._dimensions.setLastCol(lastcol);
        this._dimensions.setLastRow(lastrow);
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "Sheet.setDimensions exiting");
        }
    }
    
    public void visitContainedRecords(final RecordAggregate.RecordVisitor rv, final int offset) {
        final RecordAggregate.PositionTrackingVisitor ptv = new RecordAggregate.PositionTrackingVisitor(rv, offset);
        boolean haveSerializedIndex = false;
        for (int k = 0; k < this._records.size(); ++k) {
            final RecordBase record = this._records.get(k);
            if (record instanceof RecordAggregate) {
                final RecordAggregate agg = (RecordAggregate)record;
                agg.visitContainedRecords(ptv);
            }
            else {
                ptv.visitRecord((Record)record);
            }
            if (record instanceof BOFRecord && !haveSerializedIndex) {
                haveSerializedIndex = true;
                if (this._isUncalced) {
                    ptv.visitRecord(new UncalcedRecord());
                }
                if (this._rowsAggregate != null) {
                    final int initRecsSize = this.getSizeOfInitialSheetRecords(k);
                    final int currentPos = ptv.getPosition();
                    ptv.visitRecord(this._rowsAggregate.createIndexRecord(currentPos, initRecsSize));
                }
            }
        }
    }
    
    private int getSizeOfInitialSheetRecords(final int bofRecordIndex) {
        int result = 0;
        for (int j = bofRecordIndex + 1; j < this._records.size(); ++j) {
            final RecordBase tmpRec = this._records.get(j);
            if (tmpRec instanceof RowRecordsAggregate) {
                break;
            }
            result += tmpRec.getRecordSize();
        }
        if (this._isUncalced) {
            result += UncalcedRecord.getStaticRecordSize();
        }
        return result;
    }
    
    public void addValueRecord(final int row, final CellValueRecordInterface col) {
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "add value record  row" + row);
        }
        final DimensionsRecord d = this._dimensions;
        if (col.getColumn() >= d.getLastCol()) {
            d.setLastCol((short)(col.getColumn() + 1));
        }
        if (col.getColumn() < d.getFirstCol()) {
            d.setFirstCol(col.getColumn());
        }
        this._rowsAggregate.insertCell(col);
    }
    
    public void removeValueRecord(final int row, final CellValueRecordInterface col) {
        InternalSheet.log.log(1, "remove value record row " + row);
        this._rowsAggregate.removeCell(col);
    }
    
    public void replaceValueRecord(final CellValueRecordInterface newval) {
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "replaceValueRecord ");
        }
        this._rowsAggregate.removeCell(newval);
        this._rowsAggregate.insertCell(newval);
    }
    
    public void addRow(final RowRecord row) {
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "addRow ");
        }
        final DimensionsRecord d = this._dimensions;
        if (row.getRowNumber() >= d.getLastRow()) {
            d.setLastRow(row.getRowNumber() + 1);
        }
        if (row.getRowNumber() < d.getFirstRow()) {
            d.setFirstRow(row.getRowNumber());
        }
        final RowRecord existingRow = this._rowsAggregate.getRow(row.getRowNumber());
        if (existingRow != null) {
            this._rowsAggregate.removeRow(existingRow);
        }
        this._rowsAggregate.insertRow(row);
        if (InternalSheet.log.check(1)) {
            InternalSheet.log.log(1, "exit addRow");
        }
    }
    
    public void removeRow(final RowRecord row) {
        this._rowsAggregate.removeRow(row);
    }
    
    public Iterator<CellValueRecordInterface> getCellValueIterator() {
        return this._rowsAggregate.getCellValueIterator();
    }
    
    public RowRecord getNextRow() {
        if (this.rowRecIterator == null) {
            this.rowRecIterator = this._rowsAggregate.getIterator();
        }
        if (!this.rowRecIterator.hasNext()) {
            return null;
        }
        return this.rowRecIterator.next();
    }
    
    public RowRecord getRow(final int rownum) {
        return this._rowsAggregate.getRow(rownum);
    }
    
    static BOFRecord createBOF() {
        final BOFRecord retval = new BOFRecord();
        retval.setVersion(1536);
        retval.setType(16);
        retval.setBuild(3515);
        retval.setBuildYear(1996);
        retval.setHistoryBitMask(193);
        retval.setRequiredVersion(6);
        return retval;
    }
    
    private static CalcModeRecord createCalcMode() {
        final CalcModeRecord retval = new CalcModeRecord();
        retval.setCalcMode((short)1);
        return retval;
    }
    
    private static CalcCountRecord createCalcCount() {
        final CalcCountRecord retval = new CalcCountRecord();
        retval.setIterations((short)100);
        return retval;
    }
    
    private static RefModeRecord createRefMode() {
        final RefModeRecord retval = new RefModeRecord();
        retval.setMode((short)1);
        return retval;
    }
    
    private static IterationRecord createIteration() {
        return new IterationRecord(false);
    }
    
    private static DeltaRecord createDelta() {
        return new DeltaRecord(0.001);
    }
    
    private static SaveRecalcRecord createSaveRecalc() {
        final SaveRecalcRecord retval = new SaveRecalcRecord();
        retval.setRecalc(true);
        return retval;
    }
    
    private static PrintHeadersRecord createPrintHeaders() {
        final PrintHeadersRecord retval = new PrintHeadersRecord();
        retval.setPrintHeaders(false);
        return retval;
    }
    
    private static PrintGridlinesRecord createPrintGridlines() {
        final PrintGridlinesRecord retval = new PrintGridlinesRecord();
        retval.setPrintGridlines(false);
        return retval;
    }
    
    private static GridsetRecord createGridset() {
        final GridsetRecord retval = new GridsetRecord();
        retval.setGridset(true);
        return retval;
    }
    
    private static GutsRecord createGuts() {
        final GutsRecord retval = new GutsRecord();
        retval.setLeftRowGutter((short)0);
        retval.setTopColGutter((short)0);
        retval.setRowLevelMax((short)0);
        retval.setColLevelMax((short)0);
        return retval;
    }
    
    private GutsRecord getGutsRecord() {
        if (this._gutsRecord == null) {
            final GutsRecord result = createGuts();
            RecordOrderer.addNewSheetRecord(this._records, result);
            this._gutsRecord = result;
        }
        return this._gutsRecord;
    }
    
    private static DefaultRowHeightRecord createDefaultRowHeight() {
        final DefaultRowHeightRecord retval = new DefaultRowHeightRecord();
        retval.setOptionFlags((short)0);
        retval.setRowHeight((short)255);
        return retval;
    }
    
    private static WSBoolRecord createWSBool() {
        final WSBoolRecord retval = new WSBoolRecord();
        retval.setWSBool1((byte)4);
        retval.setWSBool2((byte)(-63));
        return retval;
    }
    
    private static DefaultColWidthRecord createDefaultColWidth() {
        final DefaultColWidthRecord retval = new DefaultColWidthRecord();
        retval.setColWidth(8);
        return retval;
    }
    
    public int getDefaultColumnWidth() {
        return this.defaultcolwidth.getColWidth();
    }
    
    public boolean isGridsPrinted() {
        if (this.gridset == null) {
            this.gridset = createGridset();
            final int loc = this.findFirstRecordLocBySid((short)10);
            this._records.add(loc, this.gridset);
        }
        return !this.gridset.getGridset();
    }
    
    public void setGridsPrinted(final boolean value) {
        this.gridset.setGridset(!value);
    }
    
    public void setDefaultColumnWidth(final int dcw) {
        this.defaultcolwidth.setColWidth(dcw);
    }
    
    public void setDefaultRowHeight(final short dch) {
        this.defaultrowheight.setRowHeight(dch);
        this.defaultrowheight.setOptionFlags((short)1);
    }
    
    public short getDefaultRowHeight() {
        return this.defaultrowheight.getRowHeight();
    }
    
    public int getColumnWidth(final int columnIndex) {
        final ColumnInfoRecord ci = this._columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            return ci.getColumnWidth();
        }
        return 256 * this.defaultcolwidth.getColWidth();
    }
    
    public short getXFIndexForColAt(final short columnIndex) {
        final ColumnInfoRecord ci = this._columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            return (short)ci.getXFIndex();
        }
        return 15;
    }
    
    public void setColumnWidth(final int column, final int width) {
        if (width > 65280) {
            throw new IllegalArgumentException("The maximum column width for an individual cell is 255 characters.");
        }
        this.setColumn(column, null, width, null, null, null);
    }
    
    public boolean isColumnHidden(final int columnIndex) {
        final ColumnInfoRecord cir = this._columnInfos.findColumnInfo(columnIndex);
        return cir != null && cir.getHidden();
    }
    
    public void setColumnHidden(final int column, final boolean hidden) {
        this.setColumn(column, null, null, null, hidden, null);
    }
    
    public void setDefaultColumnStyle(final int column, final int styleIndex) {
        this.setColumn(column, (short)styleIndex, null, null, null, null);
    }
    
    private void setColumn(final int column, final Short xfStyle, final Integer width, final Integer level, final Boolean hidden, final Boolean collapsed) {
        this._columnInfos.setColumn(column, xfStyle, width, level, hidden, collapsed);
    }
    
    public void groupColumnRange(final int fromColumn, final int toColumn, final boolean indent) {
        this._columnInfos.groupColumnRange(fromColumn, toColumn, indent);
        final int maxLevel = this._columnInfos.getMaxOutlineLevel();
        final GutsRecord guts = this.getGutsRecord();
        guts.setColLevelMax((short)(maxLevel + 1));
        if (maxLevel == 0) {
            guts.setTopColGutter((short)0);
        }
        else {
            guts.setTopColGutter((short)(29 + 12 * (maxLevel - 1)));
        }
    }
    
    private static DimensionsRecord createDimensions() {
        final DimensionsRecord retval = new DimensionsRecord();
        retval.setFirstCol((short)0);
        retval.setLastRow(1);
        retval.setFirstRow(0);
        retval.setLastCol((short)1);
        return retval;
    }
    
    private static WindowTwoRecord createWindowTwo() {
        final WindowTwoRecord retval = new WindowTwoRecord();
        retval.setOptions((short)1718);
        retval.setTopRow((short)0);
        retval.setLeftCol((short)0);
        retval.setHeaderColor(64);
        retval.setPageBreakZoom((short)0);
        retval.setNormalZoom((short)0);
        return retval;
    }
    
    private static SelectionRecord createSelection() {
        return new SelectionRecord(0, 0);
    }
    
    public short getTopRow() {
        return (short)((this.windowTwo == null) ? 0 : this.windowTwo.getTopRow());
    }
    
    public void setTopRow(final short topRow) {
        if (this.windowTwo != null) {
            this.windowTwo.setTopRow(topRow);
        }
    }
    
    public void setLeftCol(final short leftCol) {
        if (this.windowTwo != null) {
            this.windowTwo.setLeftCol(leftCol);
        }
    }
    
    public short getLeftCol() {
        return (short)((this.windowTwo == null) ? 0 : this.windowTwo.getLeftCol());
    }
    
    public int getActiveCellRow() {
        if (this._selection == null) {
            return 0;
        }
        return this._selection.getActiveCellRow();
    }
    
    public void setActiveCellRow(final int row) {
        if (this._selection != null) {
            this._selection.setActiveCellRow(row);
        }
    }
    
    public short getActiveCellCol() {
        if (this._selection == null) {
            return 0;
        }
        return (short)this._selection.getActiveCellCol();
    }
    
    public void setActiveCellCol(final short col) {
        if (this._selection != null) {
            this._selection.setActiveCellCol(col);
        }
    }
    
    public List<RecordBase> getRecords() {
        return this._records;
    }
    
    public GridsetRecord getGridsetRecord() {
        return this.gridset;
    }
    
    public Record findFirstRecordBySid(final short sid) {
        final int ix = this.findFirstRecordLocBySid(sid);
        if (ix < 0) {
            return null;
        }
        return this._records.get(ix);
    }
    
    public void setSCLRecord(final SCLRecord sclRecord) {
        final int oldRecordLoc = this.findFirstRecordLocBySid((short)160);
        if (oldRecordLoc == -1) {
            final int windowRecordLoc = this.findFirstRecordLocBySid((short)574);
            this._records.add(windowRecordLoc + 1, sclRecord);
        }
        else {
            this._records.set(oldRecordLoc, sclRecord);
        }
    }
    
    public int findFirstRecordLocBySid(final short sid) {
        for (int max = this._records.size(), i = 0; i < max; ++i) {
            final Object rb = this._records.get(i);
            if (rb instanceof Record) {
                final Record record = (Record)rb;
                if (record.getSid() == sid) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public WindowTwoRecord getWindowTwo() {
        return this.windowTwo;
    }
    
    public PrintGridlinesRecord getPrintGridlines() {
        return this.printGridlines;
    }
    
    public void setPrintGridlines(final PrintGridlinesRecord newPrintGridlines) {
        this.printGridlines = newPrintGridlines;
    }
    
    public PrintHeadersRecord getPrintHeaders() {
        return this.printHeaders;
    }
    
    public void setPrintHeaders(final PrintHeadersRecord newPrintHeaders) {
        this.printHeaders = newPrintHeaders;
    }
    
    public void setSelected(final boolean sel) {
        this.windowTwo.setSelected(sel);
    }
    
    public void createFreezePane(final int colSplit, final int rowSplit, final int topRow, final int leftmostColumn) {
        final int paneLoc = this.findFirstRecordLocBySid((short)65);
        if (paneLoc != -1) {
            this._records.remove(paneLoc);
        }
        if (colSplit == 0 && rowSplit == 0) {
            this.windowTwo.setFreezePanes(false);
            this.windowTwo.setFreezePanesNoSplit(false);
            final SelectionRecord sel = (SelectionRecord)this.findFirstRecordBySid((short)29);
            if (sel != null) {
                sel.setPane((byte)3);
            }
            return;
        }
        final int loc = this.findFirstRecordLocBySid((short)574);
        final PaneRecord pane = new PaneRecord();
        pane.setX((short)colSplit);
        pane.setY((short)rowSplit);
        pane.setTopRow((short)topRow);
        pane.setLeftColumn((short)leftmostColumn);
        if (rowSplit == 0) {
            pane.setTopRow((short)0);
            pane.setActivePane((short)1);
        }
        else if (colSplit == 0) {
            pane.setLeftColumn((short)0);
            pane.setActivePane((short)2);
        }
        else {
            pane.setActivePane((short)0);
        }
        this._records.add(loc + 1, pane);
        this.windowTwo.setFreezePanes(true);
        this.windowTwo.setFreezePanesNoSplit(true);
        final SelectionRecord sel2 = (SelectionRecord)this.findFirstRecordBySid((short)29);
        if (sel2 != null) {
            sel2.setPane((byte)pane.getActivePane());
        }
    }
    
    public void createSplitPane(final int xSplitPos, final int ySplitPos, final int topRow, final int leftmostColumn, final int activePane) {
        final int paneLoc = this.findFirstRecordLocBySid((short)65);
        if (paneLoc != -1) {
            this._records.remove(paneLoc);
        }
        final int loc = this.findFirstRecordLocBySid((short)574);
        final PaneRecord r = new PaneRecord();
        r.setX((short)xSplitPos);
        r.setY((short)ySplitPos);
        r.setTopRow((short)topRow);
        r.setLeftColumn((short)leftmostColumn);
        r.setActivePane((short)activePane);
        this._records.add(loc + 1, r);
        this.windowTwo.setFreezePanes(false);
        this.windowTwo.setFreezePanesNoSplit(false);
        final SelectionRecord sel = (SelectionRecord)this.findFirstRecordBySid((short)29);
        if (sel != null) {
            sel.setPane((byte)0);
        }
    }
    
    public PaneInformation getPaneInformation() {
        final PaneRecord rec = (PaneRecord)this.findFirstRecordBySid((short)65);
        if (rec == null) {
            return null;
        }
        return new PaneInformation(rec.getX(), rec.getY(), rec.getTopRow(), rec.getLeftColumn(), (byte)rec.getActivePane(), this.windowTwo.getFreezePanes());
    }
    
    public SelectionRecord getSelection() {
        return this._selection;
    }
    
    public void setSelection(final SelectionRecord selection) {
        this._selection = selection;
    }
    
    public WorksheetProtectionBlock getProtectionBlock() {
        return this._protectionBlock;
    }
    
    public void setDisplayGridlines(final boolean show) {
        this.windowTwo.setDisplayGridlines(show);
    }
    
    public boolean isDisplayGridlines() {
        return this.windowTwo.getDisplayGridlines();
    }
    
    public void setDisplayFormulas(final boolean show) {
        this.windowTwo.setDisplayFormulas(show);
    }
    
    public boolean isDisplayFormulas() {
        return this.windowTwo.getDisplayFormulas();
    }
    
    public void setDisplayRowColHeadings(final boolean show) {
        this.windowTwo.setDisplayRowColHeadings(show);
    }
    
    public boolean isDisplayRowColHeadings() {
        return this.windowTwo.getDisplayRowColHeadings();
    }
    
    public void setPrintRowColHeadings(final boolean show) {
        this.windowTwo.setDisplayRowColHeadings(show);
    }
    
    public boolean isPrintRowColHeadings() {
        return this.windowTwo.getDisplayRowColHeadings();
    }
    
    public boolean getUncalced() {
        return this._isUncalced;
    }
    
    public void setUncalced(final boolean uncalced) {
        this._isUncalced = uncalced;
    }
    
    public int aggregateDrawingRecords(final DrawingManager2 drawingManager, final boolean createIfMissing) {
        int loc = this.findFirstRecordLocBySid((short)236);
        final boolean noDrawingRecordsFound = loc == -1;
        if (!noDrawingRecordsFound) {
            final List<RecordBase> records = this.getRecords();
            EscherAggregate.createAggregate(records, loc);
            return loc;
        }
        if (!createIfMissing) {
            return -1;
        }
        final EscherAggregate aggregate = new EscherAggregate(true);
        loc = this.findFirstRecordLocBySid((short)9876);
        if (loc == -1) {
            loc = this.findFirstRecordLocBySid((short)574);
        }
        else {
            this.getRecords().remove(loc);
        }
        this.getRecords().add(loc, aggregate);
        return loc;
    }
    
    public void preSerialize() {
        for (final RecordBase r : this.getRecords()) {
            if (r instanceof EscherAggregate) {
                r.getRecordSize();
            }
        }
    }
    
    public PageSettingsBlock getPageSettings() {
        if (this._psBlock == null) {
            this._psBlock = new PageSettingsBlock();
            RecordOrderer.addNewSheetRecord(this._records, this._psBlock);
        }
        return this._psBlock;
    }
    
    public void setColumnGroupCollapsed(final int columnNumber, final boolean collapsed) {
        if (collapsed) {
            this._columnInfos.collapseColumn(columnNumber);
        }
        else {
            this._columnInfos.expandColumn(columnNumber);
        }
    }
    
    public void groupRowRange(final int fromRow, final int toRow, final boolean indent) {
        for (int rowNum = fromRow; rowNum <= toRow; ++rowNum) {
            RowRecord row = this.getRow(rowNum);
            if (row == null) {
                row = RowRecordsAggregate.createRow(rowNum);
                this.addRow(row);
            }
            int level = row.getOutlineLevel();
            if (indent) {
                ++level;
            }
            else {
                --level;
            }
            level = Math.max(0, level);
            level = Math.min(7, level);
            row.setOutlineLevel((short)level);
        }
        this.recalcRowGutter();
    }
    
    private void recalcRowGutter() {
        int maxLevel = 0;
        final Iterator<RowRecord> iterator = this._rowsAggregate.getIterator();
        while (iterator.hasNext()) {
            final RowRecord rowRecord = iterator.next();
            maxLevel = Math.max(rowRecord.getOutlineLevel(), maxLevel);
        }
        final GutsRecord guts = this.getGutsRecord();
        guts.setRowLevelMax((short)(maxLevel + 1));
        guts.setLeftRowGutter((short)(29 + 12 * maxLevel));
    }
    
    public DataValidityTable getOrCreateDataValidityTable() {
        if (this._dataValidityTable == null) {
            final DataValidityTable result = new DataValidityTable();
            RecordOrderer.addNewSheetRecord(this._records, result);
            this._dataValidityTable = result;
        }
        return this._dataValidityTable;
    }
    
    public NoteRecord[] getNoteRecords() {
        final List<NoteRecord> temp = new ArrayList<NoteRecord>();
        for (int i = this._records.size() - 1; i >= 0; --i) {
            final RecordBase rec = this._records.get(i);
            if (rec instanceof NoteRecord) {
                temp.add((NoteRecord)rec);
            }
        }
        if (temp.size() < 1) {
            return NoteRecord.EMPTY_ARRAY;
        }
        final NoteRecord[] result = new NoteRecord[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    public int getColumnOutlineLevel(final int columnIndex) {
        return this._columnInfos.getOutlineLevel(columnIndex);
    }
    
    public int getMinColumnIndex() {
        return this._columnInfos.getMinColumnIndex();
    }
    
    public int getMaxColumnIndex() {
        return this._columnInfos.getMaxColumnIndex();
    }
    
    static {
        InternalSheet.log = POILogFactory.getLogger(InternalSheet.class);
    }
    
    public static class UnsupportedBOFType extends RecordFormatException
    {
        private final int type;
        
        protected UnsupportedBOFType(final int type) {
            super("BOF not of a supported type, found " + type);
            this.type = type;
        }
        
        public int getType() {
            return this.type;
        }
    }
    
    private static final class RecordCloner implements RecordAggregate.RecordVisitor
    {
        private final List<Record> _destList;
        
        public RecordCloner(final List<Record> destList) {
            this._destList = destList;
        }
        
        @Override
        public void visitRecord(final Record r) {
            this._destList.add(r.copy());
        }
    }
}
