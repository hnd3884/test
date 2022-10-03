package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;

public final class UnaryMinusEval extends Fixed1ArgFunction implements ArrayFunction
{
    public static final Function instance;
    
    private UnaryMinusEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        double d;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            d = OperandResolver.coerceValueToDouble(ve);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        if (d == 0.0) {
            return NumberEval.ZERO;
        }
        return new NumberEval(-d);
    }
    
    @Override
    public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluateOneArrayArg(args[0], srcRowIndex, srcColumnIndex, valA -> this.evaluate(srcRowIndex, srcColumnIndex, valA));
    }
    
    static {
        instance = new UnaryMinusEval();
    }
}
