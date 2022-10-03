package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;

public class BicubicInterpolator implements BivariateGridInterpolator
{
    public BicubicInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[][] fval) throws NoDataException, DimensionMismatchException, NonMonotonicSequenceException, NumberIsTooSmallException {
        if (xval.length == 0 || yval.length == 0 || fval.length == 0) {
            throw new NoDataException();
        }
        if (xval.length != fval.length) {
            throw new DimensionMismatchException(xval.length, fval.length);
        }
        MathArrays.checkOrder(xval);
        MathArrays.checkOrder(yval);
        final int xLen = xval.length;
        final int yLen = yval.length;
        final double[][] dFdX = new double[xLen][yLen];
        final double[][] dFdY = new double[xLen][yLen];
        final double[][] d2FdXdY = new double[xLen][yLen];
        for (int i = 1; i < xLen - 1; ++i) {
            final int nI = i + 1;
            final int pI = i - 1;
            final double nX = xval[nI];
            final double pX = xval[pI];
            final double deltaX = nX - pX;
            for (int j = 1; j < yLen - 1; ++j) {
                final int nJ = j + 1;
                final int pJ = j - 1;
                final double nY = yval[nJ];
                final double pY = yval[pJ];
                final double deltaY = nY - pY;
                dFdX[i][j] = (fval[nI][j] - fval[pI][j]) / deltaX;
                dFdY[i][j] = (fval[i][nJ] - fval[i][pJ]) / deltaY;
                final double deltaXY = deltaX * deltaY;
                d2FdXdY[i][j] = (fval[nI][nJ] - fval[nI][pJ] - fval[pI][nJ] + fval[pI][pJ]) / deltaXY;
            }
        }
        return new BicubicInterpolatingFunction(xval, yval, fval, dFdX, dFdY, d2FdXdY) {
            @Override
            public boolean isValidPoint(final double x, final double y) {
                return x >= xval[1] && x <= xval[xval.length - 2] && y >= yval[1] && y <= yval[yval.length - 2];
            }
        };
    }
}
