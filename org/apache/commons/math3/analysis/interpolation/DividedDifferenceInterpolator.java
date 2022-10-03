package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import java.io.Serializable;

public class DividedDifferenceInterpolator implements UnivariateInterpolator, Serializable
{
    private static final long serialVersionUID = 107049519551235069L;
    
    public PolynomialFunctionNewtonForm interpolate(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y, true);
        final double[] c = new double[x.length - 1];
        System.arraycopy(x, 0, c, 0, c.length);
        final double[] a = computeDividedDifference(x, y);
        return new PolynomialFunctionNewtonForm(a, c);
    }
    
    protected static double[] computeDividedDifference(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y, true);
        final double[] divdiff = y.clone();
        final int n = x.length;
        final double[] a = new double[n];
        a[0] = divdiff[0];
        for (int i = 1; i < n; ++i) {
            for (int j = 0; j < n - i; ++j) {
                final double denominator = x[j + i] - x[j];
                divdiff[j] = (divdiff[j + 1] - divdiff[j]) / denominator;
            }
            a[i] = divdiff[0];
        }
        return a;
    }
}
