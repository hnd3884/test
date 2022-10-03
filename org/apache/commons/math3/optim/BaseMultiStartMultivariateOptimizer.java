package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomVectorGenerator;

public abstract class BaseMultiStartMultivariateOptimizer<PAIR> extends BaseMultivariateOptimizer<PAIR>
{
    private final BaseMultivariateOptimizer<PAIR> optimizer;
    private int totalEvaluations;
    private int starts;
    private RandomVectorGenerator generator;
    private OptimizationData[] optimData;
    private int maxEvalIndex;
    private int initialGuessIndex;
    
    public BaseMultiStartMultivariateOptimizer(final BaseMultivariateOptimizer<PAIR> optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer.getConvergenceChecker());
        this.maxEvalIndex = -1;
        this.initialGuessIndex = -1;
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
    
    public abstract PAIR[] getOptima();
    
    @Override
    public PAIR optimize(final OptimizationData... optData) {
        this.optimData = optData;
        return super.optimize(optData);
    }
    
    @Override
    protected PAIR doOptimize() {
        for (int i = 0; i < this.optimData.length; ++i) {
            if (this.optimData[i] instanceof MaxEval) {
                this.optimData[i] = null;
                this.maxEvalIndex = i;
            }
            if (this.optimData[i] instanceof InitialGuess) {
                this.optimData[i] = null;
                this.initialGuessIndex = i;
            }
        }
        if (this.maxEvalIndex == -1) {
            throw new MathIllegalStateException();
        }
        if (this.initialGuessIndex == -1) {
            throw new MathIllegalStateException();
        }
        RuntimeException lastException = null;
        this.totalEvaluations = 0;
        this.clear();
        final int maxEval = this.getMaxEvaluations();
        final double[] min = this.getLowerBound();
        final double[] max = this.getUpperBound();
        final double[] startPoint = this.getStartPoint();
        for (int j = 0; j < this.starts; ++j) {
            try {
                this.optimData[this.maxEvalIndex] = new MaxEval(maxEval - this.totalEvaluations);
                double[] s = null;
                if (j == 0) {
                    s = startPoint;
                }
                else {
                    int attempts = 0;
                    while (s == null) {
                        if (attempts++ >= this.getMaxEvaluations()) {
                            throw new TooManyEvaluationsException(this.getMaxEvaluations());
                        }
                        s = this.generator.nextVector();
                        for (int k = 0; s != null && k < s.length; ++k) {
                            if ((min != null && s[k] < min[k]) || (max != null && s[k] > max[k])) {
                                s = null;
                            }
                        }
                    }
                }
                this.optimData[this.initialGuessIndex] = new InitialGuess(s);
                final PAIR result = this.optimizer.optimize(this.optimData);
                this.store(result);
            }
            catch (final RuntimeException mue) {
                lastException = mue;
            }
            this.totalEvaluations += this.optimizer.getEvaluations();
        }
        final PAIR[] optima = this.getOptima();
        if (optima.length == 0) {
            throw lastException;
        }
        return optima[0];
    }
    
    protected abstract void store(final PAIR p0);
    
    protected abstract void clear();
}
