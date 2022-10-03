package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

class DenseWeightedEvaluation extends AbstractEvaluation
{
    private final LeastSquaresProblem.Evaluation unweighted;
    private final RealMatrix weightSqrt;
    
    DenseWeightedEvaluation(final LeastSquaresProblem.Evaluation unweighted, final RealMatrix weightSqrt) {
        super(weightSqrt.getColumnDimension());
        this.unweighted = unweighted;
        this.weightSqrt = weightSqrt;
    }
    
    public RealMatrix getJacobian() {
        return this.weightSqrt.multiply(this.unweighted.getJacobian());
    }
    
    public RealVector getResiduals() {
        return this.weightSqrt.operate(this.unweighted.getResiduals());
    }
    
    public RealVector getPoint() {
        return this.unweighted.getPoint();
    }
}
