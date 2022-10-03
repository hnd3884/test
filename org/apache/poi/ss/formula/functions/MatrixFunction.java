package org.apache.poi.ss.formula.functions;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.poi.ss.formula.CacheAreaEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.AnyMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;

public abstract class MatrixFunction implements Function
{
    public static final Function MINVERSE;
    public static final Function TRANSPOSE;
    public static final Function MDETERM;
    public static final Function MMULT;
    
    public static void checkValues(final double[] results) throws EvaluationException {
        for (final double result : results) {
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
        }
    }
    
    protected final double singleOperandEvaluate(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }
    
    private static double[][] fillDoubleArray(final double[] vector, final int rows, final int cols) throws EvaluationException {
        int i = 0;
        int j = 0;
        if (rows < 1 || cols < 1 || vector.length < 1) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final double[][] matrix = new double[rows][cols];
        for (final double aVector : vector) {
            if (j < matrix.length) {
                if (i == matrix[0].length) {
                    i = 0;
                    ++j;
                }
                if (j < matrix.length) {
                    matrix[j][i++] = aVector;
                }
            }
        }
        return matrix;
    }
    
    private static double[] extractDoubleArray(final double[][] matrix) throws EvaluationException {
        int idx = 0;
        if (matrix == null || matrix.length < 1 || matrix[0].length < 1) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final double[] vector = new double[matrix.length * matrix[0].length];
        for (final double[] aMatrix : matrix) {
            for (int i = 0; i < matrix[0].length; ++i) {
                vector[idx++] = aMatrix[i];
            }
        }
        return vector;
    }
    
    static {
        MINVERSE = new OneArrayArg() {
            private final MutableValueCollector instance = new MutableValueCollector(false, false);
            
            @Override
            protected double[] collectValues(final ValueEval arg) throws EvaluationException {
                final double[] values = this.instance.collectValues(arg);
                if (arg instanceof AreaEval && values.length == 1) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                return values;
            }
            
            @Override
            protected double[][] evaluate(final double[][] d1) throws EvaluationException {
                if (d1.length != d1[0].length) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                final Array2DRowRealMatrix temp = new Array2DRowRealMatrix(d1);
                return MatrixUtils.inverse((RealMatrix)temp).getData();
            }
        };
        TRANSPOSE = new OneArrayArg() {
            private final MutableValueCollector instance = new MutableValueCollector(false, true);
            
            @Override
            protected double[] collectValues(final ValueEval arg) throws EvaluationException {
                return this.instance.collectValues(arg);
            }
            
            @Override
            protected double[][] evaluate(final double[][] d1) throws EvaluationException {
                final Array2DRowRealMatrix temp = new Array2DRowRealMatrix(d1);
                return temp.transpose().getData();
            }
        };
        MDETERM = new Mdeterm();
        MMULT = new TwoArrayArg() {
            private final MutableValueCollector instance = new MutableValueCollector(false, false);
            
            @Override
            protected double[] collectValues(final ValueEval arg) throws EvaluationException {
                final double[] values = this.instance.collectValues(arg);
                if (arg instanceof AreaEval && values.length == 1) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                return values;
            }
            
            @Override
            protected double[][] evaluate(final double[][] d1, final double[][] d2) throws EvaluationException {
                final Array2DRowRealMatrix first = new Array2DRowRealMatrix(d1);
                final Array2DRowRealMatrix second = new Array2DRowRealMatrix(d2);
                try {
                    MatrixUtils.checkMultiplicationCompatible((AnyMatrix)first, (AnyMatrix)second);
                }
                catch (final DimensionMismatchException e) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                return first.multiply(second).getData();
            }
        };
    }
    
    public abstract static class OneArrayArg extends Fixed1ArgFunction
    {
        protected OneArrayArg() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
            if (!(arg0 instanceof AreaEval)) {
                double[][] result;
                try {
                    final double value = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                    final double[][] temp = { { value } };
                    result = this.evaluate(temp);
                    NumericFunction.checkValue(result[0][0]);
                }
                catch (final EvaluationException e) {
                    return e.getErrorEval();
                }
                return new NumberEval(result[0][0]);
            }
            int width;
            int height;
            double[] result2;
            try {
                final double[] values = this.collectValues(arg0);
                final double[][] array = fillDoubleArray(values, ((AreaEval)arg0).getHeight(), ((AreaEval)arg0).getWidth());
                final double[][] resultArray = this.evaluate(array);
                width = resultArray[0].length;
                height = resultArray.length;
                result2 = extractDoubleArray(resultArray);
                MatrixFunction.checkValues(result2);
            }
            catch (final EvaluationException e2) {
                return e2.getErrorEval();
            }
            final ValueEval[] vals = new ValueEval[result2.length];
            for (int idx = 0; idx < result2.length; ++idx) {
                vals[idx] = new NumberEval(result2[idx]);
            }
            if (result2.length == 1) {
                return vals[0];
            }
            return new CacheAreaEval(((AreaEval)arg0).getFirstRow(), ((AreaEval)arg0).getFirstColumn(), ((AreaEval)arg0).getFirstRow() + height - 1, ((AreaEval)arg0).getFirstColumn() + width - 1, vals);
        }
        
