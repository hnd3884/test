package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.IndexRecord;
import java.util.function.Consumer;
import org.apache.poi.hssf.record.DBCellRecord;
import java.util.Iterator;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.model.RecordStream;
import java.util.ArrayList;
import java.util.TreeMap;
import org.apache.poi.hssf.record.Record;
import java.util.List;
import org.apache.poi.hssf.record.RowRecord;
import java.util.Map;

public final class RowRecordsAggregate extends RecordAggregate
{
    private int _firstrow;
    private int _lastrow;
    private final Map<Integer, RowRecord> _rowRecords;
    private final ValueRecordsAggregate _valuesAgg;
    private final List<Record> _unknownRecords;
    private final SharedValueManager _sharedValueManager;
    private RowRecord[] _rowRecordValues;
    
    public RowRecordsAggregate() {
        this(SharedValueManager.createEmpty());
    }
    
    private RowRecordsAggregate(final SharedValueManager svm) {
        this._firstrow = -1;
        this._lastrow = -1;
        if (svm == null) {
            throw new IllegalArgumentException("SharedValueManager must be provided.");
        }
        this._rowRecords = new TreeMap<Integer, RowRecord>();
        this._valuesAgg = new ValueRecordsAggregate();
        this._unknownRecords = new ArrayList<Record>();
        this._sharedValueManager = svm;
    }
    
    public RowRecordsAggregate(final RecordStream rs, final SharedValueManager svm) {
        this(svm);
        while (rs.hasNext()) {
            final Record rec = rs.getNext();
            switch (rec.getSid()) {
                case 520: {
                    this.insertRow((RowRecord)rec);
                    continue;
                }
                case 81: {
                    this.addUnknownRecord(rec);
                    continue;
                }
                case 215: {
                    continue;
                }
                default: {
                    if (rec instanceof UnknownRecord) {
                        this.addUnknownRecord(rec);
                        while (rs.peekNextSid() == 60) {
                            this.addUnknownRecord(rs.getNext());
                        }
                        continue;
                    }
                    if (rec instanceof MulBlankRecord) {
                        this._valuesAgg.addMultipleBlanks((MulBlankRecord)rec);
                        continue;
                    }
                    if (!(rec instanceof CellValueRecordInterface)) {
                        throw new RuntimeException("Unexpected record type (" + rec.getClass().getName() + ")");
                    }
                    this._valuesAgg.construct((CellValueRecordInterface)rec, rs, svm);
                    continue;
                }
            }
        }
    }
    
    private void addUnknownRecord(final Record rec) {
        this._unknownRecords.add(rec);
    }
    
    public void insertRow(final RowRecord row) {
        this._rowRecords.put(row.getRowNumber(), row);
        this._rowRecordValues = null;
        if (row.getRowNumber() < this._firstrow || this._firstrow == -1) {
            this._firstrow = row.getRowNumber();
        }
        if (row.getRowNumber() > this._lastrow || this._lastrow == -1) {
            this._lastrow = row.getRowNumber();
        }
    }
    
    public void removeRow(final RowRecord row) {
        final int rowIndex = row.getRowNumber();
        this._valuesAgg.removeAllCellsValuesForRow(rowIndex);
        final Integer key = rowIndex;
        final RowRecord rr = this._rowRecords.remove(key);
        if (rr == null) {
            throw new RuntimeException("Invalid row index (" + (int)key + ")");
        }
        if (row != rr) {
            this._rowRecords.put(key, rr);
            throw new RuntimeException("Attempt to remove row that does not belong to this sheet");
        }
        this._rowRecordValues = null;
    }
    
