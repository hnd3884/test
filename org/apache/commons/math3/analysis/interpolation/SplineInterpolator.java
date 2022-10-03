package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class SplineInterpolator implements UnivariateInterpolator
{
    public PolynomialSplineFunction interpolate(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        }
        if (x.length < 3) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, x.length, 3, true);
        }
        final int n = x.length - 1;
        MathArrays.checkOrder(x);
        final double[] h = new double[n];
        for (int i = 0; i < n; ++i) {
            h[i] = x[i + 1] - x[i];
        }
        final double[] mu = new double[n];
        final double[] z = new double[n + 1];
        z[0] = (mu[0] = 0.0);
        double g = 0.0;
        for (int j = 1; j < n; ++j) {
            g = 2.0 * (x[j + 1] - x[j - 1]) - h[j - 1] * mu[j - 1];
            mu[j] = h[j] / g;
            z[j] = (3.0 * (y[j + 1] * h[j - 1] - y[j] * (x[j + 1] - x[j - 1]) + y[j - 1] * h[j]) / (h[j - 1] * h[j]) - h[j - 1] * z[j - 1]) / g;
        }
        final double[] b = new double[n];
        final double[] c = new double[n + 1];
        final double[] d = new double[n];
        c[n] = (z[n] = 0.0);
        for (int k = n - 1; k >= 0; --k) {
            c[k] = z[k] - mu[k] * c[k + 1];
            b[k] = (y[k + 1] - y[k]) / h[k] - h[k] * (c[k + 1] + 2.0 * c[k]) / 3.0;
            d[k] = (c[k + 1] - c[k]) / (3.0 * h[k]);
        }
        final PolynomialFunction[] polynomials = new PolynomialFunction[n];
        final double[] coefficients = new double[4];
        for (int l = 0; l < n; ++l) {
            coefficients[0] = y[l];
            coefficients[1] = b[l];
            coefficients[2] = c[l];
            coefficients[3] = d[l];
            polynomials[l] = new PolynomialFunction(coefficients);
        }
        return new PolynomialSplineFunction(x, polynomials);
    }
}
