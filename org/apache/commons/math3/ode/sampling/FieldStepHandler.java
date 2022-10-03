package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.RealFieldElement;

public interface FieldStepHandler<T extends RealFieldElement<T>>
{
    void init(final FieldODEStateAndDerivative<T> p0, final T p1);
    
    void handleStep(final FieldStepInterpolator<T> p0, final boolean p1) throws MaxCountExceededException;
}
