package org.apache.poi.xssf.streaming;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.BaseXSSFFormulaEvaluator;

public final class SXSSFFormulaEvaluator extends BaseXSSFFormulaEvaluator
{
    private static final POILogger logger;
    private SXSSFWorkbook wb;
    
    public SXSSFFormulaEvaluator(final SXSSFWorkbook workbook) {
        this(workbook, null, null);
    }
    
    private SXSSFFormulaEvaluator(final SXSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this(workbook, new WorkbookEvaluator((EvaluationWorkbook)SXSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder));
    }
    
    private SXSSFFormulaEvaluator(final SXSSFWorkbook workbook, final WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
        this.wb = workbook;
    }
    
    public static SXSSFFormulaEvaluator create(final SXSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        return new SXSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }
    
    public void notifySetFormula(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell((EvaluationCell)new SXSSFEvaluationCell((SXSSFCell)cell));
    }
    
    public void notifyDeleteCell(final Cell cell) {
        this._bookEvaluator.notifyDeleteCell((EvaluationCell)new SXSSFEvaluationCell((SXSSFCell)cell));
    }
    
    public void notifyUpdateCell(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell((EvaluationCell)new SXSSFEvaluationCell((SXSSFCell)cell));
    }
    
    @Override
    protected EvaluationCell toEvaluationCell(final Cell cell) {
        if (!(cell instanceof SXSSFCell)) {
            throw new IllegalArgumentException("Unexpected type of cell: " + cell.getClass() + ". Only SXSSFCells can be evaluated.");
        }
        return (EvaluationCell)new SXSSFEvaluationCell((SXSSFCell)cell);
    }
    
    public SXSSFCell evaluateInCell(final Cell cell) {
        return (SXSSFCell)super.evaluateInCell(cell);
    }
    
    public static void evaluateAllFormulaCells(final SXSSFWorkbook wb, final boolean skipOutOfWindow) {
        final SXSSFFormulaEvaluator eval = new SXSSFFormulaEvaluator(wb);
        for (final Sheet sheet : wb) {
            if (((SXSSFSheet)sheet).areAllRowsFlushed()) {
                throw new SheetsFlushedException();
            }
        }
        for (final Sheet sheet : wb) {
            final int lastFlushedRowNum = ((SXSSFSheet)sheet).getLastFlushedRowNum();
            if (lastFlushedRowNum > -1) {
                if (!skipOutOfWindow) {
                    throw new RowFlushedException(0);
                }
                SXSSFFormulaEvaluator.logger.log(3, new Object[] { "Rows up to " + lastFlushedRowNum + " have already been flushed, skipping" });
            }
            for (final Row r : sheet) {
                for (final Cell c : r) {
                    if (c.getCellType() == CellType.FORMULA) {
                        eval.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }
    
    public void evaluateAll() {
        evaluateAllFormulaCells(this.wb, false);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)SXSSFFormulaEvaluator.class);
    }
    
    public static class SheetsFlushedException extends IllegalStateException
    {
        protected SheetsFlushedException() {
            super("One or more sheets have been flushed, cannot evaluate all cells");
        }
    }
    
    public static class RowFlushedException extends IllegalStateException
    {
        protected RowFlushedException(final int rowNum) {
            super("Row " + rowNum + " has been flushed, cannot evaluate all cells");
        }
    }
}
