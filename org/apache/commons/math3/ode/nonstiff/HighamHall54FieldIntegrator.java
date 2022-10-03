package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class HighamHall54FieldIntegrator<T extends RealFieldElement<T>> extends EmbeddedRungeKuttaFieldIntegrator<T>
{
    private static final String METHOD_NAME = "Higham-Hall 5(4)";
    private final T[] e;
    
    public HighamHall54FieldIntegrator(final Field<T> field, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super(field, "Higham-Hall 5(4)", -1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        (this.e = MathArrays.buildArray(field, 7))[0] = this.fraction(-1, 20);
        this.e[1] = field.getZero();
        this.e[2] = this.fraction(81, 160);
        this.e[3] = this.fraction(-6, 5);
        this.e[4] = this.fraction(25, 32);
        this.e[5] = this.fraction(1, 16);
        this.e[6] = this.fraction(-1, 10);
    }
    
    public HighamHall54FieldIntegrator(final Field<T> field, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(field, "Higham-Hall 5(4)", -1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        (this.e = MathArrays.buildArray(field, 7))[0] = this.fraction(-1, 20);
        this.e[1] = field.getZero();
        this.e[2] = this.fraction(81, 160);
        this.e[3] = this.fraction(-6, 5);
        this.e[4] = this.fraction(25, 32);
        this.e[5] = this.fraction(1, 16);
        this.e[6] = this.fraction(-1, 10);
    }
    
    public T[] getC() {
        final T[] c = MathArrays.buildArray(this.getField(), 6);
        c[0] = this.fraction(2, 9);
        c[1] = this.fraction(1, 3);
        c[2] = this.fraction(1, 2);
        c[3] = this.fraction(3, 5);
        c[4] = this.getField().getOne();
        c[5] = this.getField().getOne();
        return c;
    }
    
    public T[][] getA() {
        final T[][] a = MathArrays.buildArray(this.getField(), 6, -1);
        for (int i = 0; i < a.length; ++i) {
            a[i] = MathArrays.buildArray(this.getField(), i + 1);
        }
        a[0][0] = this.fraction(2, 9);
        a[1][0] = this.fraction(1, 12);
        a[1][1] = this.fraction(1, 4);
        a[2][0] = this.fraction(1, 8);
        a[2][1] = this.getField().getZero();
        a[2][2] = this.fraction(3, 8);
        a[3][0] = this.fraction(91, 500);
        a[3][1] = this.fraction(-27, 100);
        a[3][2] = this.fraction(78, 125);
        a[3][3] = this.fraction(8, 125);
        a[4][0] = this.fraction(-11, 20);
        a[4][1] = this.fraction(27, 20);
        a[4][2] = this.fraction(12, 5);
        a[4][3] = this.fraction(-36, 5);
        a[4][4] = this.fraction(5, 1);
        a[5][0] = this.fraction(1, 12);
        a[5][1] = this.getField().getZero();
        a[5][2] = this.fraction(27, 32);
        a[5][3] = this.fraction(-4, 3);
        a[5][4] = this.fraction(125, 96);
        a[5][5] = this.fraction(5, 48);
        return a;
    }
    
    public T[] getB() {
        final T[] b = MathArrays.buildArray(this.getField(), 7);
        b[0] = this.fraction(1, 12);
        b[1] = this.getField().getZero();
        b[2] = this.fraction(27, 32);
        b[3] = this.fraction(-4, 3);
        b[4] = this.fraction(125, 96);
        b[5] = this.fraction(5, 48);
        b[6] = this.getField().getZero();
        return b;
    }
    
    @Override
    protected HighamHall54FieldStepInterpolator<T> createInterpolator(final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> mapper) {
        return new HighamHall54FieldStepInterpolator<T>(this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
    
    @Override
    public int getOrder() {
        return 5;
    }
    
    @Override
    protected T estimateError(final T[][] yDotK, final T[] y0, final T[] y1, final T h) {
        T error = this.getField().getZero();
        for (int j = 0; j < this.mainSetDimension; ++j) {
            T errSum = yDotK[0][j].multiply(this.e[0]);
            for (int l = 1; l < this.e.length; ++l) {
                errSum = errSum.add(yDotK[l][j].multiply(this.e[l]));
            }
            final T yScale = MathUtils.max(y0[j].abs(), y1[j].abs());
            final T tol = (T)((this.vecAbsoluteTolerance == null) ? ((RealFieldElement<RealFieldElement<RealFieldElement>>)yScale).multiply(this.scalRelativeTolerance).add(this.scalAbsoluteTolerance) : ((RealFieldElement<T>)((RealFieldElement<RealFieldElement<RealFieldElement>>)yScale).multiply(this.vecRelativeTolerance[j]).add(this.vecAbsoluteTolerance[j])));
            final T ratio = h.multiply(errSum).divide(tol);
            error = error.add(ratio.multiply(ratio));
        }
        return ((RealFieldElement<RealFieldElement<T>>)error).divide(this.mainSetDimension).sqrt();
    }
}
