package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;

public class StepNormalizer implements StepHandler
{
    private double h;
    private final FixedStepHandler handler;
    private double firstTime;
    private double lastTime;
    private double[] lastState;
    private double[] lastDerivatives;
    private boolean forward;
    private final StepNormalizerBounds bounds;
    private final StepNormalizerMode mode;
    
    public StepNormalizer(final double h, final FixedStepHandler handler) {
        this(h, handler, StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST);
    }
    
    public StepNormalizer(final double h, final FixedStepHandler handler, final StepNormalizerMode mode) {
        this(h, handler, mode, StepNormalizerBounds.FIRST);
    }
    
    public StepNormalizer(final double h, final FixedStepHandler handler, final StepNormalizerBounds bounds) {
        this(h, handler, StepNormalizerMode.INCREMENT, bounds);
    }
    
    public StepNormalizer(final double h, final FixedStepHandler handler, final StepNormalizerMode mode, final StepNormalizerBounds bounds) {
        this.h = FastMath.abs(h);
        this.handler = handler;
        this.mode = mode;
        this.bounds = bounds;
        this.firstTime = Double.NaN;
        this.lastTime = Double.NaN;
        this.lastState = null;
        this.lastDerivatives = null;
        this.forward = true;
    }
    
    public void init(final double t0, final double[] y0, final double t) {
        this.firstTime = Double.NaN;
        this.lastTime = Double.NaN;
        this.lastState = null;
        this.lastDerivatives = null;
        this.forward = true;
        this.handler.init(t0, y0, t);
    }
    
    public void handleStep(final StepInterpolator interpolator, final boolean isLast) throws MaxCountExceededException {
        if (this.lastState == null) {
            this.firstTime = interpolator.getPreviousTime();
            interpolator.setInterpolatedTime(this.lastTime = interpolator.getPreviousTime());
            this.lastState = interpolator.getInterpolatedState().clone();
            this.lastDerivatives = interpolator.getInterpolatedDerivatives().clone();
            if (!(this.forward = (interpolator.getCurrentTime() >= this.lastTime))) {
                this.h = -this.h;
            }
        }
        double nextTime = (this.mode == StepNormalizerMode.INCREMENT) ? (this.lastTime + this.h) : ((FastMath.floor(this.lastTime / this.h) + 1.0) * this.h);
        if (this.mode == StepNormalizerMode.MULTIPLES && Precision.equals(nextTime, this.lastTime, 1)) {
            nextTime += this.h;
        }
        for (boolean nextInStep = this.isNextInStep(nextTime, interpolator); nextInStep; nextInStep = this.isNextInStep(nextTime, interpolator)) {
            this.doNormalizedStep(false);
            this.storeStep(interpolator, nextTime);
            nextTime += this.h;
        }
        if (isLast) {
            final boolean addLast = this.bounds.lastIncluded() && this.lastTime != interpolator.getCurrentTime();
            this.doNormalizedStep(!addLast);
            if (addLast) {
                this.storeStep(interpolator, interpolator.getCurrentTime());
                this.doNormalizedStep(true);
            }
        }
    }
    
    private boolean isNextInStep(final double nextTime, final StepInterpolator interpolator) {
        return this.forward ? (nextTime <= interpolator.getCurrentTime()) : (nextTime >= interpolator.getCurrentTime());
    }
    
    private void doNormalizedStep(final boolean isLast) {
        if (!this.bounds.firstIncluded() && this.firstTime == this.lastTime) {
            return;
        }
        this.handler.handleStep(this.lastTime, this.lastState, this.lastDerivatives, isLast);
    }
    
    private void storeStep(final StepInterpolator interpolator, final double t) throws MaxCountExceededException {
        interpolator.setInterpolatedTime(this.lastTime = t);
        System.arraycopy(interpolator.getInterpolatedState(), 0, this.lastState, 0, this.lastState.length);
        System.arraycopy(interpolator.getInterpolatedDerivatives(), 0, this.lastDerivatives, 0, this.lastDerivatives.length);
    }
}
