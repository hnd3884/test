package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public interface BaseMultivariateOptimizer<FUNC extends MultivariateFunction> extends BaseOptimizer<PointValuePair>
{
    @Deprecated
    PointValuePair optimize(final int p0, final FUNC p1, final GoalType p2, final double[] p3);
}
