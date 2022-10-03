package org.apache.commons.math3.fitting.leastsquares;

public interface LeastSquaresOptimizer
{
    Optimum optimize(final LeastSquaresProblem p0);
    
    public interface Optimum extends LeastSquaresProblem.Evaluation
    {
        int getEvaluations();
        
        int getIterations();
    }
}
