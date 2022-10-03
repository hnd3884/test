package io.opencensus.internal;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public final class Utils
{
    private Utils() {
    }
    
    public static void checkArgument(final boolean isValid, @Nullable final Object errorMessage) {
        if (!isValid) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
    
    public static void checkArgument(final boolean expression, final String errorMessageTemplate, @Nullable final Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }
    
    public static void checkState(final boolean isValid, @Nullable final Object errorMessage) {
        if (!isValid) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
    
    public static void checkIndex(final int index, final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative size: " + size);
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: size=" + size + ", index=" + index);
        }
    }
    
    public static <T> T checkNotNull(final T arg, @Nullable final Object errorMessage) {
        if (arg == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return arg;
    }
    
    public static <T> void checkListElementNotNull(final List<T> list, @Nullable final Object errorMessage) {
        for (final T element : list) {
            if (element == null) {
                throw new NullPointerException(String.valueOf(errorMessage));
            }
        }
    }
    
    public static <K, V> void checkMapElementNotNull(final Map<K, V> map, @Nullable final Object errorMessage) {
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new NullPointerException(String.valueOf(errorMessage));
            }
        }
    }
    
    public static boolean equalsObjects(@Nullable final Object x, @Nullable final Object y) {
        return (x == null) ? (y == null) : x.equals(y);
    }
    
    private static String format(final String template, @Nullable final Object... args) {
        if (args == null) {
            return template;
        }
        final StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            final int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }
        return builder.toString();
    }
}
