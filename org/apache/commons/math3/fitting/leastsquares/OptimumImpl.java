package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

class OptimumImpl implements LeastSquaresOptimizer.Optimum
{
    private final LeastSquaresProblem.Evaluation value;
    private final int evaluations;
    private final int iterations;
    
    OptimumImpl(final LeastSquaresProblem.Evaluation value, final int evaluations, final int iterations) {
        this.value = value;
        this.evaluations = evaluations;
        this.iterations = iterations;
    }
    
    public int getEvaluations() {
        return this.evaluations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    public RealMatrix getCovariances(final double threshold) {
        return this.value.getCovariances(threshold);
    }
    
    public RealVector getSigma(final double covarianceSingularityThreshold) {
        return this.value.getSigma(covarianceSingularityThreshold);
    }
    
    public double getRMS() {
        return this.value.getRMS();
    }
    
    public RealMatrix getJacobian() {
        return this.value.getJacobian();
    }
    
    public double getCost() {
        return this.value.getCost();
    }
    
    public RealVector getResiduals() {
        return this.value.getResiduals();
    }
    
    public RealVector getPoint() {
        return this.value.getPoint();
    }
}
