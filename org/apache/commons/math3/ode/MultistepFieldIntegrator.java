package org.apache.commons.math3.ode;

import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853FieldIntegrator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeFieldIntegrator;
import org.apache.commons.math3.RealFieldElement;

public abstract class MultistepFieldIntegrator<T extends RealFieldElement<T>> extends AdaptiveStepsizeFieldIntegrator<T>
{
    protected T[] scaled;
    protected Array2DRowFieldMatrix<T> nordsieck;
    private FirstOrderFieldIntegrator<T> starter;
    private final int nSteps;
    private double exp;
    private double safety;
    private double minReduction;
    private double maxGrowth;
    
    protected MultistepFieldIntegrator(final Field<T> field, final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(field, name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        if (nSteps < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.INTEGRATION_METHOD_NEEDS_AT_LEAST_TWO_PREVIOUS_POINTS, nSteps, 2, true);
        }
        this.starter = new DormandPrince853FieldIntegrator<T>(field, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.nSteps = nSteps;
        this.exp = -1.0 / order;
        this.setSafety(0.9);
        this.setMinReduction(0.2);
        this.setMaxGrowth(FastMath.pow(2.0, -this.exp));
    }
    
    protected MultistepFieldIntegrator(final Field<T> field, final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(field, name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.starter = new DormandPrince853FieldIntegrator<T>(field, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.nSteps = nSteps;
        this.exp = -1.0 / order;
        this.setSafety(0.9);
        this.setMinReduction(0.2);
        this.setMaxGrowth(FastMath.pow(2.0, -this.exp));
    }
    
    public FirstOrderFieldIntegrator<T> getStarterIntegrator() {
        return this.starter;
    }
    
    public void setStarterIntegrator(final FirstOrderFieldIntegrator<T> starterIntegrator) {
        this.starter = starterIntegrator;
    }
    
    protected void start(final FieldExpandableODE<T> equations, final FieldODEState<T> initialState, final T t) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
        this.starter.clearEventHandlers();
        this.starter.clearStepHandlers();
        this.starter.addStepHandler(new FieldNordsieckInitializer(equations.getMapper(), (this.nSteps + 3) / 2));
        try {
            this.starter.integrate(equations, initialState, t);
            throw new MathIllegalStateException(LocalizedFormats.MULTISTEP_STARTER_STOPPED_EARLY, new Object[0]);
        }
        catch (final InitializationCompletedMarkerException icme) {
            this.getEvaluationsCounter().increment(this.starter.getEvaluations());
            this.starter.clearStepHandlers();
        }
    }
    
    protected abstract Array2DRowFieldMatrix<T> initializeHighOrderDerivatives(final T p0, final T[] p1, final T[][] p2, final T[][] p3);
    
    public double getMinReduction() {
        return this.minReduction;
    }
    
    public void setMinReduction(final double minReduction) {
        this.minReduction = minReduction;
    }
    
    public double getMaxGrowth() {
        return this.maxGrowth;
    }
    
    public void setMaxGrowth(final double maxGrowth) {
        this.maxGrowth = maxGrowth;
    }
    
    public double getSafety() {
        return this.safety;
    }
    
    public void setSafety(final double safety) {
        this.safety = safety;
    }
    
    public int getNSteps() {
        return this.nSteps;
    }
    
    protected void rescale(final T newStepSize) {
        final T ratio = newStepSize.divide(this.getStepSize());
        for (int i = 0; i < this.scaled.length; ++i) {
            this.scaled[i] = this.scaled[i].multiply(ratio);
        }
        final T[][] nData = this.nordsieck.getDataRef();
        T power = ratio;
        for (int j = 0; j < nData.length; ++j) {
            power = power.multiply(ratio);
            final T[] nDataI = nData[j];
            for (int k = 0; k < nDataI.length; ++k) {
                nDataI[k] = nDataI[k].multiply(power);
            }
        }
        this.setStepSize(newStepSize);
    }
    
    protected T computeStepGrowShrinkFactor(final T error) {
        return MathUtils.min(error.getField().getZero().add(this.maxGrowth), MathUtils.max(error.getField().getZero().add(this.minReduction), (T)((RealFieldElement<RealFieldElement<T>>)error).pow(this.exp).multiply(this.safety)));
    }
    
    private class FieldNordsieckInitializer implements FieldStepHandler<T>
    {
        private final FieldEquationsMapper<T> mapper;
        private int count;
        private FieldODEStateAndDerivative<T> savedStart;
        private final T[] t;
        private final T[][] y;
        private final T[][] yDot;
        
        FieldNordsieckInitializer(final FieldEquationsMapper<T> mapper, final int nbStartPoints) {
            this.mapper = mapper;
            this.count = 0;
            this.t = MathArrays.buildArray(MultistepFieldIntegrator.this.getField(), nbStartPoints);
            this.y = MathArrays.buildArray(MultistepFieldIntegrator.this.getField(), nbStartPoints, -1);
            this.yDot = MathArrays.buildArray(MultistepFieldIntegrator.this.getField(), nbStartPoints, -1);
        }
        
        public void handleStep(final FieldStepInterpolator<T> interpolator, final boolean isLast) throws MaxCountExceededException {
            if (this.count == 0) {
                final FieldODEStateAndDerivative<T> prev = interpolator.getPreviousState();
                this.savedStart = prev;
                this.t[this.count] = prev.getTime();
                this.y[this.count] = this.mapper.mapState(prev);
                this.yDot[this.count] = this.mapper.mapDerivative(prev);
            }
            ++this.count;
            final FieldODEStateAndDerivative<T> curr = interpolator.getCurrentState();
            this.t[this.count] = curr.getTime();
            this.y[this.count] = this.mapper.mapState(curr);
            this.yDot[this.count] = this.mapper.mapDerivative(curr);
            if (this.count == this.t.length - 1) {
                MultistepFieldIntegrator.this.setStepSize(((RealFieldElement<RealFieldElement>)this.t[this.t.length - 1].subtract(this.t[0])).divide(this.t.length - 1));
                MultistepFieldIntegrator.this.scaled = MathArrays.buildArray(MultistepFieldIntegrator.this.getField(), this.yDot[0].length);
                for (int j = 0; j < MultistepFieldIntegrator.this.scaled.length; ++j) {
                    MultistepFieldIntegrator.this.scaled[j] = this.yDot[0][j].multiply(MultistepFieldIntegrator.this.getStepSize());
                }
                MultistepFieldIntegrator.this.nordsieck = MultistepFieldIntegrator.this.initializeHighOrderDerivatives(MultistepFieldIntegrator.this.getStepSize(), this.t, this.y, this.yDot);
                MultistepFieldIntegrator.this.setStepStart(this.savedStart);
                throw new InitializationCompletedMarkerException();
            }
        }
        
        public void init(final FieldODEStateAndDerivative<T> initialState, final T finalTime) {
        }
    }
    
    private static class InitializationCompletedMarkerException extends RuntimeException
    {
        private static final long serialVersionUID = -1914085471038046418L;
        
        InitializationCompletedMarkerException() {
            super((Throwable)null);
        }
    }
}
