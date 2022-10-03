package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.FirstOrderFieldDifferentialEquations;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.RealFieldElement;

public abstract class RungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> implements FieldButcherArrayProvider<T>
{
    private final T[] c;
    private final T[][] a;
    private final T[] b;
    private final T step;
    
    protected RungeKuttaFieldIntegrator(final Field<T> field, final String name, final T step) {
        super(field, name);
        this.c = this.getC();
        this.a = this.getA();
        this.b = this.getB();
        this.step = step.abs();
    }
    
    protected T fraction(final int p, final int q) {
        return ((RealFieldElement<RealFieldElement<T>>)this.getField().getZero()).add(p).divide(q);
    }
    
    protected abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(final boolean p0, final T[][] p1, final FieldODEStateAndDerivative<T> p2, final FieldODEStateAndDerivative<T> p3, final FieldEquationsMapper<T> p4);
    
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
        if (forward) {
            if (this.getStepStart().getTime().add(this.step).subtract(finalTime).getReal() >= 0.0) {
                this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
            }
            else {
                this.setStepSize(this.step);
            }
        }
        else if (this.getStepStart().getTime().subtract(this.step).subtract(finalTime).getReal() <= 0.0) {
            this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
        }
        else {
            this.setStepSize(this.step.negate());
        }
        this.setIsLastStep(false);
        do {
            y2 = equations.getMapper().mapState(this.getStepStart());
            yDotK[0] = equations.getMapper().mapDerivative(this.getStepStart());
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
            for (int i = 0; i < y0.length; ++i) {
                T sum2 = yDotK[0][i].multiply(this.b[0]);
                for (int m = 1; m < stages; ++m) {
                    sum2 = sum2.add(yDotK[m][i].multiply(this.b[m]));
                }
                yTmp[i] = y2[i].add(this.getStepSize().multiply(sum2));
            }
            final T stepEnd = this.getStepStart().getTime().add(this.getStepSize());
            final T[] yDotTmp = this.computeDerivatives(stepEnd, yTmp);
            final FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<T>(stepEnd, yTmp, yDotTmp);
            System.arraycopy(yTmp, 0, y2, 0, y0.length);
            this.setStepStart(this.acceptStep(this.createInterpolator(forward, yDotK, this.getStepStart(), stateTmp, equations.getMapper()), finalTime));
            if (!this.isLastStep()) {
                final T nextT = this.getStepStart().getTime().add(this.getStepSize());
                final boolean nextIsLast = forward ? (nextT.subtract(finalTime).getReal() >= 0.0) : (nextT.subtract(finalTime).getReal() <= 0.0);
                if (!nextIsLast) {
                    continue;
                }
                this.setStepSize(finalTime.subtract(this.getStepStart().getTime()));
            }
        } while (!this.isLastStep());
        final FieldODEStateAndDerivative<T> finalState = this.getStepStart();
        this.setStepStart(null);
        this.setStepSize(null);
        return finalState;
    }
    
    public T[] singleStep(final FirstOrderFieldDifferentialEquations<T> equations, final T t0, final T[] y0, final T t) {
        final T[] y = y0.clone();
        final int stages = this.c.length + 1;
        final T[][] yDotK = MathArrays.buildArray(this.getField(), stages, -1);
        final T[] yTmp = y0.clone();
        final T h = t.subtract(t0);
        yDotK[0] = equations.computeDerivatives(t0, y);
        for (int k = 1; k < stages; ++k) {
            for (int j = 0; j < y0.length; ++j) {
                T sum = yDotK[0][j].multiply(this.a[k - 1][0]);
                for (int l = 1; l < k; ++l) {
                    sum = sum.add(yDotK[l][j].multiply(this.a[k - 1][l]));
                }
                yTmp[j] = y[j].add(h.multiply(sum));
            }
            yDotK[k] = equations.computeDerivatives(t0.add(h.multiply(this.c[k - 1])), yTmp);
        }
        for (int i = 0; i < y0.length; ++i) {
            T sum2 = yDotK[0][i].multiply(this.b[0]);
            for (int m = 1; m < stages; ++m) {
                sum2 = sum2.add(yDotK[m][i].multiply(this.b[m]));
            }
            y[i] = y[i].add(h.multiply(sum2));
        }
        return y;
    }
}
