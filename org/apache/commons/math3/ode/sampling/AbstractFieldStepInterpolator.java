package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.RealFieldElement;

public abstract class AbstractFieldStepInterpolator<T extends RealFieldElement<T>> implements FieldStepInterpolator<T>
{
    private final FieldODEStateAndDerivative<T> globalPreviousState;
    private final FieldODEStateAndDerivative<T> globalCurrentState;
    private final FieldODEStateAndDerivative<T> softPreviousState;
    private final FieldODEStateAndDerivative<T> softCurrentState;
    private final boolean forward;
    private FieldEquationsMapper<T> mapper;
    
    protected AbstractFieldStepInterpolator(final boolean isForward, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> equationsMapper) {
        this.forward = isForward;
        this.globalPreviousState = globalPreviousState;
        this.globalCurrentState = globalCurrentState;
        this.softPreviousState = softPreviousState;
        this.softCurrentState = softCurrentState;
        this.mapper = equationsMapper;
    }
    
    public AbstractFieldStepInterpolator<T> restrictStep(final FieldODEStateAndDerivative<T> previousState, final FieldODEStateAndDerivative<T> currentState) {
        return this.create(this.forward, this.globalPreviousState, this.globalCurrentState, previousState, currentState, this.mapper);
    }
    
    protected abstract AbstractFieldStepInterpolator<T> create(final boolean p0, final FieldODEStateAndDerivative<T> p1, final FieldODEStateAndDerivative<T> p2, final FieldODEStateAndDerivative<T> p3, final FieldODEStateAndDerivative<T> p4, final FieldEquationsMapper<T> p5);
    
    public FieldODEStateAndDerivative<T> getGlobalPreviousState() {
        return this.globalPreviousState;
    }
    
    public FieldODEStateAndDerivative<T> getGlobalCurrentState() {
        return this.globalCurrentState;
    }
    
    public FieldODEStateAndDerivative<T> getPreviousState() {
        return this.softPreviousState;
    }
    
    public FieldODEStateAndDerivative<T> getCurrentState() {
        return this.softCurrentState;
    }
    
    public FieldODEStateAndDerivative<T> getInterpolatedState(final T time) {
        final T thetaH = time.subtract(this.globalPreviousState.getTime());
        final T oneMinusThetaH = this.globalCurrentState.getTime().subtract(time);
        final T theta = thetaH.divide(this.globalCurrentState.getTime().subtract(this.globalPreviousState.getTime()));
        return this.computeInterpolatedStateAndDerivatives(this.mapper, time, theta, thetaH, oneMinusThetaH);
    }
    
    public boolean isForward() {
        return this.forward;
    }
    
    protected abstract FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> p0, final T p1, final T p2, final T p3, final T p4) throws MaxCountExceededException;
}
