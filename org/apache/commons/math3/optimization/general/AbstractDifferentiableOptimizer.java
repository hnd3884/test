package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.differentiation.GradientFunction;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;

@Deprecated
public abstract class AbstractDifferentiableOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateDifferentiableFunction>
{
    private MultivariateVectorFunction gradient;
    
    protected AbstractDifferentiableOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    protected double[] computeObjectiveGradient(final double[] evaluationPoint) {
        return this.gradient.value(evaluationPoint);
    }
    
    @Deprecated
    @Override
    protected PointValuePair optimizeInternal(final int maxEval, final MultivariateDifferentiableFunction f, final GoalType goalType, final double[] startPoint) {
        return this.optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }
    
    @Override
    protected PointValuePair optimizeInternal(final int maxEval, final MultivariateDifferentiableFunction f, final GoalType goalType, final OptimizationData... optData) {
        this.gradient = new GradientFunction(f);
        return super.optimizeInternal(maxEval, f, goalType, optData);
    }
}
