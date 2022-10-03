package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface FirstOrderDifferentialEquations
{
    int getDimension();
    
    void computeDerivatives(final double p0, final double[] p1, final double[] p2) throws MaxCountExceededException, DimensionMismatchException;
}
