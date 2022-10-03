package com.steadystate.css.util;

import java.util.Iterator;
import java.util.List;

public final class LangUtils
{
    public static final int HASH_SEED = 17;
    public static final int HASH_OFFSET = 37;
    
    private LangUtils() {
    }
    
    public static int hashCode(final int seed, final int hashcode) {
        return seed * 37 + hashcode;
    }
    
    public static int hashCode(final int seed, final boolean b) {
        return hashCode(seed, b ? 1 : 0);
    }
    
    public static int hashCode(final int seed, final Object obj) {
        return hashCode(seed, (obj != null) ? obj.hashCode() : 0);
    }
    
    public static String join(final List<String> values, String separator) {
        if (values == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        boolean isFirst = true;
        final StringBuilder result = new StringBuilder();
        for (final String part : values) {
            if (part != null && part.length() > 0) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    result.append(separator);
                }
                result.append(part);
            }
        }
        return result.toString();
    }
    
    public static boolean equals(final Object obj1, final Object obj2) {
        return (obj1 == null) ? (obj2 == null) : obj1.equals(obj2);
    }
}
