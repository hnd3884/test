package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateDifferentiableFunction extends UnivariateFunction
{
    DerivativeStructure value(final DerivativeStructure p0) throws DimensionMismatchException;
}
