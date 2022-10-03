package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.util.Removal;
import java.util.Iterator;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;

public final class ValueRecordsAggregate implements Iterable<CellValueRecordInterface>
{
    private static final int MAX_ROW_INDEX = 65535;
    private static final int INDEX_NOT_SET = -1;
    private int firstcell;
    private int lastcell;
    private CellValueRecordInterface[][] records;
    
    public ValueRecordsAggregate() {
        this(-1, -1, new CellValueRecordInterface[30][]);
    }
    
    private ValueRecordsAggregate(final int firstCellIx, final int lastCellIx, final CellValueRecordInterface[][] pRecords) {
        this.firstcell = -1;
        this.lastcell = -1;
        this.firstcell = firstCellIx;
        this.lastcell = lastCellIx;
        this.records = pRecords;
    }
    
    public void insertCell(final CellValueRecordInterface cell) {
        final short column = cell.getColumn();
        final int row = cell.getRow();
        if (row >= this.records.length) {
            final CellValueRecordInterface[][] oldRecords = this.records;
            int newSize = oldRecords.length * 2;
            if (newSize < row + 1) {
                newSize = row + 1;
            }
            System.arraycopy(oldRecords, 0, this.records = new CellValueRecordInterface[newSize][], 0, oldRecords.length);
        }
        CellValueRecordInterface[] rowCells = this.records[row];
        if (rowCells == null) {
            int newSize = column + 1;
            if (newSize < 10) {
                newSize = 10;
            }
            rowCells = new CellValueRecordInterface[newSize];
            this.records[row] = rowCells;
        }
        if (column >= rowCells.length) {
            final CellValueRecordInterface[] oldRowCells = rowCells;
            int newSize2 = oldRowCells.length * 2;
            if (newSize2 < column + 1) {
                newSize2 = column + 1;
            }
            rowCells = new CellValueRecordInterface[newSize2];
            System.arraycopy(oldRowCells, 0, rowCells, 0, oldRowCells.length);
            this.records[row] = rowCells;
        }
        rowCells[column] = cell;
        if (column < this.firstcell || this.firstcell == -1) {
            this.firstcell = column;
        }
        if (column > this.lastcell || this.lastcell == -1) {
            this.lastcell = column;
        }
    }
    
