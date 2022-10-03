package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import java.util.Map;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;

public class HSSFFormulaEvaluator extends BaseFormulaEvaluator
{
    private final HSSFWorkbook _book;
    
    public HSSFFormulaEvaluator(final HSSFWorkbook workbook) {
        this(workbook, null);
    }
    
    public HSSFFormulaEvaluator(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier) {
        this(workbook, stabilityClassifier, null);
    }
    
    private HSSFFormulaEvaluator(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        super(new WorkbookEvaluator(HSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder));
        this._book = workbook;
    }
    
    public static HSSFFormulaEvaluator create(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }
    
    @Override
    protected RichTextString createRichTextString(final String str) {
        return new HSSFRichTextString(str);
    }
    
    public static void setupEnvironment(final String[] workbookNames, final HSSFFormulaEvaluator[] evaluators) {
        BaseFormulaEvaluator.setupEnvironment(workbookNames, evaluators);
    }
    
    @Override
    public void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> evaluators) {
        CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(evaluators);
    }
    
    public void notifyUpdateCell(final HSSFCell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell(cell));
    }
    
    @Override
    public void notifyUpdateCell(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    public void notifyDeleteCell(final HSSFCell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell(cell));
    }
    
    @Override
    public void notifyDeleteCell(final Cell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    @Override
    public void notifySetFormula(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    @Override
    public HSSFCell evaluateInCell(final Cell cell) {
        return (HSSFCell)super.evaluateInCell(cell);
    }
    
    public static void evaluateAllFormulaCells(final HSSFWorkbook wb) {
        BaseFormulaEvaluator.evaluateAllFormulaCells(wb, new HSSFFormulaEvaluator(wb));
    }
    
    public static void evaluateAllFormulaCells(final Workbook wb) {
        BaseFormulaEvaluator.evaluateAllFormulaCells(wb);
    }
    
    @Override
    public void evaluateAll() {
        BaseFormulaEvaluator.evaluateAllFormulaCells(this._book, this);
    }
    
    @Override
    protected CellValue evaluateFormulaCellValue(final Cell cell) {
        final ValueEval eval = this._bookEvaluator.evaluate(new HSSFEvaluationCell((HSSFCell)cell));
        if (eval instanceof BoolEval) {
            final BoolEval be = (BoolEval)eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof NumericValueEval) {
            final NumericValueEval ne = (NumericValueEval)eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof StringValueEval) {
            final StringValueEval ne2 = (StringValueEval)eval;
            return new CellValue(ne2.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval)eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }
    
    @Override
    public void setIgnoreMissingWorkbooks(final boolean ignore) {
        this._bookEvaluator.setIgnoreMissingWorkbooks(ignore);
    }
    
    @Override
    public void setDebugEvaluationOutputForNextEval(final boolean value) {
        this._bookEvaluator.setDebugEvaluationOutputForNextEval(value);
    }
}
