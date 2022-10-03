package org.apache.tomcat.util.buf;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;

public final class StringUtils
{
    private static final String EMPTY_STRING = "";
    
    private StringUtils() {
    }
    
    public static String join(final String[] array) {
        if (array == null) {
            return "";
        }
        return join(Arrays.asList(array));
    }
    
    public static void join(final String[] array, final char separator, final StringBuilder sb) {
        if (array == null) {
            return;
        }
        join(Arrays.asList(array), separator, sb);
    }
    
    public static String join(final Collection<String> collection) {
        return join(collection, ',');
    }
    
    public static String join(final Collection<String> collection, final char separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        join(collection, separator, result);
        return result.toString();
    }
    
    public static void join(final Iterable<String> iterable, final char separator, final StringBuilder sb) {
        join(iterable, separator, new Function<String>() {
            @Override
            public String apply(final String t) {
                return t;
            }
        }, sb);
    }
    
    public static <T> void join(final T[] array, final char separator, final Function<T> function, final StringBuilder sb) {
        if (array == null) {
            return;
        }
        join(Arrays.asList(array), separator, function, sb);
    }
    
    public static <T> void join(final Iterable<T> iterable, final char separator, final Function<T> function, final StringBuilder sb) {
        if (iterable == null) {
            return;
        }
        boolean first = true;
        for (final T value : iterable) {
            if (first) {
                first = false;
            }
            else {
                sb.append(separator);
            }
            sb.append(function.apply(value));
        }
    }
    
    public interface Function<T>
    {
        String apply(final T p0);
    }
}
