package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.NumberEval;

public final class Delta extends Fixed2ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private static final NumberEval ONE;
    private static final NumberEval ZERO;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg1, final ValueEval arg2) {
        try {
            final Double number1 = evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (number1 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            final Double number2 = evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (number2 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return (number1.compareTo(number2) == 0) ? Delta.ONE : Delta.ZERO;
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    private static Double evaluateValue(final ValueEval arg, final int srcRowIndex, final int srcColumnIndex) throws EvaluationException {
        final ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        final String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
    
    static {
        instance = new Delta();
        ONE = new NumberEval(1.0);
        ZERO = new NumberEval(0.0);
    }
}
