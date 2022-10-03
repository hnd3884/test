package org.apache.commons.math3.analysis;

@Deprecated
public interface DifferentiableMultivariateFunction extends MultivariateFunction
{
    MultivariateFunction partialDerivative(final int p0);
    
    MultivariateVectorFunction gradient();
}
