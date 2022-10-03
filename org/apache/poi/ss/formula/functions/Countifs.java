package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Countifs extends Baseifs
{
    public static final FreeRefFunction instance;
    
    @Override
    protected boolean hasInitialRange() {
        return false;
    }
    
    static {
        instance = new Countifs();
    }
}
