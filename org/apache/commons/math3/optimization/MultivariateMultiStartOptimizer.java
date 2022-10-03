package org.apache.commons.math3.optimization;

import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class MultivariateMultiStartOptimizer extends BaseMultivariateMultiStartOptimizer<MultivariateFunction> implements MultivariateOptimizer
{
    public MultivariateMultiStartOptimizer(final MultivariateOptimizer optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}
