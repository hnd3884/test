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

public class LinearInterpolator implements UnivariateInterpolator
{
    public PolynomialSplineFunction interpolate(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        }
        if (x.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, x.length, 2, true);
        }
        final int n = x.length - 1;
        MathArrays.checkOrder(x);
        final double[] m = new double[n];
        for (int i = 0; i < n; ++i) {
            m[i] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
        }
        final PolynomialFunction[] polynomials = new PolynomialFunction[n];
        final double[] coefficients = new double[2];
        for (int j = 0; j < n; ++j) {
            coefficients[0] = y[j];
            coefficients[1] = m[j];
            polynomials[j] = new PolynomialFunction(coefficients);
        }
        return new PolynomialSplineFunction(x, polynomials);
    }
}
