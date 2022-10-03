package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.MultistepIntegrator;

public abstract class AdamsIntegrator extends MultistepIntegrator
{
    private final AdamsNordsieckTransformer transformer;
    
    public AdamsIntegrator(final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(name, nSteps, order, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.transformer = AdamsNordsieckTransformer.getInstance(nSteps);
    }
    
    public AdamsIntegrator(final String name, final int nSteps, final int order, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(name, nSteps, order, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.transformer = AdamsNordsieckTransformer.getInstance(nSteps);
    }
    
    @Override
    public abstract void integrate(final ExpandableStatefulODE p0, final double p1) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;
    
    @Override
    protected Array2DRowRealMatrix initializeHighOrderDerivatives(final double h, final double[] t, final double[][] y, final double[][] yDot) {
        return this.transformer.initializeHighOrderDerivatives(h, t, y, yDot);
    }
    
    public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final Array2DRowRealMatrix highOrder) {
        return this.transformer.updateHighOrderDerivativesPhase1(highOrder);
    }
    
    public void updateHighOrderDerivativesPhase2(final double[] start, final double[] end, final Array2DRowRealMatrix highOrder) {
        this.transformer.updateHighOrderDerivativesPhase2(start, end, highOrder);
    }
}
