package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.UnivariateFunction;
import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class PolynomialSplineFunction implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double[] knots;
    private final PolynomialFunction[] polynomials;
    private final int n;
    
    public PolynomialSplineFunction(final double[] knots, final PolynomialFunction[] polynomials) throws NullArgumentException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException {
        if (knots == null || polynomials == null) {
            throw new NullArgumentException();
        }
        if (knots.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION, 2, knots.length, false);
        }
        if (knots.length - 1 != polynomials.length) {
            throw new DimensionMismatchException(polynomials.length, knots.length);
        }
        MathArrays.checkOrder(knots);
        this.n = knots.length - 1;
        System.arraycopy(knots, 0, this.knots = new double[this.n + 1], 0, this.n + 1);
        System.arraycopy(polynomials, 0, this.polynomials = new PolynomialFunction[this.n], 0, this.n);
    }
    
    public double value(final double v) {
        if (v < this.knots[0] || v > this.knots[this.n]) {
            throw new OutOfRangeException(v, this.knots[0], this.knots[this.n]);
        }
        int i = Arrays.binarySearch(this.knots, v);
        if (i < 0) {
            i = -i - 2;
        }
        if (i >= this.polynomials.length) {
            --i;
        }
        return this.polynomials[i].value(v - this.knots[i]);
    }
    
    public UnivariateFunction derivative() {
        return this.polynomialSplineDerivative();
    }
    
    public PolynomialSplineFunction polynomialSplineDerivative() {
        final PolynomialFunction[] derivativePolynomials = new PolynomialFunction[this.n];
        for (int i = 0; i < this.n; ++i) {
            derivativePolynomials[i] = this.polynomials[i].polynomialDerivative();
        }
        return new PolynomialSplineFunction(this.knots, derivativePolynomials);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        final double t2 = t.getValue();
        if (t2 < this.knots[0] || t2 > this.knots[this.n]) {
            throw new OutOfRangeException(t2, this.knots[0], this.knots[this.n]);
        }
        int i = Arrays.binarySearch(this.knots, t2);
        if (i < 0) {
            i = -i - 2;
        }
        if (i >= this.polynomials.length) {
            --i;
        }
        return this.polynomials[i].value(t.subtract(this.knots[i]));
    }
    
    public int getN() {
        return this.n;
    }
    
    public PolynomialFunction[] getPolynomials() {
        final PolynomialFunction[] p = new PolynomialFunction[this.n];
        System.arraycopy(this.polynomials, 0, p, 0, this.n);
        return p;
    }
    
    public double[] getKnots() {
        final double[] out = new double[this.n + 1];
        System.arraycopy(this.knots, 0, out, 0, this.n + 1);
        return out;
    }
    
    public boolean isValidPoint(final double x) {
        return x >= this.knots[0] && x <= this.knots[this.n];
    }
}
