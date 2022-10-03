package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class AdamsBashforthIntegrator extends AdamsIntegrator
{
    private static final String METHOD_NAME = "Adams-Bashforth";
    
    public AdamsBashforthIntegrator(final int nSteps, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }
    
    public AdamsBashforthIntegrator(final int nSteps, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }
    
    private double errorEstimation(final double[] previousState, final double[] predictedState, final double[] predictedScaled, final RealMatrix predictedNordsieck) {
        double error = 0.0;
        for (int i = 0; i < this.mainSetDimension; ++i) {
            final double yScale = FastMath.abs(predictedState[i]);
            final double tol = (this.vecAbsoluteTolerance == null) ? (this.scalAbsoluteTolerance + this.scalRelativeTolerance * yScale) : (this.vecAbsoluteTolerance[i] + this.vecRelativeTolerance[i] * yScale);
            double variation = 0.0;
            int sign = (predictedNordsieck.getRowDimension() % 2 == 0) ? -1 : 1;
            for (int k = predictedNordsieck.getRowDimension() - 1; k >= 0; --k) {
                variation += sign * predictedNordsieck.getEntry(k, i);
                sign = -sign;
            }
            variation -= predictedScaled[i];
            final double ratio = (predictedState[i] - previousState[i] + variation) / tol;
            error += ratio * ratio;
        }
        return FastMath.sqrt(error / this.mainSetDimension);
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y = equations.getCompleteState();
        final double[] yDot = new double[y.length];
        final NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        this.initIntegration(equations.getTime(), y, t);
        this.start(equations.getTime(), y, t);
        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
        interpolator.storeTime(this.stepStart);
        double hNew = this.stepSize;
        interpolator.rescale(hNew);
        this.isLastStep = false;
        do {
            interpolator.shift();
            final double[] predictedY = new double[y.length];
            final double[] predictedScaled = new double[y.length];
            Array2DRowRealMatrix predictedNordsieck = null;
            double error = 10.0;
            while (error >= 1.0) {
                final double stepEnd = this.stepStart + hNew;
                interpolator.storeTime(stepEnd);
                final ExpandableStatefulODE expandable = this.getExpandable();
                final EquationsMapper primary = expandable.getPrimaryMapper();
                primary.insertEquationData(interpolator.getInterpolatedState(), predictedY);
                int index = 0;
                for (final EquationsMapper secondary : expandable.getSecondaryMappers()) {
                    secondary.insertEquationData(interpolator.getInterpolatedSecondaryState(index), predictedY);
                    ++index;
                }
                this.computeDerivatives(stepEnd, predictedY, yDot);
                for (int j = 0; j < predictedScaled.length; ++j) {
                    predictedScaled[j] = hNew * yDot[j];
                }
                predictedNordsieck = this.updateHighOrderDerivativesPhase1(this.nordsieck);
                this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, predictedNordsieck);
                error = this.errorEstimation(y, predictedY, predictedScaled, predictedNordsieck);
                if (error >= 1.0) {
                    final double factor = this.computeStepGrowShrinkFactor(error);
                    hNew = this.filterStep(hNew * factor, forward, false);
                    interpolator.rescale(hNew);
                }
            }
            this.stepSize = hNew;
            final double stepEnd = this.stepStart + this.stepSize;
            interpolator.reinitialize(stepEnd, this.stepSize, predictedScaled, predictedNordsieck);
            interpolator.storeTime(stepEnd);
            System.arraycopy(predictedY, 0, y, 0, y.length);
            this.stepStart = this.acceptStep(interpolator, y, yDot, t);
            this.scaled = predictedScaled;
            this.nordsieck = predictedNordsieck;
            interpolator.reinitialize(stepEnd, this.stepSize, this.scaled, this.nordsieck);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.resetOccurred) {
                    this.start(this.stepStart, y, t);
                    interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
                }
                final double factor2 = this.computeStepGrowShrinkFactor(error);
                final double scaledH = this.stepSize * factor2;
                final double nextT = this.stepStart + scaledH;
                final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
                hNew = this.filterStep(scaledH, forward, nextIsLast);
                final double filteredNextT = this.stepStart + hNew;
                final boolean filteredNextIsLast = forward ? (filteredNextT >= t) : (filteredNextT <= t);
                if (filteredNextIsLast) {
                    hNew = t - this.stepStart;
                }
                interpolator.rescale(hNew);
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y);
        this.resetInternalState();
    }
}
