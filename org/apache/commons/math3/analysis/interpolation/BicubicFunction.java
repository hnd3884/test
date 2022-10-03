package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.BivariateFunction;

class BicubicFunction implements BivariateFunction
{
    private static final short N = 4;
    private final double[][] a;
    
    BicubicFunction(final double[] coeff) {
        this.a = new double[4][4];
        for (int j = 0; j < 4; ++j) {
            final double[] aJ = this.a[j];
            for (int i = 0; i < 4; ++i) {
                aJ[i] = coeff[i * 4 + j];
            }
        }
    }
    
    public double value(final double x, final double y) {
        if (x < 0.0 || x > 1.0) {
            throw new OutOfRangeException(x, 0, 1);
        }
        if (y < 0.0 || y > 1.0) {
            throw new OutOfRangeException(y, 0, 1);
        }
        final double x2 = x * x;
        final double x3 = x2 * x;
        final double[] pX = { 1.0, x, x2, x3 };
        final double y2 = y * y;
        final double y3 = y2 * y;
        final double[] pY = { 1.0, y, y2, y3 };
        return this.apply(pX, pY, this.a);
    }
    
    private double apply(final double[] pX, final double[] pY, final double[][] coeff) {
        double result = 0.0;
        for (int i = 0; i < 4; ++i) {
            final double r = MathArrays.linearCombination(coeff[i], pY);
            result += r * pX[i];
        }
        return result;
    }
}
