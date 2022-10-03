package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;

public interface UnivariateMatrixFunctionDifferentiator
{
    UnivariateDifferentiableMatrixFunction differentiate(final UnivariateMatrixFunction p0);
}
