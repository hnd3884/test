package org.apache.poi.ss.formula.functions;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import java.util.Arrays;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.CacheAreaEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Trend implements Function
{
    MatrixFunction.MutableValueCollector collector;
    
    public Trend() {
        this.collector = new MatrixFunction.MutableValueCollector(false, false);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length < 1 || args.length > 4) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final TrendResults tr = getNewY(args);
            final ValueEval[] vals = new ValueEval[tr.vals.length];
            for (int i = 0; i < tr.vals.length; ++i) {
                vals[i] = new NumberEval(tr.vals[i]);
            }
            if (tr.vals.length == 1) {
                return vals[0];
            }
            return new CacheAreaEval(srcRowIndex, srcColumnIndex, srcRowIndex + tr.resultHeight - 1, srcColumnIndex + tr.resultWidth - 1, vals);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static double[][] evalToArray(final ValueEval arg) throws EvaluationException {
        if (arg instanceof MissingArgEval) {
            return new double[0][0];
        }
        ValueEval eval;
        if (arg instanceof RefEval) {
            final RefEval re = (RefEval)arg;
            if (re.getNumberOfSheets() > 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            eval = re.getInnerValueEval(re.getFirstSheetIndex());
        }
        else {
            eval = arg;
        }
        if (eval == null) {
            throw new RuntimeException("Parameter may not be null.");
        }
        double[][] ar;
        if (eval instanceof AreaEval) {
            final AreaEval ae = (AreaEval)eval;
            final int w = ae.getWidth();
            final int h = ae.getHeight();
            ar = new double[h][w];
            for (int i = 0; i < h; ++i) {
                for (int j = 0; j < w; ++j) {
                    final ValueEval ve = ae.getRelativeValue(i, j);
                    if (!(ve instanceof NumericValueEval)) {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    }
                    ar[i][j] = ((NumericValueEval)ve).getNumberValue();
                }
            }
        }
        else {
            if (!(eval instanceof NumericValueEval)) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            ar = new double[1][1];
            ar[0][0] = ((NumericValueEval)eval).getNumberValue();
        }
        return ar;
    }
    
    private static double[][] getDefaultArrayOneD(final int w) {
        final double[][] array = new double[w][1];
        for (int i = 0; i < w; ++i) {
            array[i][0] = i + 1;
        }
        return array;
    }
    
    private static double[] flattenArray(final double[][] twoD) {
        if (twoD.length < 1) {
            return new double[0];
        }
        final double[] oneD = new double[twoD.length * twoD[0].length];
        for (int i = 0; i < twoD.length; ++i) {
            System.arraycopy(twoD[i], 0, oneD, i * twoD[0].length + 0, twoD[0].length);
        }
        return oneD;
    }
    
    private static double[][] flattenArrayToRow(final double[][] twoD) {
        if (twoD.length < 1) {
            return new double[0][0];
        }
        final double[][] oneD = new double[twoD.length * twoD[0].length][1];
        for (int i = 0; i < twoD.length; ++i) {
            for (int j = 0; j < twoD[0].length; ++j) {
                oneD[i * twoD[0].length + j][0] = twoD[i][j];
            }
        }
        return oneD;
    }
    
    private static double[][] switchRowsColumns(final double[][] array) {
        final double[][] newArray = new double[array[0].length][array.length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[0].length; ++j) {
                newArray[j][i] = array[i][j];
            }
        }
        return newArray;
    }
    
    private static boolean isAllColumnsSame(final double[][] matrix) {
        if (matrix.length == 0) {
            return false;
        }
        final boolean[] cols = new boolean[matrix[0].length];
        for (int j = 0; j < matrix[0].length; ++j) {
            double prev = Double.NaN;
            for (int i = 0; i < matrix.length; ++i) {
                final double v = matrix[i][j];
                if (i > 0 && v != prev) {
                    cols[j] = true;
                    break;
                }
                prev = v;
            }
        }
        boolean allEquals = true;
        for (final boolean x : cols) {
            if (x) {
                allEquals = false;
                break;
            }
        }
        return allEquals;
    }
    
    private static TrendResults getNewY(final ValueEval[] args) throws EvaluationException {
        boolean passThroughOrigin = false;
        double[][] yOrig = null;
        double[][] xOrig = null;
        double[][] newXOrig = null;
        switch (args.length) {
            case 1: {
                yOrig = evalToArray(args[0]);
                xOrig = new double[0][0];
                newXOrig = new double[0][0];
                break;
            }
            case 2: {
                yOrig = evalToArray(args[0]);
                xOrig = evalToArray(args[1]);
                newXOrig = new double[0][0];
                break;
            }
            case 3: {
                yOrig = evalToArray(args[0]);
                xOrig = evalToArray(args[1]);
                newXOrig = evalToArray(args[2]);
                break;
            }
            case 4: {
                yOrig = evalToArray(args[0]);
                xOrig = evalToArray(args[1]);
                newXOrig = evalToArray(args[2]);
                if (!(args[3] instanceof BoolEval)) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                passThroughOrigin = !((BoolEval)args[3]).getBooleanValue();
                break;
            }
            default: {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        }
        if (yOrig.length < 1) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final double[] y = flattenArray(yOrig);
        double[][] newX = newXOrig;
        double[][] resultSize;
        if (newXOrig.length > 0) {
            resultSize = newXOrig;
        }
        else {
            resultSize = new double[1][1];
        }
        if (y.length == 1) {
            throw new NotImplementedException("Sample size too small");
        }
        double[][] x;
        if (yOrig.length == 1 || yOrig[0].length == 1) {
            if (xOrig.length < 1) {
                x = getDefaultArrayOneD(y.length);
                if (newXOrig.length < 1) {
                    resultSize = yOrig;
                }
            }
            else {
                x = xOrig;
                if (xOrig[0].length > 1 && yOrig.length == 1) {
                    x = switchRowsColumns(x);
                }
                if (newXOrig.length < 1) {
                    resultSize = xOrig;
                }
            }
            if (newXOrig.length > 0 && (x.length == 1 || x[0].length == 1)) {
                newX = flattenArrayToRow(newXOrig);
            }
        }
        else {
            if (xOrig.length < 1) {
                x = getDefaultArrayOneD(y.length);
                if (newXOrig.length < 1) {
                    resultSize = yOrig;
                }
            }
            else {
                x = flattenArrayToRow(xOrig);
                if (newXOrig.length < 1) {
                    resultSize = xOrig;
                }
            }
            if (newXOrig.length > 0) {
                newX = flattenArrayToRow(newXOrig);
            }
            if (y.length != x.length || yOrig.length != xOrig.length) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
        }
        if (newXOrig.length < 1) {
            newX = x;
        }
        else if (newXOrig.length == 1 && newXOrig[0].length > 1 && xOrig.length > 1 && xOrig[0].length == 1) {
            newX = switchRowsColumns(newXOrig);
        }
        if (newX[0].length != x[0].length) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        if (x[0].length >= x.length) {
            throw new NotImplementedException("Sample size too small");
        }
        final int resultHeight = resultSize.length;
        final int resultWidth = resultSize[0].length;
        if (isAllColumnsSame(x)) {
            final double[] result = new double[newX.length];
            final double avg = Arrays.stream(y).average().orElse(0.0);
            for (int i = 0; i < result.length; ++i) {
                result[i] = avg;
            }
            return new TrendResults(result, resultWidth, resultHeight);
        }
        final OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
        if (passThroughOrigin) {
            reg.setNoIntercept(true);
        }
        try {
            reg.newSampleData(y, x);
        }
        catch (final IllegalArgumentException e) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        double[] par;
        try {
            par = reg.estimateRegressionParameters();
        }
        catch (final SingularMatrixException e2) {
            throw new NotImplementedException("Singular matrix in input");
        }
        final double[] result2 = new double[newX.length];
        for (int i = 0; i < newX.length; ++i) {
            result2[i] = 0.0;
            if (passThroughOrigin) {
                for (int j = 0; j < par.length; ++j) {
                    final double[] array = result2;
                    final int n = i;
                    array[n] += par[j] * newX[i][j];
                }
            }
            else {
                result2[i] = par[0];
                for (int j = 1; j < par.length; ++j) {
                    final double[] array2 = result2;
                    final int n2 = i;
                    array2[n2] += par[j] * newX[i][j - 1];
                }
            }
        }
        return new TrendResults(result2, resultWidth, resultHeight);
    }
    
    private static final class TrendResults
    {
        public double[] vals;
        public int resultWidth;
        public int resultHeight;
        
        public TrendResults(final double[] vals, final int resultWidth, final int resultHeight) {
            this.vals = vals;
            this.resultWidth = resultWidth;
            this.resultHeight = resultHeight;
        }
    }
}
