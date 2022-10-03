package io.netty.util.internal.shaded.org.jctools.util;

public final class RangeUtil
{
    public static long checkPositive(final long n, final String name) {
        if (n <= 0L) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: > 0)");
        }
        return n;
    }
    
    public static int checkPositiveOrZero(final int n, final String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: >= 0)");
        }
        return n;
    }
    
    public static int checkLessThan(final int n, final int expected, final String name) {
        if (n >= expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: < " + expected + ')');
        }
        return n;
    }
    
    public static int checkLessThanOrEqual(final int n, final long expected, final String name) {
        if (n > expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: <= " + expected + ')');
        }
        return n;
    }
    
    public static int checkGreaterThanOrEqual(final int n, final int expected, final String name) {
        if (n < expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: >= " + expected + ')');
        }
        return n;
    }
}
