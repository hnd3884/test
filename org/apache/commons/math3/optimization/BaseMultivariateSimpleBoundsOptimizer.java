package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public interface BaseMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction> extends BaseMultivariateOptimizer<FUNC>
{
    PointValuePair optimize(final int p0, final FUNC p1, final GoalType p2, final double[] p3, final double[] p4, final double[] p5);
}
