package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.util.Internal;

@Internal
public final class XSSFEvaluationWorkbook extends BaseXSSFEvaluationWorkbook
{
    private XSSFEvaluationSheet[] _sheetCache;
    
    public static XSSFEvaluationWorkbook create(final XSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new XSSFEvaluationWorkbook(book);
    }
    
    private XSSFEvaluationWorkbook(final XSSFWorkbook book) {
        super(book);
    }
    
    @Override
    public void clearAllCachedResultValues() {
        super.clearAllCachedResultValues();
        this._sheetCache = null;
    }
    
    public int getSheetIndex(final EvaluationSheet evalSheet) {
        final XSSFSheet sheet = ((XSSFEvaluationSheet)evalSheet).getXSSFSheet();
        return this._uBook.getSheetIndex((Sheet)sheet);
    }
    
    public EvaluationSheet getSheet(final int sheetIndex) {
        if (this._sheetCache == null) {
            final int numberOfSheets = this._uBook.getNumberOfSheets();
            this._sheetCache = new XSSFEvaluationSheet[numberOfSheets];
            for (int i = 0; i < numberOfSheets; ++i) {
                this._sheetCache[i] = new XSSFEvaluationSheet(this._uBook.getSheetAt(i));
            }
        }
        if (sheetIndex < 0 || sheetIndex >= this._sheetCache.length) {
            this._uBook.getSheetAt(sheetIndex);
        }
        return (EvaluationSheet)this._sheetCache[sheetIndex];
    }
    
    public Ptg[] getFormulaTokens(final EvaluationCell evalCell) {
        final XSSFCell cell = ((XSSFEvaluationCell)evalCell).getXSSFCell();
        final int sheetIndex = this._uBook.getSheetIndex((Sheet)cell.getSheet());
        final int rowIndex = cell.getRowIndex();
        return FormulaParser.parse(cell.getCellFormula(this), (FormulaParsingWorkbook)this, FormulaType.CELL, sheetIndex, rowIndex);
    }
}
