package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;

public abstract class BaseXSSFFormulaEvaluator extends BaseFormulaEvaluator
{
    protected BaseXSSFFormulaEvaluator(final WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
    }
    
    protected RichTextString createRichTextString(final String str) {
        return (RichTextString)new XSSFRichTextString(str);
    }
    
    protected abstract EvaluationCell toEvaluationCell(final Cell p0);
    
    protected CellValue evaluateFormulaCellValue(final Cell cell) {
        final EvaluationCell evalCell = this.toEvaluationCell(cell);
        final ValueEval eval = this._bookEvaluator.evaluate(evalCell);
        if (eval instanceof NumberEval) {
            final NumberEval ne = (NumberEval)eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof BoolEval) {
            final BoolEval be = (BoolEval)eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof StringEval) {
            final StringEval ne2 = (StringEval)eval;
            return new CellValue(ne2.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval)eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }
    
    protected void setCellType(final Cell cell, final CellType cellType) {
        if (cell instanceof XSSFCell) {
            final EvaluationWorkbook evaluationWorkbook = this.getEvaluationWorkbook();
            final BaseXSSFEvaluationWorkbook xewb = BaseXSSFEvaluationWorkbook.class.isAssignableFrom(evaluationWorkbook.getClass()) ? ((BaseXSSFEvaluationWorkbook)evaluationWorkbook) : null;
            ((XSSFCell)cell).setCellType(cellType, xewb);
        }
        else {
            cell.setCellType(cellType);
        }
    }
}
