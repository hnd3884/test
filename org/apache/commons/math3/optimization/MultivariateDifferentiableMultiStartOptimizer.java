package org.apache.commons.math3.optimization;

import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;

@Deprecated
public class MultivariateDifferentiableMultiStartOptimizer extends BaseMultivariateMultiStartOptimizer<MultivariateDifferentiableFunction> implements MultivariateDifferentiableOptimizer
{
    public MultivariateDifferentiableMultiStartOptimizer(final MultivariateDifferentiableOptimizer optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}
