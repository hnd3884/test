package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.AbstractIntegrator;

public abstract class AdaptiveStepsizeIntegrator extends AbstractIntegrator
{
    protected double scalAbsoluteTolerance;
    protected double scalRelativeTolerance;
    protected double[] vecAbsoluteTolerance;
    protected double[] vecRelativeTolerance;
    protected int mainSetDimension;
    private double initialStep;
    private double minStep;
    private double maxStep;
    
    public AdaptiveStepsizeIntegrator(final String name, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super(name);
        this.setStepSizeControl(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.resetInternalState();
    }
    
    public AdaptiveStepsizeIntegrator(final String name, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(name);
        this.setStepSizeControl(minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.resetInternalState();
    }
    
    public void setStepSizeControl(final double minimalStep, final double maximalStep, final double absoluteTolerance, final double relativeTolerance) {
        this.minStep = FastMath.abs(minimalStep);
        this.maxStep = FastMath.abs(maximalStep);
        this.initialStep = -1.0;
        this.scalAbsoluteTolerance = absoluteTolerance;
        this.scalRelativeTolerance = relativeTolerance;
        this.vecAbsoluteTolerance = null;
        this.vecRelativeTolerance = null;
    }
    
    public void setStepSizeControl(final double minimalStep, final double maximalStep, final double[] absoluteTolerance, final double[] relativeTolerance) {
        this.minStep = FastMath.abs(minimalStep);
        this.maxStep = FastMath.abs(maximalStep);
        this.initialStep = -1.0;
        this.scalAbsoluteTolerance = 0.0;
        this.scalRelativeTolerance = 0.0;
        this.vecAbsoluteTolerance = absoluteTolerance.clone();
        this.vecRelativeTolerance = relativeTolerance.clone();
    }
    
    public void setInitialStepSize(final double initialStepSize) {
        if (initialStepSize < this.minStep || initialStepSize > this.maxStep) {
            this.initialStep = -1.0;
        }
        else {
            this.initialStep = initialStepSize;
        }
    }
    
    @Override
    protected void sanityChecks(final ExpandableStatefulODE equations, final double t) throws DimensionMismatchException, NumberIsTooSmallException {
        super.sanityChecks(equations, t);
        this.mainSetDimension = equations.getPrimaryMapper().getDimension();
        if (this.vecAbsoluteTolerance != null && this.vecAbsoluteTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecAbsoluteTolerance.length);
        }
        if (this.vecRelativeTolerance != null && this.vecRelativeTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecRelativeTolerance.length);
        }
    }
    
    public double initializeStep(final boolean forward, final int order, final double[] scale, final double t0, final double[] y0, final double[] yDot0, final double[] y1, final double[] yDot1) throws MaxCountExceededException, DimensionMismatchException {
        if (this.initialStep > 0.0) {
            return forward ? this.initialStep : (-this.initialStep);
        }
        double yOnScale2 = 0.0;
        double yDotOnScale2 = 0.0;
        for (int j = 0; j < scale.length; ++j) {
            double ratio = y0[j] / scale[j];
            yOnScale2 += ratio * ratio;
            ratio = yDot0[j] / scale[j];
            yDotOnScale2 += ratio * ratio;
        }
        double h = (yOnScale2 < 1.0E-10 || yDotOnScale2 < 1.0E-10) ? 1.0E-6 : (0.01 * FastMath.sqrt(yOnScale2 / yDotOnScale2));
        if (!forward) {
            h = -h;
        }
        for (int i = 0; i < y0.length; ++i) {
            y1[i] = y0[i] + h * yDot0[i];
        }
        this.computeDerivatives(t0 + h, y1, yDot1);
        double yDDotOnScale = 0.0;
        for (int k = 0; k < scale.length; ++k) {
            final double ratio = (yDot1[k] - yDot0[k]) / scale[k];
            yDDotOnScale += ratio * ratio;
        }
        yDDotOnScale = FastMath.sqrt(yDDotOnScale) / h;
        final double maxInv2 = FastMath.max(FastMath.sqrt(yDotOnScale2), yDDotOnScale);
        final double h2 = (maxInv2 < 1.0E-15) ? FastMath.max(1.0E-6, 0.001 * FastMath.abs(h)) : FastMath.pow(0.01 / maxInv2, 1.0 / order);
        h = FastMath.min(100.0 * FastMath.abs(h), h2);
        h = FastMath.max(h, 1.0E-12 * FastMath.abs(t0));
        if (h < this.getMinStep()) {
            h = this.getMinStep();
        }
        if (h > this.getMaxStep()) {
            h = this.getMaxStep();
        }
        if (!forward) {
            h = -h;
        }
        return h;
    }
    
    protected double filterStep(final double h, final boolean forward, final boolean acceptSmall) throws NumberIsTooSmallException {
        double filteredH = h;
        if (FastMath.abs(h) < this.minStep) {
            if (!acceptSmall) {
                throw new NumberIsTooSmallException(LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, FastMath.abs(h), this.minStep, true);
            }
            filteredH = (forward ? this.minStep : (-this.minStep));
        }
        if (filteredH > this.maxStep) {
            filteredH = this.maxStep;
        }
        else if (filteredH < -this.maxStep) {
            filteredH = -this.maxStep;
        }
        return filteredH;
    }
    
    @Override
    public abstract void integrate(final ExpandableStatefulODE p0, final double p1) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;
    
    @Override
    public double getCurrentStepStart() {
        return this.stepStart;
    }
    
    protected void resetInternalState() {
        this.stepStart = Double.NaN;
        this.stepSize = FastMath.sqrt(this.minStep * this.maxStep);
    }
    
    public double getMinStep() {
        return this.minStep;
    }
    
    public double getMaxStep() {
        return this.maxStep;
    }
}
