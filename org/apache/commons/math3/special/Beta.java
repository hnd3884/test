package org.apache.commons.math3.special;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.ContinuedFraction;

public class Beta
{
    private static final double DEFAULT_EPSILON = 1.0E-14;
    private static final double HALF_LOG_TWO_PI = 0.9189385332046727;
    private static final double[] DELTA;
    
    private Beta() {
    }
    
    public static double regularizedBeta(final double x, final double a, final double b) {
        return regularizedBeta(x, a, b, 1.0E-14, Integer.MAX_VALUE);
    }
    
    public static double regularizedBeta(final double x, final double a, final double b, final double epsilon) {
        return regularizedBeta(x, a, b, epsilon, Integer.MAX_VALUE);
    }
    
    public static double regularizedBeta(final double x, final double a, final double b, final int maxIterations) {
        return regularizedBeta(x, a, b, 1.0E-14, maxIterations);
    }
    
    public static double regularizedBeta(final double x, final double a, final double b, final double epsilon, final int maxIterations) {
        double ret;
        if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || x < 0.0 || x > 1.0 || a <= 0.0 || b <= 0.0) {
            ret = Double.NaN;
        }
        else if (x > (a + 1.0) / (2.0 + b + a) && 1.0 - x <= (b + 1.0) / (2.0 + b + a)) {
            ret = 1.0 - regularizedBeta(1.0 - x, b, a, epsilon, maxIterations);
        }
        else {
            final ContinuedFraction fraction = new ContinuedFraction() {
                @Override
                protected double getB(final int n, final double x) {
                    double ret;
                    if (n % 2 == 0) {
                        final double m = n / 2.0;
                        ret = m * (b - m) * x / ((a + 2.0 * m - 1.0) * (a + 2.0 * m));
                    }
                    else {
                        final double m = (n - 1.0) / 2.0;
                        ret = -((a + m) * (a + b + m) * x) / ((a + 2.0 * m) * (a + 2.0 * m + 1.0));
                    }
                    return ret;
                }
                
                @Override
                protected double getA(final int n, final double x) {
                    return 1.0;
                }
            };
            ret = FastMath.exp(a * FastMath.log(x) + b * FastMath.log1p(-x) - FastMath.log(a) - logBeta(a, b)) * 1.0 / fraction.evaluate(x, epsilon, maxIterations);
        }
        return ret;
    }
    
    @Deprecated
    public static double logBeta(final double a, final double b, final double epsilon, final int maxIterations) {
        return logBeta(a, b);
    }
    
    private static double logGammaSum(final double a, final double b) throws OutOfRangeException {
        if (a < 1.0 || a > 2.0) {
            throw new OutOfRangeException(a, 1.0, 2.0);
        }
        if (b < 1.0 || b > 2.0) {
            throw new OutOfRangeException(b, 1.0, 2.0);
        }
        final double x = a - 1.0 + (b - 1.0);
        if (x <= 0.5) {
            return Gamma.logGamma1p(1.0 + x);
        }
        if (x <= 1.5) {
            return Gamma.logGamma1p(x) + FastMath.log1p(x);
        }
        return Gamma.logGamma1p(x - 1.0) + FastMath.log(x * (1.0 + x));
    }
    
    private static double logGammaMinusLogGammaSum(final double a, final double b) throws NumberIsTooSmallException {
        if (a < 0.0) {
            throw new NumberIsTooSmallException(a, 0.0, true);
        }
        if (b < 10.0) {
            throw new NumberIsTooSmallException(b, 10.0, true);
        }
        double d;
        double w;
        if (a <= b) {
            d = b + (a - 0.5);
            w = deltaMinusDeltaSum(a, b);
        }
        else {
            d = a + (b - 0.5);
            w = deltaMinusDeltaSum(b, a);
        }
        final double u = d * FastMath.log1p(a / b);
        final double v = a * (FastMath.log(b) - 1.0);
        return (u <= v) ? (w - u - v) : (w - v - u);
    }
    
    private static double deltaMinusDeltaSum(final double a, final double b) throws OutOfRangeException, NumberIsTooSmallException {
        if (a < 0.0 || a > b) {
            throw new OutOfRangeException(a, 0, b);
        }
        if (b < 10.0) {
            throw new NumberIsTooSmallException(b, 10, true);
        }
        final double h = a / b;
        final double p = h / (1.0 + h);
        final double q = 1.0 / (1.0 + h);
        final double q2 = q * q;
        final double[] s = new double[Beta.DELTA.length];
        s[0] = 1.0;
        for (int i = 1; i < s.length; ++i) {
            s[i] = 1.0 + (q + q2 * s[i - 1]);
        }
        final double sqrtT = 10.0 / b;
        final double t = sqrtT * sqrtT;
        double w = Beta.DELTA[Beta.DELTA.length - 1] * s[s.length - 1];
        for (int j = Beta.DELTA.length - 2; j >= 0; --j) {
            w = t * w + Beta.DELTA[j] * s[j];
        }
        return w * p / b;
    }
    
    private static double sumDeltaMinusDeltaSum(final double p, final double q) {
        if (p < 10.0) {
            throw new NumberIsTooSmallException(p, 10.0, true);
        }
        if (q < 10.0) {
            throw new NumberIsTooSmallException(q, 10.0, true);
        }
        final double a = FastMath.min(p, q);
        final double b = FastMath.max(p, q);
        final double sqrtT = 10.0 / a;
        final double t = sqrtT * sqrtT;
        double z = Beta.DELTA[Beta.DELTA.length - 1];
        for (int i = Beta.DELTA.length - 2; i >= 0; --i) {
            z = t * z + Beta.DELTA[i];
        }
        return z / a + deltaMinusDeltaSum(a, b);
    }
    
    public static double logBeta(final double p, final double q) {
        if (Double.isNaN(p) || Double.isNaN(q) || p <= 0.0 || q <= 0.0) {
            return Double.NaN;
        }
        final double a = FastMath.min(p, q);
        final double b = FastMath.max(p, q);
        if (a >= 10.0) {
            final double w = sumDeltaMinusDeltaSum(a, b);
            final double h = a / b;
            final double c = h / (1.0 + h);
            final double u = -(a - 0.5) * FastMath.log(c);
            final double v = b * FastMath.log1p(h);
            if (u <= v) {
                return -0.5 * FastMath.log(b) + 0.9189385332046727 + w - u - v;
            }
            return -0.5 * FastMath.log(b) + 0.9189385332046727 + w - v - u;
        }
        else if (a > 2.0) {
            if (b > 1000.0) {
                final int n = (int)FastMath.floor(a - 1.0);
                double prod = 1.0;
                double ared = a;
                for (int i = 0; i < n; ++i) {
                    --ared;
                    prod *= ared / (1.0 + ared / b);
                }
                return FastMath.log(prod) - n * FastMath.log(b) + (Gamma.logGamma(ared) + logGammaMinusLogGammaSum(ared, b));
            }
            double prod2;
            double ared2;
            double h2;
            for (prod2 = 1.0, ared2 = a; ared2 > 2.0; --ared2, h2 = ared2 / b, prod2 *= h2 / (1.0 + h2)) {}
            if (b < 10.0) {
                double prod3;
                double bred;
                for (prod3 = 1.0, bred = b; bred > 2.0; --bred, prod3 *= bred / (ared2 + bred)) {}
                return FastMath.log(prod2) + FastMath.log(prod3) + (Gamma.logGamma(ared2) + (Gamma.logGamma(bred) - logGammaSum(ared2, bred)));
            }
            return FastMath.log(prod2) + Gamma.logGamma(ared2) + logGammaMinusLogGammaSum(ared2, b);
        }
        else if (a >= 1.0) {
            if (b <= 2.0) {
                return Gamma.logGamma(a) + Gamma.logGamma(b) - logGammaSum(a, b);
            }
            if (b < 10.0) {
                double prod4;
                double bred2;
                for (prod4 = 1.0, bred2 = b; bred2 > 2.0; --bred2, prod4 *= bred2 / (a + bred2)) {}
                return FastMath.log(prod4) + (Gamma.logGamma(a) + (Gamma.logGamma(bred2) - logGammaSum(a, bred2)));
            }
            return Gamma.logGamma(a) + logGammaMinusLogGammaSum(a, b);
        }
        else {
            if (b >= 10.0) {
                return Gamma.logGamma(a) + logGammaMinusLogGammaSum(a, b);
            }
            return FastMath.log(Gamma.gamma(a) * Gamma.gamma(b) / Gamma.gamma(a + b));
        }
    }
    
    static {
        DELTA = new double[] { 0.08333333333333333, -2.777777777777778E-5, 7.936507936507937E-8, -5.952380952380953E-10, 8.417508417508329E-12, -1.917526917518546E-13, 6.410256405103255E-15, -2.955065141253382E-16, 1.7964371635940225E-17, -1.3922896466162779E-18, 1.338028550140209E-19, -1.542460098679661E-20, 1.9770199298095743E-21, -2.3406566479399704E-22, 1.713480149663986E-23 };
    }
}
