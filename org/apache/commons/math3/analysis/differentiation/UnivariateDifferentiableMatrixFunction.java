package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateMatrixFunction;

public interface UnivariateDifferentiableMatrixFunction extends UnivariateMatrixFunction
{
    DerivativeStructure[][] value(final DerivativeStructure p0) throws MathIllegalArgumentException;
}
