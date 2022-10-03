package org.apache.commons.math3.ode.sampling;

public interface FixedStepHandler
{
    void init(final double p0, final double[] p1, final double p2);
    
    void handleStep(final double p0, final double[] p1, final double[] p2, final boolean p3);
}
