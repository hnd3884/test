package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.BaseXSSFEvaluationWorkbook;

@Internal
public final class SXSSFEvaluationWorkbook extends BaseXSSFEvaluationWorkbook
{
    private final SXSSFWorkbook _uBook;
    
    public static SXSSFEvaluationWorkbook create(final SXSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new SXSSFEvaluationWorkbook(book);
    }
    
    private SXSSFEvaluationWorkbook(final SXSSFWorkbook book) {
        super(book.getXSSFWorkbook());
        this._uBook = book;
    }
    
    public int getSheetIndex(final EvaluationSheet evalSheet) {
        final SXSSFSheet sheet = ((SXSSFEvaluationSheet)evalSheet).getSXSSFSheet();
        return this._uBook.getSheetIndex((Sheet)sheet);
    }
    
    public EvaluationSheet getSheet(final int sheetIndex) {
        return (EvaluationSheet)new SXSSFEvaluationSheet(this._uBook.getSheetAt(sheetIndex));
    }
    
    public Ptg[] getFormulaTokens(final EvaluationCell evalCell) {
        final SXSSFCell cell = ((SXSSFEvaluationCell)evalCell).getSXSSFCell();
        return FormulaParser.parse(cell.getCellFormula(), (FormulaParsingWorkbook)this, FormulaType.CELL, this._uBook.getSheetIndex((Sheet)cell.getSheet()));
    }
}