    public void removeCell(final CellValueRecordInterface cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell must not be null");
        }
        final int row = cell.getRow();
        if (row >= this.records.length) {
            throw new RuntimeException("cell row is out of range");
        }
        final CellValueRecordInterface[] rowCells = this.records[row];
        if (rowCells == null) {
            throw new RuntimeException("cell row is already empty");
        }
        final short column = cell.getColumn();
        if (column >= rowCells.length) {
            throw new RuntimeException("cell column is out of range");
        }
        rowCells[column] = null;
    }
    
    public void removeAllCellsValuesForRow(final int rowIndex) {
        if (rowIndex < 0 || rowIndex > 65535) {
            throw new IllegalArgumentException("Specified rowIndex " + rowIndex + " is outside the allowable range (0.." + 65535 + ")");
        }
        if (rowIndex >= this.records.length) {
            return;
        }
        this.records[rowIndex] = null;
    }
    
    public int getPhysicalNumberOfCells() {
        int count = 0;
        for (int r = 0; r < this.records.length; ++r) {
            final CellValueRecordInterface[] rowCells = this.records[r];
            if (rowCells != null) {
                for (int c = 0; c < rowCells.length; ++c) {
                    if (rowCells[c] != null) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }
    
    public int getFirstCellNum() {
        return this.firstcell;
    }
    
    public int getLastCellNum() {
        return this.lastcell;
    }
    
    public void addMultipleBlanks(final MulBlankRecord mbr) {
        for (int j = 0; j < mbr.getNumColumns(); ++j) {
            final BlankRecord br = new BlankRecord();
            br.setColumn((short)(j + mbr.getFirstColumn()));
            br.setRow(mbr.getRow());
            br.setXFIndex(mbr.getXFAt(j));
            this.insertCell(br);
        }
    }
    
    public void construct(final CellValueRecordInterface rec, final RecordStream rs, final SharedValueManager sfh) {
        if (rec instanceof FormulaRecord) {
            final FormulaRecord formulaRec = (FormulaRecord)rec;
            final Class<? extends Record> nextClass = rs.peekNextClass();
            StringRecord cachedText;
            if (nextClass == StringRecord.class) {
                cachedText = (StringRecord)rs.getNext();
            }
            else {
                cachedText = null;
            }
            this.insertCell(new FormulaRecordAggregate(formulaRec, cachedText, sfh));
        }
        else {
            this.insertCell(rec);
        }
    }
    
    public int getRowCellBlockSize(final int startRow, final int endRow) {
        int result = 0;
        for (int rowIx = startRow; rowIx <= endRow && rowIx < this.records.length; ++rowIx) {
            result += getRowSerializedSize(this.records[rowIx]);
        }
        return result;
    }
    
    public boolean rowHasCells(final int row) {
        if (row >= this.records.length) {
            return false;
        }
        final CellValueRecordInterface[] rowCells = this.records[row];
        if (rowCells == null) {
            return false;
        }
        for (int col = 0; col < rowCells.length; ++col) {
            if (rowCells[col] != null) {
                return true;
            }
        }
        return false;
    }
    
    private static int getRowSerializedSize(final CellValueRecordInterface[] rowCells) {
        if (rowCells == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < rowCells.length; ++i) {
            final RecordBase cvr = (RecordBase)rowCells[i];
            if (cvr != null) {
                final int nBlank = countBlanks(rowCells, i);
                if (nBlank > 1) {
                    result += 10 + 2 * nBlank;
                    i += nBlank - 1;
                }
                else {
                    result += cvr.getRecordSize();
                }
            }
        }
        return result;
    }
    
    public void visitCellsForRow(final int rowIndex, final RecordAggregate.RecordVisitor rv) {
        final CellValueRecordInterface[] rowCells = this.records[rowIndex];
        if (rowCells == null) {
            throw new IllegalArgumentException("Row [" + rowIndex + "] is empty");
        }
        for (int i = 0; i < rowCells.length; ++i) {
            final RecordBase cvr = (RecordBase)rowCells[i];
            if (cvr != null) {
                final int nBlank = countBlanks(rowCells, i);
                if (nBlank > 1) {
                    rv.visitRecord(this.createMBR(rowCells, i, nBlank));
                    i += nBlank - 1;
                }
                else if (cvr instanceof RecordAggregate) {
                    final RecordAggregate agg = (RecordAggregate)cvr;
                    agg.visitContainedRecords(rv);
                }
                else {
                    rv.visitRecord((Record)cvr);
                }
            }
        }
    }
    
    private static int countBlanks(final CellValueRecordInterface[] rowCellValues, final int startIx) {
        int i;
        for (i = startIx; i < rowCellValues.length; ++i) {
            final CellValueRecordInterface cvr = rowCellValues[i];
            if (!(cvr instanceof BlankRecord)) {
                break;
            }
        }
        return i - startIx;
    }
    
    private MulBlankRecord createMBR(final CellValueRecordInterface[] cellValues, final int startIx, final int nBlank) {
        final short[] xfs = new short[nBlank];
        for (int i = 0; i < xfs.length; ++i) {
            xfs[i] = cellValues[startIx + i].getXFIndex();
        }
        final int rowIx = cellValues[startIx].getRow();
        return new MulBlankRecord(rowIx, startIx, xfs);
    }
    
    public void updateFormulasAfterRowShift(final FormulaShifter shifter, final int currentExternSheetIndex) {
        for (int i = 0; i < this.records.length; ++i) {
            final CellValueRecordInterface[] rowCells = this.records[i];
            if (rowCells != null) {
                for (int j = 0; j < rowCells.length; ++j) {
                    final CellValueRecordInterface cell = rowCells[j];
                    if (cell instanceof FormulaRecordAggregate) {
                        final FormulaRecordAggregate fra = (FormulaRecordAggregate)cell;
                        final Ptg[] ptgs = fra.getFormulaTokens();
                        final Ptg[] ptgs2 = ((FormulaRecordAggregate)cell).getFormulaRecord().getParsedExpression();
                        if (shifter.adjustFormula(ptgs, currentExternSheetIndex)) {
                            fra.setParsedExpression(ptgs);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public Iterator<CellValueRecordInterface> iterator() {
        return new ValueIterator();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public Object clone() {
        throw new RuntimeException("clone() should not be called.  ValueRecordsAggregate should be copied via Sheet.cloneSheet()");
    }
    
    class ValueIterator implements Iterator<CellValueRecordInterface>
    {
        int curRowIndex;
        int curColIndex;
        int nextRowIndex;
        int nextColIndex;
        
        public ValueIterator() {
            this.curColIndex = -1;
            this.nextColIndex = -1;
            this.getNextPos();
        }
        
        void getNextPos() {
            if (this.nextRowIndex >= ValueRecordsAggregate.this.records.length) {
                return;
            }
            while (this.nextRowIndex < ValueRecordsAggregate.this.records.length) {
                ++this.nextColIndex;
                if (ValueRecordsAggregate.this.records[this.nextRowIndex] == null || this.nextColIndex >= ValueRecordsAggregate.this.records[this.nextRowIndex].length) {
                    ++this.nextRowIndex;
                    this.nextColIndex = -1;
                }
                else {
                    if (ValueRecordsAggregate.this.records[this.nextRowIndex][this.nextColIndex] != null) {
                        return;
                    }
                    continue;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextRowIndex < ValueRecordsAggregate.this.records.length;
        }
        
        @Override
        public CellValueRecordInterface next() {
            if (!this.hasNext()) {
                throw new IndexOutOfBoundsException("iterator has no next");
            }
            this.curRowIndex = this.nextRowIndex;
            this.curColIndex = this.nextColIndex;
            final CellValueRecordInterface ret = ValueRecordsAggregate.this.records[this.curRowIndex][this.curColIndex];
            this.getNextPos();
            return ret;
        }
        
        @Override
        public void remove() {
            ValueRecordsAggregate.this.records[this.curRowIndex][this.curColIndex] = null;
        }
    }
}
