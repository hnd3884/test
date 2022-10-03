package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.IStabilityClassifier;

public final class XSSFFormulaEvaluator extends BaseXSSFFormulaEvaluator
{
    private XSSFWorkbook _book;
    
    public XSSFFormulaEvaluator(final XSSFWorkbook workbook) {
        this(workbook, null, null);
    }
    
    private XSSFFormulaEvaluator(final XSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this(workbook, new WorkbookEvaluator((EvaluationWorkbook)XSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder));
    }
    
    protected XSSFFormulaEvaluator(final XSSFWorkbook workbook, final WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
        this._book = workbook;
    }
    
    public static XSSFFormulaEvaluator create(final XSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        return new XSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }
    
    public void notifySetFormula(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell((EvaluationCell)new XSSFEvaluationCell((XSSFCell)cell));
    }
    
    public void notifyDeleteCell(final Cell cell) {
        this._bookEvaluator.notifyDeleteCell((EvaluationCell)new XSSFEvaluationCell((XSSFCell)cell));
    }
    
    public void notifyUpdateCell(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell((EvaluationCell)new XSSFEvaluationCell((XSSFCell)cell));
    }
    
    public static void evaluateAllFormulaCells(final XSSFWorkbook wb) {
        BaseFormulaEvaluator.evaluateAllFormulaCells((Workbook)wb);
    }
    
    public XSSFCell evaluateInCell(final Cell cell) {
        return (XSSFCell)super.evaluateInCell(cell);
    }
    
    public void evaluateAll() {
        evaluateAllFormulaCells((Workbook)this._book, (FormulaEvaluator)this);
    }
    
    @Override
    protected EvaluationCell toEvaluationCell(final Cell cell) {
        if (!(cell instanceof XSSFCell)) {
            throw new IllegalArgumentException("Unexpected type of cell: " + cell.getClass() + ". Only XSSFCells can be evaluated.");
        }
        return (EvaluationCell)new XSSFEvaluationCell((XSSFCell)cell);
    }
}
