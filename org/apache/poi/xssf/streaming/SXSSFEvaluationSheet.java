package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.formula.EvaluationSheet;

@Internal
final class SXSSFEvaluationSheet implements EvaluationSheet
{
    private final SXSSFSheet _xs;
    
    public SXSSFEvaluationSheet(final SXSSFSheet sheet) {
        this._xs = sheet;
    }
    
    public SXSSFSheet getSXSSFSheet() {
        return this._xs;
    }
    
    public int getLastRowNum() {
        return this._xs.getLastRowNum();
    }
    
    public boolean isRowHidden(final int rowIndex) {
        final SXSSFRow row = this._xs.getRow(rowIndex);
        return row != null && row.getZeroHeight();
    }
    
    public EvaluationCell getCell(final int rowIndex, final int columnIndex) {
        final SXSSFRow row = this._xs.getRow(rowIndex);
        if (row == null) {
            if (rowIndex <= this._xs.getLastFlushedRowNum()) {
                throw new SXSSFFormulaEvaluator.RowFlushedException(rowIndex);
            }
            return null;
        }
        else {
            final SXSSFCell cell = row.getCell(columnIndex);
            if (cell == null) {
                return null;
            }
            return (EvaluationCell)new SXSSFEvaluationCell(cell, this);
        }
    }
    
    public void clearAllCachedResultValues() {
    }
}
