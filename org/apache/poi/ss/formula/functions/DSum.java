package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DSum implements IDStarAlgorithm
{
    private double totalValue;
    
    public DSum() {
        this.totalValue = 0.0;
    }
    
    @Override
    public boolean processMatch(final ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            final double currentValue = ((NumericValueEval)eval).getNumberValue();
            this.totalValue += currentValue;
        }
        return true;
    }
    
    @Override
    public ValueEval getResult() {
        return new NumberEval(this.totalValue);
    }
}
