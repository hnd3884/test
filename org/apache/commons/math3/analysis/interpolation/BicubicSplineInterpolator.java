package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;

@Deprecated
public class BicubicSplineInterpolator implements BivariateGridInterpolator
{
    private final boolean initializeDerivatives;
    
    public BicubicSplineInterpolator() {
        this(false);
    }
    
    public BicubicSplineInterpolator(final boolean initializeDerivatives) {
        this.initializeDerivatives = initializeDerivatives;
    }
    
    public BicubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[][] fval) throws NoDataException, DimensionMismatchException, NonMonotonicSequenceException, NumberIsTooSmallException {
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
        final double[][] fX = new double[yLen][xLen];
        for (int i = 0; i < xLen; ++i) {
            if (fval[i].length != yLen) {
                throw new DimensionMismatchException(fval[i].length, yLen);
            }
            for (int j = 0; j < yLen; ++j) {
                fX[j][i] = fval[i][j];
            }
        }
        final SplineInterpolator spInterpolator = new SplineInterpolator();
        final PolynomialSplineFunction[] ySplineX = new PolynomialSplineFunction[yLen];
        for (int k = 0; k < yLen; ++k) {
            ySplineX[k] = spInterpolator.interpolate(xval, fX[k]);
        }
        final PolynomialSplineFunction[] xSplineY = new PolynomialSplineFunction[xLen];
        for (int l = 0; l < xLen; ++l) {
            xSplineY[l] = spInterpolator.interpolate(yval, fval[l]);
        }
        final double[][] dFdX = new double[xLen][yLen];
        for (int m = 0; m < yLen; ++m) {
            final UnivariateFunction f = ySplineX[m].derivative();
            for (int i2 = 0; i2 < xLen; ++i2) {
                dFdX[i2][m] = f.value(xval[i2]);
            }
        }
        final double[][] dFdY = new double[xLen][yLen];
        for (int i3 = 0; i3 < xLen; ++i3) {
            final UnivariateFunction f2 = xSplineY[i3].derivative();
            for (int j2 = 0; j2 < yLen; ++j2) {
                dFdY[i3][j2] = f2.value(yval[j2]);
            }
        }
        final double[][] d2FdXdY = new double[xLen][yLen];
        for (int i2 = 0; i2 < xLen; ++i2) {
            final int nI = this.nextIndex(i2, xLen);
            final int pI = this.previousIndex(i2);
            for (int j3 = 0; j3 < yLen; ++j3) {
                final int nJ = this.nextIndex(j3, yLen);
                final int pJ = this.previousIndex(j3);
                d2FdXdY[i2][j3] = (fval[nI][nJ] - fval[nI][pJ] - fval[pI][nJ] + fval[pI][pJ]) / ((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ]));
            }
        }
        return new BicubicSplineInterpolatingFunction(xval, yval, fval, dFdX, dFdY, d2FdXdY, this.initializeDerivatives);
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
