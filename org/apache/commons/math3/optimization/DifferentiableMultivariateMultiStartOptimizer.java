package org.apache.commons.math3.optimization;

import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;

@Deprecated
public class DifferentiableMultivariateMultiStartOptimizer extends BaseMultivariateMultiStartOptimizer<DifferentiableMultivariateFunction> implements DifferentiableMultivariateOptimizer
{
    public DifferentiableMultivariateMultiStartOptimizer(final DifferentiableMultivariateOptimizer optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}
