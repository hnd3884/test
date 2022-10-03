package org.apache.commons.math3.optimization;

import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;

@Deprecated
public class MultivariateDifferentiableVectorMultiStartOptimizer extends BaseMultivariateVectorMultiStartOptimizer<MultivariateDifferentiableVectorFunction> implements MultivariateDifferentiableVectorOptimizer
{
    public MultivariateDifferentiableVectorMultiStartOptimizer(final MultivariateDifferentiableVectorOptimizer optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}
