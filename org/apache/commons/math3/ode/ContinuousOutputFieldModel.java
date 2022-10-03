package org.apache.commons.math3.ode;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Iterator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.ArrayList;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import java.util.List;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.RealFieldElement;

public class ContinuousOutputFieldModel<T extends RealFieldElement<T>> implements FieldStepHandler<T>
{
    private T initialTime;
    private T finalTime;
    private boolean forward;
    private int index;
    private List<FieldStepInterpolator<T>> steps;
    
    public ContinuousOutputFieldModel() {
        this.steps = new ArrayList<FieldStepInterpolator<T>>();
        this.initialTime = null;
        this.finalTime = null;
        this.forward = true;
        this.index = 0;
    }
    
    public void append(final ContinuousOutputFieldModel<T> model) throws MathIllegalArgumentException, MaxCountExceededException {
        if (model.steps.size() == 0) {
            return;
        }
        if (this.steps.size() == 0) {
            this.initialTime = model.initialTime;
            this.forward = model.forward;
        }
        else {
            final FieldODEStateAndDerivative<T> s1 = this.steps.get(0).getPreviousState();
            final FieldODEStateAndDerivative<T> s2 = model.steps.get(0).getPreviousState();
            this.checkDimensionsEquality(s1.getStateDimension(), s2.getStateDimension());
            this.checkDimensionsEquality(s1.getNumberOfSecondaryStates(), s2.getNumberOfSecondaryStates());
            for (int i = 0; i < s1.getNumberOfSecondaryStates(); ++i) {
                this.checkDimensionsEquality(s1.getSecondaryStateDimension(i), s2.getSecondaryStateDimension(i));
            }
            if (this.forward ^ model.forward) {
                throw new MathIllegalArgumentException(LocalizedFormats.PROPAGATION_DIRECTION_MISMATCH, new Object[0]);
            }
            final FieldStepInterpolator<T> lastInterpolator = this.steps.get(this.index);
            final T current = lastInterpolator.getCurrentState().getTime();
            final T previous = lastInterpolator.getPreviousState().getTime();
            final T step = current.subtract(previous);
            final T gap = model.getInitialTime().subtract(current);
            if (((RealFieldElement<RealFieldElement<RealFieldElement>>)gap).abs().subtract(((RealFieldElement<RealFieldElement<Object>>)step).abs().multiply(0.001)).getReal() > 0.0) {
                throw new MathIllegalArgumentException(LocalizedFormats.HOLE_BETWEEN_MODELS_TIME_RANGES, new Object[] { ((RealFieldElement<RealFieldElement>)gap).abs().getReal() });
            }
        }
        for (final FieldStepInterpolator<T> interpolator : model.steps) {
            this.steps.add(interpolator);
        }
        this.index = this.steps.size() - 1;
        this.finalTime = (T)this.steps.get(this.index).getCurrentState().getTime();
    }
    
    private void checkDimensionsEquality(final int d1, final int d2) throws DimensionMismatchException {
        if (d1 != d2) {
            throw new DimensionMismatchException(d2, d1);
        }
    }
    
    public void init(final FieldODEStateAndDerivative<T> initialState, final T t) {
        this.initialTime = initialState.getTime();
        this.finalTime = t;
        this.forward = true;
        this.index = 0;
        this.steps.clear();
    }
    
    public void handleStep(final FieldStepInterpolator<T> interpolator, final boolean isLast) throws MaxCountExceededException {
        if (this.steps.size() == 0) {
            this.initialTime = interpolator.getPreviousState().getTime();
            this.forward = interpolator.isForward();
        }
        this.steps.add(interpolator);
        if (isLast) {
            this.finalTime = interpolator.getCurrentState().getTime();
            this.index = this.steps.size() - 1;
        }
    }
    
    public T getInitialTime() {
        return this.initialTime;
    }
    
    public T getFinalTime() {
        return this.finalTime;
    }
    
