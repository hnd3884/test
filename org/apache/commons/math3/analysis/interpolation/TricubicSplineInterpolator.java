package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;

@Deprecated
public class TricubicSplineInterpolator implements TrivariateGridInterpolator
{
    public TricubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[] zval, final double[][][] fval) throws NoDataException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException {
        if (xval.length == 0 || yval.length == 0 || zval.length == 0 || fval.length == 0) {
            throw new NoDataException();
        }
        if (xval.length != fval.length) {
            throw new DimensionMismatchException(xval.length, fval.length);
        }
        MathArrays.checkOrder(xval);
        MathArrays.checkOrder(yval);
        MathArrays.checkOrder(zval);
        final int xLen = xval.length;
        final int yLen = yval.length;
        final int zLen = zval.length;
        final double[][][] fvalXY = new double[zLen][xLen][yLen];
        final double[][][] fvalZX = new double[yLen][zLen][xLen];
        for (int i = 0; i < xLen; ++i) {
            if (fval[i].length != yLen) {
                throw new DimensionMismatchException(fval[i].length, yLen);
            }
            for (int j = 0; j < yLen; ++j) {
                if (fval[i][j].length != zLen) {
                    throw new DimensionMismatchException(fval[i][j].length, zLen);
                }
                for (int k = 0; k < zLen; ++k) {
                    final double v = fval[i][j][k];
                    fvalXY[k][i][j] = v;
                    fvalZX[j][k][i] = v;
                }
            }
        }
        final BicubicSplineInterpolator bsi = new BicubicSplineInterpolator(true);
        final BicubicSplineInterpolatingFunction[] xSplineYZ = new BicubicSplineInterpolatingFunction[xLen];
        for (int l = 0; l < xLen; ++l) {
            xSplineYZ[l] = bsi.interpolate(yval, zval, fval[l]);
        }
        final BicubicSplineInterpolatingFunction[] ySplineZX = new BicubicSplineInterpolatingFunction[yLen];
        for (int m = 0; m < yLen; ++m) {
            ySplineZX[m] = bsi.interpolate(zval, xval, fvalZX[m]);
        }
        final BicubicSplineInterpolatingFunction[] zSplineXY = new BicubicSplineInterpolatingFunction[zLen];
        for (int k2 = 0; k2 < zLen; ++k2) {
            zSplineXY[k2] = bsi.interpolate(xval, yval, fvalXY[k2]);
        }
        final double[][][] dFdX = new double[xLen][yLen][zLen];
        final double[][][] dFdY = new double[xLen][yLen][zLen];
        final double[][][] d2FdXdY = new double[xLen][yLen][zLen];
        for (int k3 = 0; k3 < zLen; ++k3) {
            final BicubicSplineInterpolatingFunction f = zSplineXY[k3];
            for (int i2 = 0; i2 < xLen; ++i2) {
                final double x = xval[i2];
                for (int j2 = 0; j2 < yLen; ++j2) {
                    final double y = yval[j2];
                    dFdX[i2][j2][k3] = f.partialDerivativeX(x, y);
                    dFdY[i2][j2][k3] = f.partialDerivativeY(x, y);
                    d2FdXdY[i2][j2][k3] = f.partialDerivativeXY(x, y);
                }
            }
        }
        final double[][][] dFdZ = new double[xLen][yLen][zLen];
        final double[][][] d2FdYdZ = new double[xLen][yLen][zLen];
        for (int i2 = 0; i2 < xLen; ++i2) {
            final BicubicSplineInterpolatingFunction f2 = xSplineYZ[i2];
            for (int j3 = 0; j3 < yLen; ++j3) {
                final double y2 = yval[j3];
                for (int k4 = 0; k4 < zLen; ++k4) {
                    final double z = zval[k4];
                    dFdZ[i2][j3][k4] = f2.partialDerivativeY(y2, z);
                    d2FdYdZ[i2][j3][k4] = f2.partialDerivativeXY(y2, z);
                }
            }
        }
        final double[][][] d2FdZdX = new double[xLen][yLen][zLen];
        for (int j4 = 0; j4 < yLen; ++j4) {
            final BicubicSplineInterpolatingFunction f3 = ySplineZX[j4];
            for (int k5 = 0; k5 < zLen; ++k5) {
                final double z2 = zval[k5];
                for (int i3 = 0; i3 < xLen; ++i3) {
                    final double x2 = xval[i3];
                    d2FdZdX[i3][j4][k5] = f3.partialDerivativeXY(z2, x2);
                }
            }
        }
        final double[][][] d3FdXdYdZ = new double[xLen][yLen][zLen];
        for (int i4 = 0; i4 < xLen; ++i4) {
            final int nI = this.nextIndex(i4, xLen);
            final int pI = this.previousIndex(i4);
            for (int j5 = 0; j5 < yLen; ++j5) {
                final int nJ = this.nextIndex(j5, yLen);
                final int pJ = this.previousIndex(j5);
                for (int k6 = 0; k6 < zLen; ++k6) {
                    final int nK = this.nextIndex(k6, zLen);
                    final int pK = this.previousIndex(k6);
                    d3FdXdYdZ[i4][j5][k6] = (fval[nI][nJ][nK] - fval[nI][pJ][nK] - fval[pI][nJ][nK] + fval[pI][pJ][nK] - fval[nI][nJ][pK] + fval[nI][pJ][pK] + fval[pI][nJ][pK] - fval[pI][pJ][pK]) / ((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ]) * (zval[nK] - zval[pK]));
                }
            }
        }
        return new TricubicSplineInterpolatingFunction(xval, yval, zval, fval, dFdX, dFdY, dFdZ, d2FdXdY, d2FdZdX, d2FdYdZ, d3FdXdYdZ);
    }
    
    private int nextIndex(final int i, final int max) {
        final int index = i + 1;
        return (index < max) ? index : (index - 1);
    }
    
    private int previousIndex(final int i) {
        final int index = i - 1;
        return (index >= 0) ? index : 0;
    }
}
