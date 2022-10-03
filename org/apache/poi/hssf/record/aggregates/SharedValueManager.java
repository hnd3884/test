package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.hssf.record.SharedValueRecordBase;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import java.util.Map;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.ArrayRecord;
import java.util.List;

public final class SharedValueManager
{
    private final List<ArrayRecord> _arrayRecords;
    private final TableRecord[] _tableRecords;
    private final Map<SharedFormulaRecord, SharedFormulaGroup> _groupsBySharedFormulaRecord;
    private Map<Integer, SharedFormulaGroup> _groupsCache;
    
    public static SharedValueManager createEmpty() {
        return new SharedValueManager(new SharedFormulaRecord[0], new CellReference[0], new ArrayRecord[0], new TableRecord[0]);
    }
    
    private SharedValueManager(final SharedFormulaRecord[] sharedFormulaRecords, final CellReference[] firstCells, final ArrayRecord[] arrayRecords, final TableRecord[] tableRecords) {
        final int nShF = sharedFormulaRecords.length;
        if (nShF != firstCells.length) {
            throw new IllegalArgumentException("array sizes don't match: " + nShF + "!=" + firstCells.length + ".");
        }
        this._arrayRecords = toList(arrayRecords);
        this._tableRecords = tableRecords;
        final Map<SharedFormulaRecord, SharedFormulaGroup> m = new HashMap<SharedFormulaRecord, SharedFormulaGroup>(nShF * 3 / 2);
        for (int i = 0; i < nShF; ++i) {
            final SharedFormulaRecord sfr = sharedFormulaRecords[i];
            m.put(sfr, new SharedFormulaGroup(sfr, firstCells[i]));
        }
        this._groupsBySharedFormulaRecord = m;
    }
    
    private static <Z> List<Z> toList(final Z[] zz) {
        final List<Z> result = new ArrayList<Z>(zz.length);
        Collections.addAll(result, zz);
        return result;
    }
    
    public static SharedValueManager create(final SharedFormulaRecord[] sharedFormulaRecords, final CellReference[] firstCells, final ArrayRecord[] arrayRecords, final TableRecord[] tableRecords) {
        if (sharedFormulaRecords.length + firstCells.length + arrayRecords.length + tableRecords.length < 1) {
            return createEmpty();
        }
        return new SharedValueManager(sharedFormulaRecords, firstCells, arrayRecords, tableRecords);
    }
    
    public SharedFormulaRecord linkSharedFormulaRecord(final CellReference firstCell, final FormulaRecordAggregate agg) {
        final SharedFormulaGroup result = this.findFormulaGroupForCell(firstCell);
        if (null == result) {
            throw new RuntimeException("Failed to find a matching shared formula record");
        }
        result.add(agg);
        return result.getSFR();
    }
    
    private SharedFormulaGroup findFormulaGroupForCell(final CellReference cellRef) {
        if (null == this._groupsCache) {
            this._groupsCache = new HashMap<Integer, SharedFormulaGroup>(this._groupsBySharedFormulaRecord.size());
            for (final SharedFormulaGroup group : this._groupsBySharedFormulaRecord.values()) {
                this._groupsCache.put(this.getKeyForCache(group._firstCell), group);
            }
        }
        return this._groupsCache.get(this.getKeyForCache(cellRef));
    }
    
    private Integer getKeyForCache(final CellReference cellRef) {
        return cellRef.getCol() + 1 << 16 | cellRef.getRow();
    }
    
    public SharedValueRecordBase getRecordForFirstCell(final FormulaRecordAggregate agg) {
        final CellReference firstCell = agg.getFormulaRecord().getFormula().getExpReference();
        if (firstCell == null) {
            return null;
        }
        final int row = firstCell.getRow();
        final int column = firstCell.getCol();
        if (agg.getRow() != row || agg.getColumn() != column) {
            return null;
        }
        if (!this._groupsBySharedFormulaRecord.isEmpty()) {
            final SharedFormulaGroup sfg = this.findFormulaGroupForCell(firstCell);
            if (null != sfg) {
                return sfg.getSFR();
            }
        }
        for (final TableRecord tr : this._tableRecords) {
            if (tr.isFirstCell(row, column)) {
                return tr;
            }
        }
        for (final ArrayRecord ar : this._arrayRecords) {
            if (ar.isFirstCell(row, column)) {
                return ar;
            }
        }
        return null;
    }
    
    public void unlink(final SharedFormulaRecord sharedFormulaRecord) {
        final SharedFormulaGroup svg = this._groupsBySharedFormulaRecord.remove(sharedFormulaRecord);
        if (svg == null) {
            throw new IllegalStateException("Failed to find formulas for shared formula");
        }
        this._groupsCache = null;
        svg.unlinkSharedFormulas();
    }
    
    public void addArrayRecord(final ArrayRecord ar) {
        this._arrayRecords.add(ar);
    }
    
    public CellRangeAddress8Bit removeArrayFormula(final int rowIndex, final int columnIndex) {
        for (final ArrayRecord ar : this._arrayRecords) {
            if (ar.isInRange(rowIndex, columnIndex)) {
                this._arrayRecords.remove(ar);
                return ar.getRange();
            }
        }
        final String ref = new CellReference(rowIndex, columnIndex, false, false).formatAsString();
        throw new IllegalArgumentException("Specified cell " + ref + " is not part of an array formula.");
    }
    
    public ArrayRecord getArrayRecord(final int firstRow, final int firstColumn) {
        for (final ArrayRecord ar : this._arrayRecords) {
            if (ar.isFirstCell(firstRow, firstColumn)) {
                return ar;
            }
        }
        return null;
    }
    
    private static final class SharedFormulaGroup
    {
        private final SharedFormulaRecord _sfr;
        private final FormulaRecordAggregate[] _frAggs;
        private int _numberOfFormulas;
        private final CellReference _firstCell;
        
        public SharedFormulaGroup(final SharedFormulaRecord sfr, final CellReference firstCell) {
            if (!sfr.isInRange(firstCell.getRow(), firstCell.getCol())) {
                throw new IllegalArgumentException("First formula cell " + firstCell.formatAsString() + " is not shared formula range " + sfr.getRange() + ".");
            }
            this._sfr = sfr;
            this._firstCell = firstCell;
            final int width = sfr.getLastColumn() - sfr.getFirstColumn() + 1;
            final int height = sfr.getLastRow() - sfr.getFirstRow() + 1;
            this._frAggs = new FormulaRecordAggregate[width * height];
            this._numberOfFormulas = 0;
        }
        
        public void add(final FormulaRecordAggregate agg) {
            if (this._numberOfFormulas == 0 && (this._firstCell.getRow() != agg.getRow() || this._firstCell.getCol() != agg.getColumn())) {
                throw new IllegalStateException("shared formula coding error: " + this._firstCell.getCol() + '/' + this._firstCell.getRow() + " != " + agg.getColumn() + '/' + agg.getRow());
            }
            if (this._numberOfFormulas >= this._frAggs.length) {
                throw new RuntimeException("Too many formula records for shared formula group");
            }
            this._frAggs[this._numberOfFormulas++] = agg;
        }
        
        public void unlinkSharedFormulas() {
            for (int i = 0; i < this._numberOfFormulas; ++i) {
                this._frAggs[i].unlinkSharedFormula();
            }
        }
        
        public SharedFormulaRecord getSFR() {
            return this._sfr;
        }
        
        @Override
        public final String toString() {
            return this.getClass().getName() + " [" + this._sfr.getRange() + "]";
        }
    }
}
