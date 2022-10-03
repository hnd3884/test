package io.netty.util.internal;

import java.util.Map;
import java.util.Collection;

public final class ObjectUtil
{
    private static final float FLOAT_ZERO = 0.0f;
    private static final double DOUBLE_ZERO = 0.0;
    private static final long LONG_ZERO = 0L;
    private static final int INT_ZERO = 0;
    
    private ObjectUtil() {
    }
    
    public static <T> T checkNotNull(final T arg, final String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }
    
    public static <T> T[] deepCheckNotNull(final String text, final T... varargs) {
        if (varargs == null) {
            throw new NullPointerException(text);
        }
        for (final T element : varargs) {
            if (element == null) {
                throw new NullPointerException(text);
            }
        }
        return varargs;
    }
    
    public static <T> T checkNotNullWithIAE(final T arg, final String paramName) throws IllegalArgumentException {
        if (arg == null) {
            throw new IllegalArgumentException("Param '" + paramName + "' must not be null");
        }
        return arg;
    }
    
    public static <T> T checkNotNullArrayParam(final T value, final int index, final String name) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Array index " + index + " of parameter '" + name + "' must not be null");
        }
        return value;
    }
    
    public static int checkPositive(final int i, final String name) {
        if (i <= 0) {
            throw new IllegalArgumentException(name + " : " + i + " (expected: > 0)");
        }
        return i;
    }
    
    public static long checkPositive(final long l, final String name) {
        if (l <= 0L) {
            throw new IllegalArgumentException(name + " : " + l + " (expected: > 0)");
        }
        return l;
    }
    
    public static double checkPositive(final double d, final String name) {
        if (d <= 0.0) {
            throw new IllegalArgumentException(name + " : " + d + " (expected: > 0)");
        }
        return d;
    }
    
    public static float checkPositive(final float f, final String name) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException(name + " : " + f + " (expected: > 0)");
        }
        return f;
    }
    
    public static int checkPositiveOrZero(final int i, final String name) {
        if (i < 0) {
            throw new IllegalArgumentException(name + " : " + i + " (expected: >= 0)");
        }
        return i;
    }
    
    public static long checkPositiveOrZero(final long l, final String name) {
        if (l < 0L) {
            throw new IllegalArgumentException(name + " : " + l + " (expected: >= 0)");
        }
        return l;
    }
    
    public static double checkPositiveOrZero(final double d, final String name) {
        if (d < 0.0) {
            throw new IllegalArgumentException(name + " : " + d + " (expected: >= 0)");
        }
        return d;
    }
    
    public static float checkPositiveOrZero(final float f, final String name) {
        if (f < 0.0f) {
            throw new IllegalArgumentException(name + " : " + f + " (expected: >= 0)");
        }
        return f;
    }
    
    public static int checkInRange(final int i, final int start, final int end, final String name) {
        if (i < start || i > end) {
            throw new IllegalArgumentException(name + ": " + i + " (expected: " + start + "-" + end + ")");
        }
        return i;
    }
    
    public static long checkInRange(final long l, final long start, final long end, final String name) {
        if (l < start || l > end) {
            throw new IllegalArgumentException(name + ": " + l + " (expected: " + start + "-" + end + ")");
        }
        return l;
    }
    
    public static <T> T[] checkNonEmpty(final T[] array, final String name) {
        if (checkNotNull(array, name).length == 0) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return array;
    }
    
    public static byte[] checkNonEmpty(final byte[] array, final String name) {
        if (checkNotNull(array, name).length == 0) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return array;
    }
    
    public static char[] checkNonEmpty(final char[] array, final String name) {
        if (checkNotNull(array, name).length == 0) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return array;
    }
    
    public static <T extends Collection<?>> T checkNonEmpty(final T collection, final String name) {
        if (checkNotNull(collection, name).size() == 0) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return collection;
    }
    
    public static String checkNonEmpty(final String value, final String name) {
        if (checkNotNull(value, name).isEmpty()) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return value;
    }
    
    public static <K, V, T extends Map<K, V>> T checkNonEmpty(final T value, final String name) {
        if (checkNotNull(value, name).isEmpty()) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return value;
    }
    
    public static CharSequence checkNonEmpty(final CharSequence value, final String name) {
        if (checkNotNull(value, name).length() == 0) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        }
        return value;
    }
    
    public static String checkNonEmptyAfterTrim(final String value, final String name) {
        final String trimmed = checkNotNull(value, name).trim();
        return checkNonEmpty(trimmed, name);
    }
    
    public static int intValue(final Integer wrapper, final int defaultValue) {
        return (wrapper != null) ? wrapper : defaultValue;
    }
    
    public static long longValue(final Long wrapper, final long defaultValue) {
        return (wrapper != null) ? wrapper : defaultValue;
    }
}
