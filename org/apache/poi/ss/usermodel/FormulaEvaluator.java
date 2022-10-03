package org.apache.poi.ss.usermodel;

import java.util.Map;
import org.apache.poi.util.Removal;

public interface FormulaEvaluator
{
    void clearAllCachedResultValues();
    
    void notifySetFormula(final Cell p0);
    
    void notifyDeleteCell(final Cell p0);
    
    void notifyUpdateCell(final Cell p0);
    
    void evaluateAll();
    
    CellValue evaluate(final Cell p0);
    
    CellType evaluateFormulaCell(final Cell p0);
    
    @Deprecated
    @Removal(version = "4.2")
    CellType evaluateFormulaCellEnum(final Cell p0);
    
    Cell evaluateInCell(final Cell p0);
    
    void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> p0);
    
    void setIgnoreMissingWorkbooks(final boolean p0);
    
    void setDebugEvaluationOutputForNextEval(final boolean p0);
}
