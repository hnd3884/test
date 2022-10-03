package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.RealFieldElement;

public interface FieldStepInterpolator<T extends RealFieldElement<T>>
{
    FieldODEStateAndDerivative<T> getPreviousState();
    
    FieldODEStateAndDerivative<T> getCurrentState();
    
    FieldODEStateAndDerivative<T> getInterpolatedState(final T p0);
    
    boolean isForward();
}