    public FieldODEStateAndDerivative<T> getInterpolatedState(final T time) {
        int iMin = 0;
        final FieldStepInterpolator<T> sMin = this.steps.get(iMin);
        T tMin = (T)sMin.getPreviousState().getTime().add(sMin.getCurrentState().getTime()).multiply(0.5);
        int iMax = this.steps.size() - 1;
        final FieldStepInterpolator<T> sMax = this.steps.get(iMax);
        T tMax = (T)sMax.getPreviousState().getTime().add(sMax.getCurrentState().getTime()).multiply(0.5);
        if (this.locatePoint(time, sMin) <= 0) {
            this.index = iMin;
            return sMin.getInterpolatedState(time);
        }
        if (this.locatePoint(time, sMax) >= 0) {
            this.index = iMax;
            return sMax.getInterpolatedState(time);
        }
        while (iMax - iMin > 5) {
            final FieldStepInterpolator<T> si = this.steps.get(this.index);
            final int location = this.locatePoint(time, si);
            if (location < 0) {
                iMax = this.index;
                tMax = (T)si.getPreviousState().getTime().add(si.getCurrentState().getTime()).multiply(0.5);
            }
            else {
                if (location <= 0) {
                    return si.getInterpolatedState(time);
                }
                iMin = this.index;
                tMin = (T)si.getPreviousState().getTime().add(si.getCurrentState().getTime()).multiply(0.5);
            }
            final int iMed = (iMin + iMax) / 2;
            final FieldStepInterpolator<T> sMed = this.steps.get(iMed);
            final T tMed = (T)sMed.getPreviousState().getTime().add(sMed.getCurrentState().getTime()).multiply(0.5);
            if (((RealFieldElement<RealFieldElement<RealFieldElement>>)tMed.subtract(tMin)).abs().subtract(1.0E-6).getReal() < 0.0 || ((RealFieldElement<RealFieldElement<RealFieldElement>>)tMax.subtract(tMed)).abs().subtract(1.0E-6).getReal() < 0.0) {
                this.index = iMed;
            }
            else {
                final T d12 = tMax.subtract(tMed);
                final T d13 = tMed.subtract(tMin);
                final T d14 = tMax.subtract(tMin);
                final T dt1 = time.subtract(tMax);
                final T dt2 = time.subtract(tMed);
                final T dt3 = time.subtract(tMin);
                final T iLagrange = (T)((RealFieldElement)((FieldElement<RealFieldElement<RealFieldElement>>)dt2.multiply(dt3).multiply(d13)).multiply(iMax).subtract(((FieldElement<RealFieldElement>)dt1.multiply(dt3).multiply(d14)).multiply(iMed)).add(((FieldElement<Object>)dt1.multiply(dt2).multiply(d12)).multiply(iMin))).divide(d12.multiply(d13).multiply(d14));
                this.index = (int)FastMath.rint(iLagrange.getReal());
            }
            final int low = FastMath.max(iMin + 1, (9 * iMin + iMax) / 10);
            final int high = FastMath.min(iMax - 1, (iMin + 9 * iMax) / 10);
            if (this.index < low) {
                this.index = low;
            }
            else {
                if (this.index <= high) {
                    continue;
                }
                this.index = high;
            }
        }
        this.index = iMin;
        while (this.index <= iMax && this.locatePoint(time, this.steps.get(this.index)) > 0) {
            ++this.index;
        }
        return this.steps.get(this.index).getInterpolatedState(time);
    }
    
    private int locatePoint(final T time, final FieldStepInterpolator<T> interval) {
        if (this.forward) {
            if (time.subtract(interval.getPreviousState().getTime()).getReal() < 0.0) {
                return -1;
            }
            if (time.subtract(interval.getCurrentState().getTime()).getReal() > 0.0) {
                return 1;
            }
            return 0;
        }
        else {
            if (time.subtract(interval.getPreviousState().getTime()).getReal() > 0.0) {
                return -1;
            }
            if (time.subtract(interval.getCurrentState().getTime()).getReal() < 0.0) {
                return 1;
            }
            return 0;
        }
    }
}
