package org.apache.commons.math3.analysis.interpolation;

import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.analysis.BivariateFunction;

public class BicubicInterpolatingFunction implements BivariateFunction
{
    private static final int NUM_COEFF = 16;
    private static final double[][] AINV;
    private final double[] xval;
    private final double[] yval;
    private final BicubicFunction[][] splines;
    
    public BicubicInterpolatingFunction(final double[] x, final double[] y, final double[][] f, final double[][] dFdX, final double[][] dFdY, final double[][] d2FdXdY) throws DimensionMismatchException, NoDataException, NonMonotonicSequenceException {
        final int xLen = x.length;
        final int yLen = y.length;
        if (xLen == 0 || yLen == 0 || f.length == 0 || f[0].length == 0) {
            throw new NoDataException();
        }
        if (xLen != f.length) {
            throw new DimensionMismatchException(xLen, f.length);
        }
        if (xLen != dFdX.length) {
            throw new DimensionMismatchException(xLen, dFdX.length);
        }
        if (xLen != dFdY.length) {
            throw new DimensionMismatchException(xLen, dFdY.length);
        }
        if (xLen != d2FdXdY.length) {
            throw new DimensionMismatchException(xLen, d2FdXdY.length);
        }
        MathArrays.checkOrder(x);
        MathArrays.checkOrder(y);
        this.xval = x.clone();
        this.yval = y.clone();
        final int lastI = xLen - 1;
        final int lastJ = yLen - 1;
        this.splines = new BicubicFunction[lastI][lastJ];
        for (int i = 0; i < lastI; ++i) {
            if (f[i].length != yLen) {
                throw new DimensionMismatchException(f[i].length, yLen);
            }
            if (dFdX[i].length != yLen) {
                throw new DimensionMismatchException(dFdX[i].length, yLen);
            }
            if (dFdY[i].length != yLen) {
                throw new DimensionMismatchException(dFdY[i].length, yLen);
            }
            if (d2FdXdY[i].length != yLen) {
                throw new DimensionMismatchException(d2FdXdY[i].length, yLen);
            }
            final int ip1 = i + 1;
            final double xR = this.xval[ip1] - this.xval[i];
            for (int j = 0; j < lastJ; ++j) {
                final int jp1 = j + 1;
                final double yR = this.yval[jp1] - this.yval[j];
                final double xRyR = xR * yR;
                final double[] beta = { f[i][j], f[ip1][j], f[i][jp1], f[ip1][jp1], dFdX[i][j] * xR, dFdX[ip1][j] * xR, dFdX[i][jp1] * xR, dFdX[ip1][jp1] * xR, dFdY[i][j] * yR, dFdY[ip1][j] * yR, dFdY[i][jp1] * yR, dFdY[ip1][jp1] * yR, d2FdXdY[i][j] * xRyR, d2FdXdY[ip1][j] * xRyR, d2FdXdY[i][jp1] * xRyR, d2FdXdY[ip1][jp1] * xRyR };
                this.splines[i][j] = new BicubicFunction(this.computeSplineCoefficients(beta));
            }
        }
    }
    
    public double value(final double x, final double y) throws OutOfRangeException {
        final int i = this.searchIndex(x, this.xval);
        final int j = this.searchIndex(y, this.yval);
        final double xN = (x - this.xval[i]) / (this.xval[i + 1] - this.xval[i]);
        final double yN = (y - this.yval[j]) / (this.yval[j + 1] - this.yval[j]);
        return this.splines[i][j].value(xN, yN);
    }
    
    public boolean isValidPoint(final double x, final double y) {
        return x >= this.xval[0] && x <= this.xval[this.xval.length - 1] && y >= this.yval[0] && y <= this.yval[this.yval.length - 1];
    }
    
    private int searchIndex(final double c, final double[] val) {
        final int r = Arrays.binarySearch(val, c);
        if (r == -1 || r == -val.length - 1) {
            throw new OutOfRangeException(c, val[0], val[val.length - 1]);
        }
        if (r < 0) {
            return -r - 2;
        }
        final int last = val.length - 1;
        if (r == last) {
            return last - 1;
        }
        return r;
    }
    
    private double[] computeSplineCoefficients(final double[] beta) {
        final double[] a = new double[16];
        for (int i = 0; i < 16; ++i) {
            double result = 0.0;
            final double[] row = BicubicInterpolatingFunction.AINV[i];
            for (int j = 0; j < 16; ++j) {
                result += row[j] * beta[j];
            }
            a[i] = result;
        }
        return a;
    }
    
    static {
        AINV = new double[][] { { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { -3.0, 3.0, 0.0, 0.0, -2.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 2.0, -2.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -3.0, 3.0, 0.0, 0.0, -2.0, -1.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, -2.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0 }, { -3.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, -3.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, -1.0, 0.0 }, { 9.0, -9.0, -9.0, 9.0, 6.0, 3.0, -6.0, -3.0, 6.0, -6.0, 3.0, -3.0, 4.0, 2.0, 2.0, 1.0 }, { -6.0, 6.0, 6.0, -6.0, -3.0, -3.0, 3.0, 3.0, -4.0, 4.0, -2.0, 2.0, -2.0, -2.0, -1.0, -1.0 }, { 2.0, 0.0, -2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, -2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0 }, { -6.0, 6.0, 6.0, -6.0, -4.0, -2.0, 4.0, 2.0, -3.0, 3.0, -3.0, 3.0, -2.0, -1.0, -2.0, -1.0 }, { 4.0, -4.0, -4.0, 4.0, 2.0, 2.0, -2.0, -2.0, 2.0, -2.0, 2.0, -2.0, 1.0, 1.0, 1.0, 1.0 } };
    }
}
