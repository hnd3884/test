package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Logistic implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double a;
    private final double k;
    private final double b;
    private final double oneOverN;
    private final double q;
    private final double m;
    
    public Logistic(final double k, final double m, final double b, final double q, final double a, final double n) throws NotStrictlyPositiveException {
        if (n <= 0.0) {
            throw new NotStrictlyPositiveException(n);
        }
        this.k = k;
        this.m = m;
        this.b = b;
        this.q = q;
        this.a = a;
        this.oneOverN = 1.0 / n;
    }
    
    public double value(final double x) {
        return value(this.m - x, this.k, this.b, this.q, this.a, this.oneOverN);
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    private static double value(final double mMinusX, final double k, final double b, final double q, final double a, final double oneOverN) {
        return a + (k - a) / FastMath.pow(1.0 + q * FastMath.exp(b * mMinusX), oneOverN);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        return t.negate().add(this.m).multiply(this.b).exp().multiply(this.q).add(1.0).pow(this.oneOverN).reciprocal().multiply(this.k - this.a).add(this.a);
    }
    
    public static class Parametric implements ParametricUnivariateFunction
    {
        public double value(final double x, final double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            this.validateParameters(param);
            return value(param[1] - x, param[0], param[2], param[3], param[4], 1.0 / param[5]);
        }
        
        public double[] gradient(final double x, final double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            this.validateParameters(param);
            final double b = param[2];
            final double q = param[3];
            final double mMinusX = param[1] - x;
            final double oneOverN = 1.0 / param[5];
            final double exp = FastMath.exp(b * mMinusX);
            final double qExp = q * exp;
            final double qExp2 = qExp + 1.0;
            final double factor1 = (param[0] - param[4]) * oneOverN / FastMath.pow(qExp2, oneOverN);
            final double factor2 = -factor1 / qExp2;
            final double gk = value(mMinusX, 1.0, b, q, 0.0, oneOverN);
            final double gm = factor2 * b * qExp;
            final double gb = factor2 * mMinusX * qExp;
            final double gq = factor2 * exp;
            final double ga = value(mMinusX, 0.0, b, q, 1.0, oneOverN);
            final double gn = factor1 * FastMath.log(qExp2) * oneOverN;
            return new double[] { gk, gm, gb, gq, ga, gn };
        }
        
        private void validateParameters(final double[] param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            if (param == null) {
                throw new NullArgumentException();
            }
            if (param.length != 6) {
                throw new DimensionMismatchException(param.length, 6);
            }
            if (param[5] <= 0.0) {
                throw new NotStrictlyPositiveException(param[5]);
            }
        }
    }
}
