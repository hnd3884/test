package org.apache.commons.math3.util;

import java.math.BigInteger;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public final class ArithmeticUtils
{
    private ArithmeticUtils() {
    }
    
    public static int addAndCheck(final int x, final int y) throws MathArithmeticException {
        final long s = x + (long)y;
        if (s < -2147483648L || s > 2147483647L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { x, y });
        }
        return (int)s;
    }
    
    public static long addAndCheck(final long a, final long b) throws MathArithmeticException {
        return addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION);
    }
    
    @Deprecated
    public static long binomialCoefficient(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        return CombinatoricsUtils.binomialCoefficient(n, k);
    }
    
    @Deprecated
    public static double binomialCoefficientDouble(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        return CombinatoricsUtils.binomialCoefficientDouble(n, k);
    }
    
    @Deprecated
    public static double binomialCoefficientLog(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        return CombinatoricsUtils.binomialCoefficientLog(n, k);
    }
    
    @Deprecated
    public static long factorial(final int n) throws NotPositiveException, MathArithmeticException {
        return CombinatoricsUtils.factorial(n);
    }
    
    @Deprecated
    public static double factorialDouble(final int n) throws NotPositiveException {
        return CombinatoricsUtils.factorialDouble(n);
    }
    
    @Deprecated
    public static double factorialLog(final int n) throws NotPositiveException {
        return CombinatoricsUtils.factorialLog(n);
    }
    
    public static int gcd(final int p, final int q) throws MathArithmeticException {
        int a = p;
        int b = q;
        if (a != 0 && b != 0) {
            long al = a;
            long bl = b;
            boolean useLong = false;
            if (a < 0) {
                if (Integer.MIN_VALUE == a) {
                    useLong = true;
                }
                else {
                    a = -a;
                }
                al = -al;
            }
            if (b < 0) {
                if (Integer.MIN_VALUE == b) {
                    useLong = true;
                }
                else {
                    b = -b;
                }
                bl = -bl;
            }
            if (useLong) {
                if (al == bl) {
                    throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
                }
                long blbu = bl;
                bl = al;
                al = blbu % al;
                if (al == 0L) {
                    if (bl > 2147483647L) {
                        throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
                    }
                    return (int)bl;
                }
                else {
                    blbu = bl;
                    b = (int)al;
                    a = (int)(blbu % al);
                }
            }
            return gcdPositive(a, b);
        }
        if (a == Integer.MIN_VALUE || b == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
        }
        return FastMath.abs(a + b);
    }
    
    private static int gcdPositive(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        final int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos;
        final int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos;
        final int shift = FastMath.min(aTwos, bTwos);
        while (a != b) {
            final int delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);
            a >>= Integer.numberOfTrailingZeros(a);
        }
        return a << shift;
    }
    
    public static long gcd(final long p, final long q) throws MathArithmeticException {
        long u = p;
        long v = q;
        if (u == 0L || v == 0L) {
            if (u == Long.MIN_VALUE || v == Long.MIN_VALUE) {
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q });
            }
            return FastMath.abs(u) + FastMath.abs(v);
        }
        else {
            if (u > 0L) {
                u = -u;
            }
            if (v > 0L) {
                v = -v;
            }
            int k;
            for (k = 0; (u & 0x1L) == 0x0L && (v & 0x1L) == 0x0L && k < 63; u /= 2L, v /= 2L, ++k) {}
            if (k == 63) {
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q });
            }
            long t = ((u & 0x1L) == 0x1L) ? v : (-(u / 2L));
            while (true) {
                if ((t & 0x1L) == 0x0L) {
                    t /= 2L;
                }
                else {
                    if (t > 0L) {
                        u = -t;
                    }
                    else {
                        v = t;
                    }
                    t = (v - u) / 2L;
                    if (t == 0L) {
                        break;
                    }
                    continue;
                }
            }
            return -u * (1L << k);
        }
    }
    
    public static int lcm(final int a, final int b) throws MathArithmeticException {
        if (a == 0 || b == 0) {
            return 0;
        }
        final int lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_32_BITS, new Object[] { a, b });
        }
        return lcm;
    }
    
    public static long lcm(final long a, final long b) throws MathArithmeticException {
        if (a == 0L || b == 0L) {
            return 0L;
        }
        final long lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Long.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_64_BITS, new Object[] { a, b });
        }
        return lcm;
    }
    
    public static int mulAndCheck(final int x, final int y) throws MathArithmeticException {
        final long m = x * (long)y;
        if (m < -2147483648L || m > 2147483647L) {
            throw new MathArithmeticException();
        }
        return (int)m;
    }
    
    public static long mulAndCheck(final long a, final long b) throws MathArithmeticException {
        long ret;
        if (a > b) {
            ret = mulAndCheck(b, a);
        }
        else if (a < 0L) {
            if (b < 0L) {
                if (a < Long.MAX_VALUE / b) {
                    throw new MathArithmeticException();
                }
                ret = a * b;
            }
            else if (b > 0L) {
                if (Long.MIN_VALUE / b > a) {
                    throw new MathArithmeticException();
                }
                ret = a * b;
            }
            else {
                ret = 0L;
            }
        }
        else if (a > 0L) {
            if (a > Long.MAX_VALUE / b) {
                throw new MathArithmeticException();
            }
            ret = a * b;
        }
        else {
            ret = 0L;
        }
        return ret;
    }
    
    public static int subAndCheck(final int x, final int y) throws MathArithmeticException {
        final long s = x - (long)y;
        if (s < -2147483648L || s > 2147483647L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { x, y });
        }
        return (int)s;
    }
    
    public static long subAndCheck(final long a, final long b) throws MathArithmeticException {
        long ret;
        if (b == Long.MIN_VALUE) {
            if (a >= 0L) {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { a, -b });
            }
            ret = a - b;
        }
        else {
            ret = addAndCheck(a, -b, LocalizedFormats.OVERFLOW_IN_ADDITION);
        }
        return ret;
    }
    
    public static int pow(final int k, final int e) throws NotPositiveException, MathArithmeticException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        try {
            int exp = e;
            int result = 1;
            int k2p = k;
            while (true) {
                if ((exp & 0x1) != 0x0) {
                    result = mulAndCheck(result, k2p);
                }
                exp >>= 1;
                if (exp == 0) {
                    break;
                }
                k2p = mulAndCheck(k2p, k2p);
            }
            return result;
        }
        catch (final MathArithmeticException mae) {
            mae.getContext().addMessage(LocalizedFormats.OVERFLOW, new Object[0]);
            mae.getContext().addMessage(LocalizedFormats.BASE, k);
            mae.getContext().addMessage(LocalizedFormats.EXPONENT, e);
            throw mae;
        }
    }
    
    @Deprecated
    public static int pow(final int k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        int result = 1;
        int k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static long pow(final long k, final int e) throws NotPositiveException, MathArithmeticException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        try {
            int exp = e;
            long result = 1L;
            long k2p = k;
            while (true) {
                if ((exp & 0x1) != 0x0) {
                    result = mulAndCheck(result, k2p);
                }
                exp >>= 1;
                if (exp == 0) {
                    break;
                }
                k2p = mulAndCheck(k2p, k2p);
            }
            return result;
        }
        catch (final MathArithmeticException mae) {
            mae.getContext().addMessage(LocalizedFormats.OVERFLOW, new Object[0]);
            mae.getContext().addMessage(LocalizedFormats.BASE, k);
            mae.getContext().addMessage(LocalizedFormats.EXPONENT, e);
            throw mae;
        }
    }
    
    @Deprecated
    public static long pow(final long k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        long result = 1L;
        long k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static BigInteger pow(final BigInteger k, final int e) throws NotPositiveException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        return k.pow(e);
    }
    
    public static BigInteger pow(final BigInteger k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        BigInteger result = BigInteger.ONE;
        BigInteger k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e >>= 1;
        }
        return result;
    }
    
    public static BigInteger pow(final BigInteger k, BigInteger e) throws NotPositiveException {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        BigInteger result = BigInteger.ONE;
        BigInteger k2p = k;
        while (!BigInteger.ZERO.equals(e)) {
            if (e.testBit(0)) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e = e.shiftRight(1);
        }
        return result;
    }
    
    @Deprecated
    public static long stirlingS2(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        return CombinatoricsUtils.stirlingS2(n, k);
    }
    
    private static long addAndCheck(final long a, final long b, final Localizable pattern) throws MathArithmeticException {
        final long result = a + b;
        if (!((a ^ b) < 0L | (a ^ result) >= 0L)) {
            throw new MathArithmeticException(pattern, new Object[] { a, b });
        }
        return result;
    }
    
    public static boolean isPowerOfTwo(final long n) {
        return n > 0L && (n & n - 1L) == 0x0L;
    }
}
