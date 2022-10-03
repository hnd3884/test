package org.apache.commons.math3.ode;

public interface SecondOrderDifferentialEquations
{
    int getDimension();
    
    void computeSecondDerivatives(final double p0, final double[] p1, final double[] p2, final double[] p3);
}
