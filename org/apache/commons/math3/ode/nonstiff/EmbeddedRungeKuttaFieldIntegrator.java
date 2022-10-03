package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public abstract class EmbeddedRungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends AdaptiveStepsizeFieldIntegrator<T> implements FieldButcherArrayProvider<T>
{
    private final int fsal;
    private final T[] c;
    private final T[][] a;
    private final T[] b;
    private final T exp;
    private T safety;
    private T minReduction;
    private T maxGrowth;
    
    protected EmbeddedRungeKuttaFieldIntegrator(final Field<T> field, final String name, final int fsal, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super(field, name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.fsal = fsal;
        this.c = this.getC();
        this.a = this.getA();
        this.b = this.getB();
        this.exp = field.getOne().divide(-this.getOrder());
        this.setSafety(field.getZero().add(0.9));
        this.setMinReduction(field.getZero().add(0.2));
        this.setMaxGrowth(field.getZero().add(10.0));
    }
    
    protected EmbeddedRungeKuttaFieldIntegrator(final Field<T> field, final String name, final int fsal, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(field, name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.fsal = fsal;
        this.c = this.getC();
        this.a = this.getA();
        this.b = this.getB();
        this.exp = field.getOne().divide(-this.getOrder());
        this.setSafety(field.getZero().add(0.9));
        this.setMinReduction(field.getZero().add(0.2));
        this.setMaxGrowth(field.getZero().add(10.0));
    }
    
    protected T fraction(final int p, final int q) {
        return ((FieldElement<RealFieldElement<T>>)this.getField().getOne()).multiply(p).divide(q);
    }
    
    protected T fraction(final double p, final double q) {
        return ((RealFieldElement<RealFieldElement<T>>)this.getField().getOne()).multiply(p).divide(q);
    }
    
    protected abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(final boolean p0, final T[][] p1, final FieldODEStateAndDerivative<T> p2, final FieldODEStateAndDerivative<T> p3, final FieldEquationsMapper<T> p4);
    
    public abstract int getOrder();
    
    public T getSafety() {
        return this.safety;
    }
    
    public void setSafety(final T safety) {
        this.safety = safety;
    }
    
    public FieldODEStateAndDerivative<T> integrate(final FieldExpandableODE<T> equations, final FieldODEState<T> initialState, final T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(initialState, finalTime);
        final T t0 = initialState.getTime();
        final T[] y0 = equations.getMapper().mapState(initialState);
        this.setStepStart(this.initIntegration(equations, t0, y0, finalTime));
        final boolean forward = finalTime.subtract(initialState.getTime()).getReal() > 0.0;
        final int stages = this.c.length + 1;
        T[] y2 = y0;
        final T[][] yDotK = MathArrays.buildArray(this.getField(), stages, -1);
        final T[] yTmp = MathArrays.buildArray(this.getField(), y0.length);
        T hNew = this.getField().getZero();
        boolean firstTime = true;
        this.setIsLastStep(false);
        do {
            T error = this.getField().getZero().add(10.0);
            while (((RealFieldElement<RealFieldElement>)error).subtract(1.0).getReal() >= 0.0) {
                y2 = equations.getMapper().mapState(this.getStepStart());
                yDotK[0] = equations.getMapper().mapDerivative(this.getStepStart());
                if (firstTime) {
                    final T[] scale = MathArrays.buildArray(this.getField(), this.mainSetDimension);
                    if (this.vecAbsoluteTolerance == null) {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)y2[i]).abs().multiply(this.scalRelativeTolerance).add(this.scalAbsoluteTolerance);
                        }
                    }
                    else {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)y2[i]).abs().multiply(this.vecRelativeTolerance[i]).add(this.vecAbsoluteTolerance[i]);
                        }
                    }
                    hNew = this.initializeStep(forward, this.getOrder(), scale, this.getStepStart(), equations.getMapper());
                    firstTime = false;
                }
                this.setStepSize(hNew);
                if (forward) {
                    if (((RealFieldElement)this.getStepStart().getTime().add(this.getStepSize()).subtract(finalTime)).getReal() >= 0.0) {
                        this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
                    }
                }
                else if (((RealFieldElement)this.getStepStart().getTime().add(this.getStepSize()).subtract(finalTime)).getReal() <= 0.0) {
                    this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
                }
                for (int k = 1; k < stages; ++k) {
                    for (int j = 0; j < y0.length; ++j) {
                        T sum = yDotK[0][j].multiply(this.a[k - 1][0]);
                        for (int l = 1; l < k; ++l) {
                            sum = sum.add(yDotK[l][j].multiply(this.a[k - 1][l]));
                        }
                        yTmp[j] = y2[j].add(this.getStepSize().multiply(sum));
                    }
                    yDotK[k] = this.computeDerivatives(this.getStepStart().getTime().add(this.getStepSize().multiply(this.c[k - 1])), yTmp);
                }
                for (int m = 0; m < y0.length; ++m) {
                    T sum2 = yDotK[0][m].multiply(this.b[0]);
                    for (int l2 = 1; l2 < stages; ++l2) {
                        sum2 = sum2.add(yDotK[l2][m].multiply(this.b[l2]));
                    }
                    yTmp[m] = y2[m].add(this.getStepSize().multiply(sum2));
                }
                error = this.estimateError(yDotK, y2, yTmp, this.getStepSize());
                if (((RealFieldElement<RealFieldElement>)error).subtract(1.0).getReal() >= 0.0) {
                    final T factor = MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, this.safety.multiply(error.pow(this.exp))));
                    hNew = this.filterStep(this.getStepSize().multiply(factor), forward, false);
                }
            }
            final T stepEnd = this.getStepStart().getTime().add(this.getStepSize());
            final T[] yDotTmp = (this.fsal >= 0) ? yDotK[this.fsal] : this.computeDerivatives(stepEnd, yTmp);
            final FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<T>(stepEnd, yTmp, yDotTmp);
            System.arraycopy(yTmp, 0, y2, 0, y0.length);
            this.setStepStart(this.acceptStep(this.createInterpolator(forward, yDotK, this.getStepStart(), stateTmp, equations.getMapper()), finalTime));
            if (!this.isLastStep()) {
                final T factor2 = MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, this.safety.multiply(error.pow(this.exp))));
                final T scaledH = this.getStepSize().multiply(factor2);
                final T nextT = this.getStepStart().getTime().add(scaledH);
                final boolean nextIsLast = forward ? (nextT.subtract(finalTime).getReal() >= 0.0) : (nextT.subtract(finalTime).getReal() <= 0.0);
                hNew = this.filterStep(scaledH, forward, nextIsLast);
                final T filteredNextT = this.getStepStart().getTime().add(hNew);
                final boolean filteredNextIsLast = forward ? (filteredNextT.subtract(finalTime).getReal() >= 0.0) : (filteredNextT.subtract(finalTime).getReal() <= 0.0);
                if (!filteredNextIsLast) {
                    continue;
                }
                hNew = finalTime.subtract(this.getStepStart().getTime());
            }
        } while (!this.isLastStep());
        final FieldODEStateAndDerivative<T> finalState = this.getStepStart();
        this.resetInternalState();
        return finalState;
    }
    
    public T getMinReduction() {
        return this.minReduction;
    }
    
    public void setMinReduction(final T minReduction) {
        this.minReduction = minReduction;
    }
    
    public T getMaxGrowth() {
        return this.maxGrowth;
    }
    
    public void setMaxGrowth(final T maxGrowth) {
        this.maxGrowth = maxGrowth;
    }
    
    protected abstract T estimateError(final T[][] p0, final T[] p1, final T[] p2, final T p3);
}
