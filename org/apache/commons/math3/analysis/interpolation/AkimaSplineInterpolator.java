package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class AkimaSplineInterpolator implements UnivariateInterpolator
{
    private static final int MINIMUM_NUMBER_POINTS = 5;
    
    public PolynomialSplineFunction interpolate(final double[] xvals, final double[] yvals) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (xvals == null || yvals == null) {
            throw new NullArgumentException();
        }
        if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        }
        if (xvals.length < 5) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, xvals.length, 5, true);
        }
        MathArrays.checkOrder(xvals);
        final int numberOfDiffAndWeightElements = xvals.length - 1;
        final double[] differences = new double[numberOfDiffAndWeightElements];
        final double[] weights = new double[numberOfDiffAndWeightElements];
        for (int i = 0; i < differences.length; ++i) {
            differences[i] = (yvals[i + 1] - yvals[i]) / (xvals[i + 1] - xvals[i]);
        }
        for (int i = 1; i < weights.length; ++i) {
            weights[i] = FastMath.abs(differences[i] - differences[i - 1]);
        }
        final double[] firstDerivatives = new double[xvals.length];
        for (int j = 2; j < firstDerivatives.length - 2; ++j) {
            final double wP = weights[j + 1];
            final double wM = weights[j - 1];
            if (Precision.equals(wP, 0.0) && Precision.equals(wM, 0.0)) {
                final double xv = xvals[j];
                final double xvP = xvals[j + 1];
                final double xvM = xvals[j - 1];
                firstDerivatives[j] = ((xvP - xv) * differences[j - 1] + (xv - xvM) * differences[j]) / (xvP - xvM);
            }
            else {
                firstDerivatives[j] = (wP * differences[j - 1] + wM * differences[j]) / (wP + wM);
            }
        }
        firstDerivatives[0] = this.differentiateThreePoint(xvals, yvals, 0, 0, 1, 2);
        firstDerivatives[1] = this.differentiateThreePoint(xvals, yvals, 1, 0, 1, 2);
        firstDerivatives[xvals.length - 2] = this.differentiateThreePoint(xvals, yvals, xvals.length - 2, xvals.length - 3, xvals.length - 2, xvals.length - 1);
        firstDerivatives[xvals.length - 1] = this.differentiateThreePoint(xvals, yvals, xvals.length - 1, xvals.length - 3, xvals.length - 2, xvals.length - 1);
        return this.interpolateHermiteSorted(xvals, yvals, firstDerivatives);
    }
    
    private double differentiateThreePoint(final double[] xvals, final double[] yvals, final int indexOfDifferentiation, final int indexOfFirstSample, final int indexOfSecondsample, final int indexOfThirdSample) {
        final double x0 = yvals[indexOfFirstSample];
        final double x2 = yvals[indexOfSecondsample];
        final double x3 = yvals[indexOfThirdSample];
        final double t = xvals[indexOfDifferentiation] - xvals[indexOfFirstSample];
        final double t2 = xvals[indexOfSecondsample] - xvals[indexOfFirstSample];
        final double t3 = xvals[indexOfThirdSample] - xvals[indexOfFirstSample];
        final double a = (x3 - x0 - t3 / t2 * (x2 - x0)) / (t3 * t3 - t2 * t3);
        final double b = (x2 - x0 - a * t2 * t2) / t2;
        return 2.0 * a * t + b;
    }
    
    private PolynomialSplineFunction interpolateHermiteSorted(final double[] xvals, final double[] yvals, final double[] firstDerivatives) {
        if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        }
        if (xvals.length != firstDerivatives.length) {
            throw new DimensionMismatchException(xvals.length, firstDerivatives.length);
        }
        final int minimumLength = 2;
        if (xvals.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, xvals.length, 2, true);
        }
        final int size = xvals.length - 1;
        final PolynomialFunction[] polynomials = new PolynomialFunction[size];
        final double[] coefficients = new double[4];
        for (int i = 0; i < polynomials.length; ++i) {
            final double w = xvals[i + 1] - xvals[i];
            final double w2 = w * w;
            final double yv = yvals[i];
            final double yvP = yvals[i + 1];
            final double fd = firstDerivatives[i];
            final double fdP = firstDerivatives[i + 1];
            coefficients[0] = yv;
            coefficients[1] = firstDerivatives[i];
            coefficients[2] = (3.0 * (yvP - yv) / w - 2.0 * fd - fdP) / w;
            coefficients[3] = (2.0 * (yv - yvP) / w + fd + fdP) / w2;
            polynomials[i] = new PolynomialFunction(coefficients);
        }
        return new PolynomialSplineFunction(xvals, polynomials);
    }
}
