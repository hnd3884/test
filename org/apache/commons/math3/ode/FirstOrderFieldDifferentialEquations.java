package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public interface FirstOrderFieldDifferentialEquations<T extends RealFieldElement<T>>
{
    int getDimension();
    
    void init(final T p0, final T[] p1, final T p2);
    
    T[] computeDerivatives(final T p0, final T[] p1);
}
