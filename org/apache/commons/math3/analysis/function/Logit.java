package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Logit implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double lo;
    private final double hi;
    
    public Logit() {
        this(0.0, 1.0);
    }
    
    public Logit(final double lo, final double hi) {
        this.lo = lo;
        this.hi = hi;
    }
    
    public double value(final double x) throws OutOfRangeException {
        return value(x, this.lo, this.hi);
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    private static double value(final double x, final double lo, final double hi) throws OutOfRangeException {
        if (x < lo || x > hi) {
            throw new OutOfRangeException(x, lo, hi);
        }
        return FastMath.log((x - lo) / (hi - x));
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws OutOfRangeException {
        final double x = t.getValue();
        if (x < this.lo || x > this.hi) {
            throw new OutOfRangeException(x, this.lo, this.hi);
        }
        final double[] f = new double[t.getOrder() + 1];
        f[0] = FastMath.log((x - this.lo) / (this.hi - x));
        if (Double.isInfinite(f[0])) {
            if (f.length > 1) {
                f[1] = Double.POSITIVE_INFINITY;
            }
            for (int i = 2; i < f.length; ++i) {
                f[i] = f[i - 2];
            }
        }
        else {
            double xL;
            final double invL = xL = 1.0 / (x - this.lo);
            double xH;
            final double invH = xH = 1.0 / (this.hi - x);
            for (int j = 1; j < f.length; ++j) {
                f[j] = xL + xH;
                xL *= -j * invL;
                xH *= j * invH;
            }
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
            final double lo = param[0];
            final double hi = param[1];
            return new double[] { 1.0 / (lo - x), 1.0 / (hi - x) };
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
