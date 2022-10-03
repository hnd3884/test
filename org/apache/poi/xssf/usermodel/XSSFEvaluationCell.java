package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.Internal;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationCell;

final class XSSFEvaluationCell implements EvaluationCell
{
    private final EvaluationSheet _evalSheet;
    private final XSSFCell _cell;
    
    public XSSFEvaluationCell(final XSSFCell cell, final XSSFEvaluationSheet evaluationSheet) {
        this._cell = cell;
        this._evalSheet = (EvaluationSheet)evaluationSheet;
    }
    
    public XSSFEvaluationCell(final XSSFCell cell) {
        this(cell, new XSSFEvaluationSheet(cell.getSheet()));
    }
    
    public Object getIdentityKey() {
        return this._cell;
    }
    
    public XSSFCell getXSSFCell() {
        return this._cell;
    }
    
    public boolean getBooleanCellValue() {
        return this._cell.getBooleanCellValue();
    }
    
    public CellType getCellType() {
        return this._cell.getCellType();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public CellType getCellTypeEnum() {
        return this.getCellType();
    }
    
    public int getColumnIndex() {
        return this._cell.getColumnIndex();
    }
    
    public int getErrorCellValue() {
        return this._cell.getErrorCellValue();
    }
    
    public double getNumericCellValue() {
        return this._cell.getNumericCellValue();
    }
    
    public int getRowIndex() {
        return this._cell.getRowIndex();
    }
    
    public EvaluationSheet getSheet() {
        return this._evalSheet;
    }
    
    public String getStringCellValue() {
        return this._cell.getRichStringCellValue().getString();
    }
    
    public CellRangeAddress getArrayFormulaRange() {
        return this._cell.getArrayFormulaRange();
    }
    
    public boolean isPartOfArrayFormulaGroup() {
        return this._cell.isPartOfArrayFormulaGroup();
    }
    
    public CellType getCachedFormulaResultType() {
        return this._cell.getCachedFormulaResultType();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @Internal(since = "POI 3.15 beta 3")
    public CellType getCachedFormulaResultTypeEnum() {
        return this.getCachedFormulaResultType();
    }
}
