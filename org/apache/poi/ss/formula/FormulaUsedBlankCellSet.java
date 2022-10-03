package org.apache.poi.ss.formula;

import org.apache.poi.ss.util.CellReference;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

final class FormulaUsedBlankCellSet
{
    private final Map<BookSheetKey, BlankCellSheetGroup> _sheetGroupsByBookSheet;
    
    public FormulaUsedBlankCellSet() {
        this._sheetGroupsByBookSheet = new HashMap<BookSheetKey, BlankCellSheetGroup>();
    }
    
    public void addCell(final EvaluationWorkbook evalWorkbook, final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex) {
        final BlankCellSheetGroup sbcg = this.getSheetGroup(evalWorkbook, bookIndex, sheetIndex);
        sbcg.addCell(rowIndex, columnIndex);
    }
    
    private BlankCellSheetGroup getSheetGroup(final EvaluationWorkbook evalWorkbook, final int bookIndex, final int sheetIndex) {
        final BookSheetKey key = new BookSheetKey(bookIndex, sheetIndex);
        BlankCellSheetGroup result = this._sheetGroupsByBookSheet.get(key);
        if (result == null) {
            result = new BlankCellSheetGroup(evalWorkbook.getSheet(sheetIndex).getLastRowNum());
            this._sheetGroupsByBookSheet.put(key, result);
        }
        return result;
    }
    
    public boolean containsCell(final BookSheetKey key, final int rowIndex, final int columnIndex) {
        final BlankCellSheetGroup bcsg = this._sheetGroupsByBookSheet.get(key);
        return bcsg != null && bcsg.containsCell(rowIndex, columnIndex);
    }
    
    public boolean isEmpty() {
        return this._sheetGroupsByBookSheet.isEmpty();
    }
    
    public static final class BookSheetKey
    {
        private final int _bookIndex;
        private final int _sheetIndex;
        
        public BookSheetKey(final int bookIndex, final int sheetIndex) {
            this._bookIndex = bookIndex;
            this._sheetIndex = sheetIndex;
        }
        
        @Override
        public int hashCode() {
            return this._bookIndex * 17 + this._sheetIndex;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof BookSheetKey)) {
                return false;
            }
            final BookSheetKey other = (BookSheetKey)obj;
            return this._bookIndex == other._bookIndex && this._sheetIndex == other._sheetIndex;
        }
    }
    
    private static final class BlankCellSheetGroup
    {
        private final List<BlankCellRectangleGroup> _rectangleGroups;
        private int _currentRowIndex;
        private int _firstColumnIndex;
        private int _lastColumnIndex;
        private BlankCellRectangleGroup _currentRectangleGroup;
        private int _lastDefinedRow;
        
        public BlankCellSheetGroup(final int lastDefinedRow) {
            this._rectangleGroups = new ArrayList<BlankCellRectangleGroup>();
            this._currentRowIndex = -1;
            this._lastDefinedRow = lastDefinedRow;
        }
        
        public void addCell(final int rowIndex, final int columnIndex) {
            if (rowIndex > this._lastDefinedRow) {
                return;
            }
            if (this._currentRowIndex == -1) {
                this._currentRowIndex = rowIndex;
                this._firstColumnIndex = columnIndex;
                this._lastColumnIndex = columnIndex;
            }
            else if (this._currentRowIndex == rowIndex && this._lastColumnIndex + 1 == columnIndex) {
                this._lastColumnIndex = columnIndex;
            }
            else {
                if (this._currentRectangleGroup == null) {
                    this._currentRectangleGroup = new BlankCellRectangleGroup(this._currentRowIndex, this._firstColumnIndex, this._lastColumnIndex);
                }
                else if (!this._currentRectangleGroup.acceptRow(this._currentRowIndex, this._firstColumnIndex, this._lastColumnIndex)) {
                    this._rectangleGroups.add(this._currentRectangleGroup);
                    this._currentRectangleGroup = new BlankCellRectangleGroup(this._currentRowIndex, this._firstColumnIndex, this._lastColumnIndex);
                }
                this._currentRowIndex = rowIndex;
                this._firstColumnIndex = columnIndex;
                this._lastColumnIndex = columnIndex;
            }
        }
        
        public boolean containsCell(final int rowIndex, final int columnIndex) {
            if (rowIndex > this._lastDefinedRow) {
                return true;
            }
            for (int i = this._rectangleGroups.size() - 1; i >= 0; --i) {
                final BlankCellRectangleGroup bcrg = this._rectangleGroups.get(i);
                if (bcrg.containsCell(rowIndex, columnIndex)) {
                    return true;
                }
            }
            return (this._currentRectangleGroup != null && this._currentRectangleGroup.containsCell(rowIndex, columnIndex)) || (this._currentRowIndex != -1 && this._currentRowIndex == rowIndex && this._firstColumnIndex <= columnIndex && columnIndex <= this._lastColumnIndex);
        }
    }
    
    private static final class BlankCellRectangleGroup
    {
        private final int _firstRowIndex;
        private final int _firstColumnIndex;
        private final int _lastColumnIndex;
        private int _lastRowIndex;
        
        public BlankCellRectangleGroup(final int firstRowIndex, final int firstColumnIndex, final int lastColumnIndex) {
            this._firstRowIndex = firstRowIndex;
            this._firstColumnIndex = firstColumnIndex;
            this._lastColumnIndex = lastColumnIndex;
            this._lastRowIndex = firstRowIndex;
        }
        
        public boolean containsCell(final int rowIndex, final int columnIndex) {
            return columnIndex >= this._firstColumnIndex && columnIndex <= this._lastColumnIndex && rowIndex >= this._firstRowIndex && rowIndex <= this._lastRowIndex;
        }
        
        public boolean acceptRow(final int rowIndex, final int firstColumnIndex, final int lastColumnIndex) {
            if (firstColumnIndex != this._firstColumnIndex) {
                return false;
            }
            if (lastColumnIndex != this._lastColumnIndex) {
                return false;
            }
            if (rowIndex != this._lastRowIndex + 1) {
                return false;
            }
            this._lastRowIndex = rowIndex;
            return true;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(64);
            final CellReference crA = new CellReference(this._firstRowIndex, this._firstColumnIndex, false, false);
            final CellReference crB = new CellReference(this._lastRowIndex, this._lastColumnIndex, false, false);
            sb.append(this.getClass().getName());
            sb.append(" [").append(crA.formatAsString()).append(':').append(crB.formatAsString()).append("]");
            return sb.toString();
        }
    }
}
