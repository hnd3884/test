package org.apache.poi.ss.formula;

import java.util.Iterator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import java.util.Map;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public abstract class BaseFormulaEvaluator implements FormulaEvaluator, WorkbookEvaluatorProvider
{
    protected final WorkbookEvaluator _bookEvaluator;
    
    protected BaseFormulaEvaluator(final WorkbookEvaluator bookEvaluator) {
        this._bookEvaluator = bookEvaluator;
    }
    
    public static void setupEnvironment(final String[] workbookNames, final BaseFormulaEvaluator[] evaluators) {
        final WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; ++i) {
            wbEvals[i] = evaluators[i]._bookEvaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }
    
    @Override
    public void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> evaluators) {
        CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(evaluators);
    }
    
    @Override
    public WorkbookEvaluator _getWorkbookEvaluator() {
        return this._bookEvaluator;
    }
    
    protected EvaluationWorkbook getEvaluationWorkbook() {
        return this._bookEvaluator.getWorkbook();
    }
    
    @Override
    public void clearAllCachedResultValues() {
        this._bookEvaluator.clearAllCachedResultValues();
    }
    
    @Override
    public CellValue evaluate(final Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case BOOLEAN: {
                return CellValue.valueOf(cell.getBooleanCellValue());
            }
            case ERROR: {
                return CellValue.getError(cell.getErrorCellValue());
            }
            case FORMULA: {
                return this.evaluateFormulaCellValue(cell);
            }
            case NUMERIC: {
                return new CellValue(cell.getNumericCellValue());
            }
            case STRING: {
                return new CellValue(cell.getRichStringCellValue().getString());
            }
            case BLANK: {
                return null;
            }
            default: {
                throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
            }
        }
    }
    
    @Override
    public Cell evaluateInCell(final Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.FORMULA) {
            final CellValue cv = this.evaluateFormulaCellValue(cell);
            this.setCellValue(cell, cv);
            this.setCellType(cell, cv);
            this.setCellValue(cell, cv);
        }
        return cell;
    }
    
    protected abstract CellValue evaluateFormulaCellValue(final Cell p0);
    
    @Override
    public CellType evaluateFormulaCell(final Cell cell) {
        if (cell == null || cell.getCellType() != CellType.FORMULA) {
            return CellType._NONE;
        }
        final CellValue cv = this.evaluateFormulaCellValue(cell);
        this.setCellValue(cell, cv);
        return cv.getCellType();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @Override
    public CellType evaluateFormulaCellEnum(final Cell cell) {
        return this.evaluateFormulaCell(cell);
    }
    
    protected void setCellType(final Cell cell, final CellValue cv) {
        final CellType cellType = cv.getCellType();
        switch (cellType) {
            case BOOLEAN:
            case ERROR:
            case NUMERIC:
            case STRING: {
                this.setCellType(cell, cellType);
                return;
            }
            case BLANK: {
                throw new IllegalArgumentException("This should never happen. Blanks eventually get translated to zero.");
            }
            case FORMULA: {
                throw new IllegalArgumentException("This should never happen. Formulas should have already been evaluated.");
            }
            default: {
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
            }
        }
    }
    
    protected void setCellType(final Cell cell, final CellType cellType) {
        cell.setCellType(cellType);
    }
    
    protected abstract RichTextString createRichTextString(final String p0);
    
    protected void setCellValue(final Cell cell, final CellValue cv) {
        final CellType cellType = cv.getCellType();
        switch (cellType) {
            case BOOLEAN: {
                cell.setCellValue(cv.getBooleanValue());
                break;
            }
            case ERROR: {
                cell.setCellErrorValue(cv.getErrorValue());
                break;
            }
            case NUMERIC: {
                cell.setCellValue(cv.getNumberValue());
                break;
            }
            case STRING: {
                cell.setCellValue(this.createRichTextString(cv.getStringValue()));
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
            }
        }
    }
    
    public static void evaluateAllFormulaCells(final Workbook wb) {
        final FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        evaluateAllFormulaCells(wb, evaluator);
    }
    
    protected static void evaluateAllFormulaCells(final Workbook wb, final FormulaEvaluator evaluator) {
        for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
            final Sheet sheet = wb.getSheetAt(i);
            for (final Row r : sheet) {
                for (final Cell c : r) {
                    if (c.getCellType() == CellType.FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
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
