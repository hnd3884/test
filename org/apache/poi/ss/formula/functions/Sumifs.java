package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Sumifs extends Baseifs
{
    public static final FreeRefFunction instance;
    
    @Override
    protected boolean hasInitialRange() {
        return true;
    }
    
    static {
        instance = new Sumifs();
    }
}
