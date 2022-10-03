package org.apache.lucene.util;

public final class MathUtil
{
    private MathUtil() {
    }
    
    public static int log(long x, final int base) {
        if (base <= 1) {
            throw new IllegalArgumentException("base must be > 1");
        }
        int ret;
        for (ret = 0; x >= base; x /= base, ++ret) {}
        return ret;
    }
    
    public static double log(final double base, final double x) {
        return Math.log(x) / Math.log(base);
    }
    
    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        if (a == 0L) {
            return b;
        }
        if (b == 0L) {
            return a;
        }
        final int commonTrailingZeros = Long.numberOfTrailingZeros(a | b);
        a >>>= Long.numberOfTrailingZeros(a);
        while (true) {
            b >>>= Long.numberOfTrailingZeros(b);
            if (a == b) {
                break;
            }
            if (a > b || a == Long.MIN_VALUE) {
                final long tmp = a;
                a = b;
                b = tmp;
            }
            if (a == 1L) {
                break;
            }
            b -= a;
        }
        return a << commonTrailingZeros;
    }
    
    public static double asinh(double a) {
        double sign;
        if (Double.doubleToRawLongBits(a) < 0L) {
            a = Math.abs(a);
            sign = -1.0;
        }
        else {
            sign = 1.0;
        }
        return sign * Math.log(Math.sqrt(a * a + 1.0) + a);
    }
    
    public static double acosh(final double a) {
        return Math.log(Math.sqrt(a * a - 1.0) + a);
    }
    
    public static double atanh(double a) {
        double mult;
        if (Double.doubleToRawLongBits(a) < 0L) {
            a = Math.abs(a);
            mult = -0.5;
        }
        else {
            mult = 0.5;
        }
        return mult * Math.log((1.0 + a) / (1.0 - a));
    }
}
