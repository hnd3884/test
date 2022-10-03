package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.optimization.ConvergenceChecker;

@Deprecated
public abstract class BaseAbstractUnivariateOptimizer implements UnivariateOptimizer
{
    private final ConvergenceChecker<UnivariatePointValuePair> checker;
    private final Incrementor evaluations;
    private GoalType goal;
    private double searchMin;
    private double searchMax;
    private double searchStart;
    private UnivariateFunction function;
    
    protected BaseAbstractUnivariateOptimizer(final ConvergenceChecker<UnivariatePointValuePair> checker) {
        this.evaluations = new Incrementor();
        this.checker = checker;
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public GoalType getGoalType() {
        return this.goal;
    }
    
    public double getMin() {
        return this.searchMin;
    }
    
    public double getMax() {
        return this.searchMax;
    }
    
    public double getStartValue() {
        return this.searchStart;
    }
    
    protected double computeObjectiveValue(final double point) {
        try {
            this.evaluations.incrementCount();
        }
        catch (final MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return this.function.value(point);
    }
    
    public UnivariatePointValuePair optimize(final int maxEval, final UnivariateFunction f, final GoalType goalType, final double min, final double max, final double startValue) {
        if (f == null) {
            throw new NullArgumentException();
        }
        if (goalType == null) {
            throw new NullArgumentException();
        }
        this.searchMin = min;
        this.searchMax = max;
        this.searchStart = startValue;
        this.goal = goalType;
        this.function = f;
        this.evaluations.setMaximalCount(maxEval);
        this.evaluations.resetCount();
        return this.doOptimize();
    }
    
    public UnivariatePointValuePair optimize(final int maxEval, final UnivariateFunction f, final GoalType goalType, final double min, final double max) {
        return this.optimize(maxEval, f, goalType, min, max, min + 0.5 * (max - min));
    }
    
    public ConvergenceChecker<UnivariatePointValuePair> getConvergenceChecker() {
        return this.checker;
    }
    
    protected abstract UnivariatePointValuePair doOptimize();
}
