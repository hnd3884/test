package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.MatrixFunction;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.ArrayFunction;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public abstract class TwoOperandNumericOperation extends Fixed2ArgFunction implements ArrayFunction
{
    public static final Function AddEval;
    public static final Function DivideEval;
    public static final Function MultiplyEval;
    public static final Function PowerEval;
    public static final Function SubtractEval;
    
    protected final double singleOperandEvaluate(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }
    
    @Override
    public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluateTwoArrayArgs(args[0], args[1], srcRowIndex, srcColumnIndex, (vA, vB) -> {
            try {
                final double d0 = OperandResolver.coerceValueToDouble(vA);
                final double d2 = OperandResolver.coerceValueToDouble(vB);
                final double result = this.evaluate(d0, d2);
                return new NumberEval(result);
            }
            catch (final EvaluationException e) {
                return e.getErrorEval();
            }
        });
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        double result;
        try {
            final double d0 = this.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = this.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            result = this.evaluate(d0, d2);
            if (result == 0.0 && !(this instanceof SubtractEvalClass)) {
                return NumberEval.ZERO;
            }
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return ErrorEval.NUM_ERROR;
            }
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    protected abstract double evaluate(final double p0, final double p1) throws EvaluationException;
    
    static {
        AddEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return d0 + d1;
            }
        };
        DivideEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d1 == 0.0) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return d0 / d1;
            }
        };
        MultiplyEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return d0 * d1;
            }
        };
        PowerEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                if (d0 < 0.0 && Math.abs(d1) > 0.0 && Math.abs(d1) < 1.0) {
                    return -1.0 * Math.pow(d0 * -1.0, d1);
                }
                return Math.pow(d0, d1);
            }
        };
        SubtractEval = new SubtractEvalClass();
    }
    
    private final class ArrayEval extends MatrixFunction.TwoArrayArg
    {
        private final MatrixFunction.MutableValueCollector instance;
        
        private ArrayEval() {
            this.instance = new MatrixFunction.MutableValueCollector(true, true);
        }
        
        @Override
        protected double[] collectValues(final ValueEval arg) throws EvaluationException {
            return this.instance.collectValues(arg);
        }
        
        @Override
        protected double[][] evaluate(final double[][] d1, final double[][] d2) throws IllegalArgumentException, EvaluationException {
            final int width = (d1[0].length < d2[0].length) ? d1[0].length : d2[0].length;
            final int height = (d1.length < d2.length) ? d1.length : d2.length;
            final double[][] result = new double[height][width];
            for (int j = 0; j < height; ++j) {
                for (int i = 0; i < width; ++i) {
                    result[j][i] = TwoOperandNumericOperation.this.evaluate(d1[j][i], d2[j][i]);
                }
            }
            return result;
        }
    }
    
    private static final class SubtractEvalClass extends TwoOperandNumericOperation
    {
        public SubtractEvalClass() {
        }
        
        @Override
        protected double evaluate(final double d0, final double d1) {
            return d0 - d1;
        }
    }
}
