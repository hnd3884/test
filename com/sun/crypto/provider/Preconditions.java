package com.sun.crypto.provider;

import java.util.function.Function;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Preconditions
{
    private static RuntimeException outOfBounds(final BiFunction<String, List<Integer>, ? extends RuntimeException> biFunction, final String s, final Integer... array) {
        final List<Object> unmodifiableList = Collections.unmodifiableList((List<?>)Arrays.asList((T[])array));
        final RuntimeException ex = (biFunction == null) ? null : ((RuntimeException)biFunction.apply(s, unmodifiableList));
        return (ex == null) ? new IndexOutOfBoundsException(outOfBoundsMessage(s, (List<Integer>)unmodifiableList)) : ex;
    }
    
    private static RuntimeException outOfBoundsCheckIndex(final BiFunction<String, List<Integer>, ? extends RuntimeException> biFunction, final int n, final int n2) {
        return outOfBounds(biFunction, "checkIndex", n, n2);
    }
    
    private static RuntimeException outOfBoundsCheckFromToIndex(final BiFunction<String, List<Integer>, ? extends RuntimeException> biFunction, final int n, final int n2, final int n3) {
        return outOfBounds(biFunction, "checkFromToIndex", n, n2, n3);
    }
    
    private static RuntimeException outOfBoundsCheckFromIndexSize(final BiFunction<String, List<Integer>, ? extends RuntimeException> biFunction, final int n, final int n2, final int n3) {
        return outOfBounds(biFunction, "checkFromIndexSize", n, n2, n3);
    }
    
    public static <X extends RuntimeException> BiFunction<String, List<Integer>, X> outOfBoundsExceptionFormatter(final Function<String, X> function) {
        return new BiFunction<String, List<Integer>, X>() {
            @Override
            public X apply(final String s, final List<Integer> list) {
                return function.apply(outOfBoundsMessage(s, list));
            }
        };
    }
    
    private static String outOfBoundsMessage(final String s, final List<Integer> list) {
        if (s == null && list == null) {
            return String.format("Range check failed", new Object[0]);
        }
        if (s == null) {
            return String.format("Range check failed: %s", list);
        }
        if (list == null) {
            return String.format("Range check failed: %s", s);
        }
        int n = 0;
        switch (s) {
            case "checkIndex": {
                n = 2;
                break;
            }
            case "checkFromToIndex":
            case "checkFromIndexSize": {
                n = 3;
                break;
            }
        }
        final String s2 = (list.size() != n) ? "" : s;
        switch (s2) {
            case "checkIndex": {
                return String.format("Index %d out-of-bounds for length %d", list.get(0), list.get(1));
            }
            case "checkFromToIndex": {
                return String.format("Range [%d, %d) out-of-bounds for length %d", list.get(0), list.get(1), list.get(2));
            }
            case "checkFromIndexSize": {
                return String.format("Range [%d, %<d + %d) out-of-bounds for length %d", list.get(0), list.get(1), list.get(2));
            }
            default: {
                return String.format("Range check failed: %s %s", s, list);
            }
        }
    }
    
    public static <X extends RuntimeException> int checkIndex(final int n, final int n2, final BiFunction<String, List<Integer>, X> biFunction) {
        if (n < 0 || n >= n2) {
            throw outOfBoundsCheckIndex(biFunction, n, n2);
        }
        return n;
    }
    
    public static <X extends RuntimeException> int checkFromToIndex(final int n, final int n2, final int n3, final BiFunction<String, List<Integer>, X> biFunction) {
        if (n < 0 || n > n2 || n2 > n3) {
            throw outOfBoundsCheckFromToIndex(biFunction, n, n2, n3);
        }
        return n;
    }
    
    public static <X extends RuntimeException> int checkFromIndexSize(final int n, final int n2, final int n3, final BiFunction<String, List<Integer>, X> biFunction) {
        if ((n3 | n | n2) < 0 || n2 > n3 - n) {
            throw outOfBoundsCheckFromIndexSize(biFunction, n, n2, n3);
        }
        return n;
    }
}
