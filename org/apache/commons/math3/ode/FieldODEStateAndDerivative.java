package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public class FieldODEStateAndDerivative<T extends RealFieldElement<T>> extends FieldODEState<T>
{
    private final T[] derivative;
    private final T[][] secondaryDerivative;
    
    public FieldODEStateAndDerivative(final T time, final T[] state, final T[] derivative) {
        this(time, state, derivative, null, null);
    }
    
    public FieldODEStateAndDerivative(final T time, final T[] state, final T[] derivative, final T[][] secondaryState, final T[][] secondaryDerivative) {
        super(time, state, secondaryState);
        this.derivative = derivative.clone();
        this.secondaryDerivative = this.copy(time.getField(), secondaryDerivative);
    }
    
    public T[] getDerivative() {
        return this.derivative.clone();
    }
    
    public T[] getSecondaryDerivative(final int index) {
        return (index == 0) ? this.derivative.clone() : this.secondaryDerivative[index - 1].clone();
    }
}
