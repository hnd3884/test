package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.BaseOptimizer;
import org.apache.commons.math3.analysis.UnivariateFunction;

@Deprecated
public interface BaseUnivariateOptimizer<FUNC extends UnivariateFunction> extends BaseOptimizer<UnivariatePointValuePair>
{
    UnivariatePointValuePair optimize(final int p0, final FUNC p1, final GoalType p2, final double p3, final double p4);
    
    UnivariatePointValuePair optimize(final int p0, final FUNC p1, final GoalType p2, final double p3, final double p4, final double p5);
}
