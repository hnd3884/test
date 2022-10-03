package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.OptimizationProblem;

public interface LeastSquaresProblem extends OptimizationProblem<Evaluation>
{
    RealVector getStart();
    
    int getObservationSize();
    
    int getParameterSize();
    
    Evaluation evaluate(final RealVector p0);
    
    public interface Evaluation
    {
        RealMatrix getCovariances(final double p0);
        
        RealVector getSigma(final double p0);
        
        double getRMS();
        
        RealMatrix getJacobian();
        
        double getCost();
        
        RealVector getResiduals();
        
        RealVector getPoint();
    }
}
