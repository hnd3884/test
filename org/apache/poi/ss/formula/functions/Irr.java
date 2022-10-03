package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Irr implements Function
{
    private static final int MAX_ITERATION_COUNT = 20;
    private static final double ABSOLUTE_ACCURACY = 1.0E-7;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length == 0 || args.length > 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final double[] values = AggregateFunction.ValueCollector.collectValues(args[0]);
            double guess;
            if (args.length == 2) {
                guess = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            }
            else {
                guess = 0.1;
            }
            final double result = irr(values, guess);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    public static double irr(final double[] income) {
        return irr(income, 0.1);
    }
    
    public static double irr(final double[] values, final double guess) {
        double x0 = guess;
        for (int i = 0; i < 20; ++i) {
            double denominator;
            final double factor = denominator = 1.0 + x0;
            if (denominator == 0.0) {
                return Double.NaN;
            }
            double fValue = values[0];
            double fDerivative = 0.0;
            for (int k = 1; k < values.length; ++k) {
                final double value = values[k];
                fValue += value / denominator;
                denominator *= factor;
                fDerivative -= k * value / denominator;
            }
            if (fDerivative == 0.0) {
                return Double.NaN;
            }
            final double x2 = x0 - fValue / fDerivative;
            if (Math.abs(x2 - x0) <= 1.0E-7) {
                return x2;
            }
            x0 = x2;
        }
        return Double.NaN;
    }
}
