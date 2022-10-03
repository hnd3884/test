package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.random.RandomGenerator;

public class MultiStartUnivariateOptimizer extends UnivariateOptimizer
{
    private final UnivariateOptimizer optimizer;
    private int totalEvaluations;
    private int starts;
    private RandomGenerator generator;
    private UnivariatePointValuePair[] optima;
    private OptimizationData[] optimData;
    private int maxEvalIndex;
    private int searchIntervalIndex;
    
    public MultiStartUnivariateOptimizer(final UnivariateOptimizer optimizer, final int starts, final RandomGenerator generator) {
        super(optimizer.getConvergenceChecker());
        this.maxEvalIndex = -1;
        this.searchIntervalIndex = -1;
        if (starts < 1) {
            throw new NotStrictlyPositiveException(starts);
        }
        this.optimizer = optimizer;
        this.starts = starts;
        this.generator = generator;
    }
    
    @Override
    public int getEvaluations() {
        return this.totalEvaluations;
    }
    
    public UnivariatePointValuePair[] getOptima() {
        if (this.optima == null) {
            throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
        }
        return this.optima.clone();
    }
    
    @Override
    public UnivariatePointValuePair optimize(final OptimizationData... optData) {
        this.optimData = optData;
        return super.optimize(optData);
    }
    
    @Override
    protected UnivariatePointValuePair doOptimize() {
        for (int i = 0; i < this.optimData.length; ++i) {
            if (this.optimData[i] instanceof MaxEval) {
                this.optimData[i] = null;
                this.maxEvalIndex = i;
            }
            else if (this.optimData[i] instanceof SearchInterval) {
                this.optimData[i] = null;
                this.searchIntervalIndex = i;
            }
        }
        if (this.maxEvalIndex == -1) {
            throw new MathIllegalStateException();
        }
        if (this.searchIntervalIndex == -1) {
            throw new MathIllegalStateException();
        }
        RuntimeException lastException = null;
        this.optima = new UnivariatePointValuePair[this.starts];
        this.totalEvaluations = 0;
        final int maxEval = this.getMaxEvaluations();
        final double min = this.getMin();
        final double max = this.getMax();
        final double startValue = this.getStartValue();
        for (int j = 0; j < this.starts; ++j) {
            try {
                this.optimData[this.maxEvalIndex] = new MaxEval(maxEval - this.totalEvaluations);
                final double s = (j == 0) ? startValue : (min + this.generator.nextDouble() * (max - min));
                this.optimData[this.searchIntervalIndex] = new SearchInterval(min, max, s);
                this.optima[j] = this.optimizer.optimize(this.optimData);
            }
            catch (final RuntimeException mue) {
                lastException = mue;
                this.optima[j] = null;
            }
            this.totalEvaluations += this.optimizer.getEvaluations();
        }
        this.sortPairs(this.getGoalType());
        if (this.optima[0] == null) {
            throw lastException;
        }
        return this.optima[0];
    }
    
    private void sortPairs(final GoalType goal) {
        Arrays.sort(this.optima, new Comparator<UnivariatePointValuePair>() {
            public int compare(final UnivariatePointValuePair o1, final UnivariatePointValuePair o2) {
                if (o1 == null) {
                    return (o2 != null) ? 1 : 0;
                }
                if (o2 == null) {
                    return -1;
                }
                final double v1 = o1.getValue();
                final double v2 = o2.getValue();
                return (goal == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        });
    }
}
