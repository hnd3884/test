package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;

public interface UnivariateDifferentiableVectorFunction extends UnivariateVectorFunction
{
    DerivativeStructure[] value(final DerivativeStructure p0) throws MathIllegalArgumentException;
}
