package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import java.io.Serializable;

public class NevilleInterpolator implements UnivariateInterpolator, Serializable
{
    static final long serialVersionUID = 3003707660147873733L;
    
    public PolynomialFunctionLagrangeForm interpolate(final double[] x, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        return new PolynomialFunctionLagrangeForm(x, y);
    }
}
