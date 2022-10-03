package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;

final class FormulaCellCacheEntry extends CellCacheEntry
{
    private CellCacheEntry[] _sensitiveInputCells;
    private FormulaUsedBlankCellSet _usedBlankCellGroup;
    
    public FormulaCellCacheEntry() {
    }
    
    public boolean isInputSensitive() {
        return (this._sensitiveInputCells != null && this._sensitiveInputCells.length > 0) || (this._usedBlankCellGroup != null && !this._usedBlankCellGroup.isEmpty());
    }
    
    public void setSensitiveInputCells(final CellCacheEntry[] sensitiveInputCells) {
        if (sensitiveInputCells == null) {
            this._sensitiveInputCells = null;
            this.changeConsumingCells(CellCacheEntry.EMPTY_ARRAY);
        }
        else {
            this.changeConsumingCells(this._sensitiveInputCells = sensitiveInputCells.clone());
        }
    }
    
    public void clearFormulaEntry() {
        final CellCacheEntry[] usedCells = this._sensitiveInputCells;
        if (usedCells != null) {
            for (int i = usedCells.length - 1; i >= 0; --i) {
                usedCells[i].clearConsumingCell(this);
            }
        }
        this._sensitiveInputCells = null;
        this.clearValue();
    }
    
    private void changeConsumingCells(final CellCacheEntry[] usedCells) {
        final CellCacheEntry[] prevUsedCells = this._sensitiveInputCells;
        final int nUsed = usedCells.length;
        for (int i = 0; i < nUsed; ++i) {
            usedCells[i].addConsumingCell(this);
        }
        if (prevUsedCells == null) {
            return;
        }
        final int nPrevUsed = prevUsedCells.length;
        if (nPrevUsed < 1) {
            return;
        }
        Set<CellCacheEntry> usedSet;
        if (nUsed < 1) {
            usedSet = Collections.emptySet();
        }
        else {
            usedSet = new HashSet<CellCacheEntry>(nUsed * 3 / 2);
            usedSet.addAll(Arrays.asList(usedCells).subList(0, nUsed));
        }
        for (final CellCacheEntry prevUsed : prevUsedCells) {
            if (!usedSet.contains(prevUsed)) {
                prevUsed.clearConsumingCell(this);
            }
        }
    }
    
    public void updateFormulaResult(final ValueEval result, final CellCacheEntry[] sensitiveInputCells, final FormulaUsedBlankCellSet usedBlankAreas) {
        this.updateValue(result);
        this.setSensitiveInputCells(sensitiveInputCells);
        this._usedBlankCellGroup = usedBlankAreas;
    }
    
    public void notifyUpdatedBlankCell(final FormulaUsedBlankCellSet.BookSheetKey bsk, final int rowIndex, final int columnIndex, final IEvaluationListener evaluationListener) {
        if (this._usedBlankCellGroup != null && this._usedBlankCellGroup.containsCell(bsk, rowIndex, columnIndex)) {
            this.clearFormulaEntry();
            this.recurseClearCachedFormulaResults(evaluationListener);
        }
    }
}