    public RowRecord getRow(final int rowIndex) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("The row number must be between 0 and " + maxrow + ", but had: " + rowIndex);
        }
        return this._rowRecords.get(rowIndex);
    }
    
    public int getPhysicalNumberOfRows() {
        return this._rowRecords.size();
    }
    
    public int getFirstRowNum() {
        return this._firstrow;
    }
    
    public int getLastRowNum() {
        return this._lastrow;
    }
    
    public int getRowBlockCount() {
        int size = this._rowRecords.size() / 32;
        if (this._rowRecords.size() % 32 != 0) {
            ++size;
        }
        return size;
    }
    
    private int getRowBlockSize(final int block) {
        return 20 * this.getRowCountForBlock(block);
    }
    
    public int getRowCountForBlock(final int block) {
        final int startIndex = block * 32;
        int endIndex = startIndex + 32 - 1;
        if (endIndex >= this._rowRecords.size()) {
            endIndex = this._rowRecords.size() - 1;
        }
        return endIndex - startIndex + 1;
    }
    
    private int getStartRowNumberForBlock(final int block) {
        final int startIndex = block * 32;
        if (this._rowRecordValues == null) {
            this._rowRecordValues = this._rowRecords.values().toArray(new RowRecord[0]);
        }
        try {
            return this._rowRecordValues[startIndex].getRowNumber();
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Did not find start row for block " + block);
        }
    }
    
    private int getEndRowNumberForBlock(final int block) {
        int endIndex = (block + 1) * 32 - 1;
        if (endIndex >= this._rowRecords.size()) {
            endIndex = this._rowRecords.size() - 1;
        }
        if (this._rowRecordValues == null) {
            this._rowRecordValues = this._rowRecords.values().toArray(new RowRecord[0]);
        }
        try {
            return this._rowRecordValues[endIndex].getRowNumber();
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Did not find end row for block " + block);
        }
    }
    
    private int visitRowRecordsForBlock(final int blockIndex, final RecordVisitor rv) {
        final int startIndex = blockIndex * 32;
        final int endIndex = startIndex + 32;
        final Iterator<RowRecord> rowIterator = this._rowRecords.values().iterator();
        int i;
        for (i = 0; i < startIndex; ++i) {
            rowIterator.next();
        }
        int result = 0;
        while (rowIterator.hasNext() && i++ < endIndex) {
            final Record rec = rowIterator.next();
            result += rec.getRecordSize();
            rv.visitRecord(rec);
        }
        return result;
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        final PositionTrackingVisitor stv = new PositionTrackingVisitor(rv, 0);
        for (int blockCount = this.getRowBlockCount(), blockIndex = 0; blockIndex < blockCount; ++blockIndex) {
            int pos = 0;
            final int rowBlockSize = this.visitRowRecordsForBlock(blockIndex, rv);
            pos += rowBlockSize;
            final int startRowNumber = this.getStartRowNumberForBlock(blockIndex);
            final int endRowNumber = this.getEndRowNumberForBlock(blockIndex);
            final List<Short> cellOffsets = new ArrayList<Short>();
            int cellRefOffset = rowBlockSize - 20;
            for (int row = startRowNumber; row <= endRowNumber; ++row) {
                if (this._valuesAgg.rowHasCells(row)) {
                    stv.setPosition(0);
                    this._valuesAgg.visitCellsForRow(row, stv);
                    final int rowCellSize = stv.getPosition();
                    pos += rowCellSize;
                    cellOffsets.add((short)cellRefOffset);
                    cellRefOffset = rowCellSize;
                }
            }
            rv.visitRecord(new DBCellRecord(pos, shortListToArray(cellOffsets)));
        }
        this._unknownRecords.forEach(rv::visitRecord);
    }
    
    private static short[] shortListToArray(final List<Short> list) {
        final short[] arr = new short[list.size()];
        int idx = 0;
        for (final Short s : list) {
            arr[idx++] = s;
        }
        return arr;
    }
    
    public Iterator<RowRecord> getIterator() {
        return this._rowRecords.values().iterator();
    }
    
    public int findStartOfRowOutlineGroup(final int row) {
        RowRecord rowRecord = this.getRow(row);
        final int level = rowRecord.getOutlineLevel();
        int currentRow;
        for (currentRow = row; currentRow >= 0 && this.getRow(currentRow) != null; --currentRow) {
            rowRecord = this.getRow(currentRow);
            if (rowRecord.getOutlineLevel() < level) {
                return currentRow + 1;
            }
        }
        return currentRow + 1;
    }
    
    public int findEndOfRowOutlineGroup(final int row) {
        int level;
        int currentRow;
        for (level = this.getRow(row).getOutlineLevel(), currentRow = row; currentRow < this.getLastRowNum() && this.getRow(currentRow) != null && this.getRow(currentRow).getOutlineLevel() >= level; ++currentRow) {}
        return currentRow - 1;
    }
    
    private int writeHidden(final RowRecord pRowRecord, final int row) {
        int rowIx = row;
        RowRecord rowRecord = pRowRecord;
        for (int level = rowRecord.getOutlineLevel(); rowRecord != null && this.getRow(rowIx).getOutlineLevel() >= level; ++rowIx, rowRecord = this.getRow(rowIx)) {
            rowRecord.setZeroHeight(true);
        }
        return rowIx;
    }
    
    public void collapseRow(final int rowNumber) {
        final int startRow = this.findStartOfRowOutlineGroup(rowNumber);
        final RowRecord rowRecord = this.getRow(startRow);
        final int nextRowIx = this.writeHidden(rowRecord, startRow);
        RowRecord row = this.getRow(nextRowIx);
        if (row == null) {
            row = createRow(nextRowIx);
            this.insertRow(row);
        }
        row.setColapsed(true);
    }
    
    public static RowRecord createRow(final int rowNumber) {
        return new RowRecord(rowNumber);
    }
    
    public boolean isRowGroupCollapsed(final int row) {
        final int collapseRow = this.findEndOfRowOutlineGroup(row) + 1;
        return this.getRow(collapseRow) != null && this.getRow(collapseRow).getColapsed();
    }
    
    public void expandRow(final int rowNumber) {
        if (rowNumber == -1) {
            return;
        }
        if (!this.isRowGroupCollapsed(rowNumber)) {
            return;
        }
        final int startIdx = this.findStartOfRowOutlineGroup(rowNumber);
        final RowRecord row = this.getRow(startIdx);
        final int endIdx = this.findEndOfRowOutlineGroup(rowNumber);
        if (!this.isRowGroupHiddenByParent(rowNumber)) {
            for (int i = startIdx; i <= endIdx; ++i) {
                final RowRecord otherRow = this.getRow(i);
                if (row.getOutlineLevel() == otherRow.getOutlineLevel() || !this.isRowGroupCollapsed(i)) {
                    otherRow.setZeroHeight(false);
                }
            }
        }
        this.getRow(endIdx + 1).setColapsed(false);
    }
    
    public boolean isRowGroupHiddenByParent(final int row) {
        final int endOfOutlineGroupIdx = this.findEndOfRowOutlineGroup(row);
        int endLevel;
        boolean endHidden;
        if (this.getRow(endOfOutlineGroupIdx + 1) == null) {
            endLevel = 0;
            endHidden = false;
        }
        else {
            endLevel = this.getRow(endOfOutlineGroupIdx + 1).getOutlineLevel();
            endHidden = this.getRow(endOfOutlineGroupIdx + 1).getZeroHeight();
        }
        final int startOfOutlineGroupIdx = this.findStartOfRowOutlineGroup(row);
        int startLevel;
        boolean startHidden;
        if (startOfOutlineGroupIdx - 1 < 0 || this.getRow(startOfOutlineGroupIdx - 1) == null) {
            startLevel = 0;
            startHidden = false;
        }
        else {
            startLevel = this.getRow(startOfOutlineGroupIdx - 1).getOutlineLevel();
            startHidden = this.getRow(startOfOutlineGroupIdx - 1).getZeroHeight();
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }
    
    public Iterator<CellValueRecordInterface> getCellValueIterator() {
        return this._valuesAgg.iterator();
    }
    
    public IndexRecord createIndexRecord(final int indexRecordOffset, final int sizeOfInitialSheetRecords) {
        final IndexRecord result = new IndexRecord();
        result.setFirstRow(this._firstrow);
        result.setLastRowAdd1(this._lastrow + 1);
        final int blockCount = this.getRowBlockCount();
        final int indexRecSize = IndexRecord.getRecordSizeForBlockCount(blockCount);
        int currentOffset = indexRecordOffset + indexRecSize + sizeOfInitialSheetRecords;
        for (int block = 0; block < blockCount; ++block) {
            currentOffset += this.getRowBlockSize(block);
            currentOffset += this._valuesAgg.getRowCellBlockSize(this.getStartRowNumberForBlock(block), this.getEndRowNumberForBlock(block));
            result.addDbcell(currentOffset);
            currentOffset += 8 + this.getRowCountForBlock(block) * 2;
        }
        return result;
    }
    
    public void insertCell(final CellValueRecordInterface cvRec) {
        this._valuesAgg.insertCell(cvRec);
    }
    
    public void removeCell(final CellValueRecordInterface cvRec) {
        if (cvRec instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate)cvRec).notifyFormulaChanging();
        }
        this._valuesAgg.removeCell(cvRec);
    }
    
    public FormulaRecordAggregate createFormula(final int row, final int col) {
        final FormulaRecord fr = new FormulaRecord();
        fr.setRow(row);
        fr.setColumn((short)col);
        return new FormulaRecordAggregate(fr, null, this._sharedValueManager);
    }
    
    public void updateFormulasAfterRowShift(final FormulaShifter formulaShifter, final int currentExternSheetIndex) {
        this._valuesAgg.updateFormulasAfterRowShift(formulaShifter, currentExternSheetIndex);
    }
    
    public DimensionsRecord createDimensions() {
        final DimensionsRecord result = new DimensionsRecord();
        result.setFirstRow(this._firstrow);
        result.setLastRow(this._lastrow);
        result.setFirstCol((short)this._valuesAgg.getFirstCellNum());
        result.setLastCol((short)this._valuesAgg.getLastCellNum());
        return result;
    }
}
