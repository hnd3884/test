package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.ArrayList;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import java.util.List;
import java.io.Serializable;
import org.apache.commons.math3.ode.sampling.StepHandler;

public class ContinuousOutputModel implements StepHandler, Serializable
{
    private static final long serialVersionUID = -1417964919405031606L;
    private double initialTime;
    private double finalTime;
    private boolean forward;
    private int index;
    private List<StepInterpolator> steps;
    
    public ContinuousOutputModel() {
        this.steps = new ArrayList<StepInterpolator>();
        this.initialTime = Double.NaN;
        this.finalTime = Double.NaN;
        this.forward = true;
        this.index = 0;
    }
    
    public void append(final ContinuousOutputModel model) throws MathIllegalArgumentException, MaxCountExceededException {
        if (model.steps.size() == 0) {
            return;
        }
        if (this.steps.size() == 0) {
            this.initialTime = model.initialTime;
            this.forward = model.forward;
        }
        else {
            if (this.getInterpolatedState().length != model.getInterpolatedState().length) {
                throw new DimensionMismatchException(model.getInterpolatedState().length, this.getInterpolatedState().length);
            }
            if (this.forward ^ model.forward) {
                throw new MathIllegalArgumentException(LocalizedFormats.PROPAGATION_DIRECTION_MISMATCH, new Object[0]);
            }
            final StepInterpolator lastInterpolator = this.steps.get(this.index);
            final double current = lastInterpolator.getCurrentTime();
            final double previous = lastInterpolator.getPreviousTime();
            final double step = current - previous;
            final double gap = model.getInitialTime() - current;
            if (FastMath.abs(gap) > 0.001 * FastMath.abs(step)) {
                throw new MathIllegalArgumentException(LocalizedFormats.HOLE_BETWEEN_MODELS_TIME_RANGES, new Object[] { FastMath.abs(gap) });
            }
        }
        for (final StepInterpolator interpolator : model.steps) {
            this.steps.add(interpolator.copy());
        }
        this.index = this.steps.size() - 1;
        this.finalTime = this.steps.get(this.index).getCurrentTime();
    }
    
    public void init(final double t0, final double[] y0, final double t) {
        this.initialTime = Double.NaN;
        this.finalTime = Double.NaN;
        this.forward = true;
        this.index = 0;
        this.steps.clear();
    }
    
    public void handleStep(final StepInterpolator interpolator, final boolean isLast) throws MaxCountExceededException {
        if (this.steps.size() == 0) {
            this.initialTime = interpolator.getPreviousTime();
            this.forward = interpolator.isForward();
        }
        this.steps.add(interpolator.copy());
        if (isLast) {
            this.finalTime = interpolator.getCurrentTime();
            this.index = this.steps.size() - 1;
        }
    }
    
    public double getInitialTime() {
        return this.initialTime;
    }
    
    public double getFinalTime() {
        return this.finalTime;
    }
    
    public double getInterpolatedTime() {
        return this.steps.get(this.index).getInterpolatedTime();
    }
    
    public void setInterpolatedTime(final double time) {
        int iMin = 0;
        final StepInterpolator sMin = this.steps.get(iMin);
        double tMin = 0.5 * (sMin.getPreviousTime() + sMin.getCurrentTime());
        int iMax = this.steps.size() - 1;
        final StepInterpolator sMax = this.steps.get(iMax);
        double tMax = 0.5 * (sMax.getPreviousTime() + sMax.getCurrentTime());
        if (this.locatePoint(time, sMin) <= 0) {
            this.index = iMin;
            sMin.setInterpolatedTime(time);
            return;
        }
        if (this.locatePoint(time, sMax) >= 0) {
            this.index = iMax;
            sMax.setInterpolatedTime(time);
            return;
        }
        while (iMax - iMin > 5) {
            final StepInterpolator si = this.steps.get(this.index);
            final int location = this.locatePoint(time, si);
            if (location < 0) {
                iMax = this.index;
                tMax = 0.5 * (si.getPreviousTime() + si.getCurrentTime());
            }
            else {
                if (location <= 0) {
                    si.setInterpolatedTime(time);
                    return;
                }
                iMin = this.index;
                tMin = 0.5 * (si.getPreviousTime() + si.getCurrentTime());
            }
            final int iMed = (iMin + iMax) / 2;
            final StepInterpolator sMed = this.steps.get(iMed);
            final double tMed = 0.5 * (sMed.getPreviousTime() + sMed.getCurrentTime());
            if (FastMath.abs(tMed - tMin) < 1.0E-6 || FastMath.abs(tMax - tMed) < 1.0E-6) {
                this.index = iMed;
            }
            else {
                final double d12 = tMax - tMed;
                final double d13 = tMed - tMin;
                final double d14 = tMax - tMin;
                final double dt1 = time - tMax;
                final double dt2 = time - tMed;
                final double dt3 = time - tMin;
                final double iLagrange = (dt2 * dt3 * d13 * iMax - dt1 * dt3 * d14 * iMed + dt1 * dt2 * d12 * iMin) / (d12 * d13 * d14);
                this.index = (int)FastMath.rint(iLagrange);
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
        this.steps.get(this.index).setInterpolatedTime(time);
    }
    
    public double[] getInterpolatedState() throws MaxCountExceededException {
        return this.steps.get(this.index).getInterpolatedState();
    }
    
    public double[] getInterpolatedDerivatives() throws MaxCountExceededException {
        return this.steps.get(this.index).getInterpolatedDerivatives();
    }
    
    public double[] getInterpolatedSecondaryState(final int secondaryStateIndex) throws MaxCountExceededException {
        return this.steps.get(this.index).getInterpolatedSecondaryState(secondaryStateIndex);
    }
    
    public double[] getInterpolatedSecondaryDerivatives(final int secondaryStateIndex) throws MaxCountExceededException {
        return this.steps.get(this.index).getInterpolatedSecondaryDerivatives(secondaryStateIndex);
    }
    
    private int locatePoint(final double time, final StepInterpolator interval) {
        if (this.forward) {
            if (time < interval.getPreviousTime()) {
                return -1;
            }
            if (time > interval.getCurrentTime()) {
                return 1;
            }
            return 0;
        }
        else {
            if (time > interval.getPreviousTime()) {
                return -1;
            }
            if (time < interval.getCurrentTime()) {
                return 1;
            }
            return 0;
        }
    }
}
