package org.apache.commons.math3.analysis.polynomials;

import java.util.HashMap;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.CombinatoricsUtils;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.math3.fraction.BigFraction;
import java.util.List;

public class PolynomialsUtils
{
    private static final List<BigFraction> CHEBYSHEV_COEFFICIENTS;
    private static final List<BigFraction> HERMITE_COEFFICIENTS;
    private static final List<BigFraction> LAGUERRE_COEFFICIENTS;
    private static final List<BigFraction> LEGENDRE_COEFFICIENTS;
    private static final Map<JacobiKey, List<BigFraction>> JACOBI_COEFFICIENTS;
    
    private PolynomialsUtils() {
    }
    
    public static PolynomialFunction createChebyshevPolynomial(final int degree) {
        return buildPolynomial(degree, PolynomialsUtils.CHEBYSHEV_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            private final BigFraction[] coeffs = { BigFraction.ZERO, BigFraction.TWO, BigFraction.ONE };
            
            public BigFraction[] generate(final int k) {
                return this.coeffs;
            }
        });
    }
    
    public static PolynomialFunction createHermitePolynomial(final int degree) {
        return buildPolynomial(degree, PolynomialsUtils.HERMITE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            public BigFraction[] generate(final int k) {
                return new BigFraction[] { BigFraction.ZERO, BigFraction.TWO, new BigFraction(2 * k) };
            }
        });
    }
    
    public static PolynomialFunction createLaguerrePolynomial(final int degree) {
        return buildPolynomial(degree, PolynomialsUtils.LAGUERRE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            public BigFraction[] generate(final int k) {
                final int kP1 = k + 1;
                return new BigFraction[] { new BigFraction(2 * k + 1, kP1), new BigFraction(-1, kP1), new BigFraction(k, kP1) };
            }
        });
    }
    
    public static PolynomialFunction createLegendrePolynomial(final int degree) {
        return buildPolynomial(degree, PolynomialsUtils.LEGENDRE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            public BigFraction[] generate(final int k) {
                final int kP1 = k + 1;
                return new BigFraction[] { BigFraction.ZERO, new BigFraction(k + kP1, kP1), new BigFraction(k, kP1) };
            }
        });
    }
    
    public static PolynomialFunction createJacobiPolynomial(final int degree, final int v, final int w) {
        final JacobiKey key = new JacobiKey(v, w);
        if (!PolynomialsUtils.JACOBI_COEFFICIENTS.containsKey(key)) {
            final List<BigFraction> list = new ArrayList<BigFraction>();
            PolynomialsUtils.JACOBI_COEFFICIENTS.put(key, list);
            list.add(BigFraction.ONE);
            list.add(new BigFraction(v - w, 2));
            list.add(new BigFraction(2 + v + w, 2));
        }
        return buildPolynomial(degree, PolynomialsUtils.JACOBI_COEFFICIENTS.get(key), new RecurrenceCoefficientsGenerator() {
            public BigFraction[] generate(int k) {
                final int kvw = ++k + v + w;
                final int twoKvw = kvw + k;
                final int twoKvwM1 = twoKvw - 1;
                final int twoKvwM2 = twoKvw - 2;
                final int den = 2 * k * kvw * twoKvwM2;
                return new BigFraction[] { new BigFraction(twoKvwM1 * (v * v - w * w), den), new BigFraction(twoKvwM1 * twoKvw * twoKvwM2, den), new BigFraction(2 * (k + v - 1) * (k + w - 1) * twoKvw, den) };
            }
        });
    }
    
    public static double[] shift(final double[] coefficients, final double shift) {
        final int dp1 = coefficients.length;
        final double[] newCoefficients = new double[dp1];
        final int[][] coeff = new int[dp1][dp1];
        for (int i = 0; i < dp1; ++i) {
            for (int j = 0; j <= i; ++j) {
                coeff[i][j] = (int)CombinatoricsUtils.binomialCoefficient(i, j);
            }
        }
        for (int i = 0; i < dp1; ++i) {
            final double[] array = newCoefficients;
            final int n = 0;
            array[n] += coefficients[i] * FastMath.pow(shift, i);
        }
        for (int d = dp1 - 1, k = 0; k < d; ++k) {
            for (int l = k; l < d; ++l) {
                final double[] array2 = newCoefficients;
                final int n2 = k + 1;
                array2[n2] += coeff[l + 1][l - k] * coefficients[l + 1] * FastMath.pow(shift, l - k);
            }
        }
        return newCoefficients;
    }
    
    private static PolynomialFunction buildPolynomial(final int degree, final List<BigFraction> coefficients, final RecurrenceCoefficientsGenerator generator) {
        synchronized (coefficients) {
            final int maxDegree = (int)FastMath.floor(FastMath.sqrt(2 * coefficients.size())) - 1;
            if (degree > maxDegree) {
                computeUpToDegree(degree, maxDegree, generator, coefficients);
            }
        }
        final int start = degree * (degree + 1) / 2;
        final double[] a = new double[degree + 1];
        for (int i = 0; i <= degree; ++i) {
            a[i] = coefficients.get(start + i).doubleValue();
        }
        return new PolynomialFunction(a);
    }
    
    private static void computeUpToDegree(final int degree, final int maxDegree, final RecurrenceCoefficientsGenerator generator, final List<BigFraction> coefficients) {
        int startK = (maxDegree - 1) * maxDegree / 2;
        for (int k = maxDegree; k < degree; ++k) {
            final int startKm1 = startK;
            startK += k;
            final BigFraction[] ai = generator.generate(k);
            BigFraction ck = coefficients.get(startK);
            BigFraction ckm1 = coefficients.get(startKm1);
            coefficients.add(ck.multiply(ai[0]).subtract(ckm1.multiply(ai[2])));
            for (int i = 1; i < k; ++i) {
                final BigFraction ckPrev = ck;
                ck = coefficients.get(startK + i);
                ckm1 = coefficients.get(startKm1 + i);
                coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])).subtract(ckm1.multiply(ai[2])));
            }
            final BigFraction ckPrev2 = ck;
            ck = coefficients.get(startK + k);
            coefficients.add(ck.multiply(ai[0]).add(ckPrev2.multiply(ai[1])));
            coefficients.add(ck.multiply(ai[1]));
        }
    }
    
    static {
        (CHEBYSHEV_COEFFICIENTS = new ArrayList<BigFraction>()).add(BigFraction.ONE);
        PolynomialsUtils.CHEBYSHEV_COEFFICIENTS.add(BigFraction.ZERO);
        PolynomialsUtils.CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
        (HERMITE_COEFFICIENTS = new ArrayList<BigFraction>()).add(BigFraction.ONE);
        PolynomialsUtils.HERMITE_COEFFICIENTS.add(BigFraction.ZERO);
        PolynomialsUtils.HERMITE_COEFFICIENTS.add(BigFraction.TWO);
        (LAGUERRE_COEFFICIENTS = new ArrayList<BigFraction>()).add(BigFraction.ONE);
        PolynomialsUtils.LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
        PolynomialsUtils.LAGUERRE_COEFFICIENTS.add(BigFraction.MINUS_ONE);
        (LEGENDRE_COEFFICIENTS = new ArrayList<BigFraction>()).add(BigFraction.ONE);
        PolynomialsUtils.LEGENDRE_COEFFICIENTS.add(BigFraction.ZERO);
        PolynomialsUtils.LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
        JACOBI_COEFFICIENTS = new HashMap<JacobiKey, List<BigFraction>>();
    }
    
    private static class JacobiKey
    {
        private final int v;
        private final int w;
        
        JacobiKey(final int v, final int w) {
            this.v = v;
            this.w = w;
        }
        
        @Override
        public int hashCode() {
            return this.v << 16 ^ this.w;
        }
        
        @Override
        public boolean equals(final Object key) {
            if (key == null || !(key instanceof JacobiKey)) {
                return false;
            }
            final JacobiKey otherK = (JacobiKey)key;
            return this.v == otherK.v && this.w == otherK.w;
        }
    }
    
    private interface RecurrenceCoefficientsGenerator
    {
        BigFraction[] generate(final int p0);
    }
}
