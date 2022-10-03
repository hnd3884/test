package org.apache.commons.math3.optimization;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

@Deprecated
public class BaseMultivariateVectorMultiStartOptimizer<FUNC extends MultivariateVectorFunction> implements BaseMultivariateVectorOptimizer<FUNC>
{
    private final BaseMultivariateVectorOptimizer<FUNC> optimizer;
    private int maxEvaluations;
    private int totalEvaluations;
    private int starts;
    private RandomVectorGenerator generator;
    private PointVectorValuePair[] optima;
    
    protected BaseMultivariateVectorMultiStartOptimizer(final BaseMultivariateVectorOptimizer<FUNC> optimizer, final int starts, final RandomVectorGenerator generator) {
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
    
    public PointVectorValuePair[] getOptima() {
        if (this.optima == null) {
            throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
        }
        return this.optima.clone();
    }
    
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }
    
    public int getEvaluations() {
        return this.totalEvaluations;
    }
    
    public ConvergenceChecker<PointVectorValuePair> getConvergenceChecker() {
        return this.optimizer.getConvergenceChecker();
    }
    
    public PointVectorValuePair optimize(final int maxEval, final FUNC f, final double[] target, final double[] weights, final double[] startPoint) {
        this.maxEvaluations = maxEval;
        RuntimeException lastException = null;
        this.optima = new PointVectorValuePair[this.starts];
        this.totalEvaluations = 0;
        for (int i = 0; i < this.starts; ++i) {
            try {
                this.optima[i] = this.optimizer.optimize(maxEval - this.totalEvaluations, f, target, weights, (i == 0) ? startPoint : this.generator.nextVector());
            }
            catch (final ConvergenceException oe) {
                this.optima[i] = null;
            }
            catch (final RuntimeException mue) {
                lastException = mue;
                this.optima[i] = null;
            }
            this.totalEvaluations += this.optimizer.getEvaluations();
        }
        this.sortPairs(target, weights);
        if (this.optima[0] == null) {
            throw lastException;
        }
        return this.optima[0];
    }
    
    private void sortPairs(final double[] target, final double[] weights) {
        Arrays.sort(this.optima, new Comparator<PointVectorValuePair>() {
            public int compare(final PointVectorValuePair o1, final PointVectorValuePair o2) {
                if (o1 == null) {
                    return (o2 != null) ? 1 : 0;
                }
                if (o2 == null) {
                    return -1;
                }
                return Double.compare(this.weightedResidual(o1), this.weightedResidual(o2));
            }
            
            private double weightedResidual(final PointVectorValuePair pv) {
                final double[] value = pv.getValueRef();
                double sum = 0.0;
                for (int i = 0; i < value.length; ++i) {
                    final double ri = value[i] - target[i];
                    sum += weights[i] * ri * ri;
                }
                return sum;
            }
        });
    }
}