        protected abstract double[][] evaluate(final double[][] p0) throws EvaluationException;
        
        protected abstract double[] collectValues(final ValueEval p0) throws EvaluationException;
    }
    
    public abstract static class TwoArrayArg extends Fixed2ArgFunction
    {
        protected TwoArrayArg() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            int width;
            int height;
            double[] result;
            try {
                double[][] array0 = null;
                Label_0086: {
                    if (arg0 instanceof AreaEval) {
                        try {
                            final double[] values = this.collectValues(arg0);
                            array0 = fillDoubleArray(values, ((AreaEval)arg0).getHeight(), ((AreaEval)arg0).getWidth());
                            break Label_0086;
                        }
                        catch (final EvaluationException e) {
                            return e.getErrorEval();
                        }
                    }
                    try {
                        final double value = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                        array0 = new double[][] { { value } };
                    }
                    catch (final EvaluationException e) {
                        return e.getErrorEval();
                    }
                }
                double[][] array2 = null;
                Label_0177: {
                    if (arg1 instanceof AreaEval) {
                        try {
                            final double[] values = this.collectValues(arg1);
                            array2 = fillDoubleArray(values, ((AreaEval)arg1).getHeight(), ((AreaEval)arg1).getWidth());
                            break Label_0177;
                        }
                        catch (final EvaluationException e) {
                            return e.getErrorEval();
                        }
                    }
                    try {
                        final double value = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                        array2 = new double[][] { { value } };
                    }
                    catch (final EvaluationException e) {
                        return e.getErrorEval();
                    }
                }
                final double[][] resultArray = this.evaluate(array0, array2);
                width = resultArray[0].length;
                height = resultArray.length;
                result = extractDoubleArray(resultArray);
                MatrixFunction.checkValues(result);
            }
            catch (final EvaluationException e2) {
                return e2.getErrorEval();
            }
            catch (final IllegalArgumentException e3) {
                return ErrorEval.VALUE_INVALID;
            }
            final ValueEval[] vals = new ValueEval[result.length];
            for (int idx = 0; idx < result.length; ++idx) {
                vals[idx] = new NumberEval(result[idx]);
            }
            if (result.length == 1) {
                return vals[0];
            }
            return new CacheAreaEval(((AreaEval)arg0).getFirstRow(), ((AreaEval)arg0).getFirstColumn(), ((AreaEval)arg0).getFirstRow() + height - 1, ((AreaEval)arg0).getFirstColumn() + width - 1, vals);
        }
        
        protected abstract double[][] evaluate(final double[][] p0, final double[][] p1) throws EvaluationException;
        
        protected abstract double[] collectValues(final ValueEval p0) throws EvaluationException;
    }
    
    public static final class MutableValueCollector extends MultiOperandNumericFunction
    {
        public MutableValueCollector(final boolean isReferenceBoolCounted, final boolean isBlankCounted) {
            super(isReferenceBoolCounted, isBlankCounted);
        }
        
        public double[] collectValues(final ValueEval... operands) throws EvaluationException {
            return this.getNumberArray(operands);
        }
        
        @Override
        protected double evaluate(final double[] values) {
            throw new IllegalStateException("should not be called");
        }
    }
    
    private static class Mdeterm extends OneArrayArg
    {
        private final MutableValueCollector instance;
        
        public Mdeterm() {
            (this.instance = new MutableValueCollector(false, false)).setBlankEvalPolicy(MultiOperandNumericFunction.Policy.ERROR);
        }
        
        @Override
        protected double[] collectValues(final ValueEval arg) throws EvaluationException {
            final double[] values = this.instance.collectValues(arg);
            if (arg instanceof AreaEval && values.length == 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return this.instance.collectValues(arg);
        }
        
        @Override
        protected double[][] evaluate(final double[][] d1) throws EvaluationException {
            if (d1.length != d1[0].length) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            final double[][] result = new double[1][1];
            final Array2DRowRealMatrix temp = new Array2DRowRealMatrix(d1);
            result[0][0] = new LUDecomposition((RealMatrix)temp).getDeterminant();
            return result;
        }
    }
}
