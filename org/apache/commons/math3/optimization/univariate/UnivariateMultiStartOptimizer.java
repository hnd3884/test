package org.apache.commons.math3.optimization.univariate;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.analysis.UnivariateFunction;

@Deprecated
public class UnivariateMultiStartOptimizer<FUNC extends UnivariateFunction> implements BaseUnivariateOptimizer<FUNC>
{
    private final BaseUnivariateOptimizer<FUNC> optimizer;
    private int maxEvaluations;
    private int totalEvaluations;
    private int starts;
    private RandomGenerator generator;
    private UnivariatePointValuePair[] optima;
    
    public UnivariateMultiStartOptimizer(final BaseUnivariateOptimizer<FUNC> optimizer, final int starts, final RandomGenerator generator) {
        if (optimizer == null || generator == null) {
            throw new NullArgumentException();
        }
        if (starts < 1) {
            throw new NotStrictlyPositiveException(starts);
        }
        this.optimizer = optimizer;
        this.starts = starts;
        this.generator = generator;
    }
    
    public ConvergenceChecker<UnivariatePointValuePair> getConvergenceChecker() {
        return this.optimizer.getConvergenceChecker();
    }
    
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }
    
    public int getEvaluations() {
        return this.totalEvaluations;
    }
    
    public UnivariatePointValuePair[] getOptima() {
        if (this.optima == null) {
            throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
        }
        return this.optima.clone();
    }
    
    public UnivariatePointValuePair optimize(final int maxEval, final FUNC f, final GoalType goal, final double min, final double max) {
        return this.optimize(maxEval, f, goal, min, max, min + 0.5 * (max - min));
    }
    
    public UnivariatePointValuePair optimize(final int maxEval, final FUNC f, final GoalType goal, final double min, final double max, final double startValue) {
        RuntimeException lastException = null;
        this.optima = new UnivariatePointValuePair[this.starts];
        this.totalEvaluations = 0;
        for (int i = 0; i < this.starts; ++i) {
            try {
                final double s = (i == 0) ? startValue : (min + this.generator.nextDouble() * (max - min));
                this.optima[i] = this.optimizer.optimize(maxEval - this.totalEvaluations, f, goal, min, max, s);
            }
            catch (final RuntimeException mue) {
                lastException = mue;
                this.optima[i] = null;
            }
            this.totalEvaluations += this.optimizer.getEvaluations();
        }
        this.sortPairs(goal);
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
