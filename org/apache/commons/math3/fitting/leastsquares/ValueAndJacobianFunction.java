package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface ValueAndJacobianFunction extends MultivariateJacobianFunction
{
    RealVector computeValue(final double[] p0);
    
    RealMatrix computeJacobian(final double[] p0);
}
