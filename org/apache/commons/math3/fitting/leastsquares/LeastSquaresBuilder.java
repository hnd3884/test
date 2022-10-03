package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;

public class LeastSquaresBuilder
{
    private int maxEvaluations;
    private int maxIterations;
    private ConvergenceChecker<LeastSquaresProblem.Evaluation> checker;
    private MultivariateJacobianFunction model;
    private RealVector target;
    private RealVector start;
    private RealMatrix weight;
    private boolean lazyEvaluation;
    private ParameterValidator paramValidator;
    
    public LeastSquaresProblem build() {
        return LeastSquaresFactory.create(this.model, this.target, this.start, this.weight, this.checker, this.maxEvaluations, this.maxIterations, this.lazyEvaluation, this.paramValidator);
    }
    
    public LeastSquaresBuilder maxEvaluations(final int newMaxEvaluations) {
        this.maxEvaluations = newMaxEvaluations;
        return this;
    }
    
    public LeastSquaresBuilder maxIterations(final int newMaxIterations) {
        this.maxIterations = newMaxIterations;
        return this;
    }
    
    public LeastSquaresBuilder checker(final ConvergenceChecker<LeastSquaresProblem.Evaluation> newChecker) {
        this.checker = newChecker;
        return this;
    }
    
    public LeastSquaresBuilder checkerPair(final ConvergenceChecker<PointVectorValuePair> newChecker) {
        return this.checker(LeastSquaresFactory.evaluationChecker(newChecker));
    }
    
    public LeastSquaresBuilder model(final MultivariateVectorFunction value, final MultivariateMatrixFunction jacobian) {
        return this.model(LeastSquaresFactory.model(value, jacobian));
    }
    
    public LeastSquaresBuilder model(final MultivariateJacobianFunction newModel) {
        this.model = newModel;
        return this;
    }
    
    public LeastSquaresBuilder target(final RealVector newTarget) {
        this.target = newTarget;
        return this;
    }
    
    public LeastSquaresBuilder target(final double[] newTarget) {
        return this.target(new ArrayRealVector(newTarget, false));
    }
    
    public LeastSquaresBuilder start(final RealVector newStart) {
        this.start = newStart;
        return this;
    }
    
    public LeastSquaresBuilder start(final double[] newStart) {
        return this.start(new ArrayRealVector(newStart, false));
    }
    
    public LeastSquaresBuilder weight(final RealMatrix newWeight) {
        this.weight = newWeight;
        return this;
    }
    
    public LeastSquaresBuilder lazyEvaluation(final boolean newValue) {
        this.lazyEvaluation = newValue;
        return this;
    }
    
    public LeastSquaresBuilder parameterValidator(final ParameterValidator newValidator) {
        this.paramValidator = newValidator;
        return this;
    }
}
