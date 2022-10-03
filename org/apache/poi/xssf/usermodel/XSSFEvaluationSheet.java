package org.apache.poi.xssf.usermodel;

import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.HashMap;
import org.apache.poi.ss.formula.EvaluationCell;
import java.util.Map;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.formula.EvaluationSheet;

@Internal
final class XSSFEvaluationSheet implements EvaluationSheet
{
    private final XSSFSheet _xs;
    private Map<CellKey, EvaluationCell> _cellCache;
    
    public XSSFEvaluationSheet(final XSSFSheet sheet) {
        this._xs = sheet;
    }
    
    public XSSFSheet getXSSFSheet() {
        return this._xs;
    }
    
    public int getLastRowNum() {
        return this._xs.getLastRowNum();
    }
    
    public boolean isRowHidden(final int rowIndex) {
        final XSSFRow row = this._xs.getRow(rowIndex);
        return row != null && row.getZeroHeight();
    }
    
    public void clearAllCachedResultValues() {
        this._cellCache = null;
    }
    
    public EvaluationCell getCell(final int rowIndex, final int columnIndex) {
        if (rowIndex > this.getLastRowNum()) {
            return null;
        }
        if (this._cellCache == null) {
            this._cellCache = new HashMap<CellKey, EvaluationCell>(this._xs.getLastRowNum() * 3);
            for (final Row row : this._xs) {
                final int rowNum = row.getRowNum();
                for (final Cell cell : row) {
                    final CellKey key = new CellKey(rowNum, cell.getColumnIndex());
                    final EvaluationCell evalcell = (EvaluationCell)new XSSFEvaluationCell((XSSFCell)cell, this);
                    this._cellCache.put(key, evalcell);
                }
            }
        }
        final CellKey key2 = new CellKey(rowIndex, columnIndex);
        EvaluationCell evalcell2 = this._cellCache.get(key2);
        if (evalcell2 == null) {
            final XSSFRow row2 = this._xs.getRow(rowIndex);
            if (row2 == null) {
                return null;
            }
            final XSSFCell cell2 = row2.getCell(columnIndex);
            if (cell2 == null) {
                return null;
            }
            evalcell2 = (EvaluationCell)new XSSFEvaluationCell(cell2, this);
            this._cellCache.put(key2, evalcell2);
        }
        return evalcell2;
    }
    
    private static class CellKey
    {
        private final int _row;
        private final int _col;
        private int _hash;
        
        protected CellKey(final int row, final int col) {
            this._hash = -1;
            this._row = row;
            this._col = col;
        }
        
        @Override
        public int hashCode() {
            if (this._hash == -1) {
                this._hash = (629 + this._row) * 37 + this._col;
            }
            return this._hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof CellKey)) {
                return false;
            }
            final CellKey oKey = (CellKey)obj;
            return this._row == oKey._row && this._col == oKey._col;
        }
    }
}
