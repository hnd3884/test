package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;

public interface StepHandler
{
    void init(final double p0, final double[] p1, final double p2);
    
    void handleStep(final StepInterpolator p0, final boolean p1) throws MaxCountExceededException;
}
