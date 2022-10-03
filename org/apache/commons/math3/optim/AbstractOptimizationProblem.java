package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.Incrementor;

public abstract class AbstractOptimizationProblem<PAIR> implements OptimizationProblem<PAIR>
{
    private static final MaxEvalCallback MAX_EVAL_CALLBACK;
    private static final MaxIterCallback MAX_ITER_CALLBACK;
    private final int maxEvaluations;
    private final int maxIterations;
    private final ConvergenceChecker<PAIR> checker;
    
    protected AbstractOptimizationProblem(final int maxEvaluations, final int maxIterations, final ConvergenceChecker<PAIR> checker) {
        this.maxEvaluations = maxEvaluations;
        this.maxIterations = maxIterations;
        this.checker = checker;
    }
    
    public Incrementor getEvaluationCounter() {
        return new Incrementor(this.maxEvaluations, AbstractOptimizationProblem.MAX_EVAL_CALLBACK);
    }
    
    public Incrementor getIterationCounter() {
        return new Incrementor(this.maxIterations, AbstractOptimizationProblem.MAX_ITER_CALLBACK);
    }
    
    public ConvergenceChecker<PAIR> getConvergenceChecker() {
        return this.checker;
    }
    
    static {
        MAX_EVAL_CALLBACK = new MaxEvalCallback();
        MAX_ITER_CALLBACK = new MaxIterCallback();
    }
    
    private static class MaxEvalCallback implements Incrementor.MaxCountExceededCallback
    {
        public void trigger(final int max) {
            throw new TooManyEvaluationsException(max);
        }
    }
    
    private static class MaxIterCallback implements Incrementor.MaxCountExceededCallback
    {
        public void trigger(final int max) {
            throw new TooManyIterationsException(max);
        }
    }
}
