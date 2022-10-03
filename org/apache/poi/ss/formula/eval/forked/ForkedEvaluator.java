package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.lang.reflect.Method;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;

public final class ForkedEvaluator
{
    private WorkbookEvaluator _evaluator;
    private ForkedEvaluationWorkbook _sewb;
    
    private ForkedEvaluator(final EvaluationWorkbook masterWorkbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this._sewb = new ForkedEvaluationWorkbook(masterWorkbook);
        this._evaluator = new WorkbookEvaluator(this._sewb, stabilityClassifier, udfFinder);
    }
    
    private static EvaluationWorkbook createEvaluationWorkbook(final Workbook wb) {
        if (wb instanceof HSSFWorkbook) {
            return HSSFEvaluationWorkbook.create((HSSFWorkbook)wb);
        }
        try {
            final Class<?> evalWB = Class.forName("org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook");
            final Class<?> xssfWB = Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook");
            final Method createM = evalWB.getDeclaredMethod("create", xssfWB);
            return (EvaluationWorkbook)createM.invoke(null, wb);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Unexpected workbook type (" + wb.getClass().getName() + ") - check for poi-ooxml and poi-ooxml schemas jar in the classpath", e);
        }
    }
    
    public static ForkedEvaluator create(final Workbook wb, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        return new ForkedEvaluator(createEvaluationWorkbook(wb), stabilityClassifier, udfFinder);
    }
    
    public void updateCell(final String sheetName, final int rowIndex, final int columnIndex, final ValueEval value) {
        final ForkedEvaluationCell cell = this._sewb.getOrCreateUpdatableCell(sheetName, rowIndex, columnIndex);
        cell.setValue(value);
        this._evaluator.notifyUpdateCell(cell);
    }
    
    public void copyUpdatedCells(final Workbook workbook) {
        this._sewb.copyUpdatedCells(workbook);
    }
    
    public ValueEval evaluate(final String sheetName, final int rowIndex, final int columnIndex) {
        final EvaluationCell cell = this._sewb.getEvaluationCell(sheetName, rowIndex, columnIndex);
        switch (cell.getCellType()) {
            case BOOLEAN: {
                return BoolEval.valueOf(cell.getBooleanCellValue());
            }
            case ERROR: {
                return ErrorEval.valueOf(cell.getErrorCellValue());
            }
            case FORMULA: {
                return this._evaluator.evaluate(cell);
            }
            case NUMERIC: {
                return new NumberEval(cell.getNumericCellValue());
            }
            case STRING: {
                return new StringEval(cell.getStringCellValue());
            }
            case BLANK: {
                return null;
            }
            default: {
                throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
            }
        }
    }
    
    public static void setupEnvironment(final String[] workbookNames, final ForkedEvaluator[] evaluators) {
        final WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; ++i) {
            wbEvals[i] = evaluators[i]._evaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }
}
