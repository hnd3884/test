package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;

public final class UnaryPlusEval extends Fixed1ArgFunction implements ArrayFunction
{
    public static final Function instance;
    
    private UnaryPlusEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcCellRow, final int srcCellCol, final ValueEval arg0) {
        double d;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcCellRow, srcCellCol);
            if (ve instanceof StringEval) {
                return ve;
            }
            d = OperandResolver.coerceValueToDouble(ve);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(d);
    }
    
    @Override
    public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluateOneArrayArg(args[0], srcRowIndex, srcColumnIndex, valA -> this.evaluate(srcRowIndex, srcColumnIndex, valA));
    }
    
    static {
        instance = new UnaryPlusEval();
    }
}
