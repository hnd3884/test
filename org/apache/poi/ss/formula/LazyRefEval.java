package org.apache.poi.ss.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.RefEvalBase;

public final class LazyRefEval extends RefEvalBase
{
    private final SheetRangeEvaluator _evaluator;
    
    public LazyRefEval(final int rowIndex, final int columnIndex, final SheetRangeEvaluator sre) {
        super(sre, rowIndex, columnIndex);
        this._evaluator = sre;
    }
    
    @Override
    public ValueEval getInnerValueEval(final int sheetIndex) {
        return this._evaluator.getEvalForCell(sheetIndex, this.getRow(), this.getColumn());
    }
    
    @Override
    public AreaEval offset(final int relFirstRowIx, final int relLastRowIx, final int relFirstColIx, final int relLastColIx) {
        final AreaI area = new AreaI.OffsetArea(this.getRow(), this.getColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        return new LazyAreaEval(area, this._evaluator);
    }
    
    public boolean isSubTotal() {
        final SheetRefEvaluator sheetEvaluator = this._evaluator.getSheetEvaluator(this.getFirstSheetIndex());
        return sheetEvaluator.isSubTotal(this.getRow(), this.getColumn());
    }
    
    public boolean isRowHidden() {
        final SheetRefEvaluator _sre = this._evaluator.getSheetEvaluator(this._evaluator.getFirstSheetIndex());
        return _sre.isRowHidden(this.getRow());
    }
    
    @Override
    public String toString() {
        final CellReference cr = new CellReference(this.getRow(), this.getColumn());
        return this.getClass().getName() + "[" + this._evaluator.getSheetNameRange() + '!' + cr.formatAsString() + "]";
    }
}
