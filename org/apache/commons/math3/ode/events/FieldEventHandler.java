package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.RealFieldElement;

public interface FieldEventHandler<T extends RealFieldElement<T>>
{
    void init(final FieldODEStateAndDerivative<T> p0, final T p1);
    
    T g(final FieldODEStateAndDerivative<T> p0);
    
    Action eventOccurred(final FieldODEStateAndDerivative<T> p0, final boolean p1);
    
    FieldODEState<T> resetState(final FieldODEStateAndDerivative<T> p0);
}
