package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.ode.MultistepFieldIntegrator;
import org.apache.commons.math3.RealFieldElement;

public abstract class AdamsFieldIntegrator<T extends RealFieldElement<T>> extends MultistepFieldIntegrator<T>
{
    private final AdamsNordsieckFieldTransformer<T> transformer;
    
    public AdamsFieldIntegrator(final Field<T> field, final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(field, name, nSteps, order, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.transformer = AdamsNordsieckFieldTransformer.getInstance(field, nSteps);
    }
    
    public AdamsFieldIntegrator(final Field<T> field, final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(field, name, nSteps, order, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.transformer = AdamsNordsieckFieldTransformer.getInstance(field, nSteps);
    }
    
    public abstract FieldODEStateAndDerivative<T> integrate(final FieldExpandableODE<T> p0, final FieldODEState<T> p1, final T p2) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;
    
    @Override
    protected Array2DRowFieldMatrix<T> initializeHighOrderDerivatives(final T h, final T[] t, final T[][] y, final T[][] yDot) {
        return this.transformer.initializeHighOrderDerivatives(h, t, y, yDot);
    }
    
    public Array2DRowFieldMatrix<T> updateHighOrderDerivativesPhase1(final Array2DRowFieldMatrix<T> highOrder) {
        return this.transformer.updateHighOrderDerivativesPhase1(highOrder);
    }
    
    public void updateHighOrderDerivativesPhase2(final T[] start, final T[] end, final Array2DRowFieldMatrix<T> highOrder) {
        this.transformer.updateHighOrderDerivativesPhase2(start, end, highOrder);
    }
}
