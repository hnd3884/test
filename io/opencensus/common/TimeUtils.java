package io.opencensus.common;

import java.math.BigInteger;

final class TimeUtils
{
    static final long MAX_SECONDS = 315576000000L;
    static final int MAX_NANOS = 999999999;
    static final long MILLIS_PER_SECOND = 1000L;
    static final long NANOS_PER_MILLI = 1000000L;
    static final long NANOS_PER_SECOND = 1000000000L;
    private static final BigInteger MAX_LONG_VALUE;
    private static final BigInteger MIN_LONG_VALUE;
    
    private TimeUtils() {
    }
    
    static int compareLongs(final long x, final long y) {
        if (x < y) {
            return -1;
        }
        if (x == y) {
            return 0;
        }
        return 1;
    }
    
    static long checkedAdd(final long x, final long y) {
        final BigInteger sum = BigInteger.valueOf(x).add(BigInteger.valueOf(y));
        if (sum.compareTo(TimeUtils.MAX_LONG_VALUE) > 0 || sum.compareTo(TimeUtils.MIN_LONG_VALUE) < 0) {
            throw new ArithmeticException("Long sum overflow: x=" + x + ", y=" + y);
        }
        return x + y;
    }
    
    static {
        MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
        MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
    }
}
