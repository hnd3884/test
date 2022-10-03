package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.linear.RealVector;

public interface MultivariateJacobianFunction
{
    Pair<RealVector, RealMatrix> value(final RealVector p0);
}
