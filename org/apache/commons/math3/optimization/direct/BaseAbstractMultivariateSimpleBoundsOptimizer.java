package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.BaseMultivariateSimpleBoundsOptimizer;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public abstract class BaseAbstractMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction> extends BaseAbstractMultivariateOptimizer<FUNC> implements BaseMultivariateOptimizer<FUNC>, BaseMultivariateSimpleBoundsOptimizer<FUNC>
{
    @Deprecated
    protected BaseAbstractMultivariateSimpleBoundsOptimizer() {
    }
    
    protected BaseAbstractMultivariateSimpleBoundsOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    @Override
    public PointValuePair optimize(final int maxEval, final FUNC f, final GoalType goalType, final double[] startPoint) {
        return super.optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }
    
    public PointValuePair optimize(final int maxEval, final FUNC f, final GoalType goalType, final double[] startPoint, final double[] lower, final double[] upper) {
        return super.optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint), new SimpleBounds(lower, upper));
    }
}
