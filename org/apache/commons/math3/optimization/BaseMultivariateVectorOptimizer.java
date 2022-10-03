package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;

@Deprecated
public interface BaseMultivariateVectorOptimizer<FUNC extends MultivariateVectorFunction> extends BaseOptimizer<PointVectorValuePair>
{
    @Deprecated
    PointVectorValuePair optimize(final int p0, final FUNC p1, final double[] p2, final double[] p3, final double[] p4);
}
