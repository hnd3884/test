package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.formula.EvaluationSheet;

@Internal
final class HSSFEvaluationSheet implements EvaluationSheet
{
    private final HSSFSheet _hs;
    
    public HSSFEvaluationSheet(final HSSFSheet hs) {
        this._hs = hs;
    }
    
    public HSSFSheet getHSSFSheet() {
        return this._hs;
    }
    
    @Override
    public int getLastRowNum() {
        return this._hs.getLastRowNum();
    }
    
    @Override
    public boolean isRowHidden(final int rowIndex) {
        final HSSFRow row = this._hs.getRow(rowIndex);
        return row != null && row.getZeroHeight();
    }
    
    @Override
    public EvaluationCell getCell(final int rowIndex, final int columnIndex) {
        final HSSFRow row = this._hs.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        final HSSFCell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        return new HSSFEvaluationCell(cell, this);
    }
    
    @Override
    public void clearAllCachedResultValues() {
    }
}
