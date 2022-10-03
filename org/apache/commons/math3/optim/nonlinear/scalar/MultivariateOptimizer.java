package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;

public abstract class MultivariateOptimizer extends BaseMultivariateOptimizer<PointValuePair>
{
    private MultivariateFunction function;
    private GoalType goal;
    
    protected MultivariateOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        return super.optimize(optData);
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof GoalType) {
                this.goal = (GoalType)data;
            }
            else if (data instanceof ObjectiveFunction) {
                this.function = ((ObjectiveFunction)data).getObjectiveFunction();
            }
        }
    }
    
    public GoalType getGoalType() {
        return this.goal;
    }
    
    public double computeObjectiveValue(final double[] params) {
        super.incrementEvaluationCount();
        return this.function.value(params);
    }
}
