package org.apache.commons.math3.util;

import java.util.Iterator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import java.util.concurrent.atomic.AtomicReference;

public final class CombinatoricsUtils
{
    static final long[] FACTORIALS;
    static final AtomicReference<long[][]> STIRLING_S2;
    
    private CombinatoricsUtils() {
    }
    
    public static long binomialCoefficient(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 1L;
        }
        if (k == 1 || k == n - 1) {
            return n;
        }
        if (k > n / 2) {
            return binomialCoefficient(n, n - k);
        }
        long result = 1L;
        if (n <= 61) {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                result = result * i / j;
                ++i;
            }
        }
        else if (n <= 66) {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                final long d = ArithmeticUtils.gcd(i, j);
                result = result / (j / d) * (i / d);
                ++i;
            }
        }
        else {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                final long d = ArithmeticUtils.gcd(i, j);
                result = ArithmeticUtils.mulAndCheck(result / (j / d), i / d);
                ++i;
            }
        }
        return result;
    }
    
    public static double binomialCoefficientDouble(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 1.0;
        }
        if (k == 1 || k == n - 1) {
            return n;
        }
        if (k > n / 2) {
            return binomialCoefficientDouble(n, n - k);
        }
        if (n < 67) {
            return (double)binomialCoefficient(n, k);
        }
        double result = 1.0;
        for (int i = 1; i <= k; ++i) {
            result *= (n - k + i) / (double)i;
        }
        return FastMath.floor(result + 0.5);
    }
    
    public static double binomialCoefficientLog(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 0.0;
        }
        if (k == 1 || k == n - 1) {
            return FastMath.log(n);
        }
        if (n < 67) {
            return FastMath.log((double)binomialCoefficient(n, k));
        }
        if (n < 1030) {
            return FastMath.log(binomialCoefficientDouble(n, k));
        }
        if (k > n / 2) {
            return binomialCoefficientLog(n, n - k);
        }
        double logSum = 0.0;
        for (int i = n - k + 1; i <= n; ++i) {
            logSum += FastMath.log(i);
        }
        for (int i = 2; i <= k; ++i) {
            logSum -= FastMath.log(i);
        }
        return logSum;
    }
    
    public static long factorial(final int n) throws NotPositiveException, MathArithmeticException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n > 20) {
            throw new MathArithmeticException();
        }
        return CombinatoricsUtils.FACTORIALS[n];
    }
    
    public static double factorialDouble(final int n) throws NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n < 21) {
            return (double)CombinatoricsUtils.FACTORIALS[n];
        }
        return FastMath.floor(FastMath.exp(factorialLog(n)) + 0.5);
    }
    
    public static double factorialLog(final int n) throws NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n < 21) {
            return FastMath.log((double)CombinatoricsUtils.FACTORIALS[n]);
        }
        double logSum = 0.0;
        for (int i = 2; i <= n; ++i) {
            logSum += FastMath.log(i);
        }
        return logSum;
    }
    
    public static long stirlingS2(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        if (k < 0) {
            throw new NotPositiveException(k);
        }
        if (k > n) {
            throw new NumberIsTooLargeException(k, n, true);
        }
        long[][] stirlingS2 = CombinatoricsUtils.STIRLING_S2.get();
        if (stirlingS2 == null) {
            final int maxIndex = 26;
            stirlingS2 = new long[26][];
            stirlingS2[0] = new long[] { 1L };
            for (int i = 1; i < stirlingS2.length; ++i) {
                (stirlingS2[i] = new long[i + 1])[0] = 0L;
                stirlingS2[i][1] = 1L;
                stirlingS2[i][i] = 1L;
                for (int j = 2; j < i; ++j) {
                    stirlingS2[i][j] = j * stirlingS2[i - 1][j] + stirlingS2[i - 1][j - 1];
                }
            }
            CombinatoricsUtils.STIRLING_S2.compareAndSet(null, stirlingS2);
        }
        if (n < stirlingS2.length) {
            return stirlingS2[n][k];
        }
        if (k == 0) {
            return 0L;
        }
        if (k == 1 || k == n) {
            return 1L;
        }
        if (k == 2) {
            return (1L << n - 1) - 1L;
        }
        if (k == n - 1) {
            return binomialCoefficient(n, 2);
        }
        long sum = 0L;
        long sign = ((k & 0x1) == 0x0) ? 1L : -1L;
        for (int l = 1; l <= k; ++l) {
            sign = -sign;
            sum += sign * binomialCoefficient(k, l) * ArithmeticUtils.pow(l, n);
            if (sum < 0L) {
                throw new MathArithmeticException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, new Object[] { n, 0, stirlingS2.length - 1 });
            }
        }
        return sum / factorial(k);
    }
    
    public static Iterator<int[]> combinationsIterator(final int n, final int k) {
        return new Combinations(n, k).iterator();
    }
    
    public static void checkBinomial(final int n, final int k) throws NumberIsTooLargeException, NotPositiveException {
        if (n < k) {
            throw new NumberIsTooLargeException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER, k, n, true);
        }
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER, n);
        }
    }
    
    static {
        FACTORIALS = new long[] { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
        STIRLING_S2 = new AtomicReference<long[][]>(null);
    }
}
