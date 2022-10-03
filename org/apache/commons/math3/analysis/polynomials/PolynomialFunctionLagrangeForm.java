package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class PolynomialFunctionLagrangeForm implements UnivariateFunction
{
    private double[] coefficients;
    private final double[] x;
    private final double[] y;
    private boolean coefficientsComputed;
    
    public PolynomialFunctionLagrangeForm(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        this.x = new double[x.length];
        this.y = new double[y.length];
        System.arraycopy(x, 0, this.x, 0, x.length);
        System.arraycopy(y, 0, this.y, 0, y.length);
        this.coefficientsComputed = false;
        if (!verifyInterpolationArray(x, y, false)) {
            MathArrays.sortInPlace(this.x, new double[][] { this.y });
            verifyInterpolationArray(this.x, this.y, true);
        }
    }
    
    public double value(final double z) {
        return evaluateInternal(this.x, this.y, z);
    }
    
    public int degree() {
        return this.x.length - 1;
    }
    
    public double[] getInterpolatingPoints() {
        final double[] out = new double[this.x.length];
        System.arraycopy(this.x, 0, out, 0, this.x.length);
        return out;
    }
    
    public double[] getInterpolatingValues() {
        final double[] out = new double[this.y.length];
        System.arraycopy(this.y, 0, out, 0, this.y.length);
        return out;
    }
    
    public double[] getCoefficients() {
        if (!this.coefficientsComputed) {
            this.computeCoefficients();
        }
        final double[] out = new double[this.coefficients.length];
        System.arraycopy(this.coefficients, 0, out, 0, this.coefficients.length);
        return out;
    }
    
    public static double evaluate(final double[] x, final double[] y, final double z) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (verifyInterpolationArray(x, y, false)) {
            return evaluateInternal(x, y, z);
        }
        final double[] xNew = new double[x.length];
        final double[] yNew = new double[y.length];
        System.arraycopy(x, 0, xNew, 0, x.length);
        System.arraycopy(y, 0, yNew, 0, y.length);
        MathArrays.sortInPlace(xNew, new double[][] { yNew });
        verifyInterpolationArray(xNew, yNew, true);
        return evaluateInternal(xNew, yNew, z);
    }
    
    private static double evaluateInternal(final double[] x, final double[] y, final double z) {
        int nearest = 0;
        final int n = x.length;
        final double[] c = new double[n];
        final double[] d = new double[n];
        double min_dist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; ++i) {
            c[i] = y[i];
            d[i] = y[i];
            final double dist = FastMath.abs(z - x[i]);
            if (dist < min_dist) {
                nearest = i;
                min_dist = dist;
            }
        }
        double value = y[nearest];
        for (int j = 1; j < n; ++j) {
            for (int k = 0; k < n - j; ++k) {
                final double tc = x[k] - z;
                final double td = x[j + k] - z;
                final double divider = x[k] - x[j + k];
                final double w = (c[k + 1] - d[k]) / divider;
                c[k] = tc * w;
                d[k] = td * w;
            }
            if (nearest < 0.5 * (n - j + 1)) {
                value += c[nearest];
            }
            else {
                --nearest;
                value += d[nearest];
            }
        }
        return value;
    }
    
    protected void computeCoefficients() {
        final int n = this.degree() + 1;
        this.coefficients = new double[n];
        for (int i = 0; i < n; ++i) {
            this.coefficients[i] = 0.0;
        }
        final double[] c = new double[n + 1];
        c[0] = 1.0;
        for (int j = 0; j < n; ++j) {
            for (int k = j; k > 0; --k) {
                c[k] = c[k - 1] - c[k] * this.x[j];
            }
            final double[] array = c;
            final int n2 = 0;
            array[n2] *= -this.x[j];
            c[j + 1] = 1.0;
        }
        final double[] tc = new double[n];
        for (int l = 0; l < n; ++l) {
            double d = 1.0;
            for (int m = 0; m < n; ++m) {
                if (l != m) {
                    d *= this.x[l] - this.x[m];
                }
            }
            final double t = this.y[l] / d;
            tc[n - 1] = c[n];
            final double[] coefficients = this.coefficients;
            final int n3 = n - 1;
            coefficients[n3] += t * tc[n - 1];
            for (int j2 = n - 2; j2 >= 0; --j2) {
                tc[j2] = c[j2 + 1] + tc[j2 + 1] * this.x[l];
                final double[] coefficients2 = this.coefficients;
                final int n4 = j2;
                coefficients2[n4] += t * tc[j2];
            }
        }
        this.coefficientsComputed = true;
    }
    
    public static boolean verifyInterpolationArray(final double[] x, final double[] y, final boolean abort) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        }
        if (x.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.WRONG_NUMBER_OF_POINTS, 2, x.length, true);
        }
        return MathArrays.checkOrder(x, MathArrays.OrderDirection.INCREASING, true, abort);
    }
}
