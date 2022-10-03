package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.RealFieldElement;

public abstract class AdaptiveStepsizeFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T>
{
    protected double scalAbsoluteTolerance;
    protected double scalRelativeTolerance;
    protected double[] vecAbsoluteTolerance;
    protected double[] vecRelativeTolerance;
    protected int mainSetDimension;
    private T initialStep;
    private T minStep;
    private T maxStep;
    
    public AdaptiveStepsizeFieldIntegrator(final Field<T> field, final String name, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super(field, name);
        this.setStepSizeControl(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.resetInternalState();
    }
    
    public AdaptiveStepsizeFieldIntegrator(final Field<T> field, final String name, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(field, name);
        this.setStepSizeControl(minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.resetInternalState();
    }
    
    public void setStepSizeControl(final double minimalStep, final double maximalStep, final double absoluteTolerance, final double relativeTolerance) {
        this.minStep = this.getField().getZero().add(FastMath.abs(minimalStep));
        this.maxStep = this.getField().getZero().add(FastMath.abs(maximalStep));
        this.initialStep = this.getField().getOne().negate();
        this.scalAbsoluteTolerance = absoluteTolerance;
        this.scalRelativeTolerance = relativeTolerance;
        this.vecAbsoluteTolerance = null;
        this.vecRelativeTolerance = null;
    }
    
    public void setStepSizeControl(final double minimalStep, final double maximalStep, final double[] absoluteTolerance, final double[] relativeTolerance) {
        this.minStep = this.getField().getZero().add(FastMath.abs(minimalStep));
        this.maxStep = this.getField().getZero().add(FastMath.abs(maximalStep));
        this.initialStep = this.getField().getOne().negate();
        this.scalAbsoluteTolerance = 0.0;
        this.scalRelativeTolerance = 0.0;
        this.vecAbsoluteTolerance = absoluteTolerance.clone();
        this.vecRelativeTolerance = relativeTolerance.clone();
    }
    
    public void setInitialStepSize(final T initialStepSize) {
        if (initialStepSize.subtract(this.minStep).getReal() < 0.0 || initialStepSize.subtract(this.maxStep).getReal() > 0.0) {
            this.initialStep = this.getField().getOne().negate();
        }
        else {
            this.initialStep = initialStepSize;
        }
    }
    
    @Override
    protected void sanityChecks(final FieldODEState<T> eqn, final T t) throws DimensionMismatchException, NumberIsTooSmallException {
        super.sanityChecks(eqn, t);
        this.mainSetDimension = eqn.getStateDimension();
        if (this.vecAbsoluteTolerance != null && this.vecAbsoluteTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecAbsoluteTolerance.length);
        }
        if (this.vecRelativeTolerance != null && this.vecRelativeTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecRelativeTolerance.length);
        }
    }
    
    public T initializeStep(final boolean forward, final int order, final T[] scale, final FieldODEStateAndDerivative<T> state0, final FieldEquationsMapper<T> mapper) throws MaxCountExceededException, DimensionMismatchException {
        if (this.initialStep.getReal() > 0.0) {
            return forward ? this.initialStep : this.initialStep.negate();
        }
        final T[] y0 = mapper.mapState(state0);
        final T[] yDot0 = mapper.mapDerivative(state0);
        T yOnScale2 = this.getField().getZero();
        T yDotOnScale2 = this.getField().getZero();
        for (int j = 0; j < scale.length; ++j) {
            final T ratio = y0[j].divide(scale[j]);
            yOnScale2 = yOnScale2.add(ratio.multiply(ratio));
            final T ratioDot = yDot0[j].divide(scale[j]);
            yDotOnScale2 = yDotOnScale2.add(ratioDot.multiply(ratioDot));
        }
        T h = (T)((yOnScale2.getReal() < 1.0E-10 || yDotOnScale2.getReal() < 1.0E-10) ? ((T)this.getField().getZero().add(1.0E-6)) : ((RealFieldElement<RealFieldElement<RealFieldElement>>)yOnScale2.divide(yDotOnScale2)).sqrt().multiply(0.01));
        if (!forward) {
            h = h.negate();
        }
        final T[] y2 = MathArrays.buildArray(this.getField(), y0.length);
        for (int i = 0; i < y0.length; ++i) {
            y2[i] = y0[i].add(yDot0[i].multiply(h));
        }
        final T[] yDot2 = this.computeDerivatives(state0.getTime().add(h), y2);
        T yDDotOnScale = this.getField().getZero();
        for (int k = 0; k < scale.length; ++k) {
            final T ratioDotDot = yDot2[k].subtract(yDot0[k]).divide(scale[k]);
            yDDotOnScale = yDDotOnScale.add(ratioDotDot.multiply(ratioDotDot));
        }
        yDDotOnScale = ((RealFieldElement<RealFieldElement<T>>)yDDotOnScale).sqrt().divide(h);
        final T maxInv2 = MathUtils.max(yDotOnScale2.sqrt(), yDDotOnScale);
        final T h2 = (T)((maxInv2.getReal() < 1.0E-15) ? MathUtils.max(this.getField().getZero().add(1.0E-6), (T)((RealFieldElement<RealFieldElement<T>>)h).abs().multiply(0.001)) : ((FieldElement<RealFieldElement<RealFieldElement>>)maxInv2).multiply(100).reciprocal().pow(1.0 / order));
        h = MathUtils.min(((RealFieldElement<RealFieldElement<T>>)h).abs().multiply(100), h2);
        h = MathUtils.max(h, (T)state0.getTime().abs().multiply(1.0E-12));
        h = MathUtils.max(this.minStep, MathUtils.min(this.maxStep, h));
        if (!forward) {
            h = h.negate();
        }
        return h;
    }
    
    protected T filterStep(final T h, final boolean forward, final boolean acceptSmall) throws NumberIsTooSmallException {
        T filteredH = h;
        if (((RealFieldElement<RealFieldElement<T>>)h).abs().subtract(this.minStep).getReal() < 0.0) {
            if (!acceptSmall) {
                throw new NumberIsTooSmallException(LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, ((RealFieldElement<RealFieldElement>)h).abs().getReal(), this.minStep.getReal(), true);
            }
            filteredH = (forward ? this.minStep : this.minStep.negate());
        }
        if (filteredH.subtract(this.maxStep).getReal() > 0.0) {
            filteredH = this.maxStep;
        }
        else if (filteredH.add(this.maxStep).getReal() < 0.0) {
            filteredH = this.maxStep.negate();
        }
        return filteredH;
    }
    
    protected void resetInternalState() {
        this.setStepStart(null);
        this.setStepSize(this.minStep.multiply(this.maxStep).sqrt());
    }
    
    public T getMinStep() {
        return this.minStep;
    }
    
    public T getMaxStep() {
        return this.maxStep;
    }
}
