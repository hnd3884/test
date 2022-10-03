package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Arrays;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Sigmoid implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double lo;
    private final double hi;
    
    public Sigmoid() {
        this(0.0, 1.0);
    }
    
    public Sigmoid(final double lo, final double hi) {
        this.lo = lo;
        this.hi = hi;
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    public double value(final double x) {
        return value(x, this.lo, this.hi);
    }
    
    private static double value(final double x, final double lo, final double hi) {
        return lo + (hi - lo) / (1.0 + FastMath.exp(-x));
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws DimensionMismatchException {
        final double[] f = new double[t.getOrder() + 1];
        final double exp = FastMath.exp(-t.getValue());
        if (Double.isInfinite(exp)) {
            f[0] = this.lo;
            Arrays.fill(f, 1, f.length, 0.0);
        }
        else {
            final double[] p = new double[f.length];
            final double inv = 1.0 / (1.0 + exp);
            double coeff = this.hi - this.lo;
            for (int n = 0; n < f.length; ++n) {
                double v = 0.0;
                p[n] = 1.0;
                for (int k = n; k >= 0; --k) {
                    v = v * exp + p[k];
                    if (k > 1) {
                        p[k - 1] = (n - k + 2) * p[k - 2] - (k - 1) * p[k - 1];
                    }
                    else {
                        p[0] = 0.0;
                    }
                }
                coeff *= inv;
                f[n] = coeff * v;
            }
            final double[] array = f;
            final int n2 = 0;
            array[n2] += this.lo;
        }
        return t.compose(f);
    }
    
    public static class Parametric implements ParametricUnivariateFunction
    {
        public double value(final double x, final double... param) throws NullArgumentException, DimensionMismatchException {
            this.validateParameters(param);
            return value(x, param[0], param[1]);
        }
        
        public double[] gradient(final double x, final double... param) throws NullArgumentException, DimensionMismatchException {
            this.validateParameters(param);
            final double invExp1 = 1.0 / (1.0 + FastMath.exp(-x));
            return new double[] { 1.0 - invExp1, invExp1 };
        }
        
        private void validateParameters(final double[] param) throws NullArgumentException, DimensionMismatchException {
            if (param == null) {
                throw new NullArgumentException();
            }
            if (param.length != 2) {
                throw new DimensionMismatchException(param.length, 2);
            }
        }
    }
}
