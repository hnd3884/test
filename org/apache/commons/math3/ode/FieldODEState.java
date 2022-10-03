package org.apache.commons.math3.ode;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class FieldODEState<T extends RealFieldElement<T>>
{
    private final T time;
    private final T[] state;
    private final T[][] secondaryState;
    
    public FieldODEState(final T time, final T[] state) {
        this(time, state, null);
    }
    
    public FieldODEState(final T time, final T[] state, final T[][] secondaryState) {
        this.time = time;
        this.state = state.clone();
        this.secondaryState = this.copy(time.getField(), secondaryState);
    }
    
    protected T[][] copy(final Field<T> field, final T[][] original) {
        if (original == null) {
            return null;
        }
        final T[][] copied = MathArrays.buildArray(field, original.length, -1);
        for (int i = 0; i < original.length; ++i) {
            copied[i] = original[i].clone();
        }
        return copied;
    }
    
    public T getTime() {
        return this.time;
    }
    
    public int getStateDimension() {
        return this.state.length;
    }
    
    public T[] getState() {
        return this.state.clone();
    }
    
    public int getNumberOfSecondaryStates() {
        return (this.secondaryState == null) ? 0 : this.secondaryState.length;
    }
    
    public int getSecondaryStateDimension(final int index) {
        return (index == 0) ? this.state.length : this.secondaryState[index - 1].length;
    }
    
    public T[] getSecondaryState(final int index) {
        return (index == 0) ? this.state.clone() : this.secondaryState[index - 1].clone();
    }
}
