package org.apache.commons.math3.analysis.function;

import java.util.Arrays;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class StepFunction implements UnivariateFunction
{
    private final double[] abscissa;
    private final double[] ordinate;
    
    public StepFunction(final double[] x, final double[] y) throws NullArgumentException, NoDataException, DimensionMismatchException, NonMonotonicSequenceException {
        if (x == null || y == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0 || y.length == 0) {
            throw new NoDataException();
        }
        if (y.length != x.length) {
            throw new DimensionMismatchException(y.length, x.length);
        }
        MathArrays.checkOrder(x);
        this.abscissa = MathArrays.copyOf(x);
        this.ordinate = MathArrays.copyOf(y);
    }
    
    public double value(final double x) {
        final int index = Arrays.binarySearch(this.abscissa, x);
        double fx = 0.0;
        if (index < -1) {
            fx = this.ordinate[-index - 2];
        }
        else if (index >= 0) {
            fx = this.ordinate[index];
        }
        else {
            fx = this.ordinate[0];
        }
        return fx;
    }
}
