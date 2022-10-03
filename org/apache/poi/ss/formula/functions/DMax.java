package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DMax implements IDStarAlgorithm
{
    private ValueEval maximumValue;
    
    @Override
    public boolean processMatch(final ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            if (this.maximumValue == null) {
                this.maximumValue = eval;
            }
            else {
                final double currentValue = ((NumericValueEval)eval).getNumberValue();
                final double oldValue = ((NumericValueEval)this.maximumValue).getNumberValue();
                if (currentValue > oldValue) {
                    this.maximumValue = eval;
                }
            }
        }
        return true;
    }
    
    @Override
    public ValueEval getResult() {
        if (this.maximumValue == null) {
            return NumberEval.ZERO;
        }
        return this.maximumValue;
    }
}
