package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;
import java.util.Arrays;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class AdamsMoultonIntegrator extends AdamsIntegrator
{
    private static final String METHOD_NAME = "Adams-Moulton";
    
    public AdamsMoultonIntegrator(final int nSteps, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }
    
    public AdamsMoultonIntegrator(final int nSteps, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y0 = equations.getCompleteState();
        final double[] y2 = y0.clone();
        final double[] yDot = new double[y2.length];
        final double[] yTmp = new double[y2.length];
        final double[] predictedScaled = new double[y2.length];
        Array2DRowRealMatrix nordsieckTmp = null;
        final NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y2, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        this.initIntegration(equations.getTime(), y0, t);
        this.start(equations.getTime(), y2, t);
        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
        interpolator.storeTime(this.stepStart);
        double hNew = this.stepSize;
        interpolator.rescale(hNew);
        this.isLastStep = false;
        do {
            double error = 10.0;
            while (error >= 1.0) {
                this.stepSize = hNew;
                final double stepEnd = this.stepStart + this.stepSize;
                interpolator.setInterpolatedTime(stepEnd);
                final ExpandableStatefulODE expandable = this.getExpandable();
                final EquationsMapper primary = expandable.getPrimaryMapper();
                primary.insertEquationData(interpolator.getInterpolatedState(), yTmp);
                int index = 0;
                for (final EquationsMapper secondary : expandable.getSecondaryMappers()) {
                    secondary.insertEquationData(interpolator.getInterpolatedSecondaryState(index), yTmp);
                    ++index;
                }
                this.computeDerivatives(stepEnd, yTmp, yDot);
                for (int j = 0; j < y0.length; ++j) {
                    predictedScaled[j] = this.stepSize * yDot[j];
                }
                nordsieckTmp = this.updateHighOrderDerivativesPhase1(this.nordsieck);
                this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, nordsieckTmp);
                error = nordsieckTmp.walkInOptimizedOrder(new Corrector(y2, predictedScaled, yTmp));
                if (error >= 1.0) {
                    final double factor = this.computeStepGrowShrinkFactor(error);
                    hNew = this.filterStep(this.stepSize * factor, forward, false);
                    interpolator.rescale(hNew);
                }
            }
            final double stepEnd = this.stepStart + this.stepSize;
            this.computeDerivatives(stepEnd, yTmp, yDot);
            final double[] correctedScaled = new double[y0.length];
            for (int i = 0; i < y0.length; ++i) {
                correctedScaled[i] = this.stepSize * yDot[i];
            }
            this.updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, nordsieckTmp);
            System.arraycopy(yTmp, 0, y2, 0, y2.length);
            interpolator.reinitialize(stepEnd, this.stepSize, correctedScaled, nordsieckTmp);
            interpolator.storeTime(this.stepStart);
            interpolator.shift();
            interpolator.storeTime(stepEnd);
            this.stepStart = this.acceptStep(interpolator, y2, yDot, t);
            this.scaled = correctedScaled;
            this.nordsieck = nordsieckTmp;
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.resetOccurred) {
                    this.start(this.stepStart, y2, t);
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
        equations.setCompleteState(y2);
        this.resetInternalState();
    }
    
    private class Corrector implements RealMatrixPreservingVisitor
    {
        private final double[] previous;
        private final double[] scaled;
        private final double[] before;
        private final double[] after;
        
        Corrector(final double[] previous, final double[] scaled, final double[] state) {
            this.previous = previous;
            this.scaled = scaled;
            this.after = state;
            this.before = state.clone();
        }
        
        public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
            Arrays.fill(this.after, 0.0);
        }
        
        public void visit(final int row, final int column, final double value) {
            if ((row & 0x1) == 0x0) {
                final double[] after = this.after;
                after[column] -= value;
            }
            else {
                final double[] after2 = this.after;
                after2[column] += value;
            }
        }
        
        public double end() {
            double error = 0.0;
            for (int i = 0; i < this.after.length; ++i) {
                final double[] after = this.after;
                final int n = i;
                after[n] += this.previous[i] + this.scaled[i];
                if (i < AdamsMoultonIntegrator.this.mainSetDimension) {
                    final double yScale = FastMath.max(FastMath.abs(this.previous[i]), FastMath.abs(this.after[i]));
                    final double tol = (AdamsMoultonIntegrator.this.vecAbsoluteTolerance == null) ? (AdamsMoultonIntegrator.this.scalAbsoluteTolerance + AdamsMoultonIntegrator.this.scalRelativeTolerance * yScale) : (AdamsMoultonIntegrator.this.vecAbsoluteTolerance[i] + AdamsMoultonIntegrator.this.vecRelativeTolerance[i] * yScale);
                    final double ratio = (this.after[i] - this.before[i]) / tol;
                    error += ratio * ratio;
                }
            }
            return FastMath.sqrt(error / AdamsMoultonIntegrator.this.mainSetDimension);
        }
    }
}
