package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.DifferentiableMultivariateOptimizer;
import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;

@Deprecated
public abstract class AbstractScalarDifferentiableOptimizer extends BaseAbstractMultivariateOptimizer<DifferentiableMultivariateFunction> implements DifferentiableMultivariateOptimizer
{
    private MultivariateVectorFunction gradient;
    
    @Deprecated
    protected AbstractScalarDifferentiableOptimizer() {
    }
    
    protected AbstractScalarDifferentiableOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    protected double[] computeObjectiveGradient(final double[] evaluationPoint) {
        return this.gradient.value(evaluationPoint);
    }
    
    @Override
    protected PointValuePair optimizeInternal(final int maxEval, final DifferentiableMultivariateFunction f, final GoalType goalType, final double[] startPoint) {
        this.gradient = f.gradient();
        return super.optimizeInternal(maxEval, f, goalType, startPoint);
    }
    
    public PointValuePair optimize(final int maxEval, final MultivariateDifferentiableFunction f, final GoalType goalType, final double[] startPoint) {
        return this.optimizeInternal(maxEval, FunctionUtils.toDifferentiableMultivariateFunction(f), goalType, startPoint);
    }
}
