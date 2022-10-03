package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.TrivariateFunction;

class TricubicSplineFunction implements TrivariateFunction
{
    private static final short N = 4;
    private final double[][][] a;
    
    TricubicSplineFunction(final double[] aV) {
        this.a = new double[4][4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    this.a[i][j][k] = aV[i + 4 * (j + 4 * k)];
                }
            }
        }
    }
    
    public double value(final double x, final double y, final double z) throws OutOfRangeException {
        if (x < 0.0 || x > 1.0) {
            throw new OutOfRangeException(x, 0, 1);
        }
        if (y < 0.0 || y > 1.0) {
            throw new OutOfRangeException(y, 0, 1);
        }
        if (z < 0.0 || z > 1.0) {
            throw new OutOfRangeException(z, 0, 1);
        }
        final double x2 = x * x;
        final double x3 = x2 * x;
        final double[] pX = { 1.0, x, x2, x3 };
        final double y2 = y * y;
        final double y3 = y2 * y;
        final double[] pY = { 1.0, y, y2, y3 };
        final double z2 = z * z;
        final double z3 = z2 * z;
        final double[] pZ = { 1.0, z, z2, z3 };
        double result = 0.0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    result += this.a[i][j][k] * pX[i] * pY[j] * pZ[k];
                }
            }
        }
        return result;
    }
}
