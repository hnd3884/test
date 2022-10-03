package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Arrays;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Gaussian implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double mean;
    private final double is;
    private final double i2s2;
    private final double norm;
    
    public Gaussian(final double norm, final double mean, final double sigma) throws NotStrictlyPositiveException {
        if (sigma <= 0.0) {
            throw new NotStrictlyPositiveException(sigma);
        }
        this.norm = norm;
        this.mean = mean;
        this.is = 1.0 / sigma;
        this.i2s2 = 0.5 * this.is * this.is;
    }
    
    public Gaussian(final double mean, final double sigma) throws NotStrictlyPositiveException {
        this(1.0 / (sigma * FastMath.sqrt(6.283185307179586)), mean, sigma);
    }
    
    public Gaussian() {
        this(0.0, 1.0);
    }
    
    public double value(final double x) {
        return value(x - this.mean, this.norm, this.i2s2);
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    private static double value(final double xMinusMean, final double norm, final double i2s2) {
        return norm * FastMath.exp(-xMinusMean * xMinusMean * i2s2);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws DimensionMismatchException {
        final double u = this.is * (t.getValue() - this.mean);
        final double[] f = new double[t.getOrder() + 1];
        final double[] p = new double[f.length];
        p[0] = 1.0;
        final double u2 = u * u;
        double coeff = this.norm * FastMath.exp(-0.5 * u2);
        if (coeff <= Precision.SAFE_MIN) {
            Arrays.fill(f, 0.0);
        }
        else {
            f[0] = coeff;
            for (int n = 1; n < f.length; ++n) {
                double v = 0.0;
                p[n] = -p[n - 1];
                for (int k = n; k >= 0; k -= 2) {
                    v = v * u2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] - p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x1) {
                    v *= u;
                }
                coeff *= this.is;
                f[n] = coeff * v;
            }
        }
        return t.compose(f);
    }
    
    public static class Parametric implements ParametricUnivariateFunction
    {
        public double value(final double x, final double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            this.validateParameters(param);
            final double diff = x - param[1];
            final double i2s2 = 1.0 / (2.0 * param[2] * param[2]);
            return value(diff, param[0], i2s2);
        }
        
        public double[] gradient(final double x, final double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            this.validateParameters(param);
            final double norm = param[0];
            final double diff = x - param[1];
            final double sigma = param[2];
            final double i2s2 = 1.0 / (2.0 * sigma * sigma);
            final double n = value(diff, 1.0, i2s2);
            final double m = norm * n * 2.0 * i2s2 * diff;
            final double s = m * diff / sigma;
            return new double[] { n, m, s };
        }
        
        private void validateParameters(final double[] param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            if (param == null) {
                throw new NullArgumentException();
            }
            if (param.length != 3) {
                throw new DimensionMismatchException(param.length, 3);
            }
            if (param[2] <= 0.0) {
                throw new NotStrictlyPositiveException(param[2]);
            }
        }
    }
}
