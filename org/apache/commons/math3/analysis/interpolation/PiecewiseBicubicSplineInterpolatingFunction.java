package org.apache.commons.math3.analysis.interpolation;

import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.BivariateFunction;

public class PiecewiseBicubicSplineInterpolatingFunction implements BivariateFunction
{
    private static final int MIN_NUM_POINTS = 5;
    private final double[] xval;
    private final double[] yval;
    private final double[][] fval;
    
    public PiecewiseBicubicSplineInterpolatingFunction(final double[] x, final double[] y, final double[][] f) throws DimensionMismatchException, NullArgumentException, NoDataException, NonMonotonicSequenceException {
        if (x == null || y == null || f == null || f[0] == null) {
            throw new NullArgumentException();
        }
        final int xLen = x.length;
        final int yLen = y.length;
        if (xLen == 0 || yLen == 0 || f.length == 0 || f[0].length == 0) {
            throw new NoDataException();
        }
        if (xLen < 5 || yLen < 5 || f.length < 5 || f[0].length < 5) {
            throw new InsufficientDataException();
        }
        if (xLen != f.length) {
            throw new DimensionMismatchException(xLen, f.length);
        }
        if (yLen != f[0].length) {
            throw new DimensionMismatchException(yLen, f[0].length);
        }
        MathArrays.checkOrder(x);
        MathArrays.checkOrder(y);
        this.xval = x.clone();
        this.yval = y.clone();
        this.fval = f.clone();
    }
    
    public double value(final double x, final double y) throws OutOfRangeException {
        final AkimaSplineInterpolator interpolator = new AkimaSplineInterpolator();
        final int offset = 2;
        final int count = 5;
        final int i = this.searchIndex(x, this.xval, 2, 5);
        final int j = this.searchIndex(y, this.yval, 2, 5);
        final double[] xArray = new double[5];
        final double[] yArray = new double[5];
        final double[] zArray = new double[5];
        final double[] interpArray = new double[5];
        for (int index = 0; index < 5; ++index) {
            xArray[index] = this.xval[i + index];
            yArray[index] = this.yval[j + index];
        }
        for (int zIndex = 0; zIndex < 5; ++zIndex) {
            for (int index2 = 0; index2 < 5; ++index2) {
                zArray[index2] = this.fval[i + index2][j + zIndex];
            }
            final PolynomialSplineFunction spline = interpolator.interpolate(xArray, zArray);
            interpArray[zIndex] = spline.value(x);
        }
        final PolynomialSplineFunction spline2 = interpolator.interpolate(yArray, interpArray);
        final double returnValue = spline2.value(y);
        return returnValue;
    }
    
    public boolean isValidPoint(final double x, final double y) {
        return x >= this.xval[0] && x <= this.xval[this.xval.length - 1] && y >= this.yval[0] && y <= this.yval[this.yval.length - 1];
    }
    
    private int searchIndex(final double c, final double[] val, final int offset, final int count) {
        int r = Arrays.binarySearch(val, c);
        if (r == -1 || r == -val.length - 1) {
            throw new OutOfRangeException(c, val[0], val[val.length - 1]);
        }
        if (r < 0) {
            r = -r - offset - 1;
        }
        else {
            r -= offset;
        }
        if (r < 0) {
            r = 0;
        }
        if (r + count >= val.length) {
            r = val.length - count;
        }
        return r;
    }
}
