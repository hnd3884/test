package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class DormandPrince853FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    private final T[][] d;
    
    DormandPrince853FieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        this.d = MathArrays.buildArray(field, 7, 16);
        this.d[0][0] = this.fraction(field, 104257.0, 1920240.0);
        this.d[0][1] = field.getZero();
        this.d[0][2] = field.getZero();
        this.d[0][3] = field.getZero();
        this.d[0][4] = field.getZero();
        this.d[0][5] = this.fraction(field, 3399327.0, 763840.0);
        this.d[0][6] = this.fraction(field, 6.6578432E7, 3.5198415E7);
        this.d[0][7] = this.fraction(field, -1.674902723E9, 2.887164E8);
        this.d[0][8] = this.fraction(field, 5.4980371265625E13, 1.76692375811392E14);
        this.d[0][9] = this.fraction(field, -734375.0, 4826304.0);
        this.d[0][10] = this.fraction(field, 1.71414593E8, 8.512614E8);
        this.d[0][11] = this.fraction(field, 137909.0, 3084480.0);
        this.d[0][12] = field.getZero();
        this.d[0][13] = field.getZero();
        this.d[0][14] = field.getZero();
        this.d[0][15] = field.getZero();
        this.d[1][0] = ((FieldElement<RealFieldElement<T>>)this.d[0][0]).negate().add(1.0);
        this.d[1][1] = this.d[0][1].negate();
        this.d[1][2] = this.d[0][2].negate();
        this.d[1][3] = this.d[0][3].negate();
        this.d[1][4] = this.d[0][4].negate();
        this.d[1][5] = this.d[0][5].negate();
        this.d[1][6] = this.d[0][6].negate();
        this.d[1][7] = this.d[0][7].negate();
        this.d[1][8] = this.d[0][8].negate();
        this.d[1][9] = this.d[0][9].negate();
        this.d[1][10] = this.d[0][10].negate();
        this.d[1][11] = this.d[0][11].negate();
        this.d[1][12] = this.d[0][12].negate();
        this.d[1][13] = this.d[0][13].negate();
        this.d[1][14] = this.d[0][14].negate();
        this.d[1][15] = this.d[0][15].negate();
        this.d[2][0] = ((FieldElement<RealFieldElement<T>>)this.d[0][0]).multiply(2).subtract(1.0);
        this.d[2][1] = this.d[0][1].multiply(2);
        this.d[2][2] = this.d[0][2].multiply(2);
        this.d[2][3] = this.d[0][3].multiply(2);
        this.d[2][4] = this.d[0][4].multiply(2);
        this.d[2][5] = this.d[0][5].multiply(2);
        this.d[2][6] = this.d[0][6].multiply(2);
        this.d[2][7] = this.d[0][7].multiply(2);
        this.d[2][8] = this.d[0][8].multiply(2);
        this.d[2][9] = this.d[0][9].multiply(2);
        this.d[2][10] = this.d[0][10].multiply(2);
        this.d[2][11] = this.d[0][11].multiply(2);
        this.d[2][12] = ((FieldElement<RealFieldElement<T>>)this.d[0][12]).multiply(2).subtract(1.0);
        this.d[2][13] = this.d[0][13].multiply(2);
        this.d[2][14] = this.d[0][14].multiply(2);
        this.d[2][15] = this.d[0][15].multiply(2);
        this.d[3][0] = this.fraction(field, -1.7751989329E10, 2.10607656E9);
        this.d[3][1] = field.getZero();
        this.d[3][2] = field.getZero();
        this.d[3][3] = field.getZero();
        this.d[3][4] = field.getZero();
        this.d[3][5] = this.fraction(field, 4.272954039E9, 7.53986464E9);
        this.d[3][6] = this.fraction(field, -1.18476319744E11, 3.8604839385E10);
        this.d[3][7] = this.fraction(field, 7.55123450731E11, 3.166577316E11);
        this.d[3][8] = this.fraction(field, 3.6923844612348283E18, 1.7441304416342505E18);
        this.d[3][9] = this.fraction(field, -4.612609375E9, 5.293382976E9);
        this.d[3][10] = this.fraction(field, 2.091772278379E12, 9.336445866E11);
        this.d[3][11] = this.fraction(field, 2.136624137E9, 3.38298912E9);
        this.d[3][12] = this.fraction(field, -126493.0, 1421424.0);
        this.d[3][13] = this.fraction(field, 9.835E7, 5419179.0);
        this.d[3][14] = this.fraction(field, -1.8878125E7, 2053168.0);
        this.d[3][15] = this.fraction(field, -1.944542619E9, 4.38351368E8);
        this.d[4][0] = this.fraction(field, 3.2941697297E10, 3.15911484E9);
        this.d[4][1] = field.getZero();
        this.d[4][2] = field.getZero();
        this.d[4][3] = field.getZero();
        this.d[4][4] = field.getZero();
        this.d[4][5] = this.fraction(field, 4.56696183123E11, 1.88496616E9);
        this.d[4][6] = this.fraction(field, 1.9132610714624E13, 1.15814518155E11);
        this.d[4][7] = this.fraction(field, -1.77904688592943E14, 4.749865974E11);
        this.d[4][8] = this.fraction(field, -4.8211399418367652E18, 2.18016305204281312E17);
        this.d[4][9] = this.fraction(field, 3.0702015625E10, 3.970037232E9);
        this.d[4][10] = this.fraction(field, -8.5916079474274E13, 2.8009337598E12);
        this.d[4][11] = this.fraction(field, -5.919468007E9, 6.3431046E8);
        this.d[4][12] = this.fraction(field, 2479159.0, 157936.0);
        this.d[4][13] = this.fraction(field, -1.875E7, 602131.0);
        this.d[4][14] = this.fraction(field, -1.9203125E7, 2053168.0);
        this.d[4][15] = this.fraction(field, 1.5700361463E10, 4.38351368E8);
        this.d[5][0] = this.fraction(field, 1.2627015655E10, 6.31822968E8);
        this.d[5][1] = field.getZero();
        this.d[5][2] = field.getZero();
        this.d[5][3] = field.getZero();
        this.d[5][4] = field.getZero();
        this.d[5][5] = this.fraction(field, -7.2955222965E10, 1.88496616E8);
        this.d[5][6] = this.fraction(field, -1.314574495232E13, 6.9488710893E10);
        this.d[5][7] = this.fraction(field, 3.0084216194513E13, 5.6998391688E10);
        this.d[5][8] = this.fraction(field, -2.9685876100664064E17, 2.5648977082856624E16);
        this.d[5][9] = this.fraction(field, 5.69140625E8, 8.2709109E7);
        this.d[5][10] = this.fraction(field, -1.8684190637E10, 1.8672891732E10);
        this.d[5][11] = this.fraction(field, 6.9644045E7, 8.9549712E7);
        this.d[5][12] = this.fraction(field, -1.1847025E7, 4264272.0);
        this.d[5][13] = this.fraction(field, -9.7865E8, 1.6257537E7);
        this.d[5][14] = this.fraction(field, 5.19371875E8, 6159504.0);
        this.d[5][15] = this.fraction(field, 5.256837225E9, 4.38351368E8);
        this.d[6][0] = this.fraction(field, -4.50944925E8, 1.7550638E7);
        this.d[6][1] = field.getZero();
        this.d[6][2] = field.getZero();
        this.d[6][3] = field.getZero();
        this.d[6][4] = field.getZero();
        this.d[6][5] = this.fraction(field, -1.4532122925E10, 9.4248308E7);
        this.d[6][6] = this.fraction(field, -5.958769664E11, 2.573655959E9);
        this.d[6][7] = this.fraction(field, 1.88748653015E11, 5.27762886E8);
        this.d[6][8] = this.fraction(field, 2.5454854581152343E18, 2.7252038150535164E16);
        this.d[6][9] = this.fraction(field, -1.376953125E9, 3.6759604E7);
        this.d[6][10] = this.fraction(field, 5.3995596795E10, 5.18691437E8);
        this.d[6][11] = this.fraction(field, 2.10311225E8, 7047894.0);
        this.d[6][12] = this.fraction(field, -1718875.0, 39484.0);
        this.d[6][13] = this.fraction(field, 5.8E7, 602131.0);
        this.d[6][14] = this.fraction(field, -1546875.0, 39484.0);
        this.d[6][15] = this.fraction(field, -1.262172375E9, 8429834.0);
    }
    
    @Override
    protected DormandPrince853FieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new DormandPrince853FieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    private T fraction(final Field<T> field, final double p, final double q) {
        return ((RealFieldElement<RealFieldElement<T>>)field.getZero()).add(p).divide(q);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) throws MaxCountExceededException {
        final T one = time.getField().getOne();
        final T eta = one.subtract(theta);
        final T twoTheta = theta.multiply(2);
        final T theta2 = theta.multiply(theta);
        final T dot1 = one.subtract(twoTheta);
        final T dot2 = theta.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(-3).add(2.0));
        final T dot3 = twoTheta.multiply(theta.multiply(twoTheta.subtract(3.0)).add(1.0));
        final T dot4 = theta2.multiply(theta.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(5).subtract(8.0)).add(3.0));
        final T dot5 = theta2.multiply(theta.multiply(theta.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(-6).add(15.0)).subtract(12.0)).add(3.0));
        final T dot6 = theta2.multiply(theta.multiply(theta.multiply(theta.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(-7).add(18.0)).subtract(15.0)).add(4.0)));
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T f0 = thetaH;
            final T f2 = f0.multiply(eta);
            final T f3 = f2.multiply(theta);
            final T f4 = f3.multiply(eta);
            final T f5 = f4.multiply(theta);
            final T f6 = f5.multiply(eta);
            final T f7 = f6.multiply(theta);
            final T[] p = MathArrays.buildArray(time.getField(), 16);
            final T[] q = MathArrays.buildArray(time.getField(), 16);
            for (int i = 0; i < p.length; ++i) {
                p[i] = f0.multiply(this.d[0][i]).add(f2.multiply(this.d[1][i])).add(f3.multiply(this.d[2][i])).add(f4.multiply(this.d[3][i])).add(f5.multiply(this.d[4][i])).add(f6.multiply(this.d[5][i])).add(f7.multiply(this.d[6][i]));
                q[i] = this.d[0][i].add(dot1.multiply(this.d[1][i])).add(dot2.multiply(this.d[2][i])).add(dot3.multiply(this.d[3][i])).add(dot4.multiply(this.d[4][i])).add(dot5.multiply(this.d[5][i])).add(dot6.multiply(this.d[6][i]));
            }
            interpolatedState = this.previousStateLinearCombination(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
            interpolatedDerivatives = this.derivativeLinearCombination(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], q[9], q[10], q[11], q[12], q[13], q[14], q[15]);
        }
        else {
            final T f0 = oneMinusThetaH.negate();
            final T f2 = f0.multiply(theta).negate();
            final T f3 = f2.multiply(theta);
            final T f4 = f3.multiply(eta);
            final T f5 = f4.multiply(theta);
            final T f6 = f5.multiply(eta);
            final T f7 = f6.multiply(theta);
            final T[] p = MathArrays.buildArray(time.getField(), 16);
            final T[] q = MathArrays.buildArray(time.getField(), 16);
            for (int i = 0; i < p.length; ++i) {
                p[i] = f0.multiply(this.d[0][i]).add(f2.multiply(this.d[1][i])).add(f3.multiply(this.d[2][i])).add(f4.multiply(this.d[3][i])).add(f5.multiply(this.d[4][i])).add(f6.multiply(this.d[5][i])).add(f7.multiply(this.d[6][i]));
                q[i] = this.d[0][i].add(dot1.multiply(this.d[1][i])).add(dot2.multiply(this.d[2][i])).add(dot3.multiply(this.d[3][i])).add(dot4.multiply(this.d[4][i])).add(dot5.multiply(this.d[5][i])).add(dot6.multiply(this.d[6][i]));
            }
            interpolatedState = this.currentStateLinearCombination(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
            interpolatedDerivatives = this.derivativeLinearCombination(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], q[9], q[10], q[11], q[12], q[13], q[14], q[15]);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
