package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.RealFieldElement;

public class FieldStepNormalizer<T extends RealFieldElement<T>> implements FieldStepHandler<T>
{
    private double h;
    private final FieldFixedStepHandler<T> handler;
    private FieldODEStateAndDerivative<T> first;
    private FieldODEStateAndDerivative<T> last;
    private boolean forward;
    private final StepNormalizerBounds bounds;
    private final StepNormalizerMode mode;
    
    public FieldStepNormalizer(final double h, final FieldFixedStepHandler<T> handler) {
        this(h, handler, StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST);
    }
    
    public FieldStepNormalizer(final double h, final FieldFixedStepHandler<T> handler, final StepNormalizerMode mode) {
        this(h, handler, mode, StepNormalizerBounds.FIRST);
    }
    
    public FieldStepNormalizer(final double h, final FieldFixedStepHandler<T> handler, final StepNormalizerBounds bounds) {
        this(h, handler, StepNormalizerMode.INCREMENT, bounds);
    }
    
    public FieldStepNormalizer(final double h, final FieldFixedStepHandler<T> handler, final StepNormalizerMode mode, final StepNormalizerBounds bounds) {
        this.h = FastMath.abs(h);
        this.handler = handler;
        this.mode = mode;
        this.bounds = bounds;
        this.first = null;
        this.last = null;
        this.forward = true;
    }
    
    public void init(final FieldODEStateAndDerivative<T> initialState, final T finalTime) {
        this.first = null;
        this.last = null;
        this.forward = true;
        this.handler.init(initialState, finalTime);
    }
    
    public void handleStep(final FieldStepInterpolator<T> interpolator, final boolean isLast) throws MaxCountExceededException {
        if (this.last == null) {
            this.first = interpolator.getPreviousState();
            this.last = this.first;
            if (!(this.forward = interpolator.isForward())) {
                this.h = -this.h;
            }
        }
        T nextTime = (T)((this.mode == StepNormalizerMode.INCREMENT) ? ((T)this.last.getTime().add(this.h)) : this.last.getTime().getField().getZero().add((FastMath.floor(this.last.getTime().getReal() / this.h) + 1.0) * this.h));
        if (this.mode == StepNormalizerMode.MULTIPLES && Precision.equals(nextTime.getReal(), this.last.getTime().getReal(), 1)) {
            nextTime = nextTime.add(this.h);
        }
        for (boolean nextInStep = this.isNextInStep(nextTime, interpolator); nextInStep; nextInStep = this.isNextInStep(nextTime, interpolator)) {
            this.doNormalizedStep(false);
            this.last = interpolator.getInterpolatedState(nextTime);
            nextTime = nextTime.add(this.h);
        }
        if (isLast) {
            final boolean addLast = this.bounds.lastIncluded() && this.last.getTime().getReal() != interpolator.getCurrentState().getTime().getReal();
            this.doNormalizedStep(!addLast);
            if (addLast) {
                this.last = interpolator.getCurrentState();
                this.doNormalizedStep(true);
            }
        }
    }
    
    private boolean isNextInStep(final T nextTime, final FieldStepInterpolator<T> interpolator) {
        return this.forward ? (nextTime.getReal() <= interpolator.getCurrentState().getTime().getReal()) : (nextTime.getReal() >= interpolator.getCurrentState().getTime().getReal());
    }
    
    private void doNormalizedStep(final boolean isLast) {
        if (!this.bounds.firstIncluded() && this.first.getTime().getReal() == this.last.getTime().getReal()) {
            return;
        }
        this.handler.handleStep(this.last, isLast);
    }
}
