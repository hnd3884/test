package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface SecondaryEquations
{
    int getDimension();
    
    void computeDerivatives(final double p0, final double[] p1, final double[] p2, final double[] p3, final double[] p4) throws MaxCountExceededException, DimensionMismatchException;
}
