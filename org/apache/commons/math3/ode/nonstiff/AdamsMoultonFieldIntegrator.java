package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.util.MathUtils;
import java.util.Arrays;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.linear.FieldMatrixPreservingVisitor;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class AdamsMoultonFieldIntegrator<T extends RealFieldElement<T>> extends AdamsFieldIntegrator<T>
{
    private static final String METHOD_NAME = "Adams-Moulton";
    
    public AdamsMoultonFieldIntegrator(final Field<T> field, final int nSteps, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(field, "Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }
    
    public AdamsMoultonFieldIntegrator(final Field<T> field, final int nSteps, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(field, "Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }
    
    @Override
    public FieldODEStateAndDerivative<T> integrate(final FieldExpandableODE<T> equations, final FieldODEState<T> initialState, final T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(initialState, finalTime);
        final T t0 = initialState.getTime();
        final T[] y = equations.getMapper().mapState(initialState);
        this.setStepStart(this.initIntegration(equations, t0, y, finalTime));
        final boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
        this.start(equations, this.getStepStart(), finalTime);
        final FieldODEStateAndDerivative<T> stepStart = this.getStepStart();
        FieldODEStateAndDerivative<T> stepEnd = AdamsFieldStepInterpolator.taylor(stepStart, (T)stepStart.getTime().add((S)this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck);
        this.setIsLastStep(false);
        do {
            T[] predictedY = null;
            final T[] predictedScaled = MathArrays.buildArray(this.getField(), y.length);
            Array2DRowFieldMatrix<T> predictedNordsieck = null;
            T error = this.getField().getZero().add(10.0);
            while (((RealFieldElement<RealFieldElement>)error).subtract(1.0).getReal() >= 0.0) {
                predictedY = stepEnd.getState();
                final T[] yDot = this.computeDerivatives(stepEnd.getTime(), predictedY);
                for (int j = 0; j < predictedScaled.length; ++j) {
                    predictedScaled[j] = this.getStepSize().multiply(yDot[j]);
                }
                predictedNordsieck = this.updateHighOrderDerivativesPhase1(this.nordsieck);
                this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, predictedNordsieck);
                error = predictedNordsieck.walkInOptimizedOrder(new Corrector(y, predictedScaled, predictedY));
                if (((RealFieldElement<RealFieldElement>)error).subtract(1.0).getReal() >= 0.0) {
                    final T factor = this.computeStepGrowShrinkFactor(error);
                    this.rescale(this.filterStep(this.getStepSize().multiply(factor), forward, false));
                    stepEnd = AdamsFieldStepInterpolator.taylor(this.getStepStart(), (T)this.getStepStart().getTime().add((S)this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck);
                }
            }
            final T[] correctedYDot = this.computeDerivatives(stepEnd.getTime(), predictedY);
            final T[] correctedScaled = MathArrays.buildArray(this.getField(), y.length);
            for (int i = 0; i < correctedScaled.length; ++i) {
                correctedScaled[i] = this.getStepSize().multiply(correctedYDot[i]);
            }
            this.updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, predictedNordsieck);
            stepEnd = new FieldODEStateAndDerivative<T>(stepEnd.getTime(), predictedY, correctedYDot);
            this.setStepStart(this.acceptStep(new AdamsFieldStepInterpolator<T>(this.getStepSize(), stepEnd, correctedScaled, predictedNordsieck, forward, this.getStepStart(), stepEnd, equations.getMapper()), finalTime));
            this.scaled = correctedScaled;
            this.nordsieck = predictedNordsieck;
            if (!this.isLastStep()) {
                System.arraycopy(predictedY, 0, y, 0, y.length);
                if (this.resetOccurred()) {
                    this.start(equations, this.getStepStart(), finalTime);
                }
                final T factor2 = this.computeStepGrowShrinkFactor(error);
                final T scaledH = this.getStepSize().multiply(factor2);
                final T nextT = this.getStepStart().getTime().add(scaledH);
                final boolean nextIsLast = forward ? (nextT.subtract(finalTime).getReal() >= 0.0) : (nextT.subtract(finalTime).getReal() <= 0.0);
                T hNew = this.filterStep(scaledH, forward, nextIsLast);
                final T filteredNextT = this.getStepStart().getTime().add(hNew);
                final boolean filteredNextIsLast = forward ? (filteredNextT.subtract(finalTime).getReal() >= 0.0) : (filteredNextT.subtract(finalTime).getReal() <= 0.0);
                if (filteredNextIsLast) {
                    hNew = finalTime.subtract(this.getStepStart().getTime());
                }
                this.rescale(hNew);
                stepEnd = AdamsFieldStepInterpolator.taylor(this.getStepStart(), (T)this.getStepStart().getTime().add((S)this.getStepSize()), this.getStepSize(), this.scaled, this.nordsieck);
            }
        } while (!this.isLastStep());
        final FieldODEStateAndDerivative<T> finalState = this.getStepStart();
        this.setStepStart(null);
        this.setStepSize(null);
        return finalState;
    }
    
    private class Corrector implements FieldMatrixPreservingVisitor<T>
    {
        private final T[] previous;
        private final T[] scaled;
        private final T[] before;
        private final T[] after;
        
        Corrector(final T[] previous, final T[] scaled, final T[] state) {
            this.previous = previous;
            this.scaled = scaled;
            this.after = state;
            this.before = state.clone();
        }
        
        public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
            Arrays.fill(this.after, AdamsMoultonFieldIntegrator.this.getField().getZero());
        }
        
        public void visit(final int row, final int column, final T value) {
            if ((row & 0x1) == 0x0) {
                this.after[column] = this.after[column].subtract(value);
            }
            else {
                this.after[column] = this.after[column].add(value);
            }
        }
        
        public T end() {
            T error = AdamsMoultonFieldIntegrator.this.getField().getZero();
            for (int i = 0; i < this.after.length; ++i) {
                this.after[i] = this.after[i].add(this.previous[i].add(this.scaled[i]));
                if (i < AdamsMoultonFieldIntegrator.this.mainSetDimension) {
                    final T yScale = MathUtils.max(this.previous[i].abs(), this.after[i].abs());
                    final T tol = (T)((AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance == null) ? ((RealFieldElement<RealFieldElement<RealFieldElement>>)yScale).multiply(AdamsMoultonFieldIntegrator.this.scalRelativeTolerance).add(AdamsMoultonFieldIntegrator.this.scalAbsoluteTolerance) : ((RealFieldElement<T>)((RealFieldElement<RealFieldElement<RealFieldElement>>)yScale).multiply(AdamsMoultonFieldIntegrator.this.vecRelativeTolerance[i]).add(AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance[i])));
                    final T ratio = this.after[i].subtract(this.before[i]).divide(tol);
                    error = error.add(ratio.multiply(ratio));
                }
            }
            return ((RealFieldElement<RealFieldElement<T>>)error).divide(AdamsMoultonFieldIntegrator.this.mainSetDimension).sqrt();
        }
    }
}
