package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public abstract class BaseAbstractMultivariateOptimizer<FUNC extends MultivariateFunction> implements BaseMultivariateOptimizer<FUNC>
{
    protected final Incrementor evaluations;
    private ConvergenceChecker<PointValuePair> checker;
    private GoalType goal;
    private double[] start;
    private double[] lowerBound;
    private double[] upperBound;
    private MultivariateFunction function;
    
    @Deprecated
    protected BaseAbstractMultivariateOptimizer() {
        this(new SimpleValueChecker());
    }
    
    protected BaseAbstractMultivariateOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        this.evaluations = new Incrementor();
        this.checker = checker;
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public ConvergenceChecker<PointValuePair> getConvergenceChecker() {
        return this.checker;
    }
    
    protected double computeObjectiveValue(final double[] point) {
        try {
            this.evaluations.incrementCount();
        }
        catch (final MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return this.function.value(point);
    }
    
    @Deprecated
    public PointValuePair optimize(final int maxEval, final FUNC f, final GoalType goalType, final double[] startPoint) {
        return this.optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }
    
    public PointValuePair optimize(final int maxEval, final FUNC f, final GoalType goalType, final OptimizationData... optData) {
        return this.optimizeInternal(maxEval, f, goalType, optData);
    }
    
    @Deprecated
    protected PointValuePair optimizeInternal(final int maxEval, final FUNC f, final GoalType goalType, final double[] startPoint) {
        return this.optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }
    
    protected PointValuePair optimizeInternal(final int maxEval, final FUNC f, final GoalType goalType, final OptimizationData... optData) throws TooManyEvaluationsException {
        this.evaluations.setMaximalCount(maxEval);
        this.evaluations.resetCount();
        this.function = f;
        this.goal = goalType;
        this.parseOptimizationData(optData);
        this.checkParameters();
        return this.doOptimize();
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof InitialGuess) {
                this.start = ((InitialGuess)data).getInitialGuess();
            }
            else if (data instanceof SimpleBounds) {
                final SimpleBounds bounds = (SimpleBounds)data;
                this.lowerBound = bounds.getLower();
                this.upperBound = bounds.getUpper();
            }
        }
    }
    
    public GoalType getGoalType() {
        return this.goal;
    }
    
    public double[] getStartPoint() {
        return (double[])((this.start == null) ? null : ((double[])this.start.clone()));
    }
    
    public double[] getLowerBound() {
        return (double[])((this.lowerBound == null) ? null : ((double[])this.lowerBound.clone()));
    }
    
    public double[] getUpperBound() {
        return (double[])((this.upperBound == null) ? null : ((double[])this.upperBound.clone()));
    }
    
    protected abstract PointValuePair doOptimize();
    
    private void checkParameters() {
        if (this.start != null) {
            final int dim = this.start.length;
            if (this.lowerBound != null) {
                if (this.lowerBound.length != dim) {
                    throw new DimensionMismatchException(this.lowerBound.length, dim);
                }
                for (int i = 0; i < dim; ++i) {
                    final double v = this.start[i];
                    final double lo = this.lowerBound[i];
                    if (v < lo) {
                        throw new NumberIsTooSmallException(v, lo, true);
                    }
                }
            }
            if (this.upperBound != null) {
                if (this.upperBound.length != dim) {
                    throw new DimensionMismatchException(this.upperBound.length, dim);
                }
                for (int i = 0; i < dim; ++i) {
                    final double v = this.start[i];
                    final double hi = this.upperBound[i];
                    if (v > hi) {
                        throw new NumberIsTooLargeException(v, hi, true);
                    }
                }
            }
            if (this.lowerBound == null) {
                this.lowerBound = new double[dim];
                for (int i = 0; i < dim; ++i) {
                    this.lowerBound[i] = Double.NEGATIVE_INFINITY;
                }
            }
            if (this.upperBound == null) {
                this.upperBound = new double[dim];
                for (int i = 0; i < dim; ++i) {
                    this.upperBound[i] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }
}
